package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.CourseCenterRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
@Transactional
public class CourseCenterService {

    private final CourseCenterRepository courseCenterRepository;
    private final ScoreService scoreService;
    private final StudentRepository studentRepository;
    // 推荐使用构造器注入替代字段注入
    public CourseCenterService(CourseCenterRepository courseCenterRepository,ScoreService scoreService,StudentRepository studentRepository) {
        this.courseCenterRepository = courseCenterRepository;
        this.scoreService = scoreService;
        this.studentRepository = studentRepository;
    }

    // 获取所有课程（带空结果检查）
    public List<Course> getAllCourses() {
        List<Course> cours = courseCenterRepository.findAll();
        if (cours.isEmpty()) {
            throw new RuntimeException("未找到任何课程数据");
        }
        return cours;
    }

    // 根据ID获取课程（优化Optional使用）
    public Course getCourseById(Integer courseId) {
        Course c = courseCenterRepository.findByCourseId(courseId)
                .orElseThrow(() -> new RuntimeException("未找到ID为 " + courseId + " 的课程"));
        return c;
    }

    // 保存课程（增加数据校验）
    public Course saveCourse(Course course) {

        return courseCenterRepository.save(course);
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
        String username = userDetails.getUsername();
        // 调用选课方法，并传递当前用户信息
        // 根据用户名从学生表中查找学生记录。这里假设 studentNum 存储了登录用户名
        Optional<Student> studentOpt = studentRepository.findByStudentNum(username);
        if (!studentOpt.isPresent()) {
            return CommonMethod.getReturnMessageError("未找到对应学生");
        }
        Student student = studentOpt.get();

        // 根据课程ID从课程表中查找课程记录
        Optional<Course> courseOpt = courseCenterRepository.findByCourseId(courseId);
        if (!courseOpt.isPresent()) {
            return CommonMethod.getReturnMessageError("未找到对应课程");
        }
        Course course = courseOpt.get();

        // 维护双向关联（假设 Course 中有 addStudent 方法，同时内部会将 self 添加到 Student 的 courses 集合中）
        course.addStudent(student);

        // 保存更新课程（或学生），让关联关系持久化
        courseCenterRepository.save(course);
        scoreService.newScore(req, userDetails);
        return CommonMethod.getReturnMessageOK("选课成功！");
    }

    public DataResponse searchCourses(DataRequest req) {
        String keyword = req.getString("keyword"); // 获取搜索关键字
        List<Course> cours;

        if (keyword == null || keyword.trim().isEmpty()) {
            // 如果无关键字则返回所有记录
            cours = courseCenterRepository.findAll();
        } else {
            // 否则使用模糊搜索
            cours = courseCenterRepository.findByCourseNameContainingIgnoreCase(keyword);
        }
        // 此方法调用 CommonMethod.getReturnData() 封装返回，
        // 你可以根据实际情况对返回格式进行调整
        System.out.println(cours);
        return CommonMethod.getReturnData(cours);
    }

    public DataResponse getMyCourses(DataRequest req) {
        // 从请求中获取 studentId
        Integer studentId = req.getInteger("studentId");
        if (studentId == null) {
            return CommonMethod.getReturnMessageError("缺少学生ID");
        }

        // 根据 studentId 查询学生对象
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (!studentOpt.isPresent()) {
            return CommonMethod.getReturnMessageError("未找到指定的学生");
        }

        // 获取该学生关联的课程集合（会通过 courses_student 连接表返回相应课程）
        Student student = studentOpt.get();
        Set<Course> courses = student.getCourses();

        // 如果需要将 Set 转换为 List，可使用如下方式：
        List<Course> courseList = new ArrayList<>(courses);

        // 返回查询到的课程数据
        return CommonMethod.getReturnData(courseList);
    }
}



