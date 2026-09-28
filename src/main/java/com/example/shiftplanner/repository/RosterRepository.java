package com.example.shiftplanner.repository;

import com.example.shiftplanner.entity.Roster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RosterRepository extends JpaRepository<Roster, Long> {

    List<Roster> findByEmployeeId(Long employeeId);

    boolean existsByEmployeeIdAndDate(Long employeeId, LocalDate date);

    Optional<Roster> findByEmployeeIdAndDate(Long employeeId, LocalDate date);

    boolean existsByEmployeeIdAndDateAndIdNot(Long employeeId, LocalDate date, Long id);
}
