package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;


/**
 * Student学生表实体类 保存每个学生的信息，
 * Integer personId 学生表 student 主键 person_id 与Person表主键相同
 * Person person 关联到该用户所用的Person对象，账户所对应的人员信息 person_id 关联 person 表主键 person_id
 * String major 专业
 * String className 班级
 *
 */
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(	name = "student",
        uniqueConstraints = {
        })
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentId;

    @ManyToOne
    @JoinColumn(name="person_id")
    private Person person;

    @Size(max = 20)
    private String major;

    @Size(max = 50)
    private String className;

    // 多对多关联：一个学生可以选修多门课程
    @Setter
    @ManyToMany(mappedBy = "students", fetch = FetchType.LAZY)
    private Set<Course> courses = new HashSet<>();

}
//$2a$10$IgSRtR/lS/iHFfH5rh6fUuXKobn/j.caegD2JJQ4vdJggCWuzppDS