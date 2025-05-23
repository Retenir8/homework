package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Courses;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface CourseCenterRepository extends JpaRepository<Courses,String> {
    // 在 CourseCenterRepository 中添加方法
    boolean existsByCourseId(Integer courseId);
    List<Courses> findCoursesListByCourseId(Integer courseId);
    @Query("DELETE FROM Courses c WHERE c.courseId = :courseId")
    void deleteByCourseId(@Param("courseId") Integer courseId);
    // 如果需要其他查询方法
    // List<CourseCenter> findByTeacher(String teacher);
    // List<CourseCenter> findByCreditGreaterThan(int credit);
    Optional<Courses> findByCourseId(Integer courseId);
    List<Courses> findByCourseNameContainingIgnoreCase(String courseName);

}

