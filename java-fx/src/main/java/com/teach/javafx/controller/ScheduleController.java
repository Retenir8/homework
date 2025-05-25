package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleController {

    @FXML
    private GridPane timetableGrid;

    // 用于保存当前课表数据，键为 "c1" ... "c35"
    private Map<String, Object> scheduleData = new HashMap<>();

    // 当前学生ID，从后端获取（例如登录后存储的ID）
    private Integer studentId;

    // 用于记录当前正在编辑的单元格在 scheduleData 中的 key，如 "c7"
    private String currentEditingKey;

    @FXML
    public void initialize() {
        // 调用后端接口获取学生ID
        DataResponse res = HttpRequestUtil.request("/api/student/getMyStudentId", new DataRequest());
        if (res != null && res.getCode() == 0) {
            Map<String, Object> dataMap = (Map<String, Object>) res.getData();
            Object idObj = dataMap.get("studentId");
            if (idObj instanceof Number) {
                studentId = ((Number) idObj).intValue();
            }
            System.out.println("studentId: " + studentId);
        } else {
            System.out.println("服务器响应异常");
        }

        // 初始化课表数据：默认将 c1 到 c35 均设为空字符串
        for (int i = 1; i <= 35; i++) {
            scheduleData.put("c" + i, "");
        }
        updateGrid();
    }

    /**
     * 根据 scheduleData 更新界面 GridPane 中各单元格的显示（如根据 fx:id 查找按钮并设置 text）
     */
    private void updateGrid() {
        // 调用后端接口获取当前学生所有选课记录
        DataRequest req = new DataRequest();
        req.add("studentId", studentId);
        DataResponse res = HttpRequestUtil.request("/api/courses/getMyCourses", req);

        // 构建一个 gridMap，用于存储35个时段的显示内容，默认显示 "-" 表示未安排课程
        Map<String, String> gridMap = new java.util.HashMap<>();
        for (int i = 1; i <= 35; i++) {
            gridMap.put("c" + i, "-");
        }

        // 如果接口调用成功，则处理返回的课程数据
        if (res != null && res.getCode() == 0) {
            try {
                // 假设返回的数据为 List<Map<String, Object>>，每个 Map 表示一个 Course 对象的属性，包含字段 c1～c35
                List<Map<String, Object>> courseList = (List<Map<String, Object>>) res.getData();
                // 遍历所有课程数据
                for (Map<String, Object> course : courseList) {
                    // 获取课程名称和授课地点
                    String courseName = course.get("courseName") == null ? "" : course.get("courseName").toString();
                    String location = course.get("location") == null ? "" : course.get("location").toString();
                    // 组合显示文本，使用换行符分隔，如 "高数\n教学楼101"
                    String displayText = courseName;
                    if (!location.isEmpty()) {
                        displayText += "\n" + location;
                    }
                    // 遍历该课程在35个时段中的设置
                    for (int i = 1; i <= 35; i++) {
                        String key = "c" + i;
                        if (course.get(key) != null && course.get(key).toString().equals("1")) {
                            // 如果该时段默认无安排则直接设置，否则追加显示（以换行符分隔）
                            if (gridMap.get(key).equals("-")) {
                                gridMap.put(key, displayText);
                            } else {
                                gridMap.put(key, gridMap.get(key) + "\n" + displayText);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("数据转换异常: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("获取用户课程数据失败: " + (res != null ? res.getMsg() : "无响应"));
        }

        // 遍历 GridPane 的单元格：外层循环行（节次: period 1~5），内层循环列（天: day 1~7）
        for (int period = 1; period <= 5; period++) {
            for (int day = 1; day <= 7; day++) {
                // 根据映射公式计算 index
                int index = (day - 1) * 5 + period;
                String key = "c" + index;
                // 查找 fx:id 格式为 "cell_{period}_{day}" 的按钮
                Button button = (Button) timetableGrid.lookup("#cell_" + period + "_" + day);
                if (button != null) {
                    button.setText(gridMap.get(key));
                }
            }
        }
    }





    /**
     * 点击课表单元格时调用：通过按钮的 fx:id 获取被点击单元格对应的 scheduleData key，
     * 然后调用对话框方式进行选课与备注编辑。
     */
    @FXML
    public void handleCellEdit(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String id = clickedButton.getId();   // fx:id 例如 "cell_3_5"（第 3 节、第 5 列 → 星期五）
        String[] parts = id.split("_");
        if (parts.length != 3) {
            showAlert("错误", "单元格标识错误");
            return;
        }
        int period = Integer.parseInt(parts[1]); // 节数：1~5
        int day = Integer.parseInt(parts[2]);    // 星期数：1~7
        int index = (day - 1) * 5 + period;         // 映射公式：例如 cell_3_5 对应 c((5-1)*5+3)=c23
        String key = "c" + index;
        currentEditingKey = key;

        // 调用对话框编辑
        initDialog();
    }

    /**
     * 打开一个新的 Dialog（通过 schedule-edit-dialog.fxml）编辑选课与备注，
     * 编辑完成后在 scheduleData 中更新当前单元格的值，并调用 saveScheduleCell() 保存到后端。
     */
    private void initDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("schedule-edit-dialog.fxml"));
            Parent page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("编辑课程");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(MainApplication.getMainStage());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // 获取编辑对话框的控制器，并注入相关数据
            ScheduleEditController controller = loader.getController();
            // 示例数据：已选课程列表；实际可由后端获取或传入
            ObservableList<String> myCourses = FXCollections.observableArrayList("课程A", "课程B", "课程C");
            controller.setCourseListData(myCourses);
            controller.setDialogStage(dialogStage);

            // 显示对话框并等待
            dialogStage.showAndWait();

            // 当对话框关闭后，若用户点击确认，则从对话框中获取所选课程及备注并更新对应单元格
            if (controller.isOkClicked()) {
                String chosenCourse = controller.getSelectedCourse();
                String remark = controller.getRemark();
                // 组合显示格式，根据需要自行定义（例如：课程及备注合并显示）
                String cellValue = chosenCourse;
                if (remark != null && !remark.isEmpty()) {
                    cellValue += " (" + remark + ")";
                }
                // 更新当前编辑单元格对应的数据，并刷新界面
                scheduleData.put(currentEditingKey, cellValue);
                updateGrid();
                // 保存修改到后端
                saveScheduleCell(currentEditingKey, cellValue);
            }
        } catch (IOException e) {
            showAlert("错误", "加载编辑对话框失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 保存更新后的单元格数据到后端接口 /api/schedule/updateCell，
     * 请求中传入 studentId 和当前编辑单元格的键值对。
     */
    private void saveScheduleCell(String key, String value) {
        DataRequest req = new DataRequest();
        req.add("studentId", studentId);
        req.add(key, value);
        DataResponse res = HttpRequestUtil.request("/api/schedule/updateCell", req);
        if (res == null || res.getCode() != 0) {
            showAlert("错误", "保存失败: " + (res != null ? res.getMsg() : "无响应"));
        }
    }

    /**
     * 弹出提示对话框
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
