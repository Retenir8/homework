package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.TableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    // 新增“选时间”列，类型使用 Void 以便自定义单元格
    @FXML
    private TableColumn<Map, Void> timeSelectionColumn;

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
    @FXML
    private TextField searchField;
    @FXML
    private TextField teacherNumField;

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

        // 绑定数据源
        courseTable.setItems(observableList);

        // 设置“选时间”列的 cellFactory，生成带有“选时间”按钮的单元格
        timeSelectionColumn.setCellFactory(col -> new TableCell<Map, Void>() {
            private final Button btn = new Button("选时间");

            {
                btn.setOnAction((ActionEvent event) -> {
                    Map rowData = getTableView().getItems().get(getIndex());
                    openTimeSelectionDialog(rowData);
                });
                btn.setStyle("-fx-background-color: #FFA726; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        loadCourseData();
    }

    // 从后端加载课程数据
    public void loadCourseData() {
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/courses/getCoursesList", req);

        if (res != null && res.getCode() == 0) {
            try {
                // 直接获取原始数据（已经是 List<Map<String,Object>>）
                List<Map<String, Object>> rawData = (List<Map<String, Object>>) res.getData();
                observableList.setAll(rawData);
            } catch (Exception e) {
                showAlert("数据错误", "加载失败: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlert("错误", "课程加载失败: " + (res != null ? res.getMsg() : "无响应"));
        }
    }

    @FXML
    private void onVerifyTeacherClick() {
        String teacherNum = teacherNumField.getText().trim();
        if (teacherNum.isEmpty()) {
            MessageDialog.showDialog("请输入教工号后进行查询！");
            return;
        }

        DataRequest req = new DataRequest();
        req.add("teacherNum", teacherNum);
        DataResponse res = HttpRequestUtil.request("/api/teacher/getTeacherByNum", req);

        if (res != null && res.getCode() == 0 && res.getData() != null) {
            Map<String, Object> teacherData = (Map<String, Object>) res.getData();
            String teacherName = (String) teacherData.get("name");
            teacherField.setText(teacherName); // 显示姓名
            MessageDialog.showDialog("✅ 教师验证成功：" + teacherName);
        } else {
            MessageDialog.showDialog("❌ 未找到该教工号对应的教师！");
        }
    }

    // 添加课程
    @FXML
    public void addCourseButton() {
        if (validateInput()) {
            DataRequest req = new DataRequest();

            req.add("courseName", courseNameField.getText());
            req.add("teacherNum", teacherNumField.getText());
            req.add("teacherName", teacherField.getText());
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
        // 从 TableView 中获取选中的课程记录
        Map selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showAlert("Error", "Please select a course to edit");
            return;
        }

        // 如果输入框为空，说明还没有将选中课程填入表单，自动填充
        if (isFormEmpty()) {
            courseNameField.setText(selectedCourse.get("courseName") != null ? selectedCourse.get("courseName").toString() : "");
            teacherField.setText(selectedCourse.get("teacherName") != null ? selectedCourse.get("teacherName").toString() : "");
            locationField.setText(selectedCourse.get("location") != null ? selectedCourse.get("location").toString() : "");
            creditField.setText(selectedCourse.get("credit") != null ? selectedCourse.get("credit").toString() : "");
            scheduleField.setText(selectedCourse.get("schedule") != null ? selectedCourse.get("schedule").toString() : "");
            assessmentTypeField.setText(selectedCourse.get("assessmentType") != null ? selectedCourse.get("assessmentType").toString() : "");
            return;
        }

        if (validateInput()) {
            DataRequest req = new DataRequest();
            req.add("courseId", selectedCourse.get("courseId"));
            req.add("courseName", courseNameField.getText());
            req.add("teacherNum", teacherNumField.getText());
            req.add("teacherName", teacherField.getText());
            req.add("location", locationField.getText());
            req.add("credit", Integer.parseInt(creditField.getText()));
            req.add("schedule", scheduleField.getText());
            req.add("assessmentType", assessmentTypeField.getText());

            DataResponse res = HttpRequestUtil.request("/api/courses/update", req);

            if (res != null && res.getCode() == 0) {
                loadCourseData();
                clearForm();
                showAlert("成功", "课程更新成功");
            } else {
                showAlert("错误", "更新课程失败: " + (res != null ? res.getMsg() : "无响应"));
            }
        }
    }

    /**
     * 判断表单是否为空，即各输入框是否都没有内容
     */
    private boolean isFormEmpty() {
        return (courseNameField.getText() == null || courseNameField.getText().trim().isEmpty())
                && (teacherField.getText() == null || teacherField.getText().trim().isEmpty())
                && (locationField.getText() == null || locationField.getText().trim().isEmpty())
                && (creditField.getText() == null || creditField.getText().trim().isEmpty())
                && (scheduleField.getText() == null || scheduleField.getText().trim().isEmpty())
                && (assessmentTypeField.getText() == null || assessmentTypeField.getText().trim().isEmpty());
    }


    // 删除课程
    @FXML
    public void deleteCourseButton() {
        Map selected = courseTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            DataRequest req = new DataRequest();
            req.add("courseId", selected.get("courseId"));

            DataResponse res = HttpRequestUtil.request("/api/courses/delete", req);

            if (res != null && res.getCode() == 0) {
                loadCourseData();
                showAlert("成功", "课程删除成功");
            } else {
                showAlert("错误", "删除失败: " + (res != null ? res.getMsg() : "无响应"));
            }
        } else {
            showAlert("错误", "请先选择要删除的课程");
        }
    }

    // 表单验证
    private boolean validateInput() {
        String errorMessage = "";

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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * 新增：打开时间选择对话框。该方法利用 FXMLLoader 加载时间选择对话框 FXML，
     * 获取 CourseTimeSelectionController，并将当前课程数据中存储时间位置信息（"timeSlots"）传入，
     * 用户选择确认后，更新当前课程行的 "timeSlots" 字段，并同步到后端。
     */
    private void openTimeSelectionDialog(Map courseData) {
        try {
            // 使用绝对路径加载 FXML 文件，确保文件能被找到
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("course-time-selection-dialog.fxml"));
            Parent page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("选课时间设置");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(MainApplication.getMainStage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // 获取对话框的控制器并传入当前课程的时间位置信息
            CourseTimeSelectionController controller = loader.getController();
            // 获取当前课程已保存的“timeSlots”。若不存在，则默认全"0"
            String timeSlots = courseData.get("timeSlots") == null
                    ? "00000000000000000000000000000000000"
                    : courseData.get("timeSlots").toString();
            controller.setTimeSlots(timeSlots);
            controller.setDialogStage(dialogStage);

            // 显示对话框
            dialogStage.showAndWait();

            if (controller.isOkClicked()) {
                String selectedTimeSlots = controller.getTimeSlots();
                // 更新当前课程记录
                courseData.put("timeSlots", selectedTimeSlots);
                // 此处你可能想更新课程表显示（例如 "schedule" 字段），根据实际需求设置
                courseTable.refresh();

                // 同步更新到后端
                DataRequest reqUpdate = new DataRequest();
                reqUpdate.add("courseId", courseData.get("courseId"));
                reqUpdate.add("timeSlots", selectedTimeSlots);
                DataResponse resUpdate = HttpRequestUtil.request("/api/courses/updateTimeSlots", reqUpdate);
                if (resUpdate == null || resUpdate.getCode() != 0) {
                    showAlert("错误", "保存时间选择失败: " + (resUpdate != null ? resUpdate.getMsg() : "无响应"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("错误", "加载时间选择对话框失败: " + e.getMessage());
        }
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
                    System.out.println(rawData);
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


}

