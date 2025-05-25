package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "schedule")
public class Schedule {

    @Id
    @Column(name = "student_id")
    private Integer studentId;

    @Column(name = "c1")
    private Integer c1;
    @Column(name = "c2")
    private Integer c2;
    @Column(name = "c3")
    private Integer c3;
    @Column(name = "c4")
    private Integer c4;
    @Column(name = "c5")
    private Integer c5;
    @Column(name = "c6")
    private Integer c6;
    @Column(name = "c7")
    private Integer c7;
    @Column(name = "c8")
    private Integer c8;
    @Column(name = "c9")
    private Integer c9;
    @Column(name = "c10")
    private Integer c10;
    @Column(name = "c11")
    private Integer c11;
    @Column(name = "c12")
    private Integer c12;
    @Column(name = "c13")
    private Integer c13;
    @Column(name = "c14")
    private Integer c14;
    @Column(name = "c15")
    private Integer c15;
    @Column(name = "c16")
    private Integer c16;
    @Column(name = "c17")
    private Integer c17;
    @Column(name = "c18")
    private Integer c18;
    @Column(name = "c19")
    private Integer c19;
    @Column(name = "c20")
    private Integer c20;
    @Column(name = "c21")
    private Integer c21;
    @Column(name = "c22")
    private Integer c22;
    @Column(name = "c23")
    private Integer c23;
    @Column(name = "c24")
    private Integer c24;
    @Column(name = "c25")
    private Integer c25;
    @Column(name = "c26")
    private Integer c26;
    @Column(name = "c27")
    private Integer c27;
    @Column(name = "c28")
    private Integer c28;
    @Column(name = "c29")
    private Integer c29;
    @Column(name = "c30")
    private Integer c30;
    @Column(name = "c31")
    private Integer c31;
    @Column(name = "c32")
    private Integer c32;
    @Column(name = "c33")
    private Integer c33;
    @Column(name = "c34")
    private Integer c34;
    @Column(name = "c35")
    private Integer c35;

    @Column(name = "remarks")
    private String remarks;

}
