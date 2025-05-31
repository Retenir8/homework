package com.teach.javafx;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;

import java.util.List;
import java.util.Map;

public class PracticeController {
    @FXML
    private TableView<Map> PracticeTable;
    @FXML
    private TableColumn<Map, String> idColumn;
    @FXML
    private TableColumn<Map, String> nameColumn;//此处为公司姓名，what can i say
    @FXML
    private TableColumn<Map, String> titleColumn;
    @FXML
    private TableColumn<Map, String> timeColumn;
    @FXML
    private TableColumn<Map, Integer> studentNameColumn;
    @FXML
    private TableColumn<Map, String> genderColumn;//一些表的声明
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField titleField;
    @FXML
    private TextField timeField;
    @FXML
    private TextField studentNameField;
    @FXML
    private  ComboBox<String>  genderComboBox;
    @FXML
    private TextField practiceIdTextField;

    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private List<OptionItem> genderList;


    @FXML
    public void onQueryButtonClick() {
        String searchName = practiceIdTextField.getText().trim();
        if (searchName.isEmpty()) {
            MessageDialog.showDialog("请输入实习编号后查询！");
            return;
        }
        DataRequest req = new DataRequest();
        req.add("id", searchName);
        DataResponse res = HttpRequestUtil.request("/api/practice/getPracticeById", req);
        if (res != null) {
            if (res.getCode() == 0) {
                Map<String, Object> result = (Map<String, Object>)res.getData();
                if (result.isEmpty()) {
                    MessageDialog.showDialog("未找到编号为 '" + searchName + "' 的实习");
                } else {
                    observableList.setAll(result);
                }
            } else {
                MessageDialog.showDialog("查询失败: " + res.getMsg());
            }
        } else {
            MessageDialog.showDialog("网络请求失败，请检查连接");
        }
    }
    @FXML
    public void  initialize(){
        idColumn.setCellValueFactory(new MapValueFactory<>("id"));
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        titleColumn.setCellValueFactory(new MapValueFactory<>("title"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("time"));
        genderColumn.setCellValueFactory(new MapValueFactory<>("gender"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        genderComboBox.getItems().addAll("Man!", "女","男娘");
        genderComboBox.setValue("Man!");
        PracticeTable.setItems(observableList);
        loadPracticeData();
    }

    private void loadPracticeData() {
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/practice/getPracticeList", req);
        if (res != null && res.getCode() == 0) {
            try {
                List<Map<String, Object>> rawData = (List<Map<String, Object>>) res.getData();
                observableList.setAll(rawData);
            } catch (Exception e) {
                showAlert("数据错误", "加载失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    @FXML
    public void onAddButtonClick() {
        if (validateInput()) {
            DataRequest req = new DataRequest();
            req.add("id", idField.getText());
            req.add("name", nameField.getText());
            req.add("title", titleField.getText());
            req.add("time", timeField.getText());
            req.add("studentName", (studentNameField.getText()));
            if (genderComboBox.getSelectionModel() != null && genderComboBox.getSelectionModel().getSelectedItem() != null) {
                req.add("gender", genderComboBox.getSelectionModel().getSelectedItem());
            }
            DataResponse res = HttpRequestUtil.request("/api/practice/add", req);
                loadPracticeData(); // 重新加载数据

            showAlert("Success", "Course updated successfully");
        }
    }
    @FXML
    public void onDeleteButtonClick() {
        Map selected = PracticeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            DataRequest req = new DataRequest();
            req.add("id", selected.get("id")); // 注意字段名大小写与后端一致

            DataResponse res = HttpRequestUtil.request("/api/practice/delete", req);

            if (res != null && res.getCode() == 0) {
                loadPracticeData(); // 重新加载数据
                showAlert("成功", "实习经历删除成功");
            } else {
                showAlert("错误", "实习经历删除失败: " + (res != null ? res.getMsg() : "无响应"));
            }
        } else {
            showAlert("错误", "请先选择要删除的实习经历");
        }
    }
    @FXML
    public void onEditButtonClick() {
        Map selectedPractice = PracticeTable.getSelectionModel().getSelectedItem();
        if (selectedPractice != null && validateInput()) {
            DataRequest req = new DataRequest();
            req.add("id", selectedPractice.get("id")); // Original ID for identification
            req.add("name", nameField.getText());
            req.add("title", titleField.getText());
            req.add("time", timeField.getText());
            req.add("studentName", (studentNameField.getText()));
            if (genderComboBox.getSelectionModel() != null && genderComboBox.getSelectionModel().getSelectedItem() != null)
                req.add("gender", genderComboBox.getSelectionModel().getSelectedItem());
            DataResponse res = HttpRequestUtil.request("/api/practice/update", req);
                loadPracticeData();
                clearForm();
                showAlert("Success", "Course updated successfully");

        } else {
            showAlert("Error", "Please select a course to edit");
        }
    }


    private boolean validateInput() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            errorMessage += "实习公司不能为空！\n";
        }
        if (studentNameField.getText() == null || studentNameField.getText().isEmpty()) {
            errorMessage += "实习人不能为空！\n";
        }
        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert("输入错误", errorMessage);

            return false;
        }
    }
    private void clearForm() {
        idField.clear();
        nameField.clear();
        titleField.clear();
        timeField.clear();
        studentNameField.clear();
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
