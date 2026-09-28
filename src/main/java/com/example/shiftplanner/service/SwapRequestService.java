package com.example.shiftplanner.service;

import com.example.shiftplanner.dto.SwapRequestDto;
import com.example.shiftplanner.dto.SwapResponseDto;
import com.example.shiftplanner.entity.Employee;
import com.example.shiftplanner.entity.Roster;
import com.example.shiftplanner.entity.SwapRequest;
import com.example.shiftplanner.entity.SwapStatus;
import com.example.shiftplanner.exception.BusinessException;
import com.example.shiftplanner.exception.ResourceNotFoundException;
import com.example.shiftplanner.repository.EmployeeRepository;
import com.example.shiftplanner.repository.RosterRepository;
import com.example.shiftplanner.repository.SwapRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SwapRequestService {

    private final SwapRequestRepository swapRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final RosterRepository rosterRepository;

    public SwapRequestService(SwapRequestRepository swapRequestRepository,
                              EmployeeRepository employeeRepository,
                              RosterRepository rosterRepository) {
        this.swapRequestRepository = swapRequestRepository;
        this.employeeRepository = employeeRepository;
        this.rosterRepository = rosterRepository;
    }

    public List<SwapResponseDto> getAllSwaps() {
        return swapRequestRepository.findAll().stream()
                .map(SwapResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<SwapResponseDto> getMySwaps(String userEmail) {
        Employee employee = employeeRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + userEmail));

        return swapRequestRepository.findByRequestedByIdOrRequestedToId(employee.getId(), employee.getId()).stream()
                .map(SwapResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public SwapResponseDto getSwapById(Long id) {
        SwapRequest swap = swapRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Swap request not found with id: " + id));
        return SwapResponseDto.fromEntity(swap);
    }

    public SwapResponseDto createSwap(SwapRequestDto request, String requesterEmail) {
        // 1. Get logged-in employee (requester)
        Employee requester = employeeRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + requesterEmail));

        // 2. Get roster
        Roster roster = rosterRepository.findById(request.getRosterId())
                .orElseThrow(() -> new ResourceNotFoundException("Roster not found with id: " + request.getRosterId()));

        // 3. RULE 2: Check roster belongs to logged-in employee
        if (!roster.getEmployee().getId().equals(requester.getId())) {
            throw new BusinessException("You can only create a swap request for your own roster assignment.");
        }

        // 4. Get target employee
        Employee targetEmployee = employeeRepository.findById(request.getRequestedToId())
                .orElseThrow(() -> new ResourceNotFoundException("Target employee not found with id: " + request.getRequestedToId()));

        // 5. RULE 3: Requester and target employee cannot be the same
        if (requester.getId().equals(targetEmployee.getId())) {
            throw new BusinessException("Requester and target employee cannot be the same.");
        }

        // 6. Create swap request with status PENDING
        SwapRequest swap = new SwapRequest();
        swap.setRoster(roster);
        swap.setRequestedBy(requester);
        swap.setRequestedTo(targetEmployee);
        swap.setReason(request.getReason());
        swap.setStatus(SwapStatus.PENDING);

        // 7. Save and return
        SwapRequest saved = swapRequestRepository.save(swap);
        return SwapResponseDto.fromEntity(saved);
    }

    @Transactional
    public SwapResponseDto acceptSwap(Long swapId, String userEmail) {
        // 1. Get logged-in employee
        Employee employee = employeeRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + userEmail));

        // 2. Get swap request
        SwapRequest swap = swapRequestRepository.findById(swapId)
                .orElseThrow(() -> new ResourceNotFoundException("Swap request not found with id: " + swapId));

        // 3. RULE 4: Check logged-in employee is the target employee (requestedTo)
        if (!swap.getRequestedTo().getId().equals(employee.getId())) {
            throw new BusinessException("Only the target employee can accept this swap request.");
        }

        // 4. RULE 5: Check status is PENDING
        if (swap.getStatus() != SwapStatus.PENDING) {
            throw new BusinessException("Only PENDING swap requests can be accepted.");
        }

        // 5. RULE 7: Before applying the swap, check whether target employee already has a shift on that date
        Roster roster = swap.getRoster();
        if (rosterRepository.existsByEmployeeIdAndDate(employee.getId(), roster.getDate())) {
            throw new BusinessException("Target employee already has a shift on this date.");
        }

        // 6. RULE 6: Update the roster's employee to the target employee
        roster.setEmployee(employee);
        rosterRepository.save(roster);

        // 7. Update status to ACCEPTED
        swap.setStatus(SwapStatus.ACCEPTED);
        SwapRequest updated = swapRequestRepository.save(swap);

        return SwapResponseDto.fromEntity(updated);
    }

    @Transactional
    public SwapResponseDto rejectSwap(Long swapId, String userEmail) {
        // 1. Get logged-in employee
        Employee employee = employeeRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + userEmail));

        // 2. Get swap request
        SwapRequest swap = swapRequestRepository.findById(swapId)
                .orElseThrow(() -> new ResourceNotFoundException("Swap request not found with id: " + swapId));

        // 3. RULE 4: Check logged-in employee is the target employee (requestedTo)
        if (!swap.getRequestedTo().getId().equals(employee.getId())) {
            throw new BusinessException("Only the target employee can reject this swap request.");
        }

        // 4. RULE 5: Check status is PENDING
        if (swap.getStatus() != SwapStatus.PENDING) {
            throw new BusinessException("Only PENDING swap requests can be rejected.");
        }

        // 5. Set status to REJECTED (roster remains unchanged)
        swap.setStatus(SwapStatus.REJECTED);
        SwapRequest updated = swapRequestRepository.save(swap);

        return SwapResponseDto.fromEntity(updated);
    }
}
