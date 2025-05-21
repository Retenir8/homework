package com.teach.javafx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.scene.control.cell.MapValueFactory;

public class CourseCenterController {

    // FXML组件注入
    @FXML
    private TableView<Map> courseTable;
    @FXML
    private TableColumn<Map, String> courseIdColumn;
    @FXML
    private TableColumn<Map, String> courseNameColumn;
    @FXML
    private TableColumn<Map, String> teacherColumn;
    @FXML
    private TableColumn<Map, String> locationColumn;
    @FXML
    private TableColumn<Map, Integer> creditColumn;
    @FXML
    private TableColumn<Map, String> scheduleColumn;
    @FXML
    private TableColumn<Map, String> assessmentTypeColumn;

    @FXML
    private TextField courseNameField;
    @FXML
    private TextField teacherField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField creditField;
    @FXML
    private TextField scheduleField;
    @FXML
    private TextField assessmentTypeField;
    // 数据源
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 初始化表格列绑定
        courseIdColumn.setCellValueFactory(new MapValueFactory<>("courseId"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        teacherColumn.setCellValueFactory(new MapValueFactory<>("teacher"));
        locationColumn.setCellValueFactory(new MapValueFactory<>("location"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        scheduleColumn.setCellValueFactory(new MapValueFactory<>("schedule"));
        assessmentTypeColumn.setCellValueFactory(new MapValueFactory<>("assessmentType"));

        // 初始化下拉框
        //assessmentTypeCombo.getItems().addAll("考试", "考查", "论文", "实践");
        //assessmentTypeCombo.getSelectionModel().selectFirst();

        // 绑定数据源
        courseTable.setItems(observableList);


        loadCourseData();
    }

    // 从后端加载课程数据
    public void loadCourseData() {
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/courses/getCoursesList", req);

        if (res != null && res.getCode() == 0) {
            try {
                // 1. 直接获取原始数据（已经是 List<Map>，无需转换）
                List<Map<String, Object>> rawData = (List<Map<String, Object>>) res.getData();
                observableList.setAll(rawData);

            } catch (Exception e) {
                showAlert("数据错误", "加载失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // 添加课程
    @FXML
    public void addCourseButton() {
        if (validateInput()) {
            DataRequest req = new DataRequest();

            req.add("courseName", courseNameField.getText());
            req.add("teacher", teacherField.getText());
            req.add("location", locationField.getText());
            req.add("credit", Integer.parseInt(creditField.getText()));
            req.add("schedule", scheduleField.getText());
            req.add("assessmentType", assessmentTypeField.getText());

            System.out.println("请求数据: " + req.getData());
            DataResponse res = HttpRequestUtil.request("/api/courses/add", req);

            if (res != null && res.getCode() == 0) {
                loadCourseData(); // 重新加载数据
                clearForm();
                showAlert("成功", "课程添加成功");
            } else {
                showAlert("错误", "添加课程失败: " + (res != null ? res.getMsg() : "无响应"));
            }
        }
    }



    // 更新课程
    @FXML
    public void editCourseButton() {
        Map selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null && validateInput()) {
            DataRequest req = new DataRequest();
            req.add("courseId", selectedCourse.get("courseId")); // Original ID for identification
            req.add("courseName", courseNameField.getText());
            req.add("teacher", teacherField.getText());
            req.add("location", locationField.getText());
            req.add("credit", Integer.parseInt(creditField.getText()));
            req.add("schedule", scheduleField.getText());
            req.add("assessmentType", assessmentTypeField.getText());

            DataResponse res = HttpRequestUtil.request("/api/courses/update", req);

            if (res != null && res.getCode() == 0) {
                loadCourseData(); // Reload data
                clearForm();
                showAlert("Success", "Course updated successfully");
            } else {
                showAlert("Error", "Failed to update course: " + (res != null ? res.getMsg() : "No response"));
            }
        } else {
            showAlert("Error", "Please select a course to edit");
        }
    }

    // 删除课程
    @FXML
    public void deleteCourseButton() {
        Map selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            DataRequest req = new DataRequest();
            req.add("courseId", selected.get("courseId")); // 注意字段名大小写与后端一致

            DataResponse res = HttpRequestUtil.request("/api/courses/delete", req);

            if (res != null && res.getCode() == 0) {
                loadCourseData(); // 重新加载数据
                showAlert("成功", "课程删除成功");
            } else {
                showAlert("错误", "删除课程失败: " + (res != null ? res.getMsg() : "无响应"));
            }
        } else {
            showAlert("错误", "请先选择要删除的课程");
        }
    }

    // 表单验证
    private boolean validateInput() {
        String errorMessage = "";

//        if (courseIdField.getText() == null || courseIdField.getText().isEmpty()) {
//            errorMessage += "课程号不能为空！\n";
//        }
        if (courseNameField.getText() == null || courseNameField.getText().isEmpty()) {
            errorMessage += "课程名称不能为空！\n";
        }
        try {
            Integer.parseInt(creditField.getText());
        } catch (NumberFormatException e) {
            errorMessage += "学分必须为整数！\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert("输入错误", errorMessage);

            return false;
        }
    }
    // 清空表单
    private void clearForm() {
        courseNameField.clear();
        teacherField.clear();
        locationField.clear();
        creditField.clear();
        scheduleField.clear();
        assessmentTypeField.clear();
    }

    // 显示警告对话框
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void chooseCourseButton() {

    }
}

