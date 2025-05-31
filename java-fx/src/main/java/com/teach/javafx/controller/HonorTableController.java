package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.LocalDateStringConverter;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.util.CommonMethod;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HonorTableController {
    @FXML
    private TableView<Map> HonorTableView;
    @FXML private TableColumn<Map, String> studentNumColumn;
    @FXML private TableColumn<Map, String> studentNameColumn;
    @FXML private TableColumn<Map, String> titleColumn;
    @FXML private TableColumn<Map, String> timeColumn;
    @FXML private TableColumn<Map, String> hostColumn;
    @FXML private TableColumn<Map, String> typeColumn;
    @FXML private TableColumn<Map, String> levelColumn;
    @FXML private TableColumn<Map, Button> editColumn;
    @FXML private TableColumn<Map, Button> deleteColumn;
    @FXML
    private TextField inputHost;
    @FXML
    private TextField inputTitle;
    @FXML
    private DatePicker timePicker;
    @FXML
    private ComboBox<String> inputType;
    @FXML
    private ComboBox<String> inputLevel;
    @FXML
    private TextField inputStudentNameForSave;
    @FXML
    private TextField inputDataForSearch;
    @FXML
    private ComboBox<String> chooseType;
    @FXML
    private TextField inputStudentNumForSave;
    private ArrayList<Map> honorList = new ArrayList<>();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private Stage stage;
    private Integer honorId = null;

    @FXML
    public void initialize(){
        chooseType.getItems().addAll("搜索学生","搜索活动");
        chooseType.setValue("搜索学生");
        studentNumColumn.setCellValueFactory(new MapValueFactory<>("num"));//设置列值工程属性
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        hostColumn.setCellValueFactory(new MapValueFactory<>("Host"));
        timeColumn.setCellValueFactory(new MapValueFactory<>("Date"));
        levelColumn.setCellValueFactory(new MapValueFactory<>("HonorLevel"));
        titleColumn.setCellValueFactory(new MapValueFactory<>("HonorName"));
        typeColumn.setCellValueFactory(new MapValueFactory<>("HonorType"));
        timePicker.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
        editColumn.setCellValueFactory(new MapValueFactory<>("edit"));
        deleteColumn.setCellValueFactory(new MapValueFactory<>("delete"));//上述为表的初始化感觉上没什么问题,只是目前不知道这里这个delete和edit有什么用
        inputLevel.getItems().addAll("校级","市级","省级","国家级");
        inputLevel.setValue("校级");
        inputType.getItems().addAll("金奖","银奖","铜奖");
        inputType.setValue("金奖");
        setTableViewData();
    }

    void setTableViewData() {
        try {
            observableList.clear();
            DataRequest req = new DataRequest();
            DataResponse res = HttpRequestUtil.request("/api/honor/getHonorListAll", req);
            if (res == null || res.getData() == null) {
                System.err.println("请求失败：返回数据为空");
                MessageDialog.showDialog("无法获取荣誉列表数据");
                return;
            }
            if (!(res.getData() instanceof List)) {
                System.err.println("返回数据格式不正确");
                return;
            }
            honorList = (ArrayList<Map>) res.getData();
            Platform.runLater(() -> {
                for (int j = 0; j < honorList.size(); j++) {
                    Map map = honorList.get(j);
                    Button editButton = new Button("修改");
                    editButton.setId("edit"+j);
                    Button deleteButton = new Button("删除");
                    deleteButton.setId("delete"+j);
                    editButton.setOnAction(e -> editItem(((Button)e.getSource()).getId()));
                    deleteButton.setOnAction(e -> deleteItem(((Button)e.getSource()).getId()));
                    map.put("edit", editButton);
                    map.put("delete", deleteButton);
                    observableList.add(map); } // 直接添加单个map，而不是observableArrayLi
                HonorTableView.setItems(observableList);
                HonorTableView.refresh();
            });
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("加载数据时出错: " + e.getMessage());
        }
    }

    @FXML
    void onNewButtonClick() {
        try {
            String honorTitle = inputTitle.getText().trim();
            String honorHost = inputHost.getText().trim();
            String studentName = inputStudentNameForSave.getText().trim();
            String studentNum = inputStudentNumForSave.getText().trim();
            String type = inputType.getValue();
            String level = inputLevel.getValue();
            LocalDate date = timePicker.getValue();
            if(honorTitle.isEmpty() || honorHost.isEmpty() || type == null || level == null
                    || date == null || studentName.isEmpty() || studentNum.isEmpty()) {
                MessageDialog.showDialog("请填写完整信息！");
                return;
            }
            String time = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            DataRequest req = new DataRequest();
            req.add("honorName", honorTitle);
            req.add("host", honorHost);
            req.add("honorType", type);
            req.add("date", time);
            req.add("honorLevel", level);
            req.add("studentNum", studentNum);
            req.add("studentName", studentName);
            DataResponse res = HttpRequestUtil.request("/api/honor/createHonor", req);
            if (res != null && res.getCode() == 0) {
                clearInputFields();
                MessageDialog.showDialog("添加成功！");
                setTableViewData();
            } else {
                String errorMsg = (res != null) ? res.getMsg() : "服务器无响应";
                MessageDialog.showDialog("添加失败: " + errorMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageDialog.showDialog("发生错误: " + e.getMessage());
        } finally {
            MainApplication.setCanClose(false);
        }
    }
    @FXML
    void onQueryButtonClick(){
        String data=inputDataForSearch.getText();
        String type=chooseType.getValue();
        if (data.isEmpty()) {
            MessageDialog.showDialog("请输入数据进行查询！");
            return;
        }
        String url =null;
        if(type.equals("搜索学生")) url ="/api/honor/getHonorListByStudentName";
        else url ="/api/honor/getHonorListByHonorName";
        DataRequest req = new DataRequest();
        req.add("data",data);
        DataResponse res = HttpRequestUtil.request(url,req);
        if(res.getCode() == 0&&res!=null) {
            honorList = (ArrayList<Map>) res.getData();
            Platform.runLater(() -> {
                observableList.clear();
                for (int j = 0; j < honorList.size(); j++) {
                    Map map = honorList.get(j);
                    Button editButton = new Button("修改");
                    editButton.setId("edit"+j);
                    Button deleteButton = new Button("删除");
                    deleteButton.setId("delete"+j);
                    editButton.setOnAction(e -> editItem(((Button)e.getSource()).getId()));
                    deleteButton.setOnAction(e -> deleteItem(((Button)e.getSource()).getId()));
                    map.put("edit", editButton);
                    map.put("delete", deleteButton);
                    observableList.add(map); }
                HonorTableView.setItems(observableList);
                HonorTableView.refresh();
            });
        }else if(res!=null){
            MessageDialog.showDialog(res.getMsg());
        }else{
            MessageDialog.showDialog("通信错误！");
        }
    }

    private void editItem(String name) {
        if(name == null)
            return;
        int j = Integer.parseInt(name.substring(4,name.length()));
        Map data = honorList.get(j);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/teach/javafx/honor-edit-dialog.fxml"));
            Parent root = loader.load();
            HonorEditController controller = loader.getController();
            controller.setHonorData(data);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("编辑荣誉记录");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(MainApplication.getMainStage()); // 设置父窗口
            dialogStage.setScene(new Scene(root));
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
            setTableViewData();
        } catch (IOException e) {
            e.printStackTrace();
            MessageDialog.showDialog("加载编辑对话框失败！");
        } finally {
            MainApplication.setCanClose(false); // 确保在对话框关闭后可以关闭主窗口
        }
    }

    private void deleteItem(String name) {
        if(name==null){
            return;
        }
        int j =Integer.parseInt((name.substring(6,name.length())));
        Map data=honorList.get(j);
        int jet=MessageDialog.choiceDialog("确定要删除吗");
        if(jet!=MessageDialog.CHOICE_YES){
            return;
        }
        honorId = CommonMethod.getInteger(data,"courseAttendanceId");
        DataRequest req = new DataRequest();
        req.add("title",data.get("HonorName"));
        req.add("name",data.get("name"));
        DataResponse res = HttpRequestUtil.request("/api/honor/deleteHonor",req);
        if (res.getCode()==0){
            MessageDialog.showDialog("删除记录成功!");
            setTableViewData();
        }else{
            MessageDialog.showDialog(res.getMsg());
        }
    }

    private void clearInputFields() {
        inputTitle.setText("");
        inputHost.setText("");
        inputLevel.setValue(null);
        inputType.setValue(null);
        timePicker.setValue(null);
        inputStudentNameForSave.setText("");
        inputStudentNumForSave.setText("");
    }
}