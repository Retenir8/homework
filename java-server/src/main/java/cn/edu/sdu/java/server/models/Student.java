package cn.edu.sdu.java.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Integer studentId;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @Size(max = 20)
    private String major;

    @Size(max = 50)
    private String className;

    @Size(max = 20)
    private String name;

    @Size(max = 20)
    private String num;

    @ManyToMany
    @JoinTable(
            name = "student_courses",
            joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "student_id"), // 明确指定引用 Student 的主键列
            inverseJoinColumns = @JoinColumn(name = "course_id", referencedColumnName = "course_id")) // 明确指定引用 Course 的主键列
    @JsonIgnore
    private List<Course> courses;
    @ManyToMany
    @JoinTable(
            name = "activity_student",  // 中间表的名称，与 Activity 类中的对应
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id"))
    @JsonIgnore
    private List<Activity> activities;

    @ManyToMany
    @JoinTable(
            name = "honor_student",  // 中间表的名称，与 Honor 类中的对应
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "honor_id"))
    @JsonIgnore
    private List<Honor> honors;
}