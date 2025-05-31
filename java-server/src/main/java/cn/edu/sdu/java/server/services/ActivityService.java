package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Activity;
import cn.edu.sdu.java.server.models.ActivityStudent;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.ActivityRepository;
import cn.edu.sdu.java.server.repositorys.ActivityStudentRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ActivityService {
    private final StudentRepository studentRepository;
    private final ActivityRepository activityRepository;
    private final ActivityStudentRepository activityStudentRepository;


    @Autowired
    public ActivityService(StudentRepository studentRepository, ActivityRepository activityRepository, StudentService studentService, SystemService systemService, ActivityStudentRepository activityStudentRepository) {
        this.studentRepository = studentRepository;
        this.activityRepository = activityRepository;
        this.activityStudentRepository = activityStudentRepository;
    }

    public Map<String,Object> getMapFromActivityAndStudent(ActivityStudent ab) {
        Map<String,Object> m = new HashMap<>();
        if(ab == null)
            return m;
        Optional<Student> b =studentRepository.findById(ab.getStudent().getStudentId());
        Optional<Activity> a =activityRepository.findById(ab.getActivity().getActivity_id());
        m.put("title",a.get().getTitle());
        m.put("startTime",a.get().getStartTime());
        m.put("endTime", a.get().getEndTime());
        m.put("location",a.get().getLocation());
        m.put("name",b.get().getPerson().getName());
        m.put("major",b.get().getMajor());
        m.put("class",b.get().getClassName());
        return m;
    }

    public Map<String, Object> getMapFromActivity(Activity a) {
        Map<String,Object> m = new HashMap<>();
        if(a == null)
            return m;
        m.put("title",a.getTitle());
        m.put("startTime",a.getStartTime());
        m.put("endTime", a.getEndTime());
        m.put("location",a.getLocation());
        return m;
    }

    public DataResponse getActivityStudentListAll(DataRequest dataRequest) {
        List<Map<String,Object>> dataList = new ArrayList<>();
        List<ActivityStudent> sList=activityStudentRepository.findAll();
        if (sList.isEmpty())
            return CommonMethod.getReturnData(sList);
        for (ActivityStudent ab : sList) {
            dataList.add(getMapFromActivityAndStudent(ab));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse getActivityListAll(DataRequest dataRequest) {
        List<Map<String,Object>> dataList = new ArrayList<>();
        List<Activity> sList=activityRepository.findAll();
        if (sList.isEmpty())
            return CommonMethod.getReturnData(sList);
        for (Activity ab : sList) {
            dataList.add(getMapFromActivity(ab));
        }
        return CommonMethod.getReturnData(dataList);
    }

    public DataResponse activityApply(DataRequest dataRequest){
        Map<String,Object> form = dataRequest.getData();
        String title = CommonMethod.getString(form, "title");
        String num = CommonMethod.getString(form, "num");
        Activity a=null;
        Student s=null;
        Optional<Activity> op = activityRepository.findByTitle(title);
        Optional<Student> op2=studentRepository.findByPersonNum(num);
        if(op2.isEmpty() || op.isEmpty()){
            return CommonMethod.getReturnMessageError("学生或者活动不存在，请仔细检查");
        }
        a=op.get();
        s=op2.get();
        ActivityStudent ab=new ActivityStudent();
        ab.setStudent(s);
        ab.setActivity(a);
        activityStudentRepository.save(ab);
        Integer id = Math.toIntExact(ab.getId());
        return CommonMethod.getReturnData(id);
    }

    public DataResponse createActivity(DataRequest dataRequest){
        Map<String,Object> form = dataRequest.getData();
        String title = CommonMethod.getString(form, "title");
        Optional<Activity> op;
        op = activityRepository.findByTitle(title);
        if(!op.isEmpty()) return CommonMethod.getReturnMessageError("该活动存在，请仔细检查");
        Activity a=new Activity();
        a.setTitle(title);
        a.setLocation(CommonMethod.getString(form, "location"));
        a.setStartTime(CommonMethod.getDate(form,"startTime"));
        a.setEndTime(CommonMethod.getDate(form, "endTime"));
        activityRepository.save(a);
        Integer activityId = a.getActivity_id();
        return CommonMethod.getReturnData(activityId);
    }

    public DataResponse deleteActivity(DataRequest dataRequest){
        Map<String,Object> form = dataRequest.getMap("form");
        String title = CommonMethod.getString(form, "title");
        Optional<Activity> op;
        op = activityRepository.findByTitle(title);
        if(op.isEmpty()){
            return CommonMethod.getReturnMessageError("该活动不存在，请仔细检查");
        }
        List<ActivityStudent> slist=activityStudentRepository.findByActivity(op.get());
        activityStudentRepository.deleteAll(slist);
        activityRepository.delete(op.get());
        return CommonMethod.getReturnMessageOK();
    }

    public DataResponse updateActivity(DataRequest dataRequest){
        deleteActivity(dataRequest);
        createActivity(dataRequest);
        return CommonMethod.getReturnMessageOK();
    }

    @Transactional(rollbackFor = Exception.class)
    public DataResponse activityApplyDelete(DataRequest dataRequest){
        Map<String,Object> form = dataRequest.getData();
        String title = CommonMethod.getString(form, "title");
        String name = CommonMethod.getString(form, "name");
        Optional<Activity> op=activityRepository.findByTitle(title);
        List<Student> slist=studentRepository.findByPersonName(name);
        if(op.isEmpty() || slist.isEmpty()){
            return CommonMethod.getReturnMessageError("该学生或者该课程不存在，请仔细检查");
        }
        Student op2 =slist.getFirst();
        int activity_id = op.get().getActivity_id();
        Integer student_id = op2.getStudentId();
        activityStudentRepository.deleteByActivityIdAndStudentId(activity_id,student_id);
        return CommonMethod.getReturnMessageOK();
    }

    @Transactional(rollbackFor = Exception.class)
    public DataResponse searchByActivity(DataRequest dataRequest){
        Map<String,Object> form = dataRequest.getData();
        Integer activityId= activityRepository.findByTitle(CommonMethod.getString(form,"title")).get().getActivity_id();
        List<Map<String,Object>> dataList = new ArrayList<>();
        List<ActivityStudent> sList= activityStudentRepository.findByActivity(activityRepository.findById(activityId).get());
        if (sList == null || sList.isEmpty())
            return CommonMethod.getReturnMessageError("没有学生，请仔细检查");
        for (ActivityStudent ab : sList) {
            dataList.add(getMapFromActivityAndStudent(ab));
        }
        return CommonMethod.getReturnData(dataList);
    }

    @Transactional(rollbackFor = Exception.class)
    public DataResponse searchByStudent(DataRequest dataRequest){
        Map<String,Object> form = dataRequest.getData();
        Integer studentId= studentRepository.findByPersonName(CommonMethod.getString(form, "data")).getFirst().getStudentId();
        List<Map<String,Object>> dataList = new ArrayList<>();
        List<ActivityStudent> sList= activityStudentRepository.findByStudent(studentRepository.findById(studentId).get());
        if (sList == null || sList.isEmpty())
            return CommonMethod.getReturnData(dataList);
        for (ActivityStudent ab : sList) {
            dataList.add(getMapFromActivityAndStudent(ab));
        }
        return CommonMethod.getReturnData(dataList);
    }
    @Transactional(rollbackFor = Exception.class)
    public DataResponse getApplyActivityList(DataRequest dataRequest){
        Map<String,Object> form = dataRequest.getData();
        List<Map<String,Object>> sList = new ArrayList<>();
        String name = CommonMethod.getString(form, "name");
        List<Activity> dataList = activityRepository.findAll();
        List<Student> slist=studentRepository.findByPersonName(name);
        Student op = slist.get(0);
        for (Activity a : dataList) {
            if(!activityStudentRepository.existsByStudentAndActivity(op,a)){
                sList.add(getMapFromActivity(a));
            }
        }
        return CommonMethod.getReturnData(sList);
    }
    @Transactional(rollbackFor = Exception.class)
    public DataResponse getAppliedActivityList(DataRequest dataRequest){
        Map<String,Object> form = dataRequest.getData();
        List<Map<String,Object>> sList = new ArrayList<>();
        String name = CommonMethod.getString(form, "name");
        List<Activity> dataList = activityRepository.findAll();
        List<Student> slist=studentRepository.findByPersonName(name);
        Student op = slist.get(0);
        for (Activity a : dataList) {
            if(activityStudentRepository.existsByStudentAndActivity(op,a)){
                sList.add(getMapFromActivity(a));
            }
        }
        return CommonMethod.getReturnData(sList);
    }
    @Transactional(rollbackFor = Exception.class)
    public DataResponse getApplyActivityListForSearch(DataRequest dataRequest){
        Map<String,Object> form = dataRequest.getData();
        List<Map<String,Object>> sList = new ArrayList<>();
        String name = CommonMethod.getString(form, "name");
        String title = CommonMethod.getString(form, "title");
        List<Activity> dataList = activityRepository.findAll();
        List<Student> slist=studentRepository.findByPersonName(name);
        Student op = slist.getFirst();
        for (Activity a : dataList) {
            if((!activityStudentRepository.existsByStudentAndActivity(op,a))&&a.getTitle().equals(title)){
                sList.add(getMapFromActivity(a));
            }
        }
        return CommonMethod.getReturnData(sList);
    }
    @Transactional(rollbackFor = Exception.class)
    public DataResponse getAppliedActivityListForSearch(DataRequest dataRequest){
        Map<String,Object> form = dataRequest.getData();
        List<Map<String,Object>> sList = new ArrayList<>();
        String name = CommonMethod.getString(form, "name");
        String title = CommonMethod.getString(form, "title");
        List<Activity> dataList = activityRepository.findAll();
        List<Student> slist=studentRepository.findByPersonName(name);
        Student op = slist.getFirst();
        for (Activity a : dataList) {
            if((activityStudentRepository.existsByStudentAndActivity(op,a))&&a.getTitle().equals(title)){
                sList.add(getMapFromActivity(a));
            }
        }
        return CommonMethod.getReturnData(sList);
    }
}