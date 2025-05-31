package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@Service
public class StudentService {
    private static final Logger log = LoggerFactory.getLogger(StudentService.class);

    // 账户和人员相关
    private final PersonRepository personRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final PasswordEncoder encoder;
    private final FeeRepository feeRepository;
    // 课程选课相关（学生与课程间的中间表）
    private final StudentCourseRepository studentCourseRepository;
    // 其它业务模块相关（删除学生时需要删除关联的记录）
    private final LeaveInfoRepository leaveInfoRepository;
    private final ScoreRepository scoreRepository;
    private final ScheduleRepository scheduleRepository;

    @Autowired
    public StudentService(PersonRepository personRepository,
                          StudentRepository studentRepository,
                          UserRepository userRepository,
                          UserTypeRepository userTypeRepository,
                          PasswordEncoder encoder,
                          FeeRepository feeRepository,
                          StudentCourseRepository studentCourseRepository,
                          LeaveInfoRepository leaveInfoRepository,
                          ScoreRepository scoreRepository,
                          ScheduleRepository scheduleRepository) {
        this.personRepository = personRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.encoder = encoder;
        this.feeRepository = feeRepository;
        this.studentCourseRepository = studentCourseRepository;
        this.leaveInfoRepository = leaveInfoRepository;
        this.scoreRepository = scoreRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public DataResponse getStudentByNum(DataRequest dataRequest) {
        String studentNum = dataRequest.getString("studentNum");
        Optional<Student> studentOptional = studentRepository.findByStudentNum(studentNum);

        if (studentOptional.isEmpty()) {
            return CommonMethod.getReturnMessageError("未找到该学号对应的学生！");
        }

        Student student = studentOptional.get();
        Map<String, Object> studentData = new HashMap<>();
        studentData.put("name", student.getPerson().getName());
        return CommonMethod.getReturnData(studentData);
    }



    public List<OptionItem> getAllStudents() {
        List<Student> students = studentRepository.findAllWithPerson();

        if (students.isEmpty()) {
            System.out.println("数据库错误: 没有找到学生信息！");
        }

        List<OptionItem> studentOptions = new ArrayList<>();
        for (Student student : students) {
            studentOptions.add(new OptionItem(
                    student.getStudentId(),
                    student.getPerson().getPersonId().toString(),
                    student.getPerson().getName()
            ));

        }

        System.out.println("返回的学生选项: " + studentOptions);
        return studentOptions;
    }





    public Map<String, Object> getMapFromStudent(Student s) {
        Map<String, Object> m = new HashMap<>();
        // 默认值：确保所有键都存在，即使初始为 null
        m.put("studentId", "");
        m.put("personId", "");
        m.put("num", "");
        m.put("name", "");
        m.put("major", "");
        m.put("className", "");
        m.put("card", "");
        m.put("gender", "");
        m.put("genderName", "");
        m.put("birthday", ""); // 默认空字符串，等待格式化
        m.put("email", "");
        m.put("phone", "");
        m.put("address", "");
        m.put("introduce", "");
        m.put("dept", ""); // Person的dept

        if (s != null) {
            // 先填充 Student 自身的属性
            m.put("studentId", s.getStudentId() != null ? s.getStudentId().toString() : ""); // ID也转为String
            m.put("major", s.getMajor() != null ? s.getMajor() : "");
            m.put("className", s.getClassName() != null ? s.getClassName() : "");

            Person p = s.getPerson();
            System.out.println(p.getName());
            // 填充 Person 的属性
            m.put("personId", p.getPersonId() != null ? p.getPersonId().toString() : ""); // ID转为String
            m.put("num", p.getNum() != null ? p.getNum() : "");
            m.put("name", p.getName() != null ? p.getName() : "");
            m.put("dept", p.getDept() != null ? p.getDept() : ""); // 假设 Person 也有 dept
            m.put("card", p.getCard() != null ? p.getCard() : "");

            String gender = p.getGender();
            m.put("gender", gender != null ? gender : "");
            m.put("genderName", (gender != null && ComDataUtil.getInstance() != null) ?
                    ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender) : "");

            m.put("birthday", p.getBirthday());
            m.put("email", p.getEmail() != null ? p.getEmail() : "");
            m.put("phone", p.getPhone() != null ? p.getPhone() : "");
            m.put("address", p.getAddress() != null ? p.getAddress() : "");
            m.put("introduce", p.getIntroduce() != null ? p.getIntroduce() : "");
        }
        System.out.println(m);
        return m;
    }

