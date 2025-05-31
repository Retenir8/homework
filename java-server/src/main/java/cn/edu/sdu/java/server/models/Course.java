package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Integer courseId;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    @JsonIgnore
    private Teacher teacher;

    @Column(name = "teacher_name")
    private String teacherName;

    @Column(name = "location")
    private String location;

    @Column(name = "credit")
    private Double credit;

    @Column(name = "schedule")
    private String schedule;

    @Column(name = "assessment_type")
    private String assessmentType;

    @ManyToMany
    @JoinTable(
            name = "student_courses",
            joinColumns = @JoinColumn(name = "course_id", referencedColumnName = "course_id"), // 这里的 course_id 引用 Course 的主键列
            inverseJoinColumns = @JoinColumn(name = "student_id", referencedColumnName = "student_id") // 这里的 student_id 引用 Student 的主键列
    )
    private List<Student> students = new ArrayList<>(); // 初始化 students 集合

    public Course () {
        this.students = new ArrayList<>(); // 也可以在无参构造方法中初始化
    }
    // 可选的字段：如果需要把所有35位时间信息以字符串形式统一存储
    @Column(name = "timeSlots")
    private String timeSlots;

    // 以下为课程在课表中的35个时段，默认值均为 "0"
    @Column(name = "c1")
    private String c1 = "0";
    @Column(name = "c2")
    private String c2 = "0";
    @Column(name = "c3")
    private String c3 = "0";
    @Column(name = "c4")
    private String c4 = "0";
    @Column(name = "c5")
    private String c5 = "0";
    @Column(name = "c6")
    private String c6 = "0";
    @Column(name = "c7")
    private String c7 = "0";
    @Column(name = "c8")
    private String c8 = "0";
    @Column(name = "c9")
    private String c9 = "0";
    @Column(name = "c10")
    private String c10 = "0";
    @Column(name = "c11")
    private String c11 = "0";
    @Column(name = "c12")
    private String c12 = "0";
    @Column(name = "c13")
    private String c13 = "0";
    @Column(name = "c14")
    private String c14 = "0";
    @Column(name = "c15")
    private String c15 = "0";
    @Column(name = "c16")
    private String c16 = "0";
    @Column(name = "c17")
    private String c17 = "0";
    @Column(name = "c18")
    private String c18 = "0";
    @Column(name = "c19")
    private String c19 = "0";
    @Column(name = "c20")
    private String c20 = "0";
    @Column(name = "c21")
    private String c21 = "0";
    @Column(name = "c22")
    private String c22 = "0";
    @Column(name = "c23")
    private String c23 = "0";
    @Column(name = "c24")
    private String c24 = "0";
    @Column(name = "c25")
    private String c25 = "0";
    @Column(name = "c26")
    private String c26 = "0";
    @Column(name = "c27")
    private String c27 = "0";
    @Column(name = "c28")
    private String c28 = "0";
    @Column(name = "c29")
    private String c29 = "0";
    @Column(name = "c30")
    private String c30 = "0";
    @Column(name = "c31")
    private String c31 = "0";
    @Column(name = "c32")
    private String c32 = "0";
    @Column(name = "c33")
    private String c33 = "0";
    @Column(name = "c34")
    private String c34 = "0";
    @Column(name = "c35")
    private String c35 = "0";
}