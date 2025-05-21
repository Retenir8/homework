package cn.edu.sdu.java.server.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "leaveInfo")
public class LeaveInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer LeaveInfoId;

    @ManyToOne
    @JoinColumn(name="student_id")
    private Student student;
    @NotBlank
    @Size(max = 20)
    @Column(name = "studentNum")
    private String studentNum;
    @NotBlank
    @Size(max = 20)
    @Column(name = "studentName")
    private String studentName;
    //请假理由
    @Column(name = "reason")
    private String reason;
    //目的地
    @Column(name = "destination")
    private String destination;
    //联系方式
    @Column(name = "phone")
    private String phone;
    //返校时间
    @Column(name = "backTime")
    private String backTime;
    //辅导员意见
    @Column(name = "opinion")
    private String opinion;
    //是否销假
    @Column(name = "back")
    private String back;
    //审核状态
    @Column(nullable = false)
    private String auditStatus = "待审核"; // 新增字段，默认 "待审核"

}
