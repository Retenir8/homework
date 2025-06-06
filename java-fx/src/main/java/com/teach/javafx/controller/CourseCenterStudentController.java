package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CourseCenterStudentController {
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

    private Stage myCourseStage = null;
    private MyCourseController myCourseController;
    @FXML
    private TextField searchField;
    // 数据源
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 初始化表格列绑定
        courseIdColumn.setCellValueFactory(new MapValueFactory<>("courseId"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        teacherColumn.setCellValueFactory(new MapValueFactory<>("teacherName"));
        locationColumn.setCellValueFactory(new MapValueFactory<>("location"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        scheduleColumn.setCellValueFactory(new MapValueFactory<>("schedule"));
        assessmentTypeColumn.setCellValueFactory(new MapValueFactory<>("assessmentType"));

        // 初始化下拉框
        //assessmentTypeCombo.getItems().addAll("考试", "考查", "论文", "实践");
        //assessmentTypeCombo.getSelectionModel().selectFirst();

        // 绑定数据源
        courseTable.setItems(observableList);

        onSearchByNameClick();
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


    // 显示警告对话框
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    public void onSearchByNameClick() {
        // 获取搜索框的关键字
        String keyword = searchField.getText().trim();

        DataRequest req = new DataRequest();

        // 如果关键字不为空，则添加搜索参数
        if (!keyword.isEmpty()) {
            req.add("keyword", keyword);  // 后端需要解析该参数做模糊匹配，可根据实际情况调整参数名
            // 调用专门用于搜索的接口，假设接口为 /api/courses/searchCourses
            DataResponse res = HttpRequestUtil.request("/api/courses/searchCourses", req);
            if (res != null && res.getCode() == 0) {
                try {
                    // 假定后端返回的是 List<Map<String, Object>> 类型数据
                    List<Map<String, Object>> rawData = (List<Map<String, Object>>) res.getData();
                    observableList.setAll(rawData);
                } catch (Exception e) {
                    showAlert("数据错误", "加载失败: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                showAlert("错误", "课程搜索失败: " + (res != null ? res.getMsg() : "无响应"));
            }
        } else {
            // 如果关键字为空，则加载所有课程数据
            loadCourseData();
        }
    }

    @FXML
    public void onSelectCourseButton() {
        // 从 TableView 中获取当前选中的课程行
        Map selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showAlert("错误", "请选择一门课程！");
            return;
        }

        // 构造 DataRequest 对象，将课程的各项信息放入请求中
        DataRequest req = new DataRequest();
        req.add("courseId", selectedCourse.get("courseId"));
        req.add("courseName", selectedCourse.get("courseName"));
        req.add("teacher", selectedCourse.get("teacher"));
        req.add("location", selectedCourse.get("location"));
        req.add("credit", selectedCourse.get("credit"));
        req.add("schedule", selectedCourse.get("schedule"));
        req.add("assessmentType", selectedCourse.get("assessmentType"));

        DataResponse res = HttpRequestUtil.request("/api/courses/choose" , req);

        // 根据返回结果显示提示信息
        if (res != null && res.getCode() == 0) {
            showAlert("提示", "选课成功！");
            // 你还可以在这里刷新数据或更新其他前端状态
        } else {
            showAlert("错误", "选课失败: " + (res != null ? res.getMsg() : "没有响应"));
        }
        myCourseController.initialize();
    }

    public void onMyCourseButton() {
        initMyCourseDialog();
        myCourseStage.show();
    }

    private void initMyCourseDialog() {
        if (myCourseStage != null) return;
        FXMLLoader fxmlLoader;
        try {
            // 加载 FXML 布局文件
            fxmlLoader = new FXMLLoader(MainApplication.class.getResource("myCourse-panel.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // 初始化新的 Stage
            myCourseStage = new Stage();
            myCourseStage.setScene(scene);
            myCourseStage.setTitle("我的选课");
            myCourseStage.initModality(Modality.APPLICATION_MODAL);
            myCourseStage.initOwner(MainApplication.getMainStage());

            // 设置更大的窗口大小及属性（例如宽度1200，高度800）
            myCourseStage.setWidth(850);
            myCourseStage.setHeight(700);
            // 如果希望让用户也能手动调整窗口大小，就设置为 true，否则设置 false
            myCourseStage.setResizable(true);

            // 获取控制器实例，并做必要的初始化
            myCourseController = fxmlLoader.getController();
            // 如果需要给控制器传参，比如设置当前学生ID，可以在此调用 setter 方法
            // myCourseController.setStudentId(currentStudentId);
            // 若控制器中有 init() 方法，可调用以便执行数据加载操作
            myCourseController.initialize();
        } catch (IOException e) {
            throw new RuntimeException("加载 getMyCourse.fxml 失败", e);
        }
    }
}
