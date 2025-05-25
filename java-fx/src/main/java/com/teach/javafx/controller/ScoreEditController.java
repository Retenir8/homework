package com.teach.javafx.controller;

import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.util.CommonMethod;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;

/**
 * ScoreEditController 用于成绩编辑弹出对话框的控制
 */
public class ScoreEditController {

    @FXML
    private TextField studentField; // 显示学生姓名（不可编辑）
    @FXML
    private TextField courseField;  // 显示课程名称（不可编辑）
    @FXML
    private TextField markField;    // 供老师输入成绩

    // 内部记录学生和课程的ID，以及当前分数记录ID（如果是编辑）
    private Integer studentId;
    private Integer courseId;
    private Integer scoreId = null;

    // 父控制器，用于回调关闭操作
    private ScoreTableController scoreTableController = null;

    @FXML
    public void initialize() {
        // 学生和课程信息不可修改
        studentField.setEditable(false);
        courseField.setEditable(false);
    }

    /**
     * 点击 “确认” 按钮后，收集数据并回调 ScoreTableController 的 doClose 方法
     */
    @FXML
    public void okButtonClick() {
        Map<String, Object> data = new HashMap<>();
        if (studentId != null) {
            data.put("studentId", studentId);
        }
        if (courseId != null) {
            data.put("courseId", courseId);
        }
        data.put("scoreId", scoreId);
        data.put("mark", markField.getText());
        System.out.println(studentId);
        System.out.println(courseId);
        System.out.println(scoreId);
        scoreTableController.doClose("ok", data);
    }

    /**
     * 点击 “取消” 按钮时回调
     */
    @FXML
    public void cancelButtonClick() {
        scoreTableController.doClose("cancel", null);
    }

    public void setScoreTableController(ScoreTableController controller) {
        this.scoreTableController = controller;
    }

    /**
     * 显示对话框时调用：
     * 如果 data 为 null，则表示新增记录，清空所有显示内容；
     * 如果 data 不为 null，则为编辑模式，从 data 中读取并显示学生姓名、课程名称和成绩。
     * 此处要求 data 中包含 "studentId", "studentName", "courseId", "courseName", "scoreId" 和 "mark" 等信息。
     */
    public void showDialog(Map data) {
        if (data == null) {
            // 新增状态：清空所有数据，待外部调用 setStudentInfo/setCourseInfo
            scoreId = null;
            studentId = null;
            courseId = null;
            studentField.setText("");
            courseField.setText("");
            markField.setText("");
        } else {
            scoreId = CommonMethod.getInteger(data, "scoreId");
            studentId = CommonMethod.getInteger(data, "studentId");
            courseId = CommonMethod.getInteger(data, "courseId");
            studentField.setText(CommonMethod.getString(data, "studentName"));
            courseField.setText(CommonMethod.getString(data, "courseName"));
            markField.setText(CommonMethod.getString(data, "mark"));
        }
    }

    /**
     * 设置学生信息（用于新增状态时传入）
     */
    public void setStudentInfo(Integer id, String name) {
        this.studentId = id;
        studentField.setText(name);
    }

    /**
     * 设置课程信息（用于新增状态时传入）
     */
    public void setCourseInfo(Integer id, String name) {
        this.courseId = id;
        courseField.setText(name);
    }
}
