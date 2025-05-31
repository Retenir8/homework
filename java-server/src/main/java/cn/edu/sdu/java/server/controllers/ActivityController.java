package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.services.ActivityService;
import cn.edu.sdu.java.server.services.UserDetailsImpl;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    @Autowired
    public ActivityService activityService;

    @PostMapping("/creatActivity")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse activityEdit(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.createActivity(dataRequest);
    }
    @PostMapping("/deleteActivity")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse activityDelete(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.deleteActivity(dataRequest);
    }
    @PostMapping("/updateActivity")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse updateActivity(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.updateActivity(dataRequest);
    }
    @PostMapping("/searchByActivity")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse searchByStudent(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.searchByActivity(dataRequest);
    }
    @PostMapping("/searchByStudent")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse searchByActivity(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.searchByStudent(dataRequest);
    }
    @PostMapping("/getActivityStudentListAll")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getActivityStudentListAll(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.getActivityStudentListAll(dataRequest);
    }//以上为供给老师管理员的页面

    @PostMapping("/getActivityListAll")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getActivityListAll(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.getActivityListAll(dataRequest);
    }
    @PostMapping("/activityApply")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse activityApply(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.activityApply(dataRequest);
    }
    @PostMapping("/activityApplyDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse activityApplyDelete(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.activityApplyDelete(dataRequest);
    }
    @PostMapping("/getApplyActivityList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getApplyActivityList(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.getApplyActivityList(dataRequest);
    }
    @PostMapping("/getAppliedActivityList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getAppliedActivityList(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.getAppliedActivityList(dataRequest);
    }
    @PostMapping("/getApplyActivityListForSearch")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getApplyActivityListForSearch(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.getApplyActivityListForSearch(dataRequest);
    }
    @PostMapping("/getAppliedActivityListForSearch")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getAppliedActivityListForSearch(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.getAppliedActivityListForSearch(dataRequest);
    }
    //以上为供给学生使用的一些端口
    @PostMapping ("/getMyName")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public DataResponse getMyLeaveInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return CommonMethod.getReturnMessageError("❌ 用户未登录！");
        }
        String userName = userDetails.getUsername();
        String name = userDetails.getPerName();
        Map<String, String> m = new HashMap<>();
        m.put("num",userName);
        m.put("name",name);
        return CommonMethod.getReturnData(m);
    }//获取当前用户名name,num的方法
}
