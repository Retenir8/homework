package com.teach.javafx.controller;

import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActivityControllerForStudent {
    @FXML
    private TableView<Map<String, Object>> availableActivityTableView;
    @FXML private TableColumn<Map<String, Object>, String> availableTitleColumn;
    @FXML private TableColumn<Map<String, Object>, String> availableStartTimeColumn;
    @FXML private TableColumn<Map<String, Object>, String> availableEndTimeColumn;
    @FXML private TableColumn<Map<String, Object>, String> availableLocationColumn;
    @FXML
    private TableView<Map<String, Object>> appliedActivityTableView;
    @FXML private TableColumn<Map<String, Object>, String> appliedTitleColumn;
    @FXML private TableColumn<Map<String, Object>, String> appliedStartTimeColumn;
    @FXML private TableColumn<Map<String, Object>, String> appliedEndTimeColumn;
    @FXML private TableColumn<Map<String, Object>, String> appliedLocationColumn;//两个表的映射
    @FXML private Label studentInfoLabel;//学生信息标签
    @FXML private TextField appliedActivityTitleFieldForSearch;//搜索未加入活动名称
    @FXML private TextField applyActivityTitleFieldForSearch;//搜索未加入的活动名称

    DataResponse res0 = HttpRequestUtil.request("/api/activity/getMyName", new DataRequest());
    Map<String,Object> m0 = (Map<String, Object>) res0.getData();
    String name = (String) m0.get("name");
    String num = (String) m0.get("num");

    public void initialize() {
        availableTitleColumn.setCellValueFactory(cell -> {
            Object v = cell.getValue().get("title");
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
        availableStartTimeColumn.setCellValueFactory(cell -> {
            Object v = cell.getValue().get("startTime");
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
        availableEndTimeColumn.setCellValueFactory(cell -> {
            Object v = cell.getValue().get("endTime");
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
        availableLocationColumn.setCellValueFactory(cell -> {
            Object v = cell.getValue().get("location");
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
        appliedTitleColumn.setCellValueFactory(cell -> {
            Object v = cell.getValue().get("title");
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
        appliedStartTimeColumn.setCellValueFactory(cell -> {
            Object v = cell.getValue().get("startTime");
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
        appliedEndTimeColumn.setCellValueFactory(cell -> {
            Object v = cell.getValue().get("endTime");
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
        appliedLocationColumn.setCellValueFactory(cell -> {
            Object v = cell.getValue().get("location");
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });

        String str = name+" "+num;
        studentInfoLabel.setText(str);
        loadInitialData();
    }
    public void loadInitialData(){
        Map<String,Object> form = new HashMap<>();
        form.put("name",name);
        form.put("num",num);
        DataRequest req = new DataRequest();
        req.setData(form);
        DataResponse res1 = HttpRequestUtil.request("/api/activity/getApplyActivityList", req);
        DataResponse res2 = HttpRequestUtil.request("/api/activity/getAppliedActivityList",req);
        if (res1 != null && res1.getData() instanceof List) {
            List<Map<String, Object>> listOfAvailable = (List<Map<String, Object>>) res1.getData();
            ObservableList<Map<String, Object>> availableObservableList = FXCollections.observableArrayList(listOfAvailable);
            availableActivityTableView.setItems(availableObservableList);
        } else {
            System.err.println("获取可参与活动列表失败或返回数据格式不正确: " + res1);
        }
        if (res2 != null && res2.getData() instanceof List) {
            List<Map<String, Object>> listOfApplied = (List<Map<String, Object>>) res2.getData();
            ObservableList<Map<String, Object>> appliedObservableList = FXCollections.observableArrayList(listOfApplied);
            appliedActivityTableView.setItems(appliedObservableList);
        } else {
            System.err.println("获取已报名活动列表失败或返回数据格式不正确: " + res2);
        }
    }
    public void applyForActivity(){
        Map form = availableActivityTableView.getSelectionModel().getSelectedItem();
        DataRequest req = new DataRequest();
        form.put("name",name);
        form.put("num",num);
        req.setData(form);
        DataResponse res = HttpRequestUtil.request("/api/activity/activityApply", req);
        if (res.getCode() == 0) {
            showAlert(Alert.AlertType.INFORMATION, "成功", "申请成功", "已成功申请");
        }
        else{
            showAlert(Alert.AlertType.ERROR, "错误", "失败", "HTTP 错误: " + res.getCode());
        }
        loadInitialData();
    }
    public void cancelActivityApplication(){
        Map form = appliedActivityTableView.getSelectionModel().getSelectedItem();
        DataRequest req = new DataRequest();
        form.put("name",name);
        form.put("num",num);
        req.setData(form);
        DataResponse res = HttpRequestUtil.request("/api/activity/activityApplyDelete", req);
        if (res.getCode() == 0) {
            showAlert(Alert.AlertType.INFORMATION, "成功", "取消成功", "已成功取消");
        }
        else{
            showAlert(Alert.AlertType.ERROR, "错误", "失败", "HTTP 错误: " + res.getCode());
        }
        loadInitialData();
    }
    public void loadAppliedActivitiesForSearch(){
        String title = (String) appliedActivityTitleFieldForSearch.getText();
        Map form = new HashMap<>();
        form.put("title",title);
        form.put("num",num);
        form.put("name",name);
        DataRequest req = new DataRequest();
        req.setData(form);
        DataResponse res = HttpRequestUtil.request("/api/activity/getAppliedActivityListForSearch", req);
        if (res.getCode() == 0) {
            List<Map<String, Object>> listOfAvailable = (List<Map<String, Object>>) res.getData();
            ObservableList<Map<String, Object>> availableObservableList = FXCollections.observableArrayList(listOfAvailable);
            appliedActivityTableView.setItems(availableObservableList);
        }
        else{
            showAlert(Alert.AlertType.ERROR, "错误", "失败", "HTTP 错误: " + res.getCode());
        }
    }
    public void loadAllAvailableActivitiesForSearch(){
        String title = (String) applyActivityTitleFieldForSearch.getText();
        Map form = new HashMap<>();
        form.put("title",title);
        form.put("name",name);
        form.put("num",num);
        DataRequest req = new DataRequest();
        req.setData(form);
        DataResponse res = HttpRequestUtil.request("/api/activity/getApplyActivityListForSearch", req);
        if (res.getCode() == 0) {
            List<Map<String, Object>> listOfAvailable = (List<Map<String, Object>>) res.getData();
            ObservableList<Map<String, Object>> availableObservableList = FXCollections.observableArrayList(listOfAvailable);
            availableActivityTableView.setItems(availableObservableList);
        }
        else{
            showAlert(Alert.AlertType.ERROR, "错误", "失败", "HTTP 错误: " + res.getCode());
        }
    }
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}


