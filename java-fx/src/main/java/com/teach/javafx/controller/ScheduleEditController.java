package com.teach.javafx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;


public class ScheduleEditController {

    // 左侧显示选课列表
    @FXML
    private ListView<String> courseListView;
    // 右侧编辑备注
    @FXML
    private TextArea remarkTextArea;

    // 用于存储从外部设置的选课列表
    private ObservableList<String> courseListData = FXCollections.observableArrayList();

    // 用于返回选择的课程与备注
    private String selectedCourse;
    private String remark;

    // 当前对话框所属的 Stage，通过外部注入
    private Stage dialogStage;

    // 标记是否点击“确定”
    private boolean okClicked = false;

    /**
     * 初始化界面
     */
    @FXML
    public void initialize() {
        // 初始时可以设置 ListView 的数据源
        courseListView.setItems(courseListData);
    }

    /**
     * 在打开之前，由外部传入已选课程数据
     */
    public void setCourseListData(ObservableList<String> courseListData) {
        this.courseListData = courseListData;
        courseListView.setItems(this.courseListData);
    }

    /**
     * 设置 dialogStage。一般由调用者在加载 FXML 后注入。
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * 点击确定按钮后，获取所选课程和备注，关闭对话框
     */
    @FXML
    private void handleConfirm() {
        // 取得 ListView 选中的课程
        selectedCourse = courseListView.getSelectionModel().getSelectedItem();
        // 获取备注内容
        remark = remarkTextArea.getText();

        okClicked = true;
        // 关闭对话框
        dialogStage.close();
    }

    /**
     * 点击取消按钮，直接关闭对话框
     */
    @FXML
    private void handleCancel() {
        okClicked = false;
        dialogStage.close();
    }

    /**
     * 返回是否完成确认
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * 返回用户选择的课程
     */
    public String getSelectedCourse() {
        return selectedCourse;
    }

    /**
     * 返回用户输入的备注
     */
    public String getRemark() {
        return remark;
    }
}

