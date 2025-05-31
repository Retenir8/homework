package com.teach.javafx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.Map;


public class ScheduleEditController {

    // 右侧编辑备注
    @FXML
    private TextArea remarkTextArea;

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
     * 返回用户输入的备注
     */
    public String getRemark() {
        return remark;
    }
}

