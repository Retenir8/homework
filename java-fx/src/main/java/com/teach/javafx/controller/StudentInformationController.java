package com.teach.javafx.controller;

import com.teach.javafx.request.DataRequest;
import com.teach.javafx.request.DataResponse;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.Map;

public class StudentInformationController {
    @FXML private TextField numField;
    @FXML private TextField nameField;
    @FXML private TextField deptField;//院系
    @FXML private TextField majorField;//专业
    @FXML private TextField classNameField;//班级
    @FXML private TextField cardField;//证件号
    @FXML private TextField genderField;
    @FXML private TextField birthdayPick;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;

    public void initialize() {
        DataResponse res = HttpRequestUtil.request("/api/activity/getMyName", new DataRequest());
        Map<String,String> map = (Map<String, String>) res.getData();
        nameField.setText(map.get("name"));
        numField.setText(map.get("num"));
        DataRequest req = new DataRequest();
        req.add("name",nameField.getText());
        req.add("num",numField.getText());
        DataResponse res2 = HttpRequestUtil.request("/api/student/getStudentInfo", req);
        System.out.println(res2.getData());
        Map<String,Object> map2 = (Map<String, Object>) res2.getData();
        deptField.setText(map2.get("dept").toString());
        majorField.setText(map2.get("major").toString());
        classNameField.setText(map2.get("className").toString());
        cardField.setText(map2.get("card").toString());
        if(map2.get("gender").toString().equals("1")) {genderField.setText("男");}
        else  {genderField.setText("女");}
        birthdayPick.setText(map2.get("birthday").toString());
        emailField.setText(map2.get("email").toString());
        phoneField.setText(map2.get("phone").toString());
        addressField.setText(map2.get("address").toString());
    }
}
