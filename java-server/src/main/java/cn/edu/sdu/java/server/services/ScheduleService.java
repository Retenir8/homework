package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Schedule;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.repositorys.ScheduleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Transactional
    public void updateSchedule(DataRequest req) {
        Integer studentId = req.getInteger("studentId");
        if (studentId == null) {
            throw new IllegalArgumentException("缺少学生ID");
        }

        // 查询或新建指定学生的 Schedule 实体
        Optional<Schedule> optional = scheduleRepository.findByStudentId(studentId);
        Schedule schedule = optional.orElseGet(() -> {
            Schedule s = new Schedule();
            s.setStudentId(studentId);
            return s;
        });

        // 遍历请求中传入的所有键，排除 "studentId"
        Map<String, Object> dataMap = req.getData();
        for (String key : dataMap.keySet()) {
            if ("studentId".equals(key)) {
                continue;
            }
            // 只处理键符合 "c数字" 格式的字段，比如 "c1" ~ "c35"
            if (key.matches("c\\d+")) {
                try {
                    Field field = Schedule.class.getDeclaredField(key);
                    field.setAccessible(true);
                    String remark = req.getString(key);
                    // 若传入的备注为空，则保持为空字符串或按实际业务需求设置默认值
                    remark = remark == null ? "" : remark;
                    field.set(schedule, remark);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException("更新课表字段失败: " + key, e);
                }
            }
        }

        scheduleRepository.save(schedule);
    }

}
