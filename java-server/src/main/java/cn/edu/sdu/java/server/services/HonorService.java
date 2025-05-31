package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Honor;
import cn.edu.sdu.java.server.models.HonorStudent;
import cn.edu.sdu.java.server.models.Student;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.HonorRepository;
import cn.edu.sdu.java.server.repositorys.HonorStudentRepository;
import cn.edu.sdu.java.server.repositorys.StudentRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class HonorService {
    @Autowired
    private HonorRepository honorRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private HonorStudentRepository honorStudentRepository ;
    public HonorService(HonorRepository honorRepository, StudentRepository studentRepository)
    {
        this.honorRepository = honorRepository;
        this.studentRepository = studentRepository;
    }

    public Map<String,Object> getMapFromHonorAndStudent(HonorStudent hs) {
        Map<String,Object> m = new HashMap<>();
        if(hs == null)
            return m;
        Optional<Student> b =studentRepository.findById(hs.getStudent().getStudentId());
        Optional<Honor> a =honorRepository.findById(hs.getHonor().getHonorId());
        m.put("HonorName",a.get().getHonorName());
        m.put("Date",a.get().getDate());
        m.put("Host", a.get().getHost());
        m.put("HonorLevel",a.get().getHonorLevel());
        m.put("HonorType",a.get().getHonorType());
        m.put("major",b.get().getMajor());
        m.put("class",b.get().getClassName());
        m.put("name",b.get().getPerson().getName());
        m.put("num",b.get().getPerson().getNum());
        return m;
    }
    public DataResponse getHonorListAll(DataRequest dataRequest) {
        List<Map<String,Object>> dataList = new ArrayList<>();
        List<HonorStudent> sList=honorStudentRepository.findAll();
        if (sList.isEmpty())
            return CommonMethod.getReturnData(sList);
        for (HonorStudent hs : sList) {
            dataList.add(getMapFromHonorAndStudent(hs));
        }
        return CommonMethod.getReturnData(dataList);
    }

    @Transactional(rollbackFor = Exception.class)
    public DataResponse createHonor(DataRequest req) {
        String honorName = req.getString("honorName");
        Optional<Honor> honor = honorRepository.findByHonorName(honorName);
        List<Student> slist = studentRepository.findByPersonName(req.getString("studentName"));
        Honor h = new Honor();
        if (slist.isEmpty()) {
            return CommonMethod.getReturnMessageError("未找到对应的学生");
        }
        Student student = slist.getFirst();
        if(honor.isEmpty()) {
            h.setHonorName(honorName);
            h.setHonorType(req.getString("honorType"));
            h.setHonorLevel(req.getString("honorLevel"));
            h.setHost(req.getString("host"));
            h.setDate(req.getDate("date"));
            honorRepository.save(h);
        }
        else{
            h=honor.get();
        }
        HonorStudent honorStudent = new HonorStudent();
        honorStudent.setHonor(h);
        honorStudent.setStudent(student);
        honorStudentRepository.save(honorStudent);
        int id = honorStudent.getId();
        return CommonMethod.getReturnData(id);
    }

    // 通过 ID 查询荣誉信息
    public DataResponse getHonorByHonorName(DataRequest req) {
        String honorName = req.getString("data");
        Optional<Honor> honor = honorRepository.findByHonorName(honorName);
        if(honor.isEmpty()){
            return CommonMethod.getReturnMessageError("未找到荣誉信息");
        }
        List<HonorStudent> sList = honorStudentRepository.findByHonor(honor.get());
        if (sList.isEmpty()) {
            return CommonMethod.getReturnMessageError("未找到荣誉信息");
        }
        else {
            List<Map<String,Object>> dataList = new ArrayList<>();
            for (HonorStudent hs : sList) {
                dataList.add(getMapFromHonorAndStudent(hs));
            }
            return CommonMethod.getReturnData(dataList);
        }
    }


    public DataResponse getHonorsByStudentName(DataRequest req) {
        String name = req.getString("data");
        List<Student> slist = studentRepository.findByPersonName(name);
        Student student = slist.getFirst();
        List<HonorStudent> sList = honorStudentRepository.findByStudent(student);
        if (sList.isEmpty()) {
            return CommonMethod.getReturnMessageError("未找到荣誉信息");
        }
        else {
            List<Map<String,Object>> dataList = new ArrayList<>();
            for (HonorStudent hs : sList) {
                dataList.add(getMapFromHonorAndStudent(hs));
            }
            return CommonMethod.getReturnData(dataList);
        }
    }

    // 更新荣誉信息
    public DataResponse updateHonor(DataRequest req) {
        String honorName = req.getString("honorName");
        String name = req.getString("name");
        Optional<Honor> honorOptional = honorRepository.findByHonorName(honorName);
        List<Student> slist = studentRepository.findByPersonName(name);
        if (honorOptional.isPresent()&& !slist.isEmpty()) {
            Honor honor = honorOptional.get();
            honor.setHonorName(req.getString("honorName"));
            honor.setHost(req.getString("host"));
            honor.setDate(req.getDate("date"));
            honor.setHonorLevel(req.getString("honorLevel"));
            honor.setHonorType(req.getString("honorType"));
            honorRepository.save(honor);
            return CommonMethod.getReturnMessageOK("荣誉信息更新成功！");
        } else {
            return CommonMethod.getReturnMessageError("未找到荣誉信息");
        }
    }


    public DataResponse deleteHonor(DataRequest req) {
        String honorName = req.getString("title");
        String name = req.getString("name");
        Optional<Honor> op = honorRepository.findByHonorName(honorName);
        List<Student> slist = studentRepository.findByPersonName(name);
        if (op.isPresent()&&!slist.isEmpty()) {
            Student op2 = slist.getFirst();
            Integer activity_id = op.get().getHonorId();
            Integer student_id = op2.getStudentId();
            honorStudentRepository.deleteByActivityIdAndStudentId(activity_id,student_id);
            return CommonMethod.getReturnMessageOK("荣誉信息删除成功！");
        } else {
            return CommonMethod.getReturnMessageError("未找到荣誉信息");
        }
    }
}