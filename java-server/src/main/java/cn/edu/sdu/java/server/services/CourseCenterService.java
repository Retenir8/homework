package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Courses;
import cn.edu.sdu.java.server.models.Score;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.CourseCenterRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.edu.sdu.java.server.services.ScoreService ;
import java.util.List;


@Service
@Transactional
public class CourseCenterService {

    private final CourseCenterRepository courseCenterRepository;
    private final ScoreService scoreService;
    // 推荐使用构造器注入替代字段注入
    public CourseCenterService(CourseCenterRepository courseCenterRepository,ScoreService scoreService) {
        this.courseCenterRepository = courseCenterRepository;
        this.scoreService = scoreService;
    }

    // 获取所有课程（带空结果检查）
    public List<Courses> getAllCourses() {
        List<Courses> courses = courseCenterRepository.findAll();
        if (courses.isEmpty()) {
            throw new RuntimeException("未找到任何课程数据");
        }
        return courses;
    }

    // 根据ID获取课程（优化Optional使用）
    public Courses getCourseById(Integer courseId) {
        return courseCenterRepository.findById(String.valueOf(courseId))
                .orElseThrow(() -> new RuntimeException("未找到ID为 " + courseId + " 的课程"));
    }

    // 保存课程（增加数据校验）
    public Courses saveCourse(Courses course) {

        return courseCenterRepository.save(course);
    }

    // 更新课程（完整校验）
    public Courses updateCourse(Integer id, Courses courseDetails) {
        Courses existingCourse = getCourseById(id);

        // 更新前校验
        if (!existingCourse.getCourseId().equals(courseDetails.getCourseId()) &&
                courseCenterRepository.existsByCourseId(courseDetails.getCourseId())) {
            throw new RuntimeException("课程编号 " + courseDetails.getCourseId() + " 已存在");
        }

        existingCourse.setCourseId(courseDetails.getCourseId());
        existingCourse.setCourseName(courseDetails.getCourseName());
        existingCourse.setTeacher(courseDetails.getTeacher());
        existingCourse.setLocation(courseDetails.getLocation());
        existingCourse.setCredit(courseDetails.getCredit());
        existingCourse.setSchedule(courseDetails.getSchedule());
        existingCourse.setAssessmentType(courseDetails.getAssessmentType());

        return courseCenterRepository.save(existingCourse);
    }

    // 删除课程（增加存在性检查）
    public DataResponse deleteCourse(DataRequest req) {
        Integer courseId = req.getInteger("courseId");

        courseCenterRepository.deleteByCourseId(courseId);
        System.out.println("✅ 已成功删除课程: ID=" + courseId);
        return CommonMethod.getReturnMessageOK("请假记录删除成功！");

    }

    public DataResponse chooseCourse(DataRequest req) {
        Integer courseId = req.getInteger("courseId");
        //绑定学生和课程


        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return CommonMethod.getReturnMessageError("❌ 用户未登录！");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        System.out.println("🔍 当前用户: " + userDetails.getUsername());

        // 调用选课方法，并传递当前用户信息
        scoreService.newScore(req, userDetails);
        return CommonMethod.getReturnMessageOK("选课成功！");
    }
}



