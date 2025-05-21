package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "courses")  // 明确指定表名
public class Courses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer courseId;

    @Column(name = "course_name", nullable = false)  // 添加非空约束
    private String courseName;

    @Column(name = "teacher")
    private String teacher;

    @Column(name = "location")
    private String location;

    @Column(name = "credit")
    private Integer credit;

    @Column(name = "schedule")
    private String schedule;

    @Column(name = "assessment_type")
    private String assessmentType;
}