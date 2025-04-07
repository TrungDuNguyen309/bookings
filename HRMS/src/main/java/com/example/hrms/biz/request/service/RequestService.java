package com.example.hrms.biz.request.service;

import com.example.hrms.biz.request.model.Request;
import com.example.hrms.biz.request.model.criteria.RequestCriteria;
import com.example.hrms.biz.request.model.dto.RequestDto;
import com.example.hrms.biz.request.repository.RequestMapper;
import com.example.hrms.biz.user.controller.rest.UserRestController;
import com.example.hrms.biz.user.model.User;
import com.example.hrms.biz.user.service.UserService;
import com.example.hrms.common.http.criteria.Page;
import com.example.hrms.enumation.RoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class RequestService {
    private final RequestMapper requestMapper;
    private final UserService userService;

    public RequestService(RequestMapper requestMapper, UserService userService) {
        this.requestMapper = requestMapper;
        this.userService = userService;
    }
    public int count(RequestCriteria criteria) {
        log.info("Counting requests with criteria: {}", criteria);
        return requestMapper.count(criteria);
    }

    public List<RequestDto.Resp> list(Page page, RequestCriteria criteria) {
        page.validate();
        log.info("Fetching request list with criteria: {}", criteria);
        try {
            List<Request> requests = requestMapper.select(
                    page.getPageSize(),
                    page.getOffset(),
                    criteria.getRequestId(),
                    criteria.getUsername(),
                    criteria.getDepartmentId(),
                    criteria.getRequestType(),
                    criteria.getRequestReason(),
                    criteria.getRequestStatus(),
                    criteria.getApproverUsername(),
                    criteria.getStartTime(),
                    criteria.getEndTime()
            );
            log.info("Number of requests fetched: {}", requests.size());
            return requests.stream().map(RequestDto.Resp::toResponse).toList();
        } catch (Exception e) {
            log.error("Error fetching request list", e);
            throw new RuntimeException("Could not fetch request list, please try again later.");
        }
    }

    public List<RequestDto.Resp> getRequestsForSupervisor(Page page, RequestCriteria criteria) {
        // Lấy vai trò của người dùng hiện tại
        RoleEnum currentUserRole = userService.getCurrentUserRole();
        if (currentUserRole != RoleEnum.SUPERVISOR) {
            throw new RuntimeException("Access denied: Only supervisors can access this resource.");
        }

        // Lấy tên người dùng hiện tại
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Current user: {}", currentUsername);
        log.info("Request criteria: {}", criteria);

        // Lấy danh sách yêu cầu cho phòng ban của người giám sát
        page.validate();
        log.info("Fetching request list for supervisor: {}", currentUsername);
        try {
            List<Request> requests = requestMapper.selectRequestsForSupervisor(
                    currentUsername,
                    page.getPageSize(),
                    page.getOffset()
            );
            log.info("Number of requests fetched: {}", requests.size());
            return requests.stream().map(RequestDto.Resp::toResponse).toList();
        } catch (Exception e) {
            log.error("Error fetching request list for supervisor", e);
            throw new RuntimeException("Could not fetch request list for supervisor, please try again later.");
        }
    }
}