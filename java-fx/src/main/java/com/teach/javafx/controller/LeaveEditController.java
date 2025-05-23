package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaveEditController {
//    @FXML
//    private ComboBox<OptionItem> studentComboBox;
    private List<OptionItem> studentList = new ArrayList<>(); // 初始化为空列表
    @FXML
    private TextField reasonField;
    @FXML
    private TextField destinationField;
    @FXML
    private TextField phoneField;
    @FXML
    private DatePicker backTimePick;
    @FXML
    private TextField opinionField;
    @FXML
    private CheckBox backCheckBox; // 是否销假
    @FXML
    private String auditStatus = "待审核";
    private Integer leaveInfoId;

    private LeaveTableController leaveTableController;
    public void setLeaveTableController(LeaveTableController controller) {
        this.leaveTableController = controller;
    }
    private LeaveInfoStudentController leaveInfoStudentController;
    public void setLeaveInfoStudentController(LeaveInfoStudentController controller) {
        this.leaveInfoStudentController = controller;
    }
    private LeaveInfoTeacherController leaveInfoTeacherController;
    public void setLeaveInfoTeacherController(LeaveInfoTeacherController controller) {
        this.leaveInfoTeacherController = controller;
    }


    @FXML
    private TextField studentNumField; // 输入学号的文本框
    @FXML
    private Label studentNameLabel; // 显示姓名的标签

    @FXML
    public void initialize() {
        System.out.println("LeaveEditController 初始化...");
    }


    public void init() {
        if (leaveTableController == null) {
            System.out.println("错误: leaveTableController 为 null！");
            return;
        }
        studentList = leaveTableController.getStudentList();
        if (studentList == null || studentList.isEmpty()) {
            System.out.println("警告: 学生列表为空！");
            studentList = new ArrayList<>();
        } else {
            System.out.println("加载学生列表: " + studentList);
        }
//        studentComboBox.getItems().clear();
//        studentComboBox.getItems().addAll(studentList);
    }

    public void showDialog(Map<String, Object> leaveData) {
        try {
            if (leaveData == null) {
                System.out.println("🔄 进入新增请假模式...");
                leaveInfoId = null;
                studentNumField.clear();
                studentNameLabel.setText("-"); // 清空姓名
                reasonField.clear();
                destinationField.clear();
                phoneField.clear();
                backTimePick.getEditor().setText("");
                opinionField.clear();
                backCheckBox.setSelected(false);
                auditStatus = "待审核"; // 默认状态
                studentNumField.setDisable(false);
            } else {
                System.out.println("🔄 进入审核销假模式...");
                leaveInfoId = CommonMethod.getInteger(leaveData, "leaveInfoId");

                String studentNum = CommonMethod.getString(leaveData, "studentNum");
                String studentName = CommonMethod.getString(leaveData, "studentName");

                if (studentNum == null || studentNum.isEmpty()) {
                    System.out.println("❌ 审核销假模式下 studentNum 为空！");
                } else {
                    studentNumField.setText(studentNum);
                    studentNameLabel.setText(studentName != null ? studentName : "未知学生"); // 显示学生姓名
                }

                reasonField.setText(CommonMethod.getString(leaveData, "reason"));
                destinationField.setText(CommonMethod.getString(leaveData, "destination"));
                phoneField.setText(CommonMethod.getString(leaveData, "phone"));
                backTimePick.getEditor().setText(CommonMethod.getString(leaveData, "backTime"));
                opinionField.setText(CommonMethod.getString(leaveData, "opinion"));

                // 确保审核状态正确更新
                if (!"待审核".equals(auditStatus)) {
                    System.out.println("🔄 使用最新的审核状态: " + auditStatus);
                } else {
                    auditStatus = CommonMethod.getString(leaveData, "auditStatus");
                }

                studentNumField.setDisable(true); // 审核时禁用学号输入
            }
        } catch (Exception e) {
            System.out.println("❌ 发生错误: " + e.getMessage());
            e.printStackTrace();
            MessageDialog.showDialog("请假表单加载失败：" + e.getMessage());
        }
    }

    @FXML
    private void okButtonClick() {
        System.out.println("🔍 处理请假信息...");
        Map<String, Object> data = new HashMap<>();

        // 直接使用 TextField 获取学号
        String studentNum = studentNumField.getText().trim();
        if (studentNum.isEmpty()) {
            MessageDialog.showDialog("❌ 请输入学号后提交！");
            return;
        }
        data.put("studentNum", studentNum);

        data.put("leaveInfoId", leaveInfoId);
        data.put("reason", reasonField.getText());
        data.put("destination", destinationField.getText());
        data.put("phone", phoneField.getText());
        data.put("backTime", backTimePick.getValue());
        data.put("opinion", opinionField.getText());
        data.put("back", backCheckBox.isSelected() ? "已销假" : "未销假");
        data.put("auditStatus", auditStatus); // 提交审核状态

        System.out.println("📦 发送数据: " + data);
        // 根据具体调用者类型调用回调
        if (leaveTableController != null) {
            leaveTableController.doClose("ok", data);
        } else if (leaveInfoStudentController != null) {
            leaveInfoStudentController.doClose("ok", data);
        } else if (leaveInfoTeacherController != null) {
            leaveInfoTeacherController.doClose("ok",data);
        }
    }




    @FXML
    public void cancelButtonClick() {
        if (leaveTableController != null) {
            leaveTableController.doClose("cancel", null);
        } else if (leaveInfoStudentController != null) {
            leaveInfoStudentController.doClose("cancel", null);
        }
        else if (leaveInfoTeacherController != null) {
            leaveInfoTeacherController.doClose("cancel", null);
        }
    }

    @FXML
    private void onVerifyStudentClick() {
        String studentNum = studentNumField.getText().trim();
        if (studentNum.isEmpty()) {
            MessageDialog.showDialog("请输入学号后进行查询！");
            return;
        }

        System.out.println("🔍 查询学号: " + studentNum);

        DataRequest req = new DataRequest();
        req.add("studentNum", studentNum);
        DataResponse res = HttpRequestUtil.request("/api/student/getStudentByNum", req);

        if (res != null && res.getCode() == 0 && res.getData() != null) {
            Map<String, Object> studentData = (Map<String, Object>) res.getData();
            String studentName = (String) studentData.get("name");
            studentNameLabel.setText(studentName); // 显示姓名
            MessageDialog.showDialog("✅ 学生验证成功：" + studentName);
        } else {
            studentNameLabel.setText("学生不存在");
            MessageDialog.showDialog("❌ 未找到该学号对应的学生！");
        }
    }


    public void approveLeaveClick() {
        auditStatus = "审核通过";
        System.out.println("✅ 审核状态更新为: " + auditStatus);
    }

    public void rejectLeaveClick() {
        auditStatus = "审核拒绝";
        System.out.println("❌ 审核状态更新为: " + auditStatus);
    }


}
