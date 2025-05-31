package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.services.LeaveInfoService;
import cn.edu.sdu.java.server.services.UserDetailsImpl;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/leaveInfo")

public class LeaveInfoController {
    private final LeaveInfoService leaveInfoService;

    @Autowired
    public LeaveInfoController(LeaveInfoService leaveInfoService) {
        this.leaveInfoService = leaveInfoService;
    }

    @PostMapping("/getStudentOptions")
    public OptionItemList getStudentOptions(@Valid @RequestBody DataRequest dataRequest) {
        System.out.println("执行 getStudentOptions() 方法...");
        return leaveInfoService.getAvailableStudents(dataRequest);
    }

    //学生申请
    @PostMapping("/apply")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public DataResponse applyLeave( @RequestBody DataRequest dataRequest) {
        System.out.println("准备添加");
        return leaveInfoService.leaveSave(dataRequest);
    }

    @PostMapping("/deleteLeave")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public DataResponse deleteLeave(@RequestBody DataRequest req) {
        return leaveInfoService.deleteLeave(req);
    }

    //查询自己的记录
    @PostMapping ("/getMyLeaveInfo")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public DataResponse getMyLeaveInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return CommonMethod.getReturnMessageError("❌ 用户未登录！");
        }
        String userName = userDetails.getUsername(); // ✅ 获取当前登录的用户名
        System.out.println("🔍 当前用户: " + userName);
        return leaveInfoService.getLeaveInfoByUserName(userName);
    }

    //多种查询
    @PostMapping("/getLeaveRecords")
    @PreAuthorize("hasRole('TEACHER' or hasRole('ADMIN'))")
    public DataResponse getLeaveRecords(@RequestBody DataRequest req) {
        return leaveInfoService.getLeaveRecords(req);
    }
    @PostMapping("/getLeaveRecordsByName")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public DataResponse getLeaveRecordsByName(@RequestBody DataRequest req) {
        return leaveInfoService.getLeaveRecordsByName(req);
    }
    @PostMapping("/getLeaveRecordsByBack")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public DataResponse getLeaveRecordsByBack() {
        return leaveInfoService.getLeaveRecordsByBack();
    }

    //审批销假功能
    @PostMapping("/updateLeaveInfo")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public DataResponse updateLeaveInfo(@RequestBody DataRequest req) {
        System.out.println("🔍 收到审核请求: " + req);

        Integer leaveInfoId = req.getInteger("leaveInfoId");
        if (leaveInfoId == null) {
            return CommonMethod.getReturnMessageError("请假记录 ID 不能为空！");
        }

        return leaveInfoService.updateLeaveInfo(req);
    }

}

