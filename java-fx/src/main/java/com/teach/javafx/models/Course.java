package com.teach.javafx.models;
import javafx.beans.property.*;

public class Course {
    private final StringProperty courseId;
    private final StringProperty courseName;
    private final StringProperty teacher;
    private final StringProperty location;
    private final IntegerProperty credit;
    private final StringProperty schedule;
    private final StringProperty assessmentType;

    public Course(String courseId, String courseName, String teacher,
                  String location, int credit, String schedule, String assessmentType) {
        this.courseId = new SimpleStringProperty(courseId);
        this.courseName = new SimpleStringProperty(courseName);
        this.teacher = new SimpleStringProperty(teacher);
        this.location = new SimpleStringProperty(location);
        this.credit = new SimpleIntegerProperty(credit);
        this.schedule = new SimpleStringProperty(schedule);
        this.assessmentType = new SimpleStringProperty(assessmentType);
    }

    // Getter方法
    public String getCourseId() { return courseId.get(); }
    public String getCourseName() { return courseName.get(); }
    public String getTeacher() { return teacher.get(); }
    public String getLocation() { return location.get(); }
    public int getCredit() { return credit.get(); }
    public String getSchedule() { return schedule.get(); }
    public String getAssessmentType() { return assessmentType.get(); }

    // Setter方法
    public void setCourseId(String value) { courseId.set(value); }
    public void setCourseName(String value) { courseName.set(value); }
    public void setTeacher(String value) { teacher.set(value); }
    public void setLocation(String value) { location.set(value); }
    public void setCredit(int value) { credit.set(value); }
    public void setSchedule(String value) { schedule.set(value); }
    public void setAssessmentType(String value) { assessmentType.set(value); }

    // Property访问方法
    public StringProperty courseIdProperty() { return courseId; }
    public StringProperty courseNameProperty() { return courseName; }
    public StringProperty teacherProperty() { return teacher; }
    public StringProperty locationProperty() { return location; }
    public IntegerProperty creditProperty() { return credit; }
    public StringProperty scheduleProperty() { return schedule; }
    public StringProperty assessmentTypeProperty() { return assessmentType; }
}