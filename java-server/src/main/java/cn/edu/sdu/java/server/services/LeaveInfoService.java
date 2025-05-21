package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.LeaveInfo;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.LeaveInfoRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LeaveInfoService {
    private final LeaveInfoRepository leaveInfoRepository;
    private final StudentRepository studentRepository;
    private final StudentService studentService;

    @Autowired
    public LeaveInfoService(LeaveInfoRepository leaveInfoRepository, StudentRepository studentRepository, StudentService studentService) {
        this.leaveInfoRepository = leaveInfoRepository;
        this.studentRepository = studentRepository;
        this.studentService = studentService;
    }

    public DataResponse getLeaveInfoByUserName(String userName) {
        List<LeaveInfo> leaveList = leaveInfoRepository.findByStudentPersonNum(userName);
        return CommonMethod.getReturnData(leaveList);
    }


    public OptionItemList getAvailableStudents(DataRequest dataRequest) {
        List<Student> sList = studentRepository.findStudentListByNumName("");  //数据库查询操作
        List<OptionItem> students = new ArrayList<>();
        for (Student s : sList) {
            students.add(new OptionItem( s.getStudentId(),s.getStudentId()+"", s.getPerson().getNum()+"-"+s.getPerson().getName()));
        }
        System.out.println("返回的学生列表: " + students); // 调试输出
        return new OptionItemList(0, students);
    }



    // 获取某学生的请假记录
    public DataResponse getLeaveRecords(DataRequest req) {
        Integer studentId = req.getInteger("studentId");
        System.out.println("收到的 studentId: " + studentId);

        List<LeaveInfo> leaveList = leaveInfoRepository.findByStudentId(studentId);
        System.out.println("🔍 查询到的 leaveList: " + leaveList);
        if (leaveList.isEmpty()) {
            System.out.println("后端错误: 没有找到该学生的请假记录！");
            return CommonMethod.getReturnMessageError("没有找到该学生的请假记录！");
        }

        System.out.println("后端返回数据: " + leaveList);
        return CommonMethod.getReturnData(leaveList);
    }

    public DataResponse getLeaveRecordsByName(DataRequest req) {
        String studentName = req.getString("studentName");
        List<LeaveInfo> leaveList = leaveInfoRepository.findByStudentName(studentName);
        if (leaveList.isEmpty()) {
            System.out.println("后端错误: 没有找到该学生的请假记录！");
            return CommonMethod.getReturnMessageError("没有找到该学生的请假记录！");
        }

        System.out.println("后端返回数据: " + leaveList);
        return CommonMethod.getReturnData(leaveList);
    }
    public DataResponse getLeaveRecordsByBack() {
        List<LeaveInfo> leaveList = leaveInfoRepository.findUnreturnedLeaves();
        if (leaveList.isEmpty()) {
            System.out.println("后端错误: 没有找到该学生的请假记录！");
            return CommonMethod.getReturnMessageError("没有找到该学生的请假记录！");
        }

        System.out.println("后端返回数据: " + leaveList);
        return CommonMethod.getReturnData(leaveList);

    }

    public DataResponse leaveSave(DataRequest req) {
        System.out.println("收到的请假请求数据: " + req);

        Integer studentId = req.getInteger("studentId");
        if (studentId == null) {
            return CommonMethod.getReturnMessageError("没有选中学生，无法提交请假申请！");
        }

        LeaveInfo leaveInfo = new LeaveInfo();
        leaveInfo.setLeaveInfoId(req.getInteger("leaveInfoId"));
        leaveInfo.setStudent(studentRepository.findById(studentId).orElse(null));
        leaveInfo.setStudentNum(Objects.requireNonNull(studentRepository.findById(studentId).orElse(null)).getPerson().getNum());
        leaveInfo.setStudentName(Objects.requireNonNull(studentRepository.findById(studentId).orElse(null)).getPerson().getName());
        leaveInfo.setReason(req.getString("reason"));
        leaveInfo.setDestination(req.getString("destination"));
        leaveInfo.setPhone(req.getString("phone"));
        leaveInfo.setBackTime(req.getString("backTime"));
        leaveInfo.setBack("未销假");

        System.out.println("数据库保存 LeaveInfo: " + leaveInfo);
        leaveInfoRepository.save(leaveInfo);

        return CommonMethod.getReturnMessageOK("请假申请提交成功！");
    }

    public DataResponse deleteLeave(DataRequest req) {
        Integer leaveInfoId = req.getInteger("leaveInfoId");

        if (leaveInfoId == null) {
            System.out.println("❌ 错误: leaveInfoId 为空！");
            return CommonMethod.getReturnMessageError("请假记录 ID 不能为空！");
        }

        Optional<LeaveInfo> leaveInfoOptional = leaveInfoRepository.findById(leaveInfoId);
        if (leaveInfoOptional.isEmpty()) {
            System.out.println("❌ 错误: 找不到 leaveInfoId=" + leaveInfoId + " 的请假记录！");
            return CommonMethod.getReturnMessageError("请假记录不存在！");
        }

        leaveInfoRepository.deleteById(leaveInfoId);
        System.out.println("✅ 已成功删除请假记录: ID=" + leaveInfoId);
        return CommonMethod.getReturnMessageOK("请假记录删除成功！");
    }

    public DataResponse updateLeaveInfo(DataRequest req) {
        Integer leaveInfoId = req.getInteger("leaveInfoId");
        Optional<LeaveInfo> leaveInfoOptional = leaveInfoRepository.findById(leaveInfoId);

        if (leaveInfoOptional.isEmpty()) {
            return CommonMethod.getReturnMessageError("请假记录不存在！");
        }

        LeaveInfo leaveInfo = leaveInfoOptional.get();

        // 更新审核状态、辅导员意见、销假状态
        leaveInfo.setAuditStatus(req.getString("auditStatus"));
        leaveInfo.setOpinion(req.getString("opinion"));
        leaveInfo.setBack(req.getString("back"));
        System.out.println("新back状态"+ leaveInfo.getBack());
        leaveInfoRepository.save(leaveInfo);
        System.out.println("✅ 已更新请假记录 ID: " + leaveInfoId);

        return CommonMethod.getReturnMessageOK("审核信息更新成功！");
    }

}

