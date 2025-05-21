package cn.edu.sdu.java.server.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(	name = "honor",
        uniqueConstraints = {
        })
public class Honor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer honorId;

    @Size(max = 50)
    private String honorName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "honor_level", length = 50) // 指定数据库列名和长度
    private HonorLevel honorLevel;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "honor_type", length = 50) // 指定数据库列名和长度
    private HonorType honorType;

    @Size(max = 100)
    private String organization;

    @Temporal(TemporalType.DATE) // 指定数据库存储为日期类型
    private Date awardDate;

    @Size(max = 20)
    private String academicTerm;

    @ManyToOne
    @JoinColumn(name="personId")
    private Person person;
}
