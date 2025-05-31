package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.*;
import java.util.List;

@Service
public class TeacherService {
    private static final Logger log = LoggerFactory.getLogger(TeacherService.class);
    private final PersonRepository personRepository;  //人员数据操作自动注入
    private final TeacherRepository teacherRepository;  //学生数据操作自动注入
    private final UserRepository userRepository;  //学生数据操作自动注入
    private final UserTypeRepository userTypeRepository; //用户类型数据操作自动注入
    private final PasswordEncoder encoder;  //密码服务自动注入
    @Autowired
    public TeacherService(PersonRepository personRepository, TeacherRepository teacherRepository, UserRepository userRepository, UserTypeRepository userTypeRepository, PasswordEncoder encoder) {
        this.personRepository = personRepository;
        this.teacherRepository = teacherRepository;
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.encoder = encoder;

    }

    public DataResponse getTeacherByNum(DataRequest dataRequest) {
        String teacherNum = dataRequest.getString("teacherNum");
        Optional<Teacher> teacherOptional = teacherRepository.findByTeacherNum(teacherNum);

        if (teacherOptional.isEmpty()) {
            return CommonMethod.getReturnMessageError("未找到该学号对应的学生！");
        }

        Teacher teacher = teacherOptional.get();
        Map<String, Object> teacherData = new HashMap<>();
        teacherData.put("name", teacher.getPerson().getName());
        return CommonMethod.getReturnData(teacherData);
    }



    public List<OptionItem> getAllTeachers() {
        List<Teacher> teachers = teacherRepository.findAllWithPerson();

        if (teachers.isEmpty()) {
            System.out.println("数据库错误: 没有找到学生信息！");
        }

        List<OptionItem> teacherOptions = new ArrayList<>();
        for (Teacher teacher : teachers) {
            teacherOptions.add(new OptionItem(
                    teacher.getTeacherId(),
                    teacher.getPerson().getPersonId().toString(),
                    teacher.getPerson().getName()
            ));

        }

        System.out.println("返回的学生选项: " + teacherOptions);
        return teacherOptions;
    }


