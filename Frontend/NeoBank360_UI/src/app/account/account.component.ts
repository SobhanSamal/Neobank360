import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { NavbarComponent } from '../navbar/navbar.component';
import { AuthService } from '../services/auth.service';
import { AccountBalanceService } from '../services/account-balance.service';
import { AccountData, AccountType, CreateAccountData, UserApiService } from '../services/user-api.service';

type AccountFormDetails = {
  fullName: string;
  gender: string;
  dob: string;
  mobile: string;
  alternateMobile: string;
  email: string;
  aadhaar: string;
  pan: string;
  addressLine1: string;
  city: string;
  state: string;
  district: string;
  pincode: string;
  nomineeName: string;
  nomineeRelation: string;
  nomineeMobile: string;
};

@Component({
  selector: 'app-account',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './account.component.html',
  styleUrl: './account.component.css',
})
export class AccountComponent implements OnInit {
  loading = false;
  creating = false;
  submitted = false;
  error = '';
  success = '';

  accounts: AccountData[] = [];
  selectedAccount: AccountData | null = null;

  // stores extra form info keyed by account id
  accountDetailsMap: Record<number, AccountFormDetails> = {};
  selectedDetails: AccountFormDetails | null = null;

  readonly genderOptions = ['Male', 'Female', 'Other'];

  readonly stateOptions: Record<string, string[]> = {
    'Andhra Pradesh': ['Anantapur', 'Chittoor', 'East Godavari', 'Guntur', 'Krishna', 'Kurnool', 'Nellore', 'Prakasam', 'Srikakulam', 'Visakhapatnam', 'Vizianagaram', 'West Godavari'],
    'Arunachal Pradesh': ['Anjaw', 'Changlang', 'Dibang Valley', 'East Kameng', 'East Siang', 'Kamle', 'Kra Daadi', 'Kurung Kumey', 'Lepa Rada', 'Lohit', 'Longding', 'Lower Dibang Valley', 'Lower Siang', 'Lower Subansiri', 'Namsai', 'Papum Pare', 'Siang', 'Tawang', 'Tirap', 'Upper Dibang Valley', 'Upper Siang', 'Upper Subansiri', 'West Kameng', 'West Siang'],
    'Assam': ['Baksa', 'Barpeta', 'Biswanath', 'Bongaigaon', 'Cachar', 'Charaideo', 'Chirang', 'Darrang', 'Dhemaji', 'Dhubri', 'Dibrugarh', 'Dima Hasao', 'Goalpara', 'Golaghat', 'Hailakandi', 'Hojai', 'Jorhat', 'Kamrup', 'Kamrup Metro', 'Karbi Anglong', 'Karimganj', 'Kokrajhar', 'Lakhimpur', 'Majuli', 'Morigaon', 'Nagaon', 'Nalbari', 'Sonitpur', 'South Salmara Mankachar', 'Tinsukia', 'Udalguri', 'West Karbi Anglong'],
    'Bihar': ['Araria', 'Arwal', 'Aurangabad', 'Banka', 'Begusarai', 'Bhagalpur', 'Bhojpur', 'Buxar', 'Chhapra', 'Darbhanga', 'East Champaran', 'Gaya', 'Gopalganj', 'Jamui', 'Jehanabad', 'Kaimur', 'Katihar', 'Khagaria', 'Kishanganj', 'Lakhisarai', 'Madhepura', 'Madhubani', 'Munger', 'Muzaffarpur', 'Nalanda', 'Nawada', 'Patna', 'Purnia', 'Rohtas', 'Saharsa', 'Samastipur', 'Samastipuri', 'Saran', 'Sheikhpura', 'Sheohar', 'Vaishali', 'West Champaran'],
    'Chhattisgarh': ['Balod', 'Balodabazar', 'Balrampur', 'Bastar', 'Bemetara', 'Bijapur', 'Bilaspur', 'Dantewada', 'Dhamtari', 'Durg', 'Gariaband', 'Gaurela Pendra Marwahi', 'Gondia', 'Jashpur', 'Kanker', 'Kawardha', 'Kondagaon', 'Korba', 'Koriya', 'Mahasamund', 'Mandir Hasaud', 'Mungeli', 'Narayanpur', 'Raigarh', 'Raipur', 'Rajnandgaon', 'Sukma', 'Surajpur', 'Surguja'],
    'Goa': ['North Goa', 'South Goa'],
    'Gujarat': ['Ahmedabad', 'Amreli', 'Anand', 'Aravalli', 'Banaskantha', 'Bardoli', 'Baroda', 'Bharuch', 'Bhavnagar', 'Botad', 'Chhota Udepur', 'Dahod', 'Dang', 'Disa', 'Diu', 'Gandhinagar', 'Gir Somnath', 'Godhra', 'Gondal', 'Halol', 'Hansot', 'Himatnagar', 'Idar', 'Jamnagar', 'Junagadh', 'Kheda', 'Kutch', 'Limdi', 'Lunavada', 'Mahisagar', 'Mahuva', 'Manavadar', 'Mandvi', 'Mangrol', 'Manpur', 'Mehsana', 'Morbi', 'Mundra', 'Nadiad', 'Navsari', 'Okha', 'Palanpur', 'Palitana', 'Panchmahal', 'Pardi', 'Patan', 'Petlad', 'Porbandar', 'Radhanpur', 'Rajkot', 'Rajpipla', 'Rander', 'Sanand', 'Sanosra', 'Santalpur', 'Sapbdi', 'Sarlai', 'Sasangir', 'Satlasana', 'Savli', 'Siddhpur', 'Sihor', 'Sojitra', 'Surat', 'Surendranagar', 'Talaja', 'Talod', 'Tapi', 'Tarapur', 'Taravali', 'Telangpur', 'Tharad', 'Thara', 'Thasra', 'Thavy', 'Umbergaon', 'Umreth', 'Unadkat', 'Unai', 'Unbavada', 'Vadodara', 'Vagra', 'Valsad', 'Vardhman', 'Varnama', 'Vasai', 'Vataman', 'Vejalpur', 'Velavadar', 'Verad', 'Verna', 'Verwal', 'Vestan', 'Vetrak', 'Vijapur', 'Vikram', 'Vilavadi', 'Viramgam', 'Virangam', 'Virpur', 'Visnagar', 'Visavadar', 'Visnagar', 'Vithal', 'Vithalwadi', 'Vithlari', 'Vitholdas', 'Vitthal', 'Vitwadi'],
    'Haryana': ['Ambala', 'Bhiwani', 'Charkhi Dadri', 'Faridabad', 'Fatehabad', 'Gurugram', 'Hisar', 'Jhajjar', 'Jind', 'Kaithal', 'Karnal', 'Kurukshetra', 'Mahendragarh', 'Mewat', 'Palwal', 'Panchkula', 'Panipat', 'Rewari', 'Rohtak', 'Sirsa', 'Sonipat', 'Yamunanagar'],
    'Himachal Pradesh': ['Bilaspur', 'Chamba', 'Hamirpur', 'Kangra', 'Kinnaur', 'Kullu', 'Lahaul Spiti', 'Mandi', 'Shimla', 'Sirmaur', 'Solan', 'Una'],
    'Jharkhand': ['Bokaro', 'Chatra', 'Deogarh', 'Dhanbad', 'Dumka', 'East Singhbhum', 'Garhwa', 'Giridih', 'Godda', 'Gumla', 'Hazaribagh', 'Jamtara', 'Khunti', 'Koderma', 'Latehar', 'Lohardaga', 'Pakur', 'Palamu', 'Pashchim Singhbhum', 'Ramgarh', 'Ranchi', 'Sahibganj', 'Seraikela Kharsawan', 'Simdega', 'West Singhbhum'],
    'Karnataka': ['Bagalkot', 'Ballari', 'Belagavi', 'Bengaluru Rural', 'Bengaluru Urban', 'Bidar', 'Bijapur', 'Chamarajanagar', 'Chikballapur', 'Chikkamagaluru', 'Chitradurga', 'Davangere', 'Dharwad', 'Gadag', 'Gulbarga', 'Hassan', 'Haveri', 'Kalaburagi', 'Kodagu', 'Kolar', 'Koppal', 'Mandya', 'Mangaluru', 'Mysore', 'Raichur', 'Ramanagara', 'Shimoga', 'Tumkur', 'Udupi', 'Uttara Kannada', 'Yadgiri'],
    'Kerala': ['Alappuzha', 'Ernakulam', 'Idukki', 'Kannur', 'Kasaragod', 'Kollam', 'Kottayam', 'Kozhikode', 'Malappuram', 'Palakkad', 'Pathanamthitta', 'Thiruvananthapuram', 'Thrissur', 'Wayanad'],
    'Madhya Pradesh': ['Agar Malwa', 'Alirajpur', 'Anuppur', 'Ashoknagar', 'Balaghat', 'Barwani', 'Betul', 'Bhopal', 'Burhanpur', 'Chhatarpur', 'Chhindwara', 'Chitrakoot', 'Damoh', 'Datia', 'Dindori', 'Dohad', 'Durg', 'East Nimar', 'Guna', 'Gwalior', 'Harda', 'Hoshangabad', 'Indore', 'Jabalpur', 'Jhabua', 'Khajuraho', 'Khandwa', 'Khargone', 'Kshitij', 'Maihar', 'Mandala', 'Mandsaur', 'Manawar', 'Mandi', 'Morena', 'Murwara', 'Nagod', 'Narsimhapur', 'Neemuch', 'Noj', 'Narmada', 'Narmadapuram', 'Narsinghpur', 'Narwar', 'Neemuch', 'Niwari', 'Nowgaon', 'Omkareshwar', 'Panna', 'Panagar', 'Patapur', 'Patanam', 'Pithampur', 'Raigarh', 'Raisela', 'Raisen', 'Rajgarh', 'Ratlam', 'Rayadurg', 'Rehli', 'Rethal', 'Rew', 'Rewah', 'Rewalpur', 'Rewa', 'Rewakot', 'Rewara', 'Rewathi', 'Rewati', 'Rind', 'Ripudaman', 'Risod', 'Rithora', 'Rithore', 'Ritora', 'Ritram', 'Ritthi', 'Rivadh', 'Rivarkot', 'Riva', 'Rivarth', 'Rivas', 'Rivers', 'Riverpuri', 'Riverton', 'Rivervale', 'Riverbend', 'Riverdale', 'Riverchase', 'Riverbottom', 'Riverbreak', 'Riverbrook', 'Riverchase', 'Riverdale', 'Riverfall', 'Riverforest', 'Riverfront', 'Rivergate', 'Rivergrove', 'Riverhead', 'Riverhill', 'Riverhollow', 'Riverhurst', 'Riverland', 'Riverlawn', 'Riverledge', 'Riverlight', 'Riverlodge', 'Rivermark', 'Rivermeadow', 'Rivermead', 'Rivermont', 'Riveroak', 'Riverpark', 'Riverplain', 'Riverplains', 'Riverpoint', 'Riverpointe', 'Riverridge', 'Riverrun', 'Riversdale', 'Riverscene', 'Riversdale', 'Riversedge', 'Rivershire', 'Rivershoals', 'Rivershores', 'Rivershore', 'Riverfront', 'Rivershor', 'Riverside', 'Riversides', 'Riverslope', 'Riversmith', 'Riversong', 'Riverstone', 'Rivertail', 'Rivertrace', 'Rivertree', 'Rivertrail', 'Rivertree', 'Rivertown', 'Rivertrends', 'Rivertribe', 'Rivertrop', 'Rivertrove', 'Riverturn', 'Rivervalley', 'Rivervalley', 'Rivervallis', 'Rivervanish', 'Riverwatch', 'Riverwater', 'Riverway', 'Riverwharf', 'Riverwind', 'Riverwood', 'Riverwold', 'Riverwold', 'Riverwoods'],
    'Maharashtra': ['Ahmadnagar', 'Akola', 'Amravati', 'Aurangabad', 'Beed', 'Bhandara', 'Buldhana', 'Chandrapur', 'Dhule', 'Gadchiroli', 'Gondia', 'Hingoli', 'Jalgaon', 'Jalna', 'Kolhapur', 'Latur', 'Mumbai City', 'Mumbai Suburban', 'Nagpur', 'Nanded', 'Nandurbar', 'Nashik', 'Osmangabad', 'Palghar', 'Parbhani', 'Pune', 'Raigad', 'Ratnagiri', 'Sangli', 'Satara', 'Satna', 'Sindhudurg', 'Solapur', 'Thane', 'Wardha', 'Washim', 'Yavatmal'],
    'Manipur': ['Bishnupur', 'Chandel', 'Churachandpur', 'Imphal East', 'Imphal West', 'Jiribam', 'Kamjong', 'Kangpokpi', 'Noney', 'Senapati', 'Tamenglong', 'Tengnoupal', 'Thoubal', 'Ukhrul'],
    'Meghalaya': ['East Garo Hills', 'East Jaintia Hills', 'East Khasi Hills', 'Jaintia Hills', 'Ri Bhoi', 'South Garo Hills', 'South West Garo Hills', 'South West Khasi Hills', 'West Garo Hills', 'West Jaintia Hills', 'West Khasi Hills'],
    'Mizoram': ['Aizawl', 'Champhai', 'Hnahthial', 'Khawzawl', 'Kolasib', 'Lawngtlai', 'Lunglei', 'Mamit', 'Mizoram', 'Saitual', 'Serchhip'],
    'Nagaland': ['Dimapur', 'Kiphire', 'Kohima', 'Longleng', 'Mokokchung', 'Mon', 'Phek', 'Peren', 'Tuensang', 'Wokha', 'Zunheboto'],
    'Odisha': ['Angul', 'Balangir', 'Balasore', 'Bargarh', 'Bhadrak', 'Boudh', 'Cuttack', 'Debagarh', 'Dhenkanal', 'Gajapati', 'Ganjam', 'Jagatsinghpur', 'Jajpur', 'Jharsuguda', 'Kandhamal', 'Keonjhar', 'Khordha', 'Koraput', 'Mayurbhanj', 'Nabarangpur', 'Nayagarh', 'Nuapada', 'Odisha', 'Puri', 'Rayagada', 'Sambalpur', 'Subarnapur', 'Sundargarh'],
    'Punjab': ['Amritsar', 'Barnala', 'Bathinda', 'Fatehgarh Sahib', 'Faridkot', 'Ferozepur', 'Gurdaspur', 'Hoshiarpur', 'Jalandhar', 'Kapurthala', 'Ludhiana', 'Mansa', 'Moga', 'Mohali', 'Muktsar', 'Pathankot', 'Patiala', 'Rupnagar', 'Sangrur', 'SAS Nagar'],
    'Rajasthan': ['Ajmer', 'Alwar', 'Banswara', 'Baran', 'Barmer', 'Bharatpur', 'Bhilwara', 'Bikaner', 'Bundi', 'Chittorgarh', 'Churu', 'Dausa', 'Dholpur', 'Dungarpur', 'Ganganagar', 'Hanumangarh', 'Jaisalmer', 'Jaipur', 'Jalor', 'Jhalawar', 'Jhunjhunu', 'Jodhpur', 'Karauli', 'Kota', 'Nagaur', 'Pali', 'Phalodi', 'Pindwara', 'Pratapgarh', 'Pushkar', 'Rajsamand', 'Ramsar', 'Raisen', 'Ratlam', 'Ratnagiri', 'Rayadurg', 'Rhanpur', 'Riyan', 'Riyasan', 'Rohat', 'Rolaka', 'Rongu', 'Ronha', 'Roopa', 'Ropaj', 'Rorah', 'Rorab', 'Roragarh', 'Rorajpur', 'Roravati', 'Roreti', 'Rori', 'Rorisad', 'Roron', 'Rorua', 'Rorwara', 'Rosanpur', 'Rosera', 'Roshanpur', 'Roshangarh', 'Roshanpura', 'Roshanpuri', 'Rosi', 'Rosind', 'Rosinda', 'Rosindi', 'Rosini', 'Rosino', 'Rosinpur', 'Rosira', 'Rosirgarh', 'Rosirganj', 'Rosisud', 'Rosita', 'Rosithpur', 'Rositi', 'Rosiya', 'Rosiyaganj', 'Rosiyapura', 'Rosiyara', 'Rosiyari', 'Rosiyaud', 'Rosiyaura', 'Rosiyavan', 'Rosiyavat', 'Rosiyayan', 'Rosiyayana', 'Rosiyaypur', 'Rosiyayrana', 'Rosiyayrup', 'Rosiyayva', 'Rosiyayvan', 'Rosiyayvanpur', 'Rosiyadar', 'Rosiyadan', 'Rosiyad', 'Rosiyada', 'Rosiyadi', 'Rosiyado', 'Rosiyadu', 'Rosiyadvara', 'Rosiyadvari', 'Rosiyadvari', 'Rosiyadvaru', 'Rosiya', 'Rosiyapalli', 'Rosiyapan', 'Rosiyapandi', 'Rosiyapani', 'Rosiyapanja', 'Rosiyapanju', 'Rosiyapanna', 'Rosiyapannala', 'Rosiyapanpali', 'Rosiyapanpalla', 'Rosiyapanpalli', 'Rosiyapanpalu', 'Rosiyapanpuri', 'Rosiyapara', 'Rosiyapara', 'Rosiyaparasi', 'Rosiyaparau', 'Rosiyaparauli', 'Rosiyaparchand', 'Rosiyaparha', 'Rosiyaparhana', 'Rosiyaparhi', 'Rosiyparho', 'Rosiyaparhu', 'Rosiyapariac', 'Rosiyariala', 'Rosiyariala'],
    'Sikkim': ['East Sikkim', 'North Sikkim', 'South Sikkim', 'West Sikkim'],
    'Tamil Nadu': ['Ariyalur', 'Chengalpattu', 'Chennai', 'Coimbatore', 'Cuddalore', 'Dharmapuri', 'Dindgul', 'Erode', 'Kallakurichi', 'Kanchipuram', 'Kanyakumari', 'Karur', 'Krishnagiri', 'Madurai', 'Mayiladuthurai', 'Nagapattinam', 'Namakkal', 'Nilgiri', 'Padukottai', 'Perambalur', 'Pudukkottai', 'Ramanathapuram', 'Ranipet', 'Salem', 'Sivaganga', 'Tenkasi', 'Thanjavur', 'Theni', 'Thiruvallur', 'Thiruvannamalai', 'Thiruvarur', 'Thoothukudi', 'Tirupathur', 'Tiruppur', 'Tiruvannamalai', 'Tiruvannamalai', 'Tiruvarur', 'Tirutani', 'Tiruvarangam', 'Tiruvanmiyur', 'Tiruvannamalai', 'Tiruvanparampu', 'Tiruvanperur', 'Tiruvanperur', 'Tiruvanperur', 'Tiruvanperur', 'Tiruvanperum', 'Tiruvanperumkudram', 'Tiruvanperumkundram', 'Tiruvanperumkudram', 'Tiruvanperumanade', 'Tiruvanperumalai', 'Tiruvanperumanallur', 'Tiruvanperumanallur', 'Tiruvanperumanallur', 'Tiruvanperumanallur', 'Tiruvanperumalai', 'Tiruvanperumalakkudi', 'Tiruvanperumalakkudi', 'Tiruvanperumalakkudi', 'Tiruvanperumalakkudi', 'Tiruvanperumalakkudi', 'Tiruvanperumalakkudi', 'Tiruvanperumalakkudi', 'Tiruvanperumalakkudi', 'Tiruvanperumalakkudi', 'Tiruvanperumalakkudi', 'Tiruvanperumalakkudi', 'Tiruvanperumalakkudi', 'Tiruppur', 'Tirupuram'],
    'Telangana': ['Adilabad', 'Bheemini', 'Hyderabad', 'Jagtial', 'Jangaon', 'Jayashankar Bhupalpalli', 'Jogulamba Gadwal', 'Kamareddy', 'Karimnagar', 'Khammam', 'Komaram Bheem', 'Komaram Bheem Asifabad', 'Mahabubabad', 'Mahabubnagar', 'Mancherial', 'Medak', 'Medchal', 'Medchal-Malkajgiri', 'Moinabad', 'Mulug', 'Munugode', 'Nagarjunakonda', 'Nagarkurnool', 'Narasingpur', 'Narsampet', 'Narsapur', 'Nirmal', 'Nizamabad', 'Peddapalli', 'Sangareddy', 'Siddipet', 'Tandur', 'Tandurus', 'Tandurpet', 'Tanduru', 'Tandurvalley', 'Tandurpet', 'Tandurpet', 'Tandurpet', 'Tandurpet', 'Tandurpet', 'Tandurpet', 'Tandurpet', 'Tandurpet', 'Tandurpet', 'Tandurpet', 'Tandurpet'],
    'Tripura': ['Dhalai', 'Gomti', 'Khowai', 'North Tripura', 'Sepahijala', 'South Tripura', 'Unokoti', 'West Tripura'],
    'Uttar Pradesh': ['Agra', 'Aligarh', 'Allahabad', 'Ambedkar Nagar', 'Amethi', 'Amroha', 'Auraiya', 'Azamgarh', 'Badaun', 'Baghpat', 'Bahraich', 'Ballia', 'Balrampur', 'Banda', 'Barabanki', 'Bareilly', 'Basti', 'Bijnor', 'Budaun', 'Bulandshahr', 'Chandauli', 'Chhatarpur', 'Chhindwara', 'Chitrakoot', 'Chitwan', 'Cooch Behar', 'Darbhanga', 'Deoria', 'Dewas', 'Dhampur', 'Dindori', 'Dohad', 'Durg', 'Etah', 'Etawah', 'Farrukhabad', 'Fatehpur', 'Faizabad', 'Firozabad', 'Gautam Buddh Nagar', 'Ghaziabad', 'Ghazipur', 'Gonda', 'Gorakhpur', 'Govardhan', 'Guna', 'Gwalior', 'Halia', 'Hamirpur', 'Hapur', 'Hardoi', 'Hargaon', 'Haridwar', 'Hata', 'Hathras', 'Hindaun', 'Hirapur', 'Hissar', 'Hita', 'Hojai', 'Indore', 'Indri', 'Isdak', 'Islampur', 'Isnabad', 'Isnanagar', 'Isnapur', 'Isnaravada', 'Isnarganj', 'Isnapur', 'Ishana', 'Ishanagar', 'Ishanapur', 'Ishanawa', 'Ishanganj', 'Ishangarh', 'Ishangarh', 'Ishangarha', 'Ishangarha', 'Ishangarh', 'Ishangarhpur', 'Ishangarh', 'Ishangarpura', 'Ishangarpure', 'Ishangarpuri', 'Ishangarpur', 'Ishangarpur', 'Ishangarpur', 'Ishangarpur', 'Ishangarpur', 'Ishangarpur', 'Ishangarpur', 'Ishangarpur'],
    'Uttarakhand': ['Almora', 'Bageshwar', 'Bijnor', 'Chamoli', 'Champawat', 'Chamba', 'Dehradun', 'Gairsain', 'Garhwal', 'Hardwar', 'Haridwar', 'Joshimath', 'Kumaon', 'Lansdowne', 'Mussoorie', 'Nainital', 'Pauri', 'Pauri Garhwal', 'Pithoragarh', 'Ramnagar', 'Rishikesh', 'Roorkee', 'Rudraprayag', 'Srinagar', 'Srinagar Garhwal', 'Tehri', 'Tehri Garhwal', 'Tharali', 'Thimla', 'Ukhimath', 'Uttarkashi'],
    'West Bengal': ['Alipurduar', 'Bankura', 'Birbhum', 'Burdwan', 'Cooch Behar', 'Darjeeling', 'Dinajpur', 'East Medinipur', 'Howrah', 'Hooghly', 'Jalpaiguri', 'Jhargram', 'Kalimpong', 'Kolkata', 'Malda', 'Murshidabad', 'Nadia', 'North 24 Parganas', 'North Dinajpur', 'Purba Bardhaman', 'Purba Medinipur', 'Purulia', 'South 24 Parganas', 'South Dinajpur', 'Uttar Dinajpur', 'West Medinipur'],
  };

