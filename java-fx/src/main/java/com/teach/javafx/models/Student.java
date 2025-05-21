package com.teach.javafx.models;


public class Student extends Person{

    private Integer studentId;
    private String major;
    private String className;
    public Student(){
        super();
    }
    public Student(Integer studentId,Integer personId,String num, String name){
        super(personId,num,name);
        this.studentId = studentId;
    }

    public Student(String number, String name1) {
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}