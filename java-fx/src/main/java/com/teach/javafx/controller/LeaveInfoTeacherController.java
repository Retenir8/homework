package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeaveInfoTeacherController {
    @FXML
    private TableView<Map> leaveTableView;
    @FXML
    private TableColumn<Map, String> studentNumColumn;
    @FXML
    private TableColumn<Map, String> studentNameColumn;
    @FXML
    private TableColumn<Map, String> reasonColumn;
    @FXML
    private TableColumn<Map, String> destinationColumn;
    @FXML
    private TableColumn<Map, String> phoneColumn;
    @FXML
    private TableColumn<Map, String> backTimeColumn;
    @FXML
    private TableColumn<Map, String> opinionColumn;
    @FXML
    private TableColumn<Map, String> backColumn;
    @FXML
    private TableColumn<Map, String> auditStatusColumn;

    private ArrayList<Map> leaveList = new ArrayList<>();
    private ObservableList<Map> observableList = FXCollections.observableArrayList();
    private String UserRole = "";
    @FXML
    private TextField searchNameField;
    private List<OptionItem> studentList;
    @FXML
    private LeaveEditController leaveEditController;
    private Stage stage = null;

    public List<OptionItem> getStudentList() {
        return studentList;
    }

//    @FXML
//    private void getUserRole(){
//        DataResponse res = HttpRequestUtil.request("/api/user/getUserRole", new DataRequest());
//
//        if (res != null && res.getCode() == 0) {
//            String userRole = res.getData().toString();
//            System.out.println("用户身份: " + userRole);
//
//            if ("ROLE_STUDENT".equals(userRole)) {
//                System.out.println("🔹 当前是学生");
//            } else if ("ROLE_TEACHER".equals(userRole)) {
//                System.out.println("🔹 当前是教师");
//            } else if ("ROLE_ADMIN".equals(userRole)) {
//                System.out.println("🔹 当前是管理员");
//            }
//            UserRole = res.getData().toString();
//        } else {
//            System.out.println("❌ 查询失败: " + (res != null ? res.getMsg() : "服务器未响应"));
//        }
//
//
//    }

//    @FXML
//    private void getMyLeaveInfo(){
//        DataResponse res = HttpRequestUtil.request("/api/leaveInfo/getMyLeaveInfo", new DataRequest());
//
//        if (res != null && res.getCode() == 0) {
//            leaveList = (ArrayList<Map>) res.getData();
//            setTableViewData();
//            System.out.println("✅ 获取到请假数据: " + leaveList);
//        } else {
//            System.out.println("❌ 查询失败: " + (res != null ? res.getMsg() : "服务器未响应"));
//        }
//    }

    @FXML
    private void onQueryLeaveClick() {
        Integer studentId = 0;
        DataRequest req = new DataRequest();
        req.add("studentId", studentId);
        DataResponse res = HttpRequestUtil.request("/api/leaveInfo/getLeaveRecords", req);

        if (res != null && res.getCode() == 0) {
            leaveList = (ArrayList<Map>) res.getData();
        }
        setTableViewData();
        System.out.println("leaveList 数据: " + leaveList);
    }

    public void onSearchByNameClick() {
        String searchName = searchNameField.getText().trim();
        if (searchName.isEmpty()) {
            MessageDialog.showDialog("请输入姓名后查询！");
            return;
        }

        System.out.println("🔍 通过姓名查询请假记录: " + searchName);
        DataRequest req = new DataRequest();
        req.add("studentName", searchName);
        DataResponse res = HttpRequestUtil.request("/api/leaveInfo/getLeaveRecordsByName", req);

        if (res != null && res.getCode() == 0) {
            leaveList = (ArrayList<Map>) res.getData();
        }
        setTableViewData();
        System.out.println("leaveList 数据: " + leaveList);
    }
    public void onSearchByBackClick() {
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/leaveInfo/getLeaveRecordsByBack",req);
        if (res != null && res.getCode() == 0) {
            leaveList = (ArrayList<Map>) res.getData();
        }
        setTableViewData();
        System.out.println("leaveList 数据: " + leaveList);
    }


    private void setTableViewData() {
        if (leaveList == null) {
            leaveList = new ArrayList<>();
        }

        observableList.clear();
        System.out.println("🔍 加载请假记录... 记录数量: " + leaveList.size());

        for (Map<String, Object> map : leaveList) {
            // 获取 leaveInfoId
            Integer leaveInfoId = CommonMethod.getInteger(map, "leaveInfoId"); // 确保 leaveInfoId 正确提取

            // 获取 studentId
            Map<String, Object> studentData = (Map<String, Object>) map.get("student");
            Integer studentId = studentData != null ? CommonMethod.getInteger(studentData, "studentId") : null;

            if (leaveInfoId == null || studentId == null) {
                System.out.println("❌ 数据错误: 记录缺少 leaveInfoId 或 studentId -> " + map);
                continue;
            }

            // 添加数据到 Map
            map.put("leaveInfoId", leaveInfoId);
            map.put("studentId", studentId);
            map.put("studentName", CommonMethod.getString(map, "studentName"));

            observableList.add(map);
        }

        leaveTableView.setItems(observableList);
        System.out.println("✅ 表格数据加载完成！");
    }



    @FXML
    public void initialize() {
//        getUserRole();
        System.out.println("初始化 LeaveTableController...");

        studentNumColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));
        studentNameColumn.setCellValueFactory(new MapValueFactory<>("studentName"));
        reasonColumn.setCellValueFactory(new MapValueFactory<>("reason"));
        destinationColumn.setCellValueFactory(new MapValueFactory<>("destination"));
        phoneColumn.setCellValueFactory(new MapValueFactory<>("phone"));
        backTimeColumn.setCellValueFactory(new MapValueFactory<>("backTime"));
        opinionColumn.setCellValueFactory(new MapValueFactory<>("opinion"));
        backColumn.setCellValueFactory(new MapValueFactory<>("back"));
        auditStatusColumn.setCellValueFactory(new MapValueFactory<>("auditStatus"));

        DataRequest req = new DataRequest();

        // **确保 studentList 不为空**
        System.out.println("请求获取学生列表...");
        studentList = HttpRequestUtil.requestOptionItemList("/api/leaveInfo/getStudentOptions", req);

        if (studentList == null || studentList.isEmpty()) {
            System.out.println("错误: 学生列表为空！");
            studentList = new ArrayList<>(); // 赋值空列表，避免 null 异常
        } else {
            System.out.println("成功加载学生列表: " + studentList);
        }

        // **确保 leaveList 不为空**
        onQueryLeaveClick();
        if (leaveList == null || leaveList.isEmpty()) {
            System.out.println("警告: 没有请假记录！");
            leaveList = new ArrayList<>();
        } else {
            System.out.println("成功加载请假记录: " + leaveList);
        }
        onSearchByBackClick();
    }


    private void initDialog() {
        if (stage != null) return;
        FXMLLoader fxmlLoader;
        try {
            fxmlLoader = new FXMLLoader(MainApplication.class.getResource("leaveInfo-edit-dialog.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("请假申请对话框");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(MainApplication.getMainStage());

            // 直接设置窗口大小
            stage.setWidth(500);  // 设置更大的宽度
            stage.setHeight(400); // 设置更大的高度
            stage.setResizable(false); // 禁止手动调整大小

            leaveEditController = fxmlLoader.getController();
            leaveEditController.setLeaveInfoTeacherController(this);
            leaveEditController.init();
        } catch (IOException e) {
            throw new RuntimeException("加载 leaveInfo-edit-dialog.fxml 失败", e);
        }
    }



    @FXML
    private void onDeleteLeaveClick() {
        Map form = leaveTableView.getSelectionModel().getSelectedItem();
        if (form == null) {
            MessageDialog.showDialog("没有选择记录，无法删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if (ret != MessageDialog.CHOICE_YES) return;

        Integer leaveInfoId = CommonMethod.getInteger(form, "leaveInfoId");
        if (leaveInfoId == null) {
            MessageDialog.showDialog("选中的记录没有 ID，无法删除！");
            return;
        }
        System.out.println("要删除的 leaveInfoId: " + leaveInfoId);
        DataRequest req = new DataRequest();
        req.add("leaveInfoId", leaveInfoId);
        DataResponse res = HttpRequestUtil.request("/api/leaveInfo/deleteLeave", req);
        if (res != null && res.getCode() == 0) {
            System.out.println("✅ 删除成功，刷新表格...");
            leaveList.remove(form); // **从 leaveList 里移除该记录**
            setTableViewData(); // **刷新表格**
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    public void doClose(String cmd, Map<String, Object> data) {
        MainApplication.setCanClose(true);
        stage.close();

        if (!"ok".equals(cmd)) {
            return;
        }

        // 获取表单数据
        Integer leaveInfoId = CommonMethod.getInteger(data, "leaveInfoId"); // 这里提取 leaveInfoId
//        Integer studentId = CommonMethod.getInteger(data, "studentId");

//        if (studentId == null) {
//            MessageDialog.showDialog("没有选中学生，无法提交请假申请！");
//            return;
//        }

        DataRequest req = new DataRequest();
        req.add("leaveInfoId", leaveInfoId);
//        req.add("studentId", studentId);
        req.add("reason", CommonMethod.getString(data, "reason"));
        req.add("destination", CommonMethod.getString(data, "destination"));
        req.add("phone", CommonMethod.getString(data, "phone"));
        req.add("backTime", CommonMethod.getString(data, "backTime"));
        req.add("opinion", CommonMethod.getString(data, "opinion"));
        req.add("back", CommonMethod.getString(data, "back"));
        req.add("auditStatus", CommonMethod.getString(data, "auditStatus"));

        // 正确检查 leaveInfoId 是否为空
        DataResponse res;
        if (leaveInfoId == null) {
            System.out.println("✨ 进行新请假申请...");
            res = HttpRequestUtil.request("/api/leaveInfo/apply", req);
        } else {
            System.out.println("✏️ 更新已有请假记录...");
            res = HttpRequestUtil.request("/api/leaveInfo/updateLeaveInfo", req);
        }

        // 处理后端返回结果
        if (res != null && res.getCode() == 0) {
            onQueryLeaveClick(); // 刷新数据
        } else {
            MessageDialog.showDialog(res.getMsg());
        }
    }


    @FXML
    private void onAuditClick() {
        Map<String, Object> selectedLeave = leaveTableView.getSelectionModel().getSelectedItem();
        System.out.println("🔍 选中的审核记录: " + selectedLeave);

        if (selectedLeave == null) {
            MessageDialog.showDialog("未选中请假记录，无法审核！");
            return;
        }

        Integer leaveInfoId = CommonMethod.getInteger(selectedLeave, "leaveInfoId");
        Integer studentId = CommonMethod.getInteger(selectedLeave, "studentId");

        if (leaveInfoId == null || studentId == null) {
            System.out.println("❌ 审核模式: leaveInfoId 或 studentId 为空！");
            MessageDialog.showDialog("审核错误: 请假记录 ID 或学生 ID 为空！");
            return;
        }
        initDialog();
        leaveEditController.showDialog(selectedLeave);

        MainApplication.setCanClose(false);
        stage.showAndWait(); // 等待用户操作
    }



    @FXML
    private void onBackClick() {
        Map<String, Object> selectedLeave = leaveTableView.getSelectionModel().getSelectedItem();
        System.out.println("🔍 选中的销假记录: " + selectedLeave);

        if (selectedLeave == null) {
            MessageDialog.showDialog("未选中请假记录，无法销假！");
            return;
        }

        Integer leaveInfoId = CommonMethod.getInteger(selectedLeave, "leaveInfoId");
        Integer studentId = CommonMethod.getInteger(selectedLeave, "studentId");

        if (leaveInfoId == null || studentId == null) {
            System.out.println("❌ 销假模式: leaveInfoId 或 studentId 为空！");
            MessageDialog.showDialog("审核错误: 请假记录 ID 或学生 ID 为空！");
            return;
        }
        initDialog();
        leaveEditController.showDialog(selectedLeave);

        MainApplication.setCanClose(false);
        stage.showAndWait(); // 等待用户操作
    }

}
