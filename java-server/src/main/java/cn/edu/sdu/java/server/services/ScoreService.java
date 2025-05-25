package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Score;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.payload.response.OptionItemList;
import cn.edu.sdu.java.server.repositorys.CourseCenterRepository;
import cn.edu.sdu.java.server.repositorys.ScoreRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ScoreService {
    private final CourseCenterRepository courseCenterRepository ;
    private final ScoreRepository scoreRepository;
    private final StudentRepository studentRepository;

    public ScoreService(CourseCenterRepository courseCenterRepository, ScoreRepository scoreRepository, StudentRepository studentRepository) {
        this.courseCenterRepository = courseCenterRepository;
        this.scoreRepository = scoreRepository;
        this.studentRepository = studentRepository;
    }
    public OptionItemList getStudentItemOptionList( DataRequest dataRequest) {
        List<Student> sList = studentRepository.findStudentListByNumName("");  //数据库查询操作
        List<OptionItem> itemList = new ArrayList<>();
        for (Student s : sList) {
            itemList.add(new OptionItem( s.getStudentId(),s.getStudentId()+"", s.getPerson().getNum()+"-"+s.getPerson().getName()));
        }
        return new OptionItemList(0, itemList);
    }

    public OptionItemList getCourseItemOptionList(DataRequest dataRequest) {
        List<Course> sList = courseCenterRepository.findAll();  //数据库查询操作
        List<OptionItem> itemList = new ArrayList<>();
        for (Course c : sList) {
            itemList.add(new OptionItem(c.getCourseId(),c.getCourseId()+"", c.getCourseId()+"-"+c.getCourseName()));
        }
        return new OptionItemList(0, itemList);
    }

    public DataResponse getScoreList(DataRequest dataRequest) {
        // 获取并检查必需参数：studentId 和 courseId，缺失时置为0表示忽略该限制
        Integer studentId = dataRequest.getInteger("studentId");
        if (studentId == null)
            studentId = 0;

        Integer courseId = dataRequest.getInteger("courseId");
        if (courseId == null)
            courseId = 0;

        // 获取模糊搜索条件
        String studentSearch = dataRequest.getString("studentSearch");
        String courseSearch = dataRequest.getString("courseSearch");

        // 如果为空，置为空字符串（或直接传null给查询方法，根据你Repository中的处理方式来定）
        if (studentSearch == null) {
            studentSearch = "";
        }
        if (courseSearch == null) {
            courseSearch = "";
        }

        // 调用 Repository 方法带上模糊搜索条件
        List<Score> sList = scoreRepository.findScoresByStudentIdAndCourseIdOptional(studentId, courseId, studentSearch, courseSearch);

        // 将查询结果转换为 List<Map<String, Object>> 返回给前端
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Score s : sList) {
            Map<String, Object> m = new HashMap<>();
            m.put("scoreId", s.getScoreId() + "");
            m.put("personId", s.getStudent().getStudentId() + "");
            m.put("courseId", s.getCourse().getCourseId() + "");
            m.put("studentNum", s.getStudent().getPerson().getNum());
            m.put("studentName", s.getStudent().getPerson().getName());
            m.put("className", s.getStudent().getClassName());
            m.put("courseName", s.getCourse().getCourseName());
            m.put("credit", "" + s.getCourse().getCredit());
            m.put("mark", "" + s.getMark());
            dataList.add(m);
        }

        return CommonMethod.getReturnData(dataList);
    }


    public DataResponse scoreSave(DataRequest dataRequest) {
        Integer mark = dataRequest.getInteger("mark");
        Integer scoreId = dataRequest.getInteger("scoreId");
        Optional<Score> op = scoreRepository.findById(scoreId);
        Score s ;
        if (op.isPresent()) {
            s = op.get();
            System.out.println("✅ 找到分数 ");
            s.setMark(mark);
        } else {
            System.out.println("❌ 分数不存在");
            return CommonMethod.getReturnMessageOK();
        }
        System.out.println(s);
        scoreRepository.save(s);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse scoreDelete(DataRequest dataRequest) {
        Integer scoreId = dataRequest.getInteger("scoreId");
        Optional<Score> op;
        Score s = null;
        if(scoreId != null) {
            op= scoreRepository.findById(scoreId);
            if(op.isPresent()) {
                s = op.get();
                scoreRepository.delete(s);
            }
        }
        return CommonMethod.getReturnMessageOK();
    }

    public void newScore(DataRequest req, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            System.out.println("❌ 用户未登录！");
            return;
        }
        if (!userDetails.getAuthorities().iterator().next().getAuthority().equals("ROLE_STUDENT")) {
            System.out.println(userDetails.getAuthorities().iterator().next().getAuthority());
            return;
        }
        String num = userDetails.getUsername(); // ✅ 获取当前登录的用户名
        System.out.println("🔍 当前用户 num: " + num);

        Score score = new Score();

        // 查找课程
        Optional<Course> coursesOptional = courseCenterRepository.findByCourseId(req.getInteger("courseId"));
        if (coursesOptional.isPresent()) {
            Course course = coursesOptional.get();
            System.out.println("✅ 找到课程: " + course.getCourseName());
            score.setCourse(course);
        } else {
            System.out.println("❌ 课程不存在");
            return;
        }

        // 查找当前学生
        Optional<Student> studentOptional = studentRepository.findByStudentNum(num);
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            score.setStudent(student);
            System.out.println("✅ 找到学生: " + student.getPerson().getName());
        } else {
            System.out.println("❌ 学生未找到");
            return;
        }
        score.setMark(0);
        score.setRanking(0);
        scoreRepository.save(score);
    }

    public DataResponse searchMyScores(DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        String searchText = dataRequest.getString("searchText");
        String courseName = dataRequest.getString("courseName");

        // 检查必须传入的学生ID
        if (studentId == null) {
            return CommonMethod.getReturnMessageError("缺少学生ID");
        }

        // 修剪参数
        if (searchText != null) {
            searchText = searchText.trim();
        }
        if (courseName != null) {
            courseName = courseName.trim();
        }

        List<Score> scores;

        // 如果两个搜索条件都没有提供，则查询当前学生的所有成绩
        if ((searchText == null || searchText.isEmpty()) && (courseName == null || courseName.isEmpty())) {
            scores = scoreRepository.findByStudentStudentId(studentId);
        } else {
            // 否则调用自定义查询方法，进行模糊搜索（注意：可根据实际业务需求调整模糊查询条件）
            scores = scoreRepository.findMyScoresByFilter(studentId, searchText, courseName);
        }

        System.out.println("查询到的成绩记录：" + scores);
        // 将 Score 列表转换为 List<Map<String, Object>>
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Score s : scores) {
            Map<String, Object> m = new HashMap<>();
            // 注意：这里使用的字段名称要和前端需要的字段保持一致，可根据实际情况进行调整
            m.put("scoreId", s.getScoreId());
            m.put("studentId", s.getStudent().getStudentId());
            m.put("studentNum", s.getStudent().getPerson().getNum());
            m.put("studentName", s.getStudent().getPerson().getName());
            m.put("className", s.getStudent().getClassName());
            m.put("courseId", s.getCourse().getCourseId());
            m.put("courseName", s.getCourse().getCourseName());
            m.put("credit", s.getCourse().getCredit());
            m.put("mark", s.getMark());
            m.put("ranking", s.getRanking());
            dataList.add(m);
        }

        System.out.println("转换后的成绩记录：" + dataList);
        return CommonMethod.getReturnData(dataList);
    }
}
