package com.example.hrms.biz.user.repository;

import com.example.hrms.biz.user.model.User;
import com.example.hrms.biz.user.model.criteria.UserCriteria;
import com.example.hrms.enumation.RoleEnum;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT u.username, u.email, u.password, u.department_id, u.role_name, u.is_supervisor, u.status, u.employee_name, d.department_name " +
            "FROM Users u " +
            "LEFT JOIN Departments d ON u.department_id = d.department_id")
    List<User> getAllUsers();
    // Lấy người dùng theo tên người dùng
    @Select("SELECT u.username, u.email, u.password, u.department_id, d.department_name, " +
            "u.role_name, u.is_supervisor, u.status, u.employee_name " +
            "FROM Users u " +
            "LEFT JOIN Departments d ON u.department_id = d.department_id " +
            "WHERE u.username = #{username}")
    User getUserByUsername(String username);


    @Select("SELECT password FROM Users WHERE username = #{username}")
    String getPasswordByUsername(String username);

    // Thêm người dùng mới
    @Insert("INSERT INTO Users (username, email, password, department_id, role_name, is_supervisor, status, employee_name) VALUES (#{username}, #{email}, #{password}, #{departmentId}, #{role_name}, #{isSupervisor}, #{status}, #{employee_name})")
    void insertUser(User user);

    // Cập nhật thông tin người dùng
    @Update("UPDATE Users SET email = #{email}, password = #{password}, " +
            "department_id = (SELECT department_id FROM Departments WHERE department_name = #{departmentName}), " +
            "role_name = #{role_name}, is_supervisor = #{isSupervisor}, status = #{status}, " +
            "employee_name = #{employee_name} " +
            "WHERE username = #{username}")
    void updateUser(User user);


    // Xóa người dùng theo tên người dùng
    @Delete("DELETE FROM Users WHERE username = #{username}")
    void deleteUser(String username);

    // Tìm kiếm người dùng dựa trên departmentIds và roles
    @Select("<script>" +
            "SELECT username, email, password, department_id, role_name, is_supervisor, status, employee_name " +
            "FROM Users " +
            "WHERE " +
            "    (#{departmentIds} IS NULL OR department_id IN " +
            "        <foreach item='departmentId' collection='departmentIds' open='(' separator=',' close=')'>" +
            "            #{departmentId}" +
            "        </foreach>) " +
            "    AND (#{roles} IS NULL OR role_name IN " +
            "        <foreach item='role' collection='roles' open='(' separator=',' close=')'>" +
            "            #{role}" +
            "        </foreach>)" +
            "</script>")
    List<User> searchUsers(@Param("departmentIds") List<Long> departmentIds, @Param("roles") List<RoleEnum> roles);

    // Đếm số lượng người dùng dựa trên tiêu chí
    int count(UserCriteria criteria);

    // Lấy đơn vị và quyền của tất cả người dùng
    @Select("SELECT department_id, role_name FROM Users")
    List<User> getDepartmentsAndRoles();

    @Select("SELECT COUNT(*) FROM Users WHERE username = #{username}")
    int checkUsernameExists(String username);
}