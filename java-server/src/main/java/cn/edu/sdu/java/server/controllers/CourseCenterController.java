package cn.edu.sdu.java.server.controllers;
import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.repositorys.CourseCenterRepository;
import cn.edu.sdu.java.server.repositorys.TeacherRepository;
import cn.edu.sdu.java.server.services.CourseCenterService;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/courses")
public class CourseCenterController {
    @Autowired
    private CourseCenterService courseCenterService;
    @Autowired
    private CourseCenterRepository courseCenterRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    public CourseCenterController(CourseCenterService courseCenterService) {this.courseCenterService = courseCenterService;}


    @PostMapping("/getCoursesList")
    public DataResponse getAllCourses(@RequestBody DataRequest req) {
        List<Course> course = courseCenterService.getAllCourses();
        return DataResponse.success(course);
    }

    // 添加新课程
    @PostMapping("/add")
    public DataResponse createCourse(@RequestBody DataRequest req) {

        System.out.println("收到请求数据: " + req.getData());
//        String name = req.getString("teacherName");
//        String num = req.getString("teacherNum");
//        if(teacherRepository.findByPersonNum(num) != null || teacherRepository.findByPersonName(name) != null || teacherRepository.findByPersonNum(num)!=teacherRepository.findByPersonName(name)) {
//            return DataResponse.error(0,"教工号与教师不匹配");
//        }
        Course course = new Course();
        course.setCourseName(req.getString("courseName"));
        course.setTeacher(teacherRepository.findByPersonNum(req.getString("teacherNum")));
        course.setTeacherName(req.getString("teacherName"));
        course.setLocation(req.getString("location"));
        course.setCredit(req.getInteger("credit"));
        course.setSchedule(req.getString("schedule"));
        course.setAssessmentType(req.getString("assessmentType"));
        System.out.println(course);
        Course courses = courseCenterService.saveCourse(course);
        System.out.println("保存成功"+courses);
        return DataResponse.success(courses);
    }

    // 更新课程
    @PostMapping("/update")
    public DataResponse updateCourse(@RequestBody DataRequest req) {
        Integer courseId = req.getInteger("courseId");
        Course existing = courseCenterService.getCourseById(courseId);
//        String name = req.getString("teacherName");
//        String num = req.getString("teacherNum");
//        if(teacherRepository.findByPersonNum(num) != null || teacherRepository.findByPersonName(name) != null || teacherRepository.findByPersonNum(num)!=teacherRepository.findByPersonName(name)) {
//            return DataResponse.error(0,"教工号与教师不匹配");
//        }
        if(existing != null) {

            existing.setCourseName(req.getString("courseName"));
            existing.setTeacher(teacherRepository.findByPersonNum(req.getString("teacherNum")));
            existing.setTeacherName(req.getString("teacherName"));
            existing.setLocation(req.getString("location"));
            existing.setCredit(req.getInteger("credit"));
            existing.setSchedule(req.getString("schedule"));
            existing.setAssessmentType(req.getString("assessmentType"));
            System.out.println("保存成功");
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

    @PostMapping("/searchCourses")
    public DataResponse searchCourses(@RequestBody DataRequest req) {

        return courseCenterService.searchCourses(req);
    }

    @PostMapping("/getMyCourses")
    public DataResponse getMyCourses(@RequestBody DataRequest req) {

        return courseCenterService.getMyCourses(req);
    }
}

