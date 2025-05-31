package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Integer> {
    // 查找学生和课程的选课记录
    // 之前是根据ID，现在是根据关联实体对象
    Optional<StudentCourse> findByStudentAndCourse(Student student, Course course);

    // **重要：现在是 deleteByStudent_StudentId**
    // `Student` 指的是 StudentCourse 实体中的 `student` 字段
    // `StudentId` 指的是 Student 实体中的主键 `studentId`
    void deleteByStudent_StudentId(Integer studentId);

    // 在 StudentCourseRepository 中定义删除方法
// 根据 course 对象中的 courseId 删除所有关联记录
    void deleteByCourse_CourseId(Integer courseId);

    // 查找某个学生的所有选课记录
    List<StudentCourse> findByStudent(Student student);

}

