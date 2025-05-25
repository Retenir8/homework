package com.teach.javafx.controller;

import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.MapValueFactory;

import java.util.List;
import java.util.Map;

public class MyCourseController {

    @FXML
    private TableView<Map<String, Object>> courseTable;
    @FXML
    private TableColumn<Map, String> courseIdColumn;
    @FXML
    private TableColumn<Map, String> courseNameColumn;
    @FXML
    private TableColumn<Map, String> locationColumn;
    @FXML
    private TableColumn<Map, String> creditColumn;
    @FXML
    private TableColumn<Map, String> teacherNameColumn;
    @FXML
    private TableColumn<Map, String> scheduleColumn;
    @FXML
    private TableColumn<Map, String> assessmentTypeColumn;

    // 数据源
    private ObservableList<Map<String, Object>> courseList = FXCollections.observableArrayList();
    Integer studentId ;
    @FXML
    public void initialize() {
        // 绑定表格列到数据 Map 中的键值
        courseIdColumn.setCellValueFactory(new MapValueFactory<>("courseId"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        locationColumn.setCellValueFactory(new MapValueFactory<>("location"));
        teacherNameColumn.setCellValueFactory(new MapValueFactory<>("teacherName"));
        scheduleColumn.setCellValueFactory(new MapValueFactory<>("schedule"));
        assessmentTypeColumn.setCellValueFactory(new MapValueFactory<>("assessmentType"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));

        // 将数据源绑定到 TableView
        courseTable.setItems(courseList);
        DataResponse res = HttpRequestUtil.request("/api/student/getMyStudentId", new DataRequest());
        if (res != null && res.getCode() == 0) {
            // 将返回的 data 转换为 Map
            Map<String, Object> dataMap = (Map<String, Object>) res.getData();
            // 通过 key "studentId" 获取值，并转换为 Integer
            Object idObj = dataMap.get("studentId");
            if (idObj instanceof Number) {
                studentId = ((Number) idObj).intValue();
            }
            System.out.println("studentId: " + studentId);
        } else {
            System.out.println("服务器响应异常");
        }
        // 加载“我的课程”数据
        loadMyCourses();
    }

    /**
     * 从后端接口加载当前用户选的课程数据
     */
    public void loadMyCourses() {
        DataRequest req = new DataRequest();
        req.add("studentId", studentId);
        DataResponse res = HttpRequestUtil.request("/api/courses/getMyCourses", req);
        if (res != null && res.getCode() == 0) {
            try {
                List<Map<String, Object>> data = (List<Map<String, Object>>) res.getData();
                courseList.setAll(data);
            } catch (Exception e) {
                showAlert("数据错误", "加载我的课程数据失败: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert("错误", "加载我的课程失败: " + (res != null ? res.getMsg() : "服务器无响应"));
        }
    }

    /**
     * 显示提示信息
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
