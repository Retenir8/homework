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
    private TextField courseNameTextField;
    @FXML
    private ComboBox<String> courseComboBox;

    private ObservableList<Map<String, Object>> scoreList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentNameColumn"));
        classNameColumn.setCellValueFactory(new MapValueFactory<>("className"));
        courseNumColumn.setCellValueFactory(new MapValueFactory<>("courseNum"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        markColumn.setCellValueFactory(new MapValueFactory<>("mark"));
        loadScoreData(); // ✅ 初始化加载学生成绩
        setupComboBox(); // ✅ 初始化课程选择框
    }

    private void loadScoreData() {
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/score/getMyScores", req);

        if (res != null && res.getCode() == 0) {
            List<Map<String, Object>> scores = (List<Map<String, Object>>) res.getData();
            scoreList.setAll(scores);
            dataTableView.setItems(scoreList);
            System.out.println("✅ 成绩数据加载成功！");
        } else {
            System.out.println("❌ 获取成绩失败: " + (res != null ? res.getMsg() : "服务器无响应"));
        }
    }

    private void setupComboBox() {
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/courses/getAllCourses", req);

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
        req.add("searchText", searchText);
        req.add("selectedCourse", selectedCourse);

        DataResponse res = HttpRequestUtil.request("/api/score/searchMyScores", req);

        if (res != null && res.getCode() == 0) {
            List<Map<String, Object>> scores = (List<Map<String, Object>>) res.getData();
            scoreList.setAll(scores);
            System.out.println("✅ 模糊搜索结果加载成功！");
        } else {
            System.out.println("❌ 查询失败: " + (res != null ? res.getMsg() : "服务器无响应"));
        }
    }
}
