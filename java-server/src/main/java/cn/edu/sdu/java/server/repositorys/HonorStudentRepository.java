package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Honor;
import cn.edu.sdu.java.server.models.HonorStudent;
import cn.edu.sdu.java.server.models.Student;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HonorStudentRepository extends JpaRepository<HonorStudent, Long> {


    // 例如，查询特定活动的所有参与记录
    List<HonorStudent> findByHonor(Honor honor);

    // 例如，查询特定学生参与的所有记录
    List<HonorStudent> findByStudent(Student student);


    @Transactional
    @Modifying
    @Query("DELETE FROM HonorStudent as ast " +
            "WHERE ast.honor.honorId = :honorId " +
            "AND ast.student.studentId = :studentId")
    void deleteByActivityIdAndStudentId(@Param("honorId") int honorId, @Param("studentId") int studentId);

}