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
     * 更新课表单元格，前端传入 { "studentId": ..., "key": "c7", "value": "101" }
     */
    @PostMapping("/updateCell")
    public DataResponse updateCell(@RequestBody DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        String key = dataRequest.getString("key");
        String value = dataRequest.getString("value"); // 传输参数中用 "value" 表示新值
        if (studentId == null || key == null) {
            return CommonMethod.getReturnMessageError("缺少必要参数");
        }
        Schedule schedule = scheduleService.updateScheduleCell(studentId, key, value);
        return CommonMethod.getReturnData(schedule);
    }

}