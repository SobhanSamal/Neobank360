package com.neobank.repository;
 
import com.neobank.entity.Bill;
import com.neobank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
 
import java.time.LocalDate;
import java.util.List;
 
public interface BillRepository extends JpaRepository<Bill, Long> {
   List<Bill> findByUserAndBillerNameIgnoreCaseAndDueDateBetween(
       User user,
       String billerName,
       LocalDate start,
       LocalDate end
   );
 
   List<Bill> findByUserOrderByDueDateAsc(User user);
 
   List<Bill> findByUserAndStatusOrderByPaidAtDesc(User user, Bill.BillStatus status);
   
   List<Bill> findByAccountUser(User user);
}
 