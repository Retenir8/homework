package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Activity;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.ActivityRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
public class ActivityService {
    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    // 获取活动列表
    public DataResponse getActivityList(DataRequest dataRequest) {
        String filter = dataRequest.getString("filter");
        List<Activity> activity = activityRepository.findByNameContaining(filter);
        return CommonMethod.getReturnData(activity);
    }

    // 保存活动（新增或更新）
    public DataResponse activitySave(DataRequest dataRequest) {
        Activity activity = new Activity();
        activity.setName(dataRequest.getString("name"));
        activity.setDescription(dataRequest.getString("description"));
        activity.setLocation(dataRequest.getString("location"));
        activity.setStartTime(dataRequest.getDate("startTime"));
        activity.setEndTime(dataRequest.getDate("endTime"));
        activityRepository.save(activity);
        return CommonMethod.getReturnMessageOK();
    }

    // 删除活动
    public DataResponse activityDelete(DataRequest dataRequest) {
        Integer activityId = dataRequest.getInteger("activityId");
        activityRepository.deleteById(activityId);
        return CommonMethod.getReturnMessageOK();
    }
}