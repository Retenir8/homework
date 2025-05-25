package com.teach.javafx.controller;

import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.MapValueFactory;

import java.util.List;
import java.util.Map;

public class ScoreStudentController {

    @FXML
    private TableView<Map<String, Object>> dataTableView;
    @FXML
    private TableColumn<Map, String> studentNumColumn;
    @FXML
    private TableColumn<Map, String> studentNameColumn;
    @FXML
    private TableColumn<Map, String> classNameColumn;
    @FXML
    private TableColumn<Map, String> courseNumColumn;
    @FXML
    private TableColumn<Map, String> courseNameColumn;
    @FXML
    private TableColumn<Map, String> creditColumn;
    @FXML
    private TableColumn<Map, String> markColumn;
    @FXML
    private TableColumn<Map, String> rankingColumn;
    @FXML
    private TextField courseNameTextField;
    @FXML
    private ComboBox<String> courseComboBox;

    private ObservableList<Map<String, Object>> scoreList = FXCollections.observableArrayList();

    Integer studentId ;
    @FXML
    public void initialize() {
        studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        classNameColumn.setCellValueFactory(new MapValueFactory<>("className"));
        courseNumColumn.setCellValueFactory(new MapValueFactory<>("courseId"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        markColumn.setCellValueFactory(new MapValueFactory<>("mark"));
        rankingColumn.setCellValueFactory(new MapValueFactory<>("ranking"));

        // 将 scoreList 设置为 dataTableView 的数据源
        dataTableView.setItems(scoreList);

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

        onQueryButtonClick(); // ✅ 初始化加载学生成绩
        setupComboBox(); // ✅ 初始化课程选择框
    }


    private void setupComboBox() {
        DataRequest req = new DataRequest();
        req.add("studentId", studentId);
        DataResponse res = HttpRequestUtil.request("/api/courses/getMyCourses", req);

        if (res != null && res.getCode() == 0) {
            List<String> courseNames = (List<String>) res.getData();
            courseComboBox.setItems(FXCollections.observableArrayList(courseNames));
            System.out.println("✅ 课程列表加载成功！");
        } else {
            System.out.println("❌ 获取课程列表失败！");
        }
    }

    @FXML
    private void onQueryButtonClick() {
        String searchText = courseNameTextField.getText().trim();
        String selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();

        DataRequest req = new DataRequest();
        req.add("studentId", studentId);
        req.add("searchText", searchText);
        req.add("courseName", selectedCourse);

        DataResponse res = HttpRequestUtil.request("/api/score/searchMyScores", req);
        System.out.println(res.getData());
        if (res != null && res.getCode() == 0) {
            List<Map<String, Object>> scores = (List<Map<String, Object>>) res.getData();
            scoreList.setAll(scores);
            System.out.println("✅ 模糊搜索结果加载成功！");
        } else {
            System.out.println("❌ 查询失败: " + (res != null ? res.getMsg() : "服务器无响应"));
        }
    }
}
