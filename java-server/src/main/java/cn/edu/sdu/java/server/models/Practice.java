package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table( name = "practice",
        uniqueConstraints = {
        })
public class Practice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 20)
    private String name;//实习公司名字
    @Size(max = 20)
    private String title;
    @Size(max = 20)
    private String time;
    @Size(max = 20)
    private String gender;
    @Size(max = 20)
    private String studentName;

}
