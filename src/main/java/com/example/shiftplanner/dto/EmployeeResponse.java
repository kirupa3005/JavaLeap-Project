package com.example.shiftplanner.dto;

import com.example.shiftplanner.entity.Employee;
import com.example.shiftplanner.entity.Role;

public class EmployeeResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private Role role;

    public EmployeeResponse() {
    }

    public EmployeeResponse(Long id, String name, String email, String phone, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public static EmployeeResponse fromEntity(Employee employee) {
        if (employee == null) return null;
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getRole()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
