package com.teach.javafx.controller;

import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class HonorForStudentController {
    @FXML
    private Label userInfoLabel;
    @FXML
    private TableView honorTable;
    @FXML private TableColumn<Map<String, Object>, String> honorNameColumn;
    @FXML private TableColumn<Map<String, Object>, String> awardingBodyColumn;
    @FXML private TableColumn<Map<String, Object>, String> timeColumn;
    @FXML private TableColumn<Map<String, Object>, String> levelColumn;
    @FXML private TableColumn<Map<String, Object>, String>typeColumn;

    private ObservableList<Map<String,Object>> observableList = FXCollections.observableArrayList();
    DataResponse res = HttpRequestUtil.request("/api/activity/getMyName", new DataRequest());
    Map m = (Map) res.getData();
    private ArrayList<Map> honorList = new ArrayList();
    public void initialize() {
        userInfoLabel.setText(m.get("name").toString()+m.get("num").toString());
        honorNameColumn.setCellValueFactory(cell -> {
            Object v = cell.getValue().get("HonorName");
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
        awardingBodyColumn.setCellValueFactory(cell -> {
            Object v = cell.getValue().get("Host");
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
        typeColumn.setCellValueFactory(cell -> {
            Object v = cell.getValue().get("HonorType");
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
        timeColumn.setCellValueFactory(cell -> {
            Object v = cell.getValue().get("Date");
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
        levelColumn.setCellValueFactory(cell -> {
            Object v = cell.getValue().get("HonorLevel");
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
        setTableview();
    }
    public void refreshTable() {
        setTableview();
    }

    public void setTableview(){
        DataRequest req = new DataRequest();
        req.add("data",m.get("name"));
        DataResponse res = HttpRequestUtil.request("/api/honor/getHonorListByStudentName",req);
        if (res != null && res.getCode() == 0) {
            honorList = (ArrayList<Map>) res.getData();
            observableList.setAll(honorList.stream()
                    .map(map -> (Map<String, Object>) map)
                    .collect(Collectors.toList()));
        } else {
            showAlert(Alert.AlertType.ERROR, "错误", "加载数据失败", "HTTP 错误: " + (res != null ? res.getCode() : "未知错误"));
        }
        honorTable.setItems(observableList);
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
