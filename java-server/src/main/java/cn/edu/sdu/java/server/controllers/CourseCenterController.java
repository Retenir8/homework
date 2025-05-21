package cn.edu.sdu.java.server.controllers;
import ch.qos.logback.classic.Logger;
import cn.edu.sdu.java.server.models.Courses;
import cn.edu.sdu.java.server.services.CourseCenterService;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/courses")
public class CourseCenterController {
    @Autowired
    private CourseCenterService courseCenterService;
    //public CourseCenterController(CourseCenterService courseCenterService) {this.courseCenterService = courseCenterService;}


    @PostMapping("/getCoursesList")
    public DataResponse getAllCourses(@RequestBody DataRequest req) {
        List<Courses> courses = courseCenterService.getAllCourses();
        return DataResponse.success(courses);
    }

    // 添加新课程
    @PostMapping("/add")
    public DataResponse createCourse(@RequestBody DataRequest req) {

        System.out.println("收到请求数据: " + req.getData());

        Courses course = new Courses();
        course.setCourseName(req.getString("courseName"));
        course.setTeacher(req.getString("teacher"));
        course.setLocation(req.getString("location"));
        course.setCredit(req.getInteger("credit"));
        course.setSchedule(req.getString("schedule"));
        course.setAssessmentType(req.getString("assessmentType"));

        Courses courses = courseCenterService.saveCourse(course);
        return DataResponse.success(courses);
    }

    // 更新课程
    @PostMapping("/update")
    public DataResponse updateCourse(@RequestBody DataRequest req) {
        Integer id = req.getInteger("id");
        Courses existing = courseCenterService.getCourseById(id);
        if(existing != null) {
            existing.setCourseId(req.getInteger("CourseId"));
            existing.setCourseName(req.getString("courseName"));
            existing.setTeacher(req.getString("teacher"));
            existing.setLocation(req.getString("location"));
            existing.setCredit(req.getInteger("credit"));
            existing.setSchedule(req.getString("schedule"));
            existing.setAssessmentType(req.getString("assessmentType"));

            return DataResponse.success(courseCenterService.saveCourse(existing));
        }
        return DataResponse.error("课程不存在");
    }

    // 删除课程
    @PostMapping("/delete")
    public DataResponse deleteCourse(@RequestBody DataRequest req) {

        return courseCenterService.deleteCourse(req);
    }
//选课
    @PostMapping("/choose")
    public DataResponse chooseCourse(@RequestBody DataRequest req) {

        return courseCenterService.chooseCourse(req);
    }

}

