package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScoreTableController {

    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map, String> studentNumColumn;
    @FXML
    private TableColumn<Map, String> studentNameColumn;
    @FXML
    private TableColumn<Map, String> classNameColumn;
    @FXML
    private TableColumn<Map, String> courseNumColumn;
    @FXML
    private TableColumn<Map, String> courseNameColumn;
    @FXML
    private TableColumn<Map, String> creditColumn;
    @FXML
    private TableColumn<Map, String> markColumn;
    @FXML
    private TableColumn<Map, String> rankingColumn;
    @FXML
    private TableColumn<Map, Button> editColumn;

    // 搜索条件：通过文本框输入模糊查询信息
    @FXML
    private TextField studentNameTextField;  // 学生姓名或学号搜索
    @FXML
    private TextField courseNameTextField;   // 课程名称或课程号搜索

    @FXML
    private ComboBox<OptionItem> studentComboBox;
    @FXML
    private ComboBox<OptionItem> courseComboBox;

    // 存放后端获取的成绩数据，每个 Map 对象代表一行记录
    private ArrayList<Map> scoreList = new ArrayList<>();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();

    private List<OptionItem> studentList;
    private List<OptionItem> courseList;

    private ScoreEditController scoreEditController = null;
    private Stage stage = null;

    public List<OptionItem> getStudentList() {
        return studentList;
    }

    public List<OptionItem> getCourseList() {
        return courseList;
    }

    @FXML
    public void initialize() {
        // 绑定 TableView 的各列到 Map 对象对应的键
        studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        classNameColumn.setCellValueFactory(new MapValueFactory<>("className"));
        courseNumColumn.setCellValueFactory(new MapValueFactory<>("courseNum"));
        courseNameColumn.setCellValueFactory(new MapValueFactory<>("courseName"));
        creditColumn.setCellValueFactory(new MapValueFactory<>("credit"));
        markColumn.setCellValueFactory(new MapValueFactory<>("mark"));
        editColumn.setCellValueFactory(new MapValueFactory<>("edit")); // 将放入 Map 中的编辑按钮显示到该列

        // 加载学生和课程下拉选项列表，从后端获取数据（返回值为 OptionItem 的 List）
        DataRequest req = new DataRequest();
        studentList = HttpRequestUtil.requestOptionItemList("/api/score/getStudentItemOptionList", req);
        courseList = HttpRequestUtil.requestOptionItemList("/api/score/getCourseItemOptionList", req);
        // 添加默认选项“请选择”
        OptionItem defaultItem = new OptionItem(null, "0", "请选择");
        studentComboBox.getItems().add(defaultItem);
        studentComboBox.getItems().addAll(studentList);
        courseComboBox.getItems().add(defaultItem);
        courseComboBox.getItems().addAll(courseList);

        dataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // 初始查询：加载成绩数据
        onQueryButtonClick();
    }

    /**
     * 按钮点击事件：查询/模糊搜索成绩数据。
     * 从文本框中读取搜索条件，同时获取下拉列表选中的学生和课程，传递给后端接口。
     */
    @FXML
    private void onQueryButtonClick() {
        // 获取模糊搜索条件
        String studentSearch = studentNameTextField.getText().trim();
        String courseSearch = courseNameTextField.getText().trim();

        // 下拉列表获得精确筛选条件
        Integer studentId = 0;
        Integer courseId = 0;
        OptionItem op = studentComboBox.getSelectionModel().getSelectedItem();
        if (op != null) {
            try {
                studentId = Integer.parseInt(op.getValue());
            } catch (NumberFormatException ex) {
                studentId = 0;
            }
        }
        op = courseComboBox.getSelectionModel().getSelectedItem();
        if (op != null) {
            try {
                courseId = Integer.parseInt(op.getValue());
            } catch (NumberFormatException ex) {
                courseId = 0;
            }
        }

        DataRequest req = new DataRequest();
        req.add("studentId", studentId);
        req.add("courseId", courseId);
        req.add("studentSearch", studentSearch); // 传入学生姓名/学号
        req.add("courseSearch", courseSearch);   // 传入课程名称/课程号

        // 请求后端 API，接口应支持模糊查询，名称可自行调整（例如：/api/score/searchScoreList）
        DataResponse res = HttpRequestUtil.request("/api/score/getScoreList", req);
        if (res != null && res.getCode() == 0) {
            scoreList = (ArrayList<Map>) res.getData();
        } else {
            MessageDialog.showDialog("查询失败: " + (res != null ? res.getMsg() : "服务器无响应！"));
        }
        setTableViewData();
    }

    /**
     * 将查询获取的成绩数据放入 TableView 中显示
     */
    private void setTableViewData() {
        observableList.clear();
        // 遍历成绩列表，为每一行数据加入一个“编辑”按钮
        for (Map map : scoreList) {
            Button editButton = new Button("编辑");
            // 将当前行的数据放到按钮的 userData 中，点击时传递给编辑界面
            editButton.setUserData(map);
            editButton.setOnAction(e -> editItem((Map) editButton.getUserData()));
            map.put("edit", editButton);
            observableList.add(map);
        }
        dataTableView.setItems(observableList);
    }

    /**
     * 编辑操作：弹出对话框让用户编辑（添加/修改）成绩
     */
    public void editItem(Map scoreData) {
        if (scoreData == null)
            return;
        initDialog();
        System.out.println(scoreData);
        scoreEditController.showDialog(scoreData);
        MainApplication.setCanClose(false);
        stage.showAndWait();
    }

    /**
     * 初始化编辑对话框
     */
    private void initDialog() {
        if (stage != null)
            return;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("score-edit-dialog.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 260, 140);
            stage = new Stage();
            stage.initOwner(MainApplication.getMainStage());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setAlwaysOnTop(true);
            stage.setScene(scene);
            stage.setTitle("成绩录入对话框");
            stage.setOnCloseRequest(event -> MainApplication.setCanClose(true));
            scoreEditController = fxmlLoader.getController();
            scoreEditController.setScoreTableController(this);
            scoreEditController.initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 对话框关闭回调：保存添加/修改成绩
     */
    public void doClose(String cmd, Map<String, Object> data) {
        MainApplication.setCanClose(true);
        stage.close();
        if (!"ok".equals(cmd))
            return;
        DataRequest req = new DataRequest();
        Integer studentId = CommonMethod.getInteger(data, "studentId");

        Integer courseId = CommonMethod.getInteger(data, "courseId");
        if (courseId == null) {
            MessageDialog.showDialog("没有选中课程，不能添加保存！");
            return;
        }
        req.add("studentId", studentId);
        req.add("courseId", courseId);
        req.add("scoreId", CommonMethod.getInteger(data, "scoreId"));
        req.add("mark", CommonMethod.getInteger(data, "mark"));
        DataResponse res = HttpRequestUtil.request("/api/score/scoreSave", req);
        if (res != null && res.getCode() == 0) {
            onQueryButtonClick();
        } else {
            MessageDialog.showDialog(res != null ? res.getMsg() : "保存失败，服务器无响应！");
        }
    }

//    @FXML
//    private void onDeleteButtonClick() {
//        Map<String, Object> form = (Map<String, Object>) dataTableView.getSelectionModel().getSelectedItem();
//        if (form == null) {
//            MessageDialog.showDialog("没有选择，不能删除");
//            return;
//        }
//        int ret = MessageDialog.choiceDialog("确认要删除吗?");
//        if (ret != MessageDialog.CHOICE_YES) {
//            return;
//        }
//        Integer scoreId = CommonMethod.getInteger(form, "scoreId");
//        DataRequest req = new DataRequest();
//        req.add("scoreId", scoreId);
//        DataResponse res = HttpRequestUtil.request("/api/score/scoreDelete", req);
//        if (res != null && res.getCode() == 0) {
//            onQueryButtonClick();
//        } else {
//            MessageDialog.showDialog(res != null ? res.getMsg() : "删除失败，服务器无响应！");
//        }
//    }
}
