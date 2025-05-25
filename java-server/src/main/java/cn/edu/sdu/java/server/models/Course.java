package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "course")  // 明确指定表名
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Integer courseId;

    @Column(name = "course_name", nullable = false)  // 添加非空约束
    private String courseName;

    @ManyToOne
    @JoinColumn(name="teacher_id")
    @JsonIgnore
    private Teacher teacher;

    @Column(name = "teacher_name")
    private String teacherName;

    @Column(name = "location")
    private String location;

    @Column(name = "credit")
    private Integer credit;

    @Column(name = "schedule")
    private String schedule;

    @Column(name = "assessment_type")
    private String assessmentType;

    // 多对多关联：一个课程可以有多个学生选修
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "student_courses", // 连接表名称
            joinColumns = @JoinColumn(name = "course_id"),  // 当前实体在连接表的外键列
            inverseJoinColumns = @JoinColumn(name = "student_id")  // 另一边（Student）的外键列
    )
    private Set<Student> students = new HashSet<>();

    // 为了方便维护双向关联关系，可以添加一个辅助方法：
    public void addStudent(Student student) {
        this.students.add(student);
        student.getCourses().add(this);
    }

    public void removeStudent(Student student) {
        this.students.remove(student);
        student.getCourses().remove(this);
    }
}