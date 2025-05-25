package com.teach.javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CourseTimeSelectionController {

    @FXML
    private GridPane timeGrid; // GridPane中包含5行7列的CheckBox，fx:id格式："cb_1_1" 到 "cb_5_7"

    private Stage dialogStage;
    private boolean okClicked = false;
    // 存储35位字符串，“1”表示选中，“0”表示未选
    private String timeSlots;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * 设置初始状态。要求传入的 timeSlots 必须为35位字符串，否则默认全"0"。
     * 按列排列映射：对于每个 CheckBox，其索引计算公式为
     *    index = (day - 1) * 5 + (period - 1)
     * 例如，fx:id为"cb_1_1"（第一行、第一列）：index = (1-1)*5 + (1-1) = 0，对应 c1；
     *          "cb_5_1"：index = (1-1)*5 + (5-1) = 4，对应 c5；
     *          "cb_1_2"：index = (2-1)*5 + (1-1) = 5，对应 c6；
     *          "cb_5_7"：index = (7-1)*5 + (5-1) = 6*5+4 = 34，对应 c35。
     */
    public void setTimeSlots(String timeSlots) {
        if (timeSlots == null || timeSlots.length() != 35) {
            timeSlots = "00000000000000000000000000000000000".substring(0, 35);
        }
        this.timeSlots = timeSlots;
        for (int period = 1; period <= 5; period++) {
            for (int day = 1; day <= 7; day++) {
                int idx = (day - 1) * 5 + (period - 1);
                CheckBox cb = (CheckBox) timeGrid.lookup("#cb_" + period + "_" + day);
                if (cb != null) {
                    cb.setSelected(timeSlots.charAt(idx) == '1');
                }
            }
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public String getTimeSlots() {
        return timeSlots;
    }

    /**
     * 用户点击“确定”按钮时，根据所有 CheckBox 状态构造35位字符串，
     * 使用公式：index = (day - 1) * 5 + (period - 1)；
     * 即fx:id "cb_1_1" 对应第1位，"cb_5_1" 对应第5位，
     * "cb_1_2" 对应第6位……"cb_5_7" 对应第35位。
     */
    @FXML
    private void handleConfirm() {
        StringBuilder sb = new StringBuilder();
        for (int period = 1; period <= 5; period++) {
            for (int day = 1; day <= 7; day++) {
                CheckBox cb = (CheckBox) timeGrid.lookup("#cb_" + period + "_" + day);
                sb.append((cb != null && cb.isSelected()) ? "1" : "0");
            }
        }
        // 但注意：当前循环顺序为按行排序，
        // 为了生成按列排序的字符串，需要调整循环顺序：先遍历 day，再遍历 period
        // 如果需要按列排序，请参考下面的修改：
        /*
        StringBuilder sbCols = new StringBuilder();
        for (int day = 1; day <= 7; day++) {
            for (int period = 1; period <= 5; period++) {
                CheckBox cb = (CheckBox) timeGrid.lookup("#cb_" + period + "_" + day);
                sbCols.append((cb != null && cb.isSelected()) ? "1" : "0");
            }
        }
        timeSlots = sbCols.toString();
        */
        // 如果你希望生成按列排序的字符串，请使用上面的注释部分
        // 否则，若按当前顺序（行优先），则需要转换顺序：
        //
        // 下面采用按列排序的方式：
        StringBuilder sbCols = new StringBuilder();
        for (int day = 1; day <= 7; day++) {
            for (int period = 1; period <= 5; period++) {
                CheckBox cb = (CheckBox) timeGrid.lookup("#cb_" + period + "_" + day);
                sbCols.append((cb != null && cb.isSelected()) ? "1" : "0");
            }
        }
        timeSlots = sbCols.toString();

        okClicked = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        okClicked = false;
        dialogStage.close();
    }
}
