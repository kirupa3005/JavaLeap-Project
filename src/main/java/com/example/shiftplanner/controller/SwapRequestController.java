package com.example.shiftplanner.controller;

import com.example.shiftplanner.dto.SwapRequestDto;
import com.example.shiftplanner.dto.SwapResponseDto;
import com.example.shiftplanner.service.SwapRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/swaps")
public class SwapRequestController {

    private final SwapRequestService swapRequestService;

    public SwapRequestController(SwapRequestService swapRequestService) {
        this.swapRequestService = swapRequestService;
    }

    @PostMapping
    public ResponseEntity<SwapResponseDto> createSwap(@Valid @RequestBody SwapRequestDto request,
                                                      Authentication authentication) {
        String requesterEmail = authentication.getName();
        SwapResponseDto response = swapRequestService.createSwap(request, requesterEmail);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SwapResponseDto>> getAllSwaps() {
        return ResponseEntity.ok(swapRequestService.getAllSwaps());
    }

    @GetMapping("/my")
    public ResponseEntity<List<SwapResponseDto>> getMySwaps(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(swapRequestService.getMySwaps(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SwapResponseDto> getSwapById(@PathVariable Long id) {
        return ResponseEntity.ok(swapRequestService.getSwapById(id));
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<SwapResponseDto> acceptSwap(@PathVariable Long id,
                                                      Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(swapRequestService.acceptSwap(id, email));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<SwapResponseDto> rejectSwap(@PathVariable Long id,
                                                      Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(swapRequestService.rejectSwap(id, email));
    }
}
