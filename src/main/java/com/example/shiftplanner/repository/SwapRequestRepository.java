package com.example.shiftplanner.repository;

import com.example.shiftplanner.entity.SwapRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SwapRequestRepository extends JpaRepository<SwapRequest, Long> {

    List<SwapRequest> findByRequestedById(Long employeeId);

    List<SwapRequest> findByRequestedToId(Long employeeId);

    List<SwapRequest> findByRequestedByIdOrRequestedToId(Long requestedById, Long requestedToId);
}
