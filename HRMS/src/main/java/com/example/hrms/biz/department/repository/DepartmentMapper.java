package com.example.hrms.biz.department.repository;

import com.example.hrms.biz.department.model.Department;
import com.example.hrms.biz.department.model.criteria.DepartmentCriteria;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface DepartmentMapper {

    @Insert("INSERT INTO Departments (department_name) VALUES (#{departmentName})")
    @Options(useGeneratedKeys = true, keyProperty = "departmentId")
    void insert(Department department);

    @Select("SELECT department_name FROM Departments")
    List<String> findAllDepartmentNames();
    @Select("""
        SELECT d.department_id AS departmentId,d.department_name AS departmentName,r.role_name AS roleName,u.username AS userName
        FROM Departments d
        JOIN Users u ON d.department_id = u.department_id
        JOIN Roles r ON u.role_name = r.role_name
        WHERE d.department_id = #{id}
        ORDER BY r.role_name ASC;
    """)
    List<Department> findById(Long id);

    @Select("SELECT " +
            "d.department_id AS departmentId, " +
            "d.department_name AS departmentName, " +
            "u.username AS userName, " +
            "r.role_name AS roleName " +
            "FROM " +
            "Users u " +
            "JOIN " +
            "Departments d ON u.department_id = d.department_id " +
            "JOIN " +
            "Roles r ON u.role_name = r.role_name " +
            "WHERE " +
            "1=1 " +
            "AND (#{departmentId} IS NULL OR d.department_id = #{departmentId}) " +
            "AND (#{departmentName} IS NULL OR d.department_name LIKE CONCAT('%', #{departmentName}, '%')) " +
            "AND (#{userName} IS NULL OR u.username LIKE CONCAT('%', #{userName}, '%')) " +
            "AND (#{roleName} IS NULL OR r.role_name LIKE CONCAT('%', #{roleName}, '%')) " +
            "ORDER BY " +
            "d.department_name ASC, r.role_name ASC")
    List<Department> filterByCriteria(DepartmentCriteria criteria);

    @Select("SELECT COUNT(*) FROM Departments WHERE department_name = #{departmentName} AND department_id != #{departmentId}")
    int countByNameExcludingId(@Param("departmentName") String departmentName, @Param("departmentId") Long departmentId);

    @Select("SELECT COUNT(*) FROM Departments WHERE department_name = #{departmentName}")
    int countByName(String departmentName);

    @Select("SELECT COUNT(*) FROM Departments")
    int count(DepartmentCriteria criteria);

    @Update("UPDATE Departments SET department_name = #{departmentName} WHERE department_id = #{departmentId} ")
    void updateDepartment(Department department);

    @Delete("DELETE FROM Departments WHERE department_id = #{departmentId}")
    void deleteDepartment(Long departmentId);
}