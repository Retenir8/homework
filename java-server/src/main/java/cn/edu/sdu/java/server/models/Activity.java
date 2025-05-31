package cn.edu.sdu.java.server.models;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "activity") // 指定数据库表名为 "activity"
@Getter
@Setter
public class Activity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自动生成主键
    @Column(name = "activity_id") // 映射到数据库表的 "id" 列
    private int activity_id;

    @Column(name = "title", nullable = false, length = 255) // 映射到 "title" 列，非空，长度255
    private String title;

    @Column(name = "start_time", nullable = false) // 映射到 "start_time" 列，非空
    @Temporal(TemporalType.TIMESTAMP) // 指定映射到数据库的时间戳类型
    private Date startTime;

    @Column(name = "end_time", nullable = false) // 映射到 "end_time" 列，非空
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    @Column(name = "location", length = 255) // 映射到 "location" 列，长度255
    private String location;

    @ManyToMany
    @JoinTable(
            name = "activity_student",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> students = new ArrayList<>(); // 初始化 students 集合

    public Activity() {
        this.students = new ArrayList<>(); // 也可以在无参构造方法中初始化
    }
}

