package cn.edu.sdu.java.server.controllers;


import cn.edu.sdu.java.server.models.Schedule;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.ScheduleService;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 获取课表数据，前端传入 { "studentId": ... }
     */
    @PostMapping("/getSchedule")
    public DataResponse getSchedule(@RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        if (studentId == null) {
            return CommonMethod.getReturnMessageError("缺少学生ID");
        }
        Schedule schedule = scheduleService.getScheduleByStudentId(studentId);
        if (schedule == null) {
            return CommonMethod.getReturnMessageError("未找到课表记录");
        }
        return CommonMethod.getReturnData(schedule);
    }

    /**
     * 更新课表备注接口
     * 前端传入的 JSON 应该包含 studentId 和 c1~c35 的键值对。
     */
    @PostMapping("/updateSchedule")
    public DataResponse updateSchedule(@RequestBody DataRequest req) {
        try {
            scheduleService.updateSchedule(req);
            return CommonMethod.getReturnMessageOK("更新课表成功");
        } catch (Exception e) {
            return CommonMethod.getReturnMessageError("更新课表失败：" + e.getMessage());
        }
    }

}