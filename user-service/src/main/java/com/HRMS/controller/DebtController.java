package com.HRMS.controller;

import com.HRMS.dto.requests.DebtRequestDto;
import com.HRMS.dto.response.DebtResponseDto;
import com.HRMS.services.DebtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.HRMS.constants.RestApiList.*;

@RestController
@RequestMapping(DEBT)
@RequiredArgsConstructor
@CrossOrigin("*")
public class DebtController {
    final DebtService debtService;
    @PostMapping("/debt-request/{token}")
    public ResponseEntity<Boolean> requestAdvance(@RequestBody DebtRequestDto requestDTO, @PathVariable String token) {
        return ResponseEntity.ok(debtService.requestAdvance(requestDTO,token));
    }

    @GetMapping(ALLDEBTS)
    public List<DebtResponseDto> getAllDebts() {
        return debtService.getAllDebts();
    }








}
