package com.example.shiftplanner.controller;

import com.example.shiftplanner.dto.RosterRequest;
import com.example.shiftplanner.dto.RosterResponse;
import com.example.shiftplanner.service.RosterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rosters")
public class RosterController {

    private final RosterService rosterService;

    public RosterController(RosterService rosterService) {
        this.rosterService = rosterService;
    }

    @PostMapping
    public ResponseEntity<RosterResponse> createRoster(@Valid @RequestBody RosterRequest request) {
        RosterResponse response = rosterService.createRoster(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RosterResponse>> getAllRosters() {
        return ResponseEntity.ok(rosterService.getAllRosters());
    }

    @GetMapping("/my")
    public ResponseEntity<List<RosterResponse>> getMyRosters(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(rosterService.getMyRosters(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RosterResponse> getRosterById(@PathVariable Long id) {
        return ResponseEntity.ok(rosterService.getRosterById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RosterResponse> updateRoster(@PathVariable Long id,
                                                       @Valid @RequestBody RosterRequest request) {
        return ResponseEntity.ok(rosterService.updateRoster(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRoster(@PathVariable Long id) {
        rosterService.deleteRoster(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Roster deleted successfully");
        return ResponseEntity.ok(response);
    }
}
