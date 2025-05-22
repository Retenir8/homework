package com.teach.javafx.controller;

import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ScoreEditController 用于成绩编辑弹出对话框的控制
 */
public class ScoreEditController {
    @FXML
    private ComboBox<OptionItem> studentComboBox;
    private List<OptionItem> studentList;
    @FXML
    private ComboBox<OptionItem> courseComboBox;
    private List<OptionItem> courseList;
    @FXML
    private TextField markField;

    // 用于记录当前编辑的记录，如果为 null 则表示新增
    private Integer scoreId = null;

    private ScoreTableController scoreTableController = null;

    @FXML
    public void initialize() {
        // 这里可做一些初始化操作（如对组件样式等的配置）
    }

    /**
     * 点击 “确定” 按钮后，收集数据并回调 ScoreTableController 的 doClose 方法
     */
    @FXML
    public void okButtonClick() {
        Map<String, Object> data = new HashMap<>();

        OptionItem op = studentComboBox.getSelectionModel().getSelectedItem();
        if (op != null) {
            // 将学生 id 保存到数据里，这里 op.getValue() 为学生的 id
            data.put("personId", Integer.parseInt(op.getValue()));
        }

        op = courseComboBox.getSelectionModel().getSelectedItem();
        if (op != null) {
            // 将课程 id 保存到数据里
            data.put("courseId", Integer.parseInt(op.getValue()));
        }

        data.put("scoreId", scoreId);
        data.put("mark", markField.getText());

        scoreTableController.doClose("ok", data);
    }

    /**
     * 点击 “取消” 按钮时回调
     */
    @FXML
    public void cancelButtonClick() {
        scoreTableController.doClose("cancel", null);
    }

    public void setScoreTableController(ScoreTableController scoreTableController) {
        this.scoreTableController = scoreTableController;
    }

    /**
     * 初始化 ComboBox 的数据来源，将从 ScoreTableController 中获得的学生和课程列表加载到各自的 ComboBox 中
     */
    public void init() {
        studentList = scoreTableController.getStudentList();
        courseList = scoreTableController.getCourseList();
        studentComboBox.getItems().clear();
        courseComboBox.getItems().clear();
        studentComboBox.getItems().addAll(studentList);
        courseComboBox.getItems().addAll(courseList);
    }

    /**
     * 显示对话框时调用：
     * 如果 data 为 null，则表示新增记录，清空各选项；
     * 如果 data 不为 null，则为编辑模式，从 data 中读取学生 id 和课程 id，
     * 根据对应 OptionItem 来显示学生姓名和课程名称，并将 ComboBox 锁定（不可修改）。
     *
     * @param data 包含成绩记录的 Map 数据，键包括 "scoreId", "personId", "courseId", "mark" 等
     */
    public void showDialog(Map data) {
        if (data == null) {
            // 新增状态：清空所有数据，允许修改
            scoreId = null;
            studentComboBox.getSelectionModel().clearSelection();
            courseComboBox.getSelectionModel().clearSelection();
            studentComboBox.setDisable(false);
            courseComboBox.setDisable(false);
            markField.setText("");
        } else {
            // 编辑状态：提取记录中的 id 值，并根据 OptionItem 列表找到对应的下标
            scoreId = CommonMethod.getInteger(data, "scoreId");

            // 获取存储的学生 id 和课程 id
            String studentIdStr = CommonMethod.getString(data, "personId");
            String courseIdStr = CommonMethod.getString(data, "courseId");

            // 根据 id 在列表中查找对应的 OptionItem 下标，确保显示的是学生姓名和课程名称
            int studentIndex = CommonMethod.getOptionItemIndexByValue(studentList, studentIdStr);
            if (studentIndex >= 0) {
                studentComboBox.getSelectionModel().select(studentIndex);
            }
            int courseIndex = CommonMethod.getOptionItemIndexByValue(courseList, courseIdStr);
            if (courseIndex >= 0) {
                courseComboBox.getSelectionModel().select(courseIndex);
            }

            // 编辑模式下，通常不允许修改学生和课程，所以禁用这两个 ComboBox
            studentComboBox.setDisable(true);
            courseComboBox.setDisable(true);
            markField.setText(CommonMethod.getString(data, "mark"));
        }
    }
}
