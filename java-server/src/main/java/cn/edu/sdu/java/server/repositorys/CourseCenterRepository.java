package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Course;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface CourseCenterRepository extends JpaRepository<Course,String> {
    // 在 CourseCenterRepository 中添加方法
    boolean existsByCourseId(Integer courseId);
    List<Course> findCoursesListByCourseId(Integer courseId);

    void deleteByCourseId(@Param("courseId") Integer courseId);
    // 如果需要其他查询方法
    // List<CourseCenter> findByTeacher(String teacher);
    // List<CourseCenter> findByCreditGreaterThan(int credit);
    Optional<Course> findByCourseId(Integer courseId);
    List<Course> findByCourseNameContainingIgnoreCase(String courseName);

}

