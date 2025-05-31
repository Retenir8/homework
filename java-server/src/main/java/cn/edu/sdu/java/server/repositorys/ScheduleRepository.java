package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // 需要导入 @Param
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    Optional<Schedule> findByStudentId(Integer studentId);

    void deleteByStudentId(Integer studentId);
    // 可根据需要添加自定义查询
}