    public List<Map<String,Object>> getStudentMapList(String numName) {
        List<Map<String,Object>> dataList = new ArrayList<>();
        List<Student> sList = studentRepository.findStudentListByNumName(numName);  //数据库查询操作
        if (sList == null || sList.isEmpty())
            return dataList;
        for (Student student : sList) {
            dataList.add(getMapFromStudent(student));
        }
        return dataList;
    }

    public DataResponse getStudentList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String,Object>> dataList = getStudentMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }



    public DataResponse studentDelete(DataRequest dataRequest) {
        Integer personId ;//获取student_id值
        Integer studentId = dataRequest.getInteger("studentId");
        System.out.println(studentId);
        Student s = null;
        Optional<Student> op;
        if (studentId != null && studentId > 0) {

            op = studentRepository.findById(studentId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
                // 1. 删除选课记录（中间表数据）
                studentCourseRepository.deleteByStudent_StudentId(studentId);

                // 2. 删除请假记录
                leaveInfoRepository.deleteByStudentStudentId(studentId);

                // 3. 删除成绩记录
                scoreRepository.deleteByStudentStudentId(studentId);

                // 4. 删除荣誉关联（中间表），即只清空学生实体中保存的荣誉集合
                if (s.getHonors() != null) {
                    s.getHonors().clear();
                }

                // 5. 删除活动关联（中间表），清空学生实体中保存的活动集合
                if (s.getActivities() != null) {
                    s.getActivities().clear();
                }

                // 6. 删除课程表记录
                scheduleRepository.deleteByStudentId(s.getStudentId());

                personId = studentRepository.findById(studentId).get().getPerson().getPersonId();
                Optional<User> uOp = userRepository.findByPersonPersonId(personId); //查询对应该学生的账户
                //删除对应该学生的账户
                uOp.ifPresent(userRepository::delete);
                studentRepository.delete(s);    //首先数据库永久删除学生信息
                Person p = s.getPerson();
                personRepository.delete(p);   // 然后数据库永久删除学生信息
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    public DataResponse getStudentInfo(DataRequest dataRequest) {
        String num = dataRequest.getString("num");
        Student s = null;
        Optional<Student> op;
        if (num != null) {
            op = studentRepository.findByPersonNum(num); //根据学生主键从数据库查询学生的信息
            if (op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromStudent(s)); //这里回传包含学生信息的Map对象
    }

    @Transactional
    public DataResponse studentEditSave(DataRequest dataRequest) {
        // 获取前端提交的表单数据
        Map<String, Object> form = dataRequest.getMap("form");
        if (form == null) {
            return CommonMethod.getReturnMessageError("请求数据为空！");
        }

        // 从表单中获取 studentId 和学号（num）
        Integer studentId = CommonMethod.getInteger(form, "studentId");
        String num = CommonMethod.getString(form, "num");
        if (num == null || num.trim().isEmpty()) {
            return CommonMethod.getReturnMessageError("学号不能为空！");
        }

        Student student = null;
        Person person = null;
        User user = null;

        // 如果 studentId 有值则认为是修改，否则是新增
        if (studentId != null) {
            Optional<Student> studentOpt = studentRepository.findById(studentId);
            if (studentOpt.isPresent()) {
                student = studentOpt.get();
                person = student.getPerson();
            } else {
                return CommonMethod.getReturnMessageError("学生信息不存在，无法修改！");
            }
        }

        // 检查学号是否已被其他人占用
        Optional<Person> existPersonOpt = personRepository.findByNum(num);
        if (existPersonOpt.isPresent()) {
            Person existPerson = existPersonOpt.get();
            if (person == null || !existPerson.getPersonId().equals(person.getPersonId())) {
                return CommonMethod.getReturnMessageError("该学号已被其他用户占用，不能添加或修改！");
            }
        }

        // 新增学生逻辑
        if (student == null) {
            person = new Person();
            person.setNum(num);
            person.setType("1");
            personRepository.save(person);

            user = new User();
            user.setPerson(person);
            user.setUserName(num);
            user.setPassword(encoder.encode("123456")); // 默认密码“123456”
            UserType userType = userTypeRepository.findByName("ROLE_STUDENT");
            if (userType == null) {
                return CommonMethod.getReturnMessageError("系统配置错误：用户类型 'ROLE_STUDENT' 未找到。");
            }
            user.setUserType(userType);
            userRepository.save(user);

            student = new Student();
            student.setPerson(person);
        } else {
            // 修改逻辑：若学号发生变化，则更新关联的 User 的用户名，同时更新 Person 中的 num
            if (!num.equals(person.getNum())) {
                Optional<User> userOpt = userRepository.findByPersonPersonId(person.getPersonId());
                if (userOpt.isPresent()) {
                    user = userOpt.get();
                    user.setUserName(num);
                    userRepository.save(user);
                }
                person.setNum(num);
            }
        }

        // 更新 Person 的其它属性
        person.setName(CommonMethod.getString(form, "name"));
        person.setDept(CommonMethod.getString(form, "dept"));
        person.setCard(CommonMethod.getString(form, "card"));
        person.setGender(CommonMethod.getString(form, "gender"));
        person.setBirthday(CommonMethod.getString(form, "birthday"));
        person.setEmail(CommonMethod.getString(form, "email"));
        person.setPhone(CommonMethod.getString(form, "phone"));
        person.setAddress(CommonMethod.getString(form, "address"));
        personRepository.save(person);

        // 更新 Student 的属性
        student.setMajor(CommonMethod.getString(form, "major"));
        student.setClassName(CommonMethod.getString(form, "className"));
        studentRepository.save(student);

        return CommonMethod.getReturnData(student.getStudentId());
    }


    public List<Map<String,Object>> getStudentScoreList(List<Score> sList) {
        List<Map<String,Object>> list = new ArrayList<>();
        if (sList == null || sList.isEmpty())
            return list;
        Map<String,Object> m;
        Course c;
        for (Score s : sList) {
            m = new HashMap<>();
            c = s.getCourse();
            m.put("studentNum", s.getStudent().getPerson().getNum());
            m.put("scoreId", s.getScoreId());
            m.put("courseId", c.getCourseId());
            m.put("courseName", c.getCourseName());
            m.put("credit", c.getCredit());
            m.put("mark", s.getMark());
            m.put("ranking", s.getRanking());
            list.add(m);
        }
        return list;
    }



    public String importFeeData(Integer personId, InputStream in){
        try {
            Student student = studentRepository.findById(personId).get();
            XSSFWorkbook workbook = new XSSFWorkbook(in);  //打开Excl数据流
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            Row row;
            Cell cell;
            int i;
            i = 1;
            String day, money;
            Optional<Fee> fOp;
            double dMoney;
            Fee f;
            rowIterator.next();
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                cell = row.getCell(0);
                if (cell == null)
                    break;
                day = cell.getStringCellValue();  //获取一行消费记录 日期 金额
                cell = row.getCell(1);
                money = cell.getStringCellValue();
                fOp = feeRepository.findByStudentStudentIdAndDay(personId, day);  //查询是否存在记录
                if (fOp.isEmpty()) {
                    f = new Fee();
                    f.setDay(day);
                    f.setStudent(student);  //不存在 添加
                } else {
                    f = fOp.get();  //存在 更新
                }
                if (money != null && !money.isEmpty())
                    dMoney = Double.parseDouble(money);
                else
                    dMoney = 0d;
                f.setMoney(dMoney);
                feeRepository.save(f);
            }
            workbook.close();  //关闭Excl输入流
            return null;
        } catch (Exception e) {
            log.error(e.getMessage());
            return "上传错误！";
        }

    }

    public DataResponse importFeeData(@RequestBody byte[] barr,
                                      String personIdStr
                                      ) {
        Integer personId =  Integer.parseInt(personIdStr);
        String msg = importFeeData(personId,new ByteArrayInputStream(barr));
        if(msg == null)
            return CommonMethod.getReturnMessageOK();
        else
            return CommonMethod.getReturnMessageError(msg);
    }

    public ResponseEntity<StreamingResponseBody> getStudentListExcl( DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String,Object>> list = getStudentMapList(numName);
        Integer[] widths = {8, 20, 10, 15, 15, 15, 25, 10, 15, 30, 20, 30};
        int i, j, k;
        String[] titles = {"序号", "学号", "姓名", "学院", "专业", "班级", "证件号码", "性别", "出生日期", "邮箱", "电话", "地址"};
        String outPutSheetName = "student.xlsx";
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFCellStyle styleTitle = CommonMethod.createCellStyle(wb, 20);
        XSSFSheet sheet = wb.createSheet(outPutSheetName);
        for (j = 0; j < widths.length; j++) {
            sheet.setColumnWidth(j, widths[j] * 256);
        }
        //合并第一行
        XSSFCellStyle style = CommonMethod.createCellStyle(wb, 11);
        XSSFRow row = null;
        XSSFCell[] cell = new XSSFCell[widths.length];
        row = sheet.createRow((int) 0);
        for (j = 0; j < widths.length; j++) {
            cell[j] = row.createCell(j);
            cell[j].setCellStyle(style);
            cell[j].setCellValue(titles[j]);
            cell[j].getCellStyle();
        }
        Map<String,Object> m;
        if (list != null && !list.isEmpty()) {
            for (i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 1);
                for (j = 0; j < widths.length; j++) {
                    cell[j] = row.createCell(j);
                    cell[j].setCellStyle(style);
                }
                m = list.get(i);
                cell[0].setCellValue((i + 1) + "");
                cell[1].setCellValue(CommonMethod.getString(m, "num"));
                cell[2].setCellValue(CommonMethod.getString(m, "name"));
                cell[3].setCellValue(CommonMethod.getString(m, "dept"));
                cell[4].setCellValue(CommonMethod.getString(m, "major"));
                cell[5].setCellValue(CommonMethod.getString(m, "className"));
                cell[6].setCellValue(CommonMethod.getString(m, "card"));
                cell[7].setCellValue(CommonMethod.getString(m, "genderName"));
                cell[8].setCellValue(CommonMethod.getString(m, "birthday"));
                cell[9].setCellValue(CommonMethod.getString(m, "email"));
                cell[10].setCellValue(CommonMethod.getString(m, "phone"));
                cell[11].setCellValue(CommonMethod.getString(m, "address"));
            }
        }
        try {
            StreamingResponseBody stream = wb::write;
            return ResponseEntity.ok()
                    .contentType(CommonMethod.exelType)
                    .body(stream);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    public DataResponse getStudentPageData(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        Integer cPage = dataRequest.getCurrentPage();
        int dataTotal = 0;
        int size = 40;
        List<Map<String,Object>> dataList = new ArrayList<>();
        Page<Student> page = null;
        Pageable pageable = PageRequest.of(cPage, size);
        page = studentRepository.findStudentPageByNumName(numName, pageable);
        Map<String,Object> m;
        if (page != null) {
            dataTotal = (int) page.getTotalElements();
            List<Student> list = page.getContent();
            if (!list.isEmpty()) {
                for (Student student : list) {
                    m = getMapFromStudent(student);
                    dataList.add(m);
                }
            }
        }
        Map<String,Object> data = new HashMap<>();
        data.put("dataTotal", dataTotal);
        data.put("pageSize", size);
        data.put("dataList", dataList);
        return CommonMethod.getReturnData(data);
    }


    public DataResponse getMyStudentId(String userName) {
        Integer studentId = studentRepository.findByStudentNum(userName).get().getStudentId();
        String studentNum = studentRepository.findByStudentNum(userName).get().getPerson().getNum();
        System.out.println(studentId);
        Map<String, Object> result = new HashMap<>();
        result.put("studentId", studentId);
        result.put("studentNum", studentNum);
        return CommonMethod.getReturnData(result);
    }
}
