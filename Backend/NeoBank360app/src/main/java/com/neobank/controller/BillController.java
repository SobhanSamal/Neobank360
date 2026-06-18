package com.neobank.controller;

import com.neobank.dto.BillRequestDTO;
import com.neobank.dto.BillResponseDTO;
import com.neobank.service.BillService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
public class BillController {

   private final BillService billService;

   public BillController(BillService billService) {
       this.billService = billService;
   }

   @PostMapping
   public ResponseEntity<BillResponseDTO> create(@Valid @RequestBody BillRequestDTO request) {
       return ResponseEntity.status(HttpStatus.CREATED).body(billService.create(request));
   }

   @GetMapping
   public ResponseEntity<List<BillResponseDTO>> listMine() {
       return ResponseEntity.ok(billService.listMine());
   }

   @GetMapping("/{id}")
   public ResponseEntity<BillResponseDTO> getById(@PathVariable Long id) {
       return ResponseEntity.ok(billService.getById(id));
   }

   @PatchMapping("/{id}/status")
   public ResponseEntity<BillResponseDTO> updateStatus(
       @PathVariable Long id,
       @RequestBody Map<String, String> request
   ) {
       return ResponseEntity.ok(billService.updateStatus(id, request.get("status")));
   }
}
 