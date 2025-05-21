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
import java.util.*;
import java.util.List;

@Service
public class StudentService {
    private static final Logger log = LoggerFactory.getLogger(StudentService.class);
    private final PersonRepository personRepository;  //人员数据操作自动注入
    private final StudentRepository studentRepository;  //学生数据操作自动注入
    private final UserRepository userRepository;  //学生数据操作自动注入
    private final UserTypeRepository userTypeRepository; //用户类型数据操作自动注入
    private final PasswordEncoder encoder;  //密码服务自动注入
    private final FeeRepository feeRepository;  //消费数据操作自动注入
    private final FamilyMemberRepository familyMemberRepository;
    private final SystemService systemService;
    @Autowired
    public StudentService(PersonRepository personRepository, StudentRepository studentRepository, UserRepository userRepository, UserTypeRepository userTypeRepository, PasswordEncoder encoder,  FeeRepository feeRepository, FamilyMemberRepository familyMemberRepository, SystemService systemService) {
        this.personRepository = personRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.encoder = encoder;
        this.feeRepository = feeRepository;
        this.familyMemberRepository = familyMemberRepository;
        this.systemService = systemService;
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





    public Map<String,Object> getMapFromStudent(Student s) {
        Map<String,Object> m = new HashMap<>();
        Person p;
        if(s == null)
            return m;
        m.put("major",s.getMajor());
        m.put("className",s.getClassName());
        p = s.getPerson();
        if(p == null)
            return m;
        m.put("studentId", s.getStudentId());
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

    //Java 对象的注入 我们定义的这下Java的操作对象都不能自己管理是由有Spring框架来管理的， StudentController 中要使用StudentRepository接口的实现类对象，
    // 需要下列方式注入，否则无法使用， studentRepository 相当于StudentRepository接口实现对象的一个引用，由框架完成对这个引用的赋值，
    // StudentController中的方法可以直接使用

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
        Integer personId = dataRequest.getInteger("personId");//获取student_id值
        Integer studentId = dataRequest.getInteger("studentId");
        Student s = null;
        Optional<Student> op;
        if (studentId != null && studentId > 0) {
            op = studentRepository.findById(studentId);   //查询获得实体对象
            if(op.isPresent()) {
                s = op.get();
                Optional<User> uOp = userRepository.findById(personId); //查询对应该学生的账户
                //删除对应该学生的账户
                uOp.ifPresent(userRepository::delete);
                Person p = s.getPerson();
                studentRepository.delete(s);    //首先数据库永久删除学生信息
                personRepository.delete(p);   // 然后数据库永久删除学生信息
            }
        }
        return CommonMethod.getReturnMessageOK();  //通知前端操作正常
    }


    public DataResponse getStudentInfo(DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        Student s = null;
        Optional<Student> op;
        if (studentId != null) {
            op = studentRepository.findById(studentId); //根据学生主键从数据库查询学生的信息
            if (op.isPresent()) {
                s = op.get();
            }
        }
        return CommonMethod.getReturnData(getMapFromStudent(s)); //这里回传包含学生信息的Map对象
    }

    @Transactional // 确保事务一致性
    public DataResponse studentEditSave(DataRequest dataRequest) {
        System.out.println("--- Entering studentEditSave method ---");
        System.out.println("Received dataRequest: " + dataRequest.getData());

        // 从请求中获取 studentId 和 form 数据
        Map<String, Object> form = dataRequest.getMap("form"); // 参数获取Map对象
        if (form == null) {
            return CommonMethod.getReturnMessageError("请求数据为空！");
        }
        // 确保从 form 中正确获取 studentId
        Integer studentId = CommonMethod.getInteger(form, "studentId"); // 使用 CommonMethod 或直接从 Map 中获取

        System.out.println("Form data: " + form); // 打印 form 数据
        System.out.println("Extracted studentId from form: " + studentId); // 打印提取到的 studentId

        String num = CommonMethod.getString(form,"num");  //Map 获取属性的值
        if (num == null || num.trim().isEmpty()) {
            return CommonMethod.getReturnMessageError("学号不能为空！"); // 学号为空校验
        }

        Student s = null; // 学生实体
        Person p = null;  // 人员实体
        User u = null;    // 用户实体

        Integer personId = CommonMethod.getInteger(form, "personId"); // 从 form 中获取 personId

        // 现在这里的逻辑应该是正确的，因为 studentId 应该有正确的值了
        if (studentId != null) {
            Optional<Student> op = studentRepository.findById(studentId);
            if (op.isPresent()) {
                s = op.get();
                p = s.getPerson(); // 获取关联的 Person
                personId = p.getPersonId(); // 确保 personId 是从数据库获取的最新值
                // --- 调试点 B ---
                System.out.println("Existing student found. studentId=" + studentId + ", personId=" + personId + ", num=" + p.getNum());
            } else {
                System.err.println("Error: Student with ID " + studentId + " not found despite being passed.");
                return CommonMethod.getReturnMessageError("学生信息不存在，无法修改！");
            }
        } else {
            // --- 调试点 C ---
            System.out.println("This is a new student creation scenario.");
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
            System.out.println("--- Creating new student ---");
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

            // 获取用户类型 (ROLE_STUDENT)
            UserType userType = userTypeRepository.findByName("ROLE_STUDENT");
            if (userType == null) {
                System.err.println("Error: UserType 'ROLE_STUDENT' not found in database.");
                return CommonMethod.getReturnMessageError("系统配置错误：用户类型 'ROLE_STUDENT' 未找到。");
            }
            u.setUserType(userType);
            System.out.println("UserType 'ROLE_STUDENT' assigned to new user.");

            try {
                userRepository.save(u); // 保存新的 User 记录
                System.out.println("New User saved for Person ID: " + personId);
            } catch (Exception e) {
                e.printStackTrace();
                // 如果保存用户失败，考虑回滚Person的创建，或者清理数据
                personRepository.delete(p); // 尝试回滚已创建的Person
                return CommonMethod.getReturnMessageError("创建用户失败 (学号: " + num + ")：" + e.getMessage());
            }

            s = new Student(); // 新建 Student 实体
            s.setPerson(p); // 将 Student 与新创建的 Person 关联
            System.out.println("New Student entity created.");

        } else {
            // 4. 处理修改学生逻辑 (p 和 personId 已经在上面获取)
            System.out.println("--- Modifying existing student (ID: " + studentId + ") ---");
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

        // 6. 更新 Student 实体属性
        s.setMajor(CommonMethod.getString(form, "major"));
        s.setClassName(CommonMethod.getString(form, "className"));
        System.out.println("Student attributes updated.");

        try {
            studentRepository.save(s); // 保存修改后的学生信息
            System.out.println("Student saved: " + s.getStudentId());
        } catch (Exception e) {
            e.printStackTrace();
            return CommonMethod.getReturnMessageError("保存学生信息失败: " + e.getMessage());
        }

        System.out.println("--- Exiting studentEditSave method. Success. ---");
        return CommonMethod.getReturnData(s.getStudentId()); // 将 studentId 返回前端
    }

    public List<Map<String,Object>> getStudentScoreList(List<Score> sList) {
        List<Map<String,Object>> list = new ArrayList<>();
        if (sList == null || sList.isEmpty())
            return list;
        Map<String,Object> m;
        Courses c;
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


    public List<Map<String,Object>> getStudentMarkList(List<Score> sList) {
        String[] title = {"优", "良", "中", "及格", "不及格"};
        int[] count = new int[5];
        List<Map<String,Object>> list = new ArrayList<>();
        if (sList == null || sList.isEmpty())
            return list;
        Map<String,Object> m;
        Courses c;
        for (Score s : sList) {
            c = s.getCourse();
            if (s.getMark() >= 90)
                count[0]++;
            else if (s.getMark() >= 80)
                count[1]++;
            else if (s.getMark() >= 70)
                count[2]++;
            else if (s.getMark() >= 60)
                count[3]++;
            else
                count[4]++;
        }
        for (int i = 0; i < 5; i++) {
            m = new HashMap<>();
            m.put("name", title[i]);
            m.put("title", title[i]);
            m.put("value", count[i]);
            list.add(m);
        }
        return list;
    }


    public List<Map<String,Object>> getStudentFeeList(Integer personId) {
        List<Fee> sList = feeRepository.findListByStudent(personId);  // 查询某个学生消费记录集合
        List<Map<String,Object>> list = new ArrayList<>();
        if (sList == null || sList.isEmpty())
            return list;
        Map<String,Object> m;
        Course c;
        for (Fee s : sList) {
            m = new HashMap<>();
            m.put("title", s.getDay());
            m.put("value", s.getMoney());
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



    /*
        FamilyMember
     */
    public DataResponse getFamilyMemberList(DataRequest dataRequest) {
        Integer studentId = dataRequest.getInteger("studentId");
        List<FamilyMember> fList = familyMemberRepository.findByStudentStudentId(studentId);
        List<Map<String,Object>> dataList = new ArrayList<>();
        Map<String,Object> m;
        if (fList != null) {
            for (FamilyMember f : fList) {
                m = new HashMap<>();
                m.put("memberId", f.getMemberId());
                m.put("personId", f.getStudent().getStudentId());
                m.put("relation", f.getRelation());
                m.put("name", f.getName());
                m.put("gender", f.getGender());
                m.put("age", f.getAge()+"");
                m.put("unit", f.getUnit());
                dataList.add(m);
            }
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse familyMemberSave(DataRequest dataRequest) {
        Map<String,Object> form = dataRequest.getMap("form");
        Integer studentId = CommonMethod.getInteger(form,"studentId");
        Integer memberId = CommonMethod.getInteger(form,"memberId");
        Optional<FamilyMember> op;
        FamilyMember f = null;
        if(memberId != null) {
            op = familyMemberRepository.findById(memberId);
            if(op.isPresent()) {
                f = op.get();
            }
        }
        if(f== null) {
            f = new FamilyMember();
            assert studentId != null;
            f.setStudent(studentRepository.findById(studentId).get());
        }
        f.setRelation(CommonMethod.getString(form,"relation"));
        f.setName(CommonMethod.getString(form,"name"));
        f.setGender(CommonMethod.getString(form,"gender"));
        f.setAge(CommonMethod.getInteger(form,"age"));
        f.setUnit(CommonMethod.getString(form,"unit"));
        familyMemberRepository.save(f);
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse familyMemberDelete(DataRequest dataRequest) {
        Integer memberId = dataRequest.getInteger("memberId");
        Optional<FamilyMember> op;
        op = familyMemberRepository.findById(memberId);
        op.ifPresent(familyMemberRepository::delete);
        return CommonMethod.getReturnMessageOK();
    }


    public DataResponse importFeeDataWeb(Map<String,Object> request,MultipartFile file) {
        Integer studentId = CommonMethod.getInteger(request, "studentId");
        try {
            String msg= importFeeData(studentId,file.getInputStream());
            if(msg == null)
                return CommonMethod.getReturnMessageOK();
            else
                return CommonMethod.getReturnMessageError(msg);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return CommonMethod.getReturnMessageError("上传错误！");
    }

}
