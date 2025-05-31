package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Activity;
import cn.edu.sdu.java.server.models.ActivityStudent;
import cn.edu.sdu.java.server.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.transaction.Transactional;
import java.util.List;

@Repository
public interface ActivityStudentRepository extends JpaRepository<ActivityStudent, Long> {


    // 例如，查询特定活动的所有参与记录
    List<ActivityStudent> findByActivity(Activity activity);

    // 例如，查询特定学生参与的所有记录
    List<ActivityStudent> findByStudent(Student student);

    @Transactional
    @Modifying
    @Query("DELETE FROM ActivityStudent as ast " +
            "WHERE ast.activity.activity_id = :activityId " +
            "AND ast.student.studentId = :studentId")
    void deleteByActivityIdAndStudentId(@Param("activityId") int activityId, @Param("studentId") int studentId);

    boolean existsByStudentAndActivity(Student student, Activity activity);

    void deleteByActivityAndStudent(Activity activity, Student student);
}
