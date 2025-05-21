package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Teacher 教师表实体类 保存每位教师的信息
 * Integer personId 教师表 teacher 主键 person_id，与 Person 表主键相同
 * Person person 关联到该教师所用的 Person 对象，账户所对应的人员信息
 * String title 教师职称
 * String degree 教师学历
 */

@Getter
@Setter
@Entity
@Table(name = "teacher",
        uniqueConstraints = {
        })
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer teacherId;

    @ManyToOne
    @JoinColumn(name="person_id")
    private Person person;

    @Size(max = 20)
    private String title;  // 教师职称，如“教授”或“副教授”

    @Size(max = 20)
    private String degree;  // 教师学历，如“博士”或“硕士”

}
