package com.teach.javafx.controller;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class HonorEditController {
    private Stage dialogStage;
    private boolean saved = false;
    private Map<String, Object> honorData;


    // FXML 注入的控件
    @FXML private TextField honorNameField;
    @FXML private TextField hostField;
    @FXML private TextField studentNameField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<String> levelComboBox;
    @FXML private DatePicker timePicker;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;


    public static Map<String, Object> showDialog(Map<String, Object> honorData) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    HonorEditController.class.getResource("/com/teach/javafx/honor-edit-dialog.fxml"));
            Parent root = loader.load();
            HonorEditController controller = loader.getController();
            controller.setHonorData(honorData);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("编辑荣誉记录");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();
            return controller.isSaved() ? controller.getUpdatedHonorData() : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void initialize() {
        typeComboBox.setItems(FXCollections.observableArrayList("金奖","银奖","铜奖"));
        levelComboBox.setItems(FXCollections.observableArrayList("校级","市级","省级","国家级"));
        saveButton.setOnAction(event -> handleSave());
        cancelButton.setOnAction(event -> handleCancel());
    }
    public void setHonorData(Map<String, Object> data) {
        this.honorData = new HashMap<>(data);
        honorNameField.setText(data.get("HonorName").toString());
        hostField.setText(data.get("Host").toString());
        studentNameField.setText(data.get("name").toString());
        typeComboBox.setValue(data.get("HonorType").toString());
        levelComboBox.setValue(data.get("HonorLevel").toString());
        if (data.containsKey("type")) {
            typeComboBox.getSelectionModel().select(data.get("type").toString());
        }
        if (data.containsKey("level")) {
            levelComboBox.getSelectionModel().select(data.get("level").toString());
        }
        if (data.containsKey("time")) {
            try {
                timePicker.setValue(java.time.LocalDate.parse(data.get("time").toString()));
            } catch (Exception e) {
            }
        }
    }
    public Map<String, Object> getUpdatedHonorData() {
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("honorName", honorNameField.getText());
        updatedData.put("Host", hostField.getText());
        updatedData.put("studentName", studentNameField.getText());
        updatedData.put("Type", typeComboBox.getValue());
        updatedData.put("Level", levelComboBox.getValue());
        updatedData.put("Time", timePicker.getValue() != null ? timePicker.getValue().toString() : "");
        return updatedData;
    }
    private void handleSave() {
        if (honorNameField.getText().isEmpty()) {
            MessageDialog.showDialog("荣誉名称不能为空！");
            return;
        }
        if (studentNameField.getText().isEmpty()) {
            MessageDialog.showDialog("学生姓名不能为空！");
            return;
        }
        if (timePicker.getValue() == null) {
            MessageDialog.showDialog("请选择获奖时间！");
            return;
        }

        Map m =getUpdatedHonorData();
        DataRequest req = new DataRequest();
        req.add("title",honorData.get("HonorName"));
        req.add("name",honorData.get("name"));
        DataResponse res1 = HttpRequestUtil.request("/api/honor/deleteHonor", req);
        DataRequest req2 = new DataRequest();
        req2.add("honorName",m.get("honorName"));
        req2.add("studentName",m.get("studentName"));
        req2.add("honorType",m.get("Type"));
        req2.add("honorLevel",m.get("Level"));
        req2.add("date",m.get("Time"));
        req2.add("host",m.get("Host"));
        DataResponse res2 = HttpRequestUtil.request("/api/honor/createHonor", req2);
        if(res1.getCode()==0&&res2.getCode()==0){
            MessageDialog.showDialog("修改" + "成功！");
        }
        saved = true;
        dialogStage.close();
    }
    private void handleCancel() {
        saved = false;
        dialogStage.close();
    }
    public boolean isSaved() {
        return saved;
    }
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}