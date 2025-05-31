package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "honor_student")
@Getter
@Setter
public class HonorStudent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "honor_id")
    private Honor honor;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;


}