    public Map<String, Object> getMapFromTeacher(Teacher s) {
        Map<String, Object> m = new HashMap<>();

        // 默认值：确保所有键都存在，即使初始为 null
        m.put("teacherId", "");
        m.put("personId", "");
        m.put("num", "");
        m.put("name", "");
        m.put("title", "");
        m.put("degree", "");
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
            // 先填充 Teacher 自身的属性
            m.put("teacherId", s.getTeacherId() != null ? s.getTeacherId().toString() : ""); // ID也转为String
            m.put("title", s.getTitle() != null ? s.getTitle() : "");
            m.put("degree", s.getDegree() != null ? s.getDegree() : "");

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

    public List<Map<String,Object>> getTeacherMapList(String numName) {
        List<Map<String,Object>> dataList = new ArrayList<>();
        List<Teacher> sList = teacherRepository.findTeacherListByNumName(numName);  //数据库查询操作
        if (sList == null || sList.isEmpty())
            return dataList;
        for (Teacher teacher : sList) {
            dataList.add(getMapFromTeacher(teacher));
        }
        return dataList;
    }

    public DataResponse getTeacherList(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String,Object>> dataList = getTeacherMapList(numName);
        return CommonMethod.getReturnData(dataList);  //按照测试框架规范会送Map的list
    }



    public DataResponse teacherDelete(DataRequest dataRequest) {
        Integer personId ;//获取teacher_id值
        Integer teacherId = dataRequest.getInteger("teacherId");
        Teacher s = null;
        Optional<Teacher> op;
        if (teacherId != null && teacherId > 0) {
            op = teacherRepository.findById(teacherId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
                personId = teacherRepository.findById(teacherId).get().getPerson().getPersonId();
                Optional<User> uOp = userRepository.findByPersonPersonId(personId); //查询对应该学生的账户
                //删除对应该学生的账户
                uOp.ifPresent(userRepository::delete);
                teacherRepository.delete(s);    //首先数据库永久删除学生信息
                Person p = s.getPerson();
                personRepository.delete(p);   // 然后数据库永久删除学生信息
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    public DataResponse getTeacherInfo(DataRequest dataRequest) {
        String num = dataRequest.getString("num");
        Teacher s = null;
        if (num != null) {
            s = teacherRepository.findByPersonNum(num);
        }
        return CommonMethod.getReturnData(getMapFromTeacher(s));
    }

    @Transactional
    public DataResponse teacherEditSave(DataRequest dataRequest) {
        // 获取前端提交的表单数据
        Map<String, Object> form = dataRequest.getMap("form");
        if (form == null) {
            return CommonMethod.getReturnMessageError("请求数据为空！");
        }

        // 从表单中获取 teacherId 和学号（num）
        Integer teacherId = CommonMethod.getInteger(form, "teacherId");
        String num = CommonMethod.getString(form, "num");
        if (num == null || num.trim().isEmpty()) {
            return CommonMethod.getReturnMessageError("学号不能为空！");
        }

        Teacher teacher = null;
        Person person = null;
        User user = null;

        // 如果 teacherId 有值则认为是修改，否则是新增
        if (teacherId != null) {
            Optional<Teacher> teacherOpt = teacherRepository.findById(teacherId);
            if (teacherOpt.isPresent()) {
                teacher = teacherOpt.get();
                person = teacher.getPerson();
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
        if (teacher == null) {
            person = new Person();
            person.setNum(num);
            person.setType("2");
            personRepository.save(person);

            user = new User();
            user.setPerson(person);
            user.setUserName(num);
            user.setPassword(encoder.encode("123456")); // 默认密码“123456”
            UserType userType = userTypeRepository.findByName("ROLE_TEACHER");
            if (userType == null) {
                return CommonMethod.getReturnMessageError("系统配置错误：用户类型 'ROLE_TEACHER' 未找到。");
            }
            user.setUserType(userType);
            userRepository.save(user);

            teacher = new Teacher();
            teacher.setPerson(person);
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

        // 更新 Teacher 的属性
        teacher.setDegree(CommonMethod.getString(form, "degree"));
        teacher.setTitle(CommonMethod.getString(form, "title"));
        teacherRepository.save(teacher);

        return CommonMethod.getReturnData(teacher.getTeacherId());
    }


    public ResponseEntity<StreamingResponseBody> getTeacherListExcl(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        List<Map<String,Object>> list = getTeacherMapList(numName);
        Integer[] widths = {8, 20, 10, 15, 15, 15, 25, 10, 15, 30, 20, 30};
        int i, j, k;
        String[] titles = {"序号", "教工号", "姓名", "学院", "职称", "学历", "证件号码", "性别", "出生日期", "邮箱", "电话", "地址"};
        String outPutSheetName = "teacher.xlsx";
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
                cell[4].setCellValue(CommonMethod.getString(m, "title"));
                cell[5].setCellValue(CommonMethod.getString(m, "degree"));
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

    public DataResponse getTeacherPageData(DataRequest dataRequest) {
        String numName = dataRequest.getString("numName");
        Integer cPage = dataRequest.getCurrentPage();
        int dataTotal = 0;
        int size = 40;
        List<Map<String,Object>> dataList = new ArrayList<>();
        Page<Teacher> page = null;
        Pageable pageable = PageRequest.of(cPage, size);
        page = teacherRepository.findTeacherPageByNumName(numName, pageable);
        Map<String,Object> m;
        if (page != null) {
            dataTotal = (int) page.getTotalElements();
            List<Teacher> list = page.getContent();
            if (!list.isEmpty()) {
                for (Teacher teacher : list) {
                    m = getMapFromTeacher(teacher);
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


}
