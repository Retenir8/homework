package cn.edu.sdu.java.server.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@Entity
@Table( name = "honor",
        uniqueConstraints = {
        })
public class Honor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer honorId;

    @Size(max = 50)
    private String honorName;

    @NotNull
    @Column(name = "honor_level", length = 50) // 指定数据库列名和长度
    private String honorLevel;

    @NotNull
    @Column(name = "honor_type", length = 50) // 指定数据库列名和长度
    private String honorType;

    @Size(max = 100)
    private String host;

    @Temporal(TemporalType.DATE) // 指定数据库存储为日期类型
    private Date date;

    @ManyToMany
    @JoinTable(
            name = "Honor_student",
            joinColumns = @JoinColumn(name = "honor_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> students = new ArrayList<>(); // 初始化 students 集合
    public Honor() {
        this.students = new ArrayList<>(); // 也可以在无参构造方法中初始化
    }
}
