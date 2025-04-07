package com.example.hrms.biz.user.model.criteria;

import com.example.hrms.common.http.criteria.Page;
import com.example.hrms.enumation.RoleEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserCriteria extends Page {
    private String username;
    private String employee_name;
    private Long departmentId;
    private String department_name;
    private RoleEnum role_name;
    private Boolean isSupervisor;
    private String status;
    private String email;

    public UserCriteria(String username,String employee_name ,Long departmentId, String department_name , RoleEnum role_name, Boolean isSupervisor, String status, String email) {
        this.username = username;
        this.employee_name = employee_name;
        this.departmentId = departmentId;
        this.department_name = department_name;
        this.role_name = role_name;
        this.isSupervisor = isSupervisor;
        this.status = status;
        this.email = email;
    }

    public List<RoleEnum> getRoles() {
        return role_name != null ? List.of(role_name) : List.of();
    }

    public List<Long> getDepartmentIds() {
        return departmentId != null ? List.of(departmentId) : List.of();
    }
}