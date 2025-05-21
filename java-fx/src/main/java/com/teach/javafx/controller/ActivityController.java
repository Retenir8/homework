package com.teach.javafx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.*;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

public class ActivityController {
    // 定义活动数据模型
    public static class Activity {
        private String activityName;
        private String organizer;
        private String content;
        private String location;
        private Date startTime;
        private Date endTime;
        private String participant;

        // 构造函数
        public Activity(String activityName, String organizer, String content, String location,
                        Date startTime, Date endTime, String participant) {
            this.activityName = activityName;
            this.organizer = organizer;
            this.content = content;
            this.location = location;
            this.startTime = startTime;
            this.endTime = endTime;
            this.participant = participant;
        }

        // Getter 和 Setter 方法
        public String getActivityName() {
            return activityName;
        }

        public void setActivityName(String activityName) {
            this.activityName = activityName;
        }

        public String getOrganizer() {
            return organizer;
        }

        public void setOrganizer(String organizer) {
            this.organizer = organizer;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public Date getEndTime() {
            return endTime;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }

        public String getParticipant() {
            return participant;
        }

        public void setParticipant(String participant) {
            this.participant = participant;
        }


    }

    // FXML 注入的控件
    @FXML
    private TableView<Activity> dataTableView;
    @FXML
    private TableColumn<Activity, String> ActivityColumn;
    @FXML
    private TableColumn<Activity, String> organizerColumn;
    @FXML
    private TableColumn<Activity, String> contentColumn;
    @FXML
    private TableColumn<Activity, String> locationColumn;
    @FXML
    private TableColumn<Activity, Date> startTimeColumn;
    @FXML
    private TableColumn<Activity, Date> endTimeColumn;
    @FXML
    private TableColumn<Activity, String> participantColumn;

    // 数据列表
    private ObservableList<Activity> activityList = FXCollections.observableArrayList();

    // 初始化方法
    @FXML
    public void initialize() {
        // 设置表格列的绑定
        ActivityColumn.setCellValueFactory(new PropertyValueFactory<>("activityName"));
        organizerColumn.setCellValueFactory(new PropertyValueFactory<>("organizer"));
        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        participantColumn.setCellValueFactory(new PropertyValueFactory<>("participant"));

        // 设置表格数据
        dataTableView.setItems(activityList);

    }

    // 添加按钮的事件处理器
    public void onAddButtonClick(ActionEvent event) {
        // 弹出对话框获取用户输入的活动信息
        // 这里可以使用 JavaFX 的 Dialog 或者自定义对话框
        // 示例代码省略，假设用户输入了活动信息并保存到 activity 对象中
        Activity newActivity = new Activity("新活动", "新组织者", "新内容", "新地点", null, null, "新参与者");
        activityList.add(newActivity);
    }

    // 修改按钮的事件处理器
    public void onEditButtonClick(ActionEvent event) {
        Activity selectedActivity = dataTableView.getSelectionModel().getSelectedItem();
        if (selectedActivity == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "请先选择一个活动进行修改", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        System.out.println("选中的活动名称：" + selectedActivity.getActivityName());

        // 弹出对话框获取用户输入
        TextInputDialog dialog = new TextInputDialog(selectedActivity.getActivityName());
        dialog.setTitle("修改活动");
        dialog.setHeaderText("请输入新的活动名称");
        dialog.setContentText("活动名称:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            selectedActivity.setActivityName(result.get());
            System.out.println("修改后的活动名称：" + selectedActivity.getActivityName());
            dataTableView.refresh(); // 刷新表格显示
        }
    }



    // 删除按钮的事件处理器
    public void onDeleteButtonClick(ActionEvent event) {
        // 获取选中的活动
        Activity selectedActivity = dataTableView.getSelectionModel().getSelectedItem();
        if (selectedActivity == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "请先选择一个活动进行删除", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // 弹出确认对话框
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "确定要删除这个活动吗？", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            activityList.remove(selectedActivity);
        }
    }
}