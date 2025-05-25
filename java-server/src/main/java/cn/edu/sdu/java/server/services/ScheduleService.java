package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Schedule;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.CourseCenterRepository;
import cn.edu.sdu.java.server.repositorys.ScheduleRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    /**
     * 根据 studentId 获取课表记录
     */
    public Schedule getScheduleByStudentId(Integer studentId) {
        Optional<Schedule> optional = scheduleRepository.findById(studentId);
        return optional.orElse(null);
    }

    /**
     * 更新课表中的某个单元格
     * @param studentId 学生ID
     * @param key       要更新的字段（如 "c7" 或 "remarks"）
     * @param value     新值，对于 c1~c35 需要是课程ID数字（以字符串传递），空字符串表示清空字段
     */
    public Schedule updateScheduleCell(Integer studentId, String key, String value) {
        Optional<Schedule> optional = scheduleRepository.findById(studentId);
        if (!optional.isPresent()){
            throw new RuntimeException("未找到课表记录，studentId: " + studentId);
        }
        Schedule schedule = optional.get();
        try {
            if ("remarks".equals(key)) {
                schedule.setRemarks(value);
            } else if (key.startsWith("c")) {
                Integer courseId = null;
                if (value != null && !value.trim().isEmpty()) {
                    courseId = Integer.parseInt(value);
                }
                // 通过反射设置对应字段（确保 key 名称与实体中字段名称完全一致）
                Field field = Schedule.class.getDeclaredField(key);
                field.setAccessible(true);
                field.set(schedule, courseId);
            } else {
                throw new RuntimeException("无效的字段: " + key);
            }
            return scheduleRepository.save(schedule);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("更新课表失败", e);
        }
    }
}
