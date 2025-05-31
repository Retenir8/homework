package com.teach.javafx.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;

import javafx.scene.control.TableColumn;

import javafx.scene.control.TableView;

import javafx.scene.control.cell.PropertyValueFactory;

import javafx.stage.Stage;

import java.util.List;

import java.util.Map;

public class ActivitySearchStudentController {
    @FXML
    private TableView<Map<String, Object>> resultTableView;
    @FXML
    private TableColumn<Map<String, Object>, String> StudentClass;
    @FXML
    private TableColumn<Map<String, Object>, String> StudentName;
    private ObservableList<Map<String,Object>> observableList = FXCollections.observableArrayList();
    private Stage dialogStage;
    public void initialize() {
        StudentClass.setCellValueFactory(cell -> {
            Object v = cell.getValue().get("class");
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
        StudentName.setCellValueFactory(cell -> {
            Object v = cell.getValue().get("name");
            return new SimpleStringProperty(v == null ? "" : v.toString());
        });
        resultTableView.setItems(observableList);
    }
    public void setData(List<Map<String, Object>> sList) {
        observableList.clear(); // 如果需要，清除现有数据
        observableList.addAll(sList);
        resultTableView.refresh();
        resultTableView.setItems(observableList);
    }
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    public Stage getDialogStage() {
        return dialogStage;
    }
}