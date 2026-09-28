package com.example.shiftplanner.service;

import com.example.shiftplanner.dto.RosterRequest;
import com.example.shiftplanner.dto.RosterResponse;
import com.example.shiftplanner.entity.Employee;
import com.example.shiftplanner.entity.Roster;
import com.example.shiftplanner.entity.Shift;
import com.example.shiftplanner.exception.BusinessException;
import com.example.shiftplanner.exception.ResourceNotFoundException;
import com.example.shiftplanner.repository.EmployeeRepository;
import com.example.shiftplanner.repository.RosterRepository;
import com.example.shiftplanner.repository.ShiftRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RosterService {

    private final RosterRepository rosterRepository;
    private final EmployeeRepository employeeRepository;
    private final ShiftRepository shiftRepository;

    public RosterService(RosterRepository rosterRepository,
                         EmployeeRepository employeeRepository,
                         ShiftRepository shiftRepository) {
        this.rosterRepository = rosterRepository;
        this.employeeRepository = employeeRepository;
        this.shiftRepository = shiftRepository;
    }

    public List<RosterResponse> getAllRosters() {
        return rosterRepository.findAll().stream()
                .map(RosterResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public RosterResponse getRosterById(Long id) {
        Roster roster = rosterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Roster not found with id: " + id));
        return RosterResponse.fromEntity(roster);
    }

    public List<RosterResponse> getMyRosters(String userEmail) {
        Employee employee = employeeRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + userEmail));

        return rosterRepository.findByEmployeeId(employee.getId()).stream()
                .map(RosterResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public RosterResponse createRoster(RosterRequest request) {
        // 1. Get employee by ID
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + request.getEmployeeId()));

        // 2. Get shift by ID
        Shift shift = shiftRepository.findById(request.getShiftId())
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + request.getShiftId()));

        // 3. RULE 1: Check whether the employee already has a roster for that date
        if (rosterRepository.existsByEmployeeIdAndDate(employee.getId(), request.getDate())) {
            throw new BusinessException("Employee already has a shift assigned on this date.");
        }

        // 4. Create and save roster
        Roster roster = new Roster();
        roster.setEmployee(employee);
        roster.setShift(shift);
        roster.setDate(request.getDate());

        Roster saved = rosterRepository.save(roster);
        return RosterResponse.fromEntity(saved);
    }

    public RosterResponse updateRoster(Long id, RosterRequest request) {
        Roster existingRoster = rosterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Roster not found with id: " + id));

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + request.getEmployeeId()));

        Shift shift = shiftRepository.findById(request.getShiftId())
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + request.getShiftId()));

        // Check if employee already has another shift on that date (excluding this roster record)
        if (rosterRepository.existsByEmployeeIdAndDateAndIdNot(employee.getId(), request.getDate(), id)) {
            throw new BusinessException("Employee already has a shift assigned on this date.");
        }

        existingRoster.setEmployee(employee);
        existingRoster.setShift(shift);
        existingRoster.setDate(request.getDate());

        Roster updated = rosterRepository.save(existingRoster);
        return RosterResponse.fromEntity(updated);
    }

    public void deleteRoster(Long id) {
        if (!rosterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Roster not found with id: " + id);
        }
        rosterRepository.deleteById(id);
    }
}
