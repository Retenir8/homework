package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.*;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.payload.response.OptionItem;
import cn.edu.sdu.java.server.repositorys.*;
import cn.edu.sdu.java.server.util.ComDataUtil;
import cn.edu.sdu.java.server.util.CommonMethod;
import cn.edu.sdu.java.server.util.DateTimeTool;
import jakarta.transaction.Transactional;
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





    public Map<String,Object> getMapFromTeacher(Teacher s) {
        Map<String,Object> m = new HashMap<>();
        Person p;
        if(s == null)
            return m;
        m.put("title",s.getTitle());
        m.put("degree",s.getDegree());
        p = s.getPerson();
        if(p == null)
            return m;
        m.put("teacherId", s.getTeacherId());
        m.put("personId", p.getPersonId());
        m.put("num",p.getNum());
        m.put("name",p.getName());
        m.put("dept",p.getDept());
        m.put("card",p.getCard());
        String gender = p.getGender();
        m.put("gender",gender);
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
        m.put("birthday", p.getBirthday());  //时间格式转换字符串
        m.put("email",p.getEmail());
        m.put("phone",p.getPhone());
        m.put("address",p.getAddress());
        m.put("introduce",p.getIntroduce());
        return m;
    }

    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， TeacherController 中要使用TeacherRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， teacherRepository 相当于TeacherRepository接口实现对象的一个引用，由框架完成对这个引用的赋值，
    // TeacherController中的方法可以直接使用

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
        Integer personId = dataRequest.getInteger("personId");//获取teacher_id值
        Integer teacherId = dataRequest.getInteger("teacherId");
        Teacher s = null;
        Optional<Teacher> op;
        if (teacherId != null && teacherId > 0) {
            op = teacherRepository.findById(teacherId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
                Optional<User> uOp = userRepository.findById(personId); //查询对应该学生的账户
                //删除对应该学生的账户
                uOp.ifPresent(userRepository::delete);
                Person p = s.getPerson();
                teacherRepository.delete(s);    //首先数据库永久删除学生信息
                personRepository.delete(p);   // 然后数据库永久删除学生信息
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    public DataResponse getTeacherInfo(DataRequest dataRequest) {
        Integer teacherId = dataRequest.getInteger("teacherId");
        Teacher s = null;
        Optional<Teacher> op;
        if (teacherId != null) {
            op = teacherRepository.findById(teacherId); //根据学生主键从数据库查询学生的信息
            if (op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromTeacher(s)); //这里回传包含学生信息的Map对象
    }

    @Transactional // 确保事务一致性
    public DataResponse teacherEditSave(DataRequest dataRequest) {
        System.out.println("--- Entering teacherEditSave method ---");
        System.out.println("Received dataRequest: " + dataRequest.getData());

        // 从请求中获取 teacherId 和 form 数据
        Map<String, Object> form = dataRequest.getMap("form"); // 参数获取Map对象
        if (form == null) {
            return CommonMethod.getReturnMessageError("请求数据为空！");
        }
        // 确保从 form 中正确获取 teacherId
        Integer teacherId = CommonMethod.getInteger(form, "teacherId"); // 使用 CommonMethod 或直接从 Map 中获取

        System.out.println("Form data: " + form); // 打印 form 数据
        System.out.println("Extracted teacherId from form: " + teacherId); // 打印提取到的 teacherId

        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        if (num == null || num.trim().isEmpty()) {
            return CommonMethod.getReturnMessageError("学号不能为空！"); // 学号为空校验
        }

        Teacher s = null; // 学生实体
        Person p = null;  // 人员实体
        User u = null;    // 用户实体

        Integer personId = CommonMethod.getInteger(form, "personId"); // 从 form 中获取 personId

        // 现在这里的逻辑应该是正确的，因为 teacherId 应该有正确的值了
        if (teacherId != null) {
            Optional<Teacher> op = teacherRepository.findById(teacherId);
            if (op.isPresent()) {
                s = op.get();
                p = s.getPerson(); // 获取关联的 Person
                personId = p.getPersonId(); // 确保 personId 是从数据库获取的最新值
                // --- 调试点 B ---
                System.out.println("Existing teacher found. teacherId=" + teacherId + ", personId=" + personId + ", num=" + p.getNum());
            } else {
                System.err.println("Error: Teacher with ID " + teacherId + " not found despite being passed.");
                return CommonMethod.getReturnMessageError("学生信息不存在，无法修改！");
            }
        } else {
            // --- 调试点 C ---
            System.out.println("This is a new teacher creation scenario.");
        }

        // --- 调试点 D ---
        System.out.println("Checking for existing person with num: " + num);
        Optional<Person> existingPersonWithNum = personRepository.findByNum(num);

        if (existingPersonWithNum.isPresent()) {
            Person existingP = existingPersonWithNum.get();
            // --- 调试点 E ---
            System.out.println("Found existing Person with same num. Its personId: " + existingP.getPersonId() + ", its num: " + existingP.getNum());
            System.out.println("Current Person's personId (p.getPersonId()): " + (p != null ? p.getPersonId() : "null"));
            System.out.println("Are their IDs equal? " + (p != null && existingP.getPersonId().equals(p.getPersonId())));

            if (s == null || !existingP.getPersonId().equals(p.getPersonId())) {
                // --- 调试点 F ---
                System.out.println("Triggering unique number error. s == null is " + (s == null) + ", !existingP.getPersonId().equals(p.getPersonId()) is " + (!existingP.getPersonId().equals(p.getPersonId())));
                return CommonMethod.getReturnMessageError("该学号已被其他用户占用，不能添加或修改！");
            }
        }


        // 3. 处理新增学生逻辑
        if (s == null) { // 意味着是新增操作
            System.out.println("--- Creating new teacher ---");
            p = new Person(); // 新建 Person 实体
            p.setNum(num);
            p.setType("1"); // 设置人员类型为学生 ('1' 通常代表学生，请根据你的业务定义确认)
            try {
                personRepository.save(p); // 保存新的 Person 记录
                personId = p.getPersonId(); // 获取新生成的 PersonId
                System.out.println("New Person saved with ID: " + personId);
            } catch (Exception e) {
                e.printStackTrace();
                return CommonMethod.getReturnMessageError("保存人员信息失败: " + e.getMessage());
            }

            // 创建新用户
            u = new User();
            u.setPerson(p); // 将 User 与新创建的 Person 关联
            u.setUserName(num); // 用户名设置为学号
            String defaultPassword = encoder.encode("123456"); // 默认密码加密
            u.setPassword(defaultPassword);
            System.out.println("User password encrypted.");

            // 获取用户类型 (ROLE_TEACHER)
            UserType userType = userTypeRepository.findByName("ROLE_TEACHER");
            if (userType == null) {
                System.err.println("Error: UserType 'ROLE_TEACHER' not found in database.");
                return CommonMethod.getReturnMessageError("系统配置错误：用户类型 'ROLE_TEACHER' 未找到。");
            }
            u.setUserType(userType);
            System.out.println("UserType 'ROLE_TEACHER' assigned to new user.");

            try {
                userRepository.save(u); // 保存新的 User 记录
                System.out.println("New User saved for Person ID: " + personId);
            } catch (Exception e) {
                e.printStackTrace();
                // 如果保存用户失败，考虑回滚Person的创建，或者清理数据
                personRepository.delete(p); // 尝试回滚已创建的Person
                return CommonMethod.getReturnMessageError("创建用户失败 (学号: " + num + ")：" + e.getMessage());
            }

            s = new Teacher(); // 新建 Teacher 实体
            s.setPerson(p); // 将 Teacher 与新创建的 Person 关联
            System.out.println("New Teacher entity created.");

        } else {
            // 4. 处理修改学生逻辑 (p 和 personId 已经在上面获取)
            System.out.println("--- Modifying existing teacher (ID: " + teacherId + ") ---");
            // 检查学号是否被修改，如果修改了，需要更新关联的 User 的用户名
            if (!num.equals(p.getNum())) {
                Optional<User> uOp = userRepository.findByPersonPersonId(personId);
                if (uOp.isPresent()) {
                    u = uOp.get();
                    u.setUserName(num); // 更新用户名
                    try {
                        userRepository.save(u); // 保存更新的 User 记录
                        System.out.println("User username updated for Person ID: " + personId + " to: " + num);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return CommonMethod.getReturnMessageError("更新用户账号失败: " + e.getMessage());
                    }
                } else {
                    System.err.println("Warning: No user found for Person ID: " + personId + ". Cannot update username.");
                }
                p.setNum(num); // 更新 Person 的学号
            }
        }

        // 5. 更新 Person 实体属性
        p.setName(CommonMethod.getString(form, "name"));
        p.setDept(CommonMethod.getString(form, "dept"));
        p.setCard(CommonMethod.getString(form, "card"));
        p.setGender(CommonMethod.getString(form, "gender"));
        p.setBirthday(CommonMethod.getString(form, "birthday"));
        p.setEmail(CommonMethod.getString(form, "email"));
        p.setPhone(CommonMethod.getString(form, "phone"));
        p.setAddress(CommonMethod.getString(form, "address"));
        System.out.println("Person attributes updated.");

        try {
            personRepository.save(p); // 保存修改后的人员信息
            System.out.println("Person saved: " + p.getPersonId());
        } catch (Exception e) {
            e.printStackTrace();
            return CommonMethod.getReturnMessageError("保存人员信息失败: " + e.getMessage());
        }

        // 6. 更新 Teacher 实体属性
        s.setTitle(CommonMethod.getString(form, "title"));
        s.setDegree(CommonMethod.getString(form, "degree"));
        System.out.println("Teacher attributes updated.");

        try {
            teacherRepository.save(s); // 保存修改后的学生信息
            System.out.println("Teacher saved: " + s.getTeacherId());
        } catch (Exception e) {
            e.printStackTrace();
            return CommonMethod.getReturnMessageError("保存学生信息失败: " + e.getMessage());
        }

        System.out.println("--- Exiting teacherEditSave method. Success. ---");
        return CommonMethod.getReturnData(s.getTeacherId()); // 将 teacherId 返回前端
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