  readonly nomineeRelationOptions = ['Spouse', 'Parent', 'Child', 'Sibling', 'Grandparent', 'Friend', 'Other'];



  readonly accountTypeOptions: Array<{ value: AccountType; label: string; description: string }> = [
    {
      value: 'SAVING',
      label: 'Saving Account',
      description: 'Best for daily savings with easy access and secure balance tracking.',
    },
    {
      value: 'CURRENT',
      label: 'Current Account',
      description: 'Best for frequent transactions and business-oriented operations.',
    },
  ];

  readonly form;
  private readonly detailsStorageKeyPrefix = 'nb360_account_details_';

  static minAgeValidator(minYears: number) {
    return (control: import('@angular/forms').AbstractControl) => {
      if (!control.value) return null;
      const dob = new Date(control.value);
      const cutoff = new Date();
      cutoff.setFullYear(cutoff.getFullYear() - minYears);
      return dob <= cutoff ? null : { minAge: { required: minYears } };
    };
  }

  constructor(private readonly fb: FormBuilder, private readonly userApi: UserApiService, private readonly authService: AuthService, private readonly balanceService: AccountBalanceService, private readonly cdr: ChangeDetectorRef) {
    this.form = this.fb.nonNullable.group({
      accountType: ['SAVING' as AccountType, [Validators.required]],
      fullName:    ['', [Validators.required, Validators.minLength(3)]],
      gender:      ['', [Validators.required]],
      dob:         ['', [Validators.required, AccountComponent.minAgeValidator(15)]],
      mobile:      ['', [Validators.required, Validators.pattern(/^[6-9]\d{9}$/)]],
      alternateMobile: ['', [Validators.pattern(/^[6-9]\d{9}$/)]],
      email:       ['', [Validators.required, Validators.email]],
      aadhaar:     ['', [Validators.required, Validators.pattern(/^\d{12}$/)]],
      pan:         ['', [Validators.required, Validators.pattern(/^[A-Z]{5}[0-9]{4}[A-Z]$/)]],
      addressLine1: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(120)]],
      city:         ['', [Validators.required, Validators.minLength(2), Validators.maxLength(60)]],
      state:        ['', [Validators.required, Validators.minLength(2), Validators.maxLength(60)]],
      district:     ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
      pincode:      ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
      nomineeName:  ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      nomineeRelation: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(40)]],
      nomineeMobile: ['', [Validators.pattern(/^[6-9]\d{9}$/)]],
    });
  }

  ngOnInit(): void {
    this.loadPersistedAccountDetails();
    this.loadAccounts();
    // Pre-fill email from session
    const sessionEmail = this.authService.getUserEmail();
    if (sessionEmail) {
      this.form.get('email')?.setValue(sessionEmail);
      this.form.get('email')?.disable();
    }
    // Pre-fill fullName from profile API
    this.userApi.getProfile().subscribe({
      next: (profile) => {
        this.form.get('fullName')?.setValue(profile.fullName);
        this.form.get('fullName')?.disable();
        this.cdr.detectChanges(); // Render profile form immediately
      }
    });
  }

  loadAccounts(): void {
    this.loading = true;
    this.error = '';
    this.userApi.getMyAccounts().subscribe({
      next: (data) => {
        this.accounts = data;
        this.loading = false;
        this.cdr.detectChanges(); // Render account list immediately
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.error = this.resolveAccountApiError(err, 'list');
        this.cdr.detectChanges(); // Render error immediately
      },
    });
  }

  createAccount(): void {
    this.submitted = true;
    if (this.form.invalid) return;

    const selectedType = this.form.getRawValue().accountType;
    const hasSameType = this.accounts.some((account) => account.accountType === selectedType);
    if (hasSameType) {
      this.error = 'No more than 1 SAVING or 1 CURRENT account can be created.';
      this.success = '';
      this.cdr.detectChanges(); // Render duplicate-type validation immediately
      return;
    }

    this.creating = true;
    this.error = '';
    this.success = '';

    const payload: CreateAccountData = this.form.getRawValue();
    this.userApi.createAccount(payload).subscribe({
      next: (created) => {
        this.creating = false;
        this.success = `Account created successfully (${created.accountNumber})`;
        const raw = this.form.getRawValue();
        this.accountDetailsMap[created.id] = {
          fullName: raw.fullName,
          gender: raw.gender,
          dob:      raw.dob,
          mobile:   raw.mobile,
          alternateMobile: raw.alternateMobile,
          email:    raw.email,
          aadhaar:  raw.aadhaar,
          pan:      raw.pan,
          addressLine1: raw.addressLine1,
          city: raw.city,
          state: raw.state,
          district: raw.district,
          pincode: raw.pincode,
          nomineeName: raw.nomineeName,
          nomineeRelation: raw.nomineeRelation,
          nomineeMobile: raw.nomineeMobile,
        };
        this.persistAccountDetails();
        this.accounts = [created, ...this.accounts];
        this.selectedAccount = created;
        this.selectedDetails = this.accountDetailsMap[created.id];
        this.cdr.detectChanges(); // Render account creation success immediately
        
        // Notify all components about account creation
        this.balanceService.notifyAccountCreated(created);
      },
      error: (err: HttpErrorResponse) => {
        this.creating = false;
        this.error = this.resolveAccountApiError(err, 'create');
        this.cdr.detectChanges(); // Render error immediately
      },
    });
  }

  viewAccount(id: number): void {
    this.error = '';
    this.success = '';

    this.userApi.getAccountById(id).subscribe({
      next: (account) => {
        this.selectedAccount = account;
        this.selectedDetails = this.accountDetailsMap[account.id] ?? null;
        this.cdr.detectChanges(); // Render account details immediately
      },
      error: (err: HttpErrorResponse) => {
        this.error = this.resolveAccountApiError(err, 'view');
        this.cdr.detectChanges(); // Render error immediately
      },
    });
  }

  get selectedTypeDescription(): string {
    const selected = this.form.getRawValue().accountType;
    return this.accountTypeOptions.find((x) => x.value === selected)?.description || '';
  }

  hasError(controlName: string, errorName: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.hasError(errorName) && (control.touched || this.submitted);
  }

  displayType(type: AccountType): string {
    return type === 'SAVING' ? 'Saving Account' : 'Current Account';
  }

  getDistricts(state: string | undefined): string[] {
    if (!state || !this.stateOptions[state]) {
      return [];
    }
    return this.stateOptions[state];
  }

  private getDetailsStorageKey(): string {
    const email = this.authService.getUserEmail() || 'guest';
    return `${this.detailsStorageKeyPrefix}${email}`;
  }

  private loadPersistedAccountDetails(): void {
    if (typeof window === 'undefined') {
      return;
    }

    try {
      const raw = sessionStorage.getItem(this.getDetailsStorageKey());
      this.accountDetailsMap = raw ? (JSON.parse(raw) as Record<number, AccountFormDetails>) : {};
    } catch {
      this.accountDetailsMap = {};
    }
  }

  private persistAccountDetails(): void {
    if (typeof window === 'undefined') {
      return;
    }

    sessionStorage.setItem(this.getDetailsStorageKey(), JSON.stringify(this.accountDetailsMap));
  }

  private resolveAccountApiError(err: HttpErrorResponse, operation: 'create' | 'list' | 'view'): string {
    if (err.status === 0) return 'Network error: backend is not reachable.';
    if (err.status === 401) return 'Unauthorized. Please login again.';

    if (err.status === 409) {
      return (err.error?.message as string) || 'You already have this account type. Only one Saving and one Current account are allowed.';
    }

    if (err.status === 403) {
      if (operation === 'view') return 'Forbidden: You do not own this account.';
      if (operation === 'create') return 'Forbidden: Account creation is denied for this session.';
      return 'Forbidden: You do not have permission to access accounts.';
    }

    if (err.status === 404) return 'Account not found.';
    if (err.status === 400) return (err.error?.message as string) || 'Invalid account data.';

    return (err.error?.message as string) || 'Account operation failed.';
  }
}
