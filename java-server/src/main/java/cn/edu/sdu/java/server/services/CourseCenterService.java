package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.models.StudentCourse;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.CourseCenterRepository;
import cn.edu.sdu.java.server.repositorys.StudentCourseRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
public class CourseCenterService {

    private final CourseCenterRepository courseCenterRepository;
    private final ScoreService scoreService;
    private final StudentRepository studentRepository;
    private final StudentCourseRepository studentCourseRepository;
    // 推荐使用构造器注入替代字段注入
    public CourseCenterService(CourseCenterRepository courseCenterRepository,ScoreService scoreService,StudentRepository studentRepository,StudentCourseRepository studentCourseRepository) {
        this.courseCenterRepository = courseCenterRepository;
        this.scoreService = scoreService;
        this.studentRepository = studentRepository;
        this.studentCourseRepository = studentCourseRepository;
    }

    // 获取所有课程（带空结果检查）
    public List<Course> getAllCourses() {
        List<Course> courses = courseCenterRepository.findAll();
        if (courses.isEmpty()) {
            throw new RuntimeException("未找到任何课程数据");
        }
        return courses;
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


    @Transactional
    public DataResponse deleteCourse(DataRequest dataRequest) {
        Integer courseId = dataRequest.getInteger("courseId");
        if (courseId == null) {
            return CommonMethod.getReturnMessageError("缺少课程ID");
        }

        // 检查课程是否存在，以提供更友好的错误信息
        Optional<Course> courseOpt = courseCenterRepository.findByCourseId(courseId);
        if (!courseOpt.isPresent()) {
            return CommonMethod.getReturnMessageError("❌ 课程不存在，无法删除！");
        }

        Course courseToDelete = courseOpt.get();

        // 先删除所有选课记录
        // 此处使用 Spring Data JPA 的属性查询语法，根据 Course 对象关联的 courseId 删除所有中间记录
        studentCourseRepository.deleteByCourse_CourseId(courseId);

        // 删除课程记录
        courseCenterRepository.delete(courseToDelete);

        return CommonMethod.getReturnMessageOK("删除成功！");
    }


    @Transactional  // 确保整个操作在一个事务中
    public DataResponse chooseCourse(DataRequest req) {
        // 获取课程ID参数，并检查
        Integer courseId = req.getInteger("courseId");
        if (courseId == null) {
            return CommonMethod.getReturnMessageError("缺少课程ID");
        }

        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return CommonMethod.getReturnMessageError("❌ 用户未登录！");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        System.out.println("🔍 当前用户: " + userDetails.getUsername());
        String username = userDetails.getUsername();

        Optional<Student> studentOpt = studentRepository.findByPersonNum(username);
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

        // 防止重复选课：根据 Student 和 Course 实体查询中间选课记录
        Optional<StudentCourse> existing = studentCourseRepository.findByStudentAndCourse(student, course);
        if (existing.isPresent()) {
            return CommonMethod.getReturnMessageError("你已选过该课程！");
        }

        try {
            // 创建 StudentCourse 实体，并绑定学生与课程
            StudentCourse studentCourse = new StudentCourse();
            studentCourse.setStudent(student);  // 传递整个学生实体
            studentCourse.setCourse(course);    // 传递整个课程实体


            if (!student.getCourses().contains(course)) {
                student.getCourses().add(course);
            }
            if (!course.getStudents().contains(student)) {
                course.getStudents().add(student);
            }

            // 保存新的选课记录
            studentCourseRepository.save(studentCourse);

            // 如有需要，同时创建成绩记录（具体逻辑由 scoreService.newScore 实现）
            scoreService.newScore(req, userDetails);

            return CommonMethod.getReturnMessageOK("选课成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return CommonMethod.getReturnMessageError("选课过程中出现异常：" + e.getMessage());
        }
    }


    public DataResponse searchCourses(DataRequest req) {
        String keyword = req.getString("keyword"); // 获取搜索关键字
        List<Course> courses;

        if (keyword == null || keyword.trim().isEmpty()) {
            // 如果无关键字则返回所有记录
            courses = courseCenterRepository.findAll();
        } else {
            // 否则使用模糊搜索
            courses = courseCenterRepository.findByCourseNameContainingIgnoreCase(keyword);
        }
        System.out.println(courses);
        return CommonMethod.getReturnData(courses);
    }

    @Transactional(readOnly = true)
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
        Student student = studentOpt.get();

        // 通过 studentCourseRepository 查询该学生的中间选课记录
        List<StudentCourse> enrollmentList = studentCourseRepository.findByStudent(student);

        // 遍历每个 StudentCourse 记录，提取出对应关联的 Course 实体
        Set<Course> courses = enrollmentList.stream()
                .map(StudentCourse::getCourse)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 将 Set 转换为 List（如果前端需要列表形式）
        List<Course> courseList = new ArrayList<>(courses);
        System.out.println("当前选课数量：" + courseList.size());

        return CommonMethod.getReturnData(courseList);
    }



    public void updateTimeSlots(Integer courseId, String timeSlots) {
        if (timeSlots == null || timeSlots.length() != 35) {
            throw new IllegalArgumentException("timeSlots 参数必须为 35 位字符串");
        }
        Optional<Course> opt = courseCenterRepository.findByCourseId(courseId);
        if (!opt.isPresent()) {
            throw new RuntimeException("课程不存在，courseId = " + courseId);
        }
        Course course = opt.get();

        try {
            // 遍历1到35，动态设置 Course 实体中的 c1 ~ c35 字段
            for (int i = 1; i <= 35; i++) {
                String fieldName = "c" + i;
                java.lang.reflect.Field field = Course.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                // 将对应位置的字符转换成字符串后设置字段值
                field.set(course, String.valueOf(timeSlots.charAt(i - 1)));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("更新课程时间位置信息失败", e);
        }
        course.setTimeSlots(timeSlots);
        courseCenterRepository.save(course);
    }

}



