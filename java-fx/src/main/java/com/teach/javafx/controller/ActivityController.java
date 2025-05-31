package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActivityController {

    @FXML private TextField titleTextField; //活动名称输入域
    @FXML private TextField locationTextField; //活动位置输入域
    @FXML private DatePicker startTimePicker;//开始时间信息
    @FXML private DatePicker endTimePicker;//结束时间输入域，以上输入相关保存信息的地方
    @FXML private ChoiceBox<String> searchTypeChoiceBox;//选择搜索方式
    @FXML private TextField searchTextField;//搜索信息域

    @FXML
    private TableView<Map<String, Object>> resultTableView;
    @FXML private TableColumn<Map<String, Object>, String> activityTitleColumn;
    @FXML private TableColumn<Map<String, Object>, String> startTimeColumn;
    @FXML private TableColumn<Map<String, Object>, String> endTimeColumn;
    @FXML private TableColumn<Map<String, Object>, String> locationColumn;//表格相关设置

    private Integer activityId = null;
    private ObservableList<Map<String,Object>> observableList = FXCollections.observableArrayList();
    private ArrayList<Map> activityList = new ArrayList();

    @FXML
    public void initialize() {
        searchTypeChoiceBox.getItems().addAll("按活动搜索", "按学生搜索");
        searchTypeChoiceBox.setValue("按学生搜索");
        activityTitleColumn.setCellValueFactory(cell -> {
            Object v = cell.getValue().get("title");
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
        startTimeColumn.setCellValueFactory(cell -> {
            Object v = cell.getValue().get("startTime");
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
        endTimeColumn.setCellValueFactory(cell -> {
            Object v = cell.getValue().get("endTime");
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
        locationColumn.setCellValueFactory(cell -> {
            Object v = cell.getValue().get("location");
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
        loadInitialData();
    }

    private void loadInitialData() {
        DataResponse res = HttpRequestUtil.request("/api/activity/getActivityListAll", new DataRequest());
        if (res != null && res.getCode() == 0) {
            activityList = (ArrayList<Map>) res.getData();
            observableList.setAll(activityList.stream()
                    .map(map -> (Map<String, Object>) map)
                    .collect(Collectors.toList()));
        } else {
            showAlert(Alert.AlertType.ERROR, "错误", "加载数据失败", "HTTP 错误: " + (res != null ? res.getCode() : "未知错误"));
        }
        resultTableView.setItems(observableList);
    }

    @FXML
    private void handleSave() {
        String title = titleTextField.getText();
        String location = locationTextField.getText();
        LocalDate startTime = startTimePicker.getValue();
        LocalDate endTime = endTimePicker.getValue();

        if (title.isEmpty() || location.isEmpty() || startTime == null || endTime == null) {
            showAlert(Alert.AlertType.ERROR, "错误", "输入错误", "请填写所有字段");
            return;
        }
        if (endTime.isBefore(startTime)) {
            showAlert(Alert.AlertType.ERROR, "错误", "输入错误", "结束时间不能早于开始时间");
            return;
        }
        try {
            Map<String,Object> form = new HashMap<>();
            form.put("title", title);
            form.put("location", location);
            form.put("startTime", startTime.format(DateTimeFormatter.ISO_LOCAL_DATE));
            form.put("endTime", endTime.format(DateTimeFormatter.ISO_LOCAL_DATE));
            DataRequest req = new DataRequest();
            req.add("activityId", activityId);
            req.setData(form);
            DataResponse res = HttpRequestUtil.request("/api/activity/creatActivity", req);
            if (res != null && res.getCode() == 0) {
                showAlert(Alert.AlertType.INFORMATION, "成功", "创建成功", "活动已成功创建");
                loadInitialData();
                titleTextField.clear();
                locationTextField.clear();
                startTimePicker.setValue(null);
                endTimePicker.setValue(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "错误", "保存失败", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        try {
            Map form = resultTableView.getSelectionModel().getSelectedItem();
            DataRequest req = new DataRequest();
            req.add("form", form);
            DataResponse response = HttpRequestUtil.request("/api/activity/deleteActivity", req);
            if (response.getCode() == 0) {
                    showAlert(Alert.AlertType.INFORMATION, "成功", "删除成功", "记录已成功删除");
                    loadInitialData();
                    resultTableView.refresh();
                    loadInitialData();
            } else {
                showAlert(Alert.AlertType.ERROR, "错误", "删除失败", "错误: " + response.getCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "错误", "删除失败", e.getMessage());
        }
    }

    @FXML
    private void handleInspect() throws IOException {
        Map form = resultTableView.getSelectionModel().getSelectedItem();
        DataRequest req = new DataRequest();
        req.setData(form);
        DataResponse res = HttpRequestUtil.request("/api/activity/searchByActivity", req);
        if (res != null && res.getCode() == 0) {
            List<Map<String,Object>> list = (List<Map<String, Object>>) res.getData();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/teach/javafx/activity-search-student.fxml"));
            Parent root = loader.load();
            ActivitySearchStudentController controller = loader.getController();
            controller.setData(list);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("查看参与学生");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(MainApplication.getMainStage()); // 设置父窗口
            dialogStage.setScene(new Scene(root));
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
        }
        else{
            showAlert(Alert.AlertType.ERROR, "空", "没有学生", res.getMsg());;
        }
    }

    @FXML
    private void handleSearch() {
            String url = "";
            String choice = searchTypeChoiceBox.getValue();
            if (choice.equals("按活动搜索")) {
                url = "/api/activity/searchByActivity";
            } else {
                url = "/api/activity/searchByStudent";
            }
            Map<String, Object> form = new HashMap<>();
            form.put("data", searchTextField.getText());
            DataRequest req = new DataRequest();
            req.add("activityId", activityId);
            req.setData(form);
            DataResponse res = HttpRequestUtil.request(url, req);
        if (res != null && res.getCode() == 0) {
            activityList = (ArrayList<Map>) res.getData();
            observableList.setAll(activityList.stream()
                    .map(map -> (Map<String, Object>) map)
                    .collect(Collectors.toList()));
        } else {
            showAlert(Alert.AlertType.ERROR, "错误", "加载数据失败", "HTTP 错误: " + (res != null ? res.getCode() : "未知错误"));
        }
        resultTableView.setItems(observableList);
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}