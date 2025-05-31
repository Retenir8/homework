package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.LeaveInfo;
import cn.edu.sdu.java.server.models.Student;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LeaveInfoRepository extends JpaRepository<LeaveInfo, Integer> {
    // 根据学生查询请假记录
    List<LeaveInfo> findByStudent(Student student);
    @Query("SELECT l FROM LeaveInfo l WHERE l.student.studentId = :studentId")
    List<LeaveInfo> findByStudentId(@Param("studentId") Integer studentId);
    @Query("SELECT l FROM LeaveInfo l WHERE l.student.person.name LIKE %:name%")
    List<LeaveInfo> findByStudentName(@Param("name") String name);

    // 查询所有未销假的请假记录
    @Query("SELECT l FROM LeaveInfo l WHERE l.back = '未销假'")
    List<LeaveInfo> findUnreturnedLeaves();

    @Query("SELECT l FROM LeaveInfo l WHERE l.student.person.num = :num")
    List<LeaveInfo> findByStudentPersonNum(@Param("num") String num);

    void deleteById(Integer leaveInfoId); // 直接删除记录


    void deleteByStudentStudentId(Integer studentId);
}
