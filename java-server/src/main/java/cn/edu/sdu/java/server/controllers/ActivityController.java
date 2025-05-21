package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.ActivityService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/activity")
public class ActivityController {
    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping("/getActivityList")
    public DataResponse getActivityList(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.getActivityList(dataRequest);
    }

    @PostMapping("/activitySave")
    public DataResponse activitySave(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.activitySave(dataRequest);
    }

    @PostMapping("/activityDelete")
    public DataResponse activityDelete(@Valid @RequestBody DataRequest dataRequest) {
        return activityService.activityDelete(dataRequest);
    }
}