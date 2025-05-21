package cn.edu.sdu.java.server.repositorys;

import cn.edu.sdu.java.server.models.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // 需要导入 @Param
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Integer> {

    /**
     * 根据关联的 Student 的 studentId 查询成绩列表。
     * Spring Data JPA 自动生成查询，无需 @Query。
     * 这里的 'StudentStudentId' 表示：
     * findBy（通过...查询） + Student（Score 实体中关联的 Student 字段名） + StudentId（Student 实体中的 studentId 字段名）。
     * @param studentId 学生ID
     * @return 成绩列表
     */
    List<Score> findByStudentStudentId(Integer studentId);

    // ---

    /**
     * 根据学生ID和课程ID（可选）查询成绩列表。
     * 如果 studentId 或 courseId 为 0，则忽略相应的过滤条件。
     * @param studentId 学生ID (如果为0则不筛选)
     * @param courseId 课程ID (如果为0则不筛选)
     * @return 成绩列表
     */
    @Query(value="from Score s where (:studentId = 0 or s.student.studentId = :studentId) and (:courseId = 0 or s.course.courseId = :courseId)")
    List<Score> findScoresByStudentIdAndCourseIdOptional(@Param("studentId") Integer studentId, @Param("courseId") Integer courseId);

    // ---

    /**
     * 根据学生ID和课程名称（可选，模糊匹配）查询成绩列表。
     * 如果 courseName 为 null 或空字符串，则忽略课程名称的过滤条件。
     * @param studentId 学生ID
     * @param courseName 课程名称 (如果为null或空字符串则不筛选，否则进行模糊匹配)
     * @return 成绩列表
     */
    @Query(value="from Score s where s.student.studentId = :studentId and (:courseName is null or :courseName = '' or s.course.courseName like %:courseName%)")
    List<Score> findScoresByStudentIdAndCourseNameOptional(@Param("studentId") Integer studentId, @Param("courseName") String courseName);
}