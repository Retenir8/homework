package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Course;
import cn.edu.sdu.java.server.models.Courses;
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
        List<Courses> sList = courseCenterRepository.findAll();  //数据库查询操作
        List<OptionItem> itemList = new ArrayList<>();
        for (Courses c : sList) {
            itemList.add(new OptionItem(c.getCourseId(),c.getCourseId()+"", c.getCourseId()+"-"+c.getCourseName()));
        }
        return new OptionItemList(0, itemList);
    }

    public DataResponse getScoreList(DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        if(studentId == null)
            studentId = 0;
        Integer courseId = dataRequest.getInteger("courseId");
        if(courseId == null)
            courseId = 0;
        List<Score> sList = scoreRepository.findScoresByStudentIdAndCourseIdOptional(studentId, courseId);  //数据库查询操作
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        for (Score s : sList) {
            m = new HashMap<>();
            m.put("scoreId", s.getScoreId()+"");
            m.put("personId",s.getStudent().getStudentId()+"");
            m.put("courseId",s.getCourse().getCourseId()+"");
            m.put("studentNum",s.getStudent().getPerson().getNum());
            m.put("studentName",s.getStudent().getPerson().getName());
            m.put("className",s.getStudent().getClassName());
            m.put("courseId",s.getCourse().getCourseId());
            m.put("courseName",s.getCourse().getCourseName());
            m.put("credit",""+s.getCourse().getCredit());
            m.put("mark",""+s.getMark());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
    public DataResponse scoreSave(DataRequest dataRequest) {
        Integer mark = dataRequest.getInteger("mark");
        Integer scoreId = dataRequest.getInteger("scoreId");
        Optional<Score> op = scoreRepository.findById(scoreId);
        Score s = null;
        if (op.isPresent()) {
            Score score = op.get();
            System.out.println("✅ 找到分数 ");
            score.setMark(mark);
        } else {
            System.out.println("❌ 分数不存在");
            return CommonMethod.getReturnMessageOK();
        }
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

        String num = userDetails.getUsername(); // ✅ 获取当前登录的用户名
        System.out.println("🔍 当前用户 num: " + num);

        Score score = new Score();

        // 查找课程
        Optional<Courses> coursesOptional = courseCenterRepository.findByCourseId(req.getInteger("courseId"));
        if (coursesOptional.isPresent()) {
            Courses course = coursesOptional.get();
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
    }
}
