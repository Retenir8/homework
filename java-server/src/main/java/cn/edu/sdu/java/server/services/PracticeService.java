package cn.edu.sdu.java.server.services;

import cn.edu.sdu.java.server.models.Practice;
import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.repositorys.PracticeRepository;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PracticeService {
    @Autowired
    private PracticeRepository practiceRepository;
    private PracticeService practiceService;

    @Autowired
    public PracticeService(PracticeRepository practiceRepository) {
        this.practiceRepository = practiceRepository;
    }
    public Map<String,Object> getMapFromPractice(Practice practice) {
        Map<String,Object> map = new HashMap<>();
        map.put("name",practice.getName());
        map.put("title",practice.getTitle());
        map.put("time",practice.getTime());
        map.put("gender",practice.getGender());
        map.put("studentName",practice.getStudentName());
        map.put("id",practice.getId());
        return map;
    }

    public DataResponse getPracticeById(DataRequest req) {
        Integer id =req.getInteger("id");
        Optional<Practice> p = practiceRepository.findById(id);
        return CommonMethod.getReturnData(getMapFromPractice(p.get()));
    }

    public DataResponse getPracticeList(DataRequest dataRequest) {
        List<Practice> practice = practiceRepository.findAll();
        if (practice.isEmpty()) {
            throw new RuntimeException("未找到任何实习数据");
        }
        List<Map> datalist= new ArrayList<>();
        for(Practice p : practice) {
            datalist.add(getMapFromPractice(p));
        }
        return CommonMethod.getReturnData(datalist);
    }

    public  DataResponse practiceSave(DataRequest req) {
        Practice practice = new Practice();
        practice.setName(req.getString("name"));
        practice.setTitle(req.getString("title"));
        practice.setTime(req.getString("time"));
        practice.setGender(req.getString("gender"));
        practice.setStudentName(req.getString("studentName"));;
        practiceRepository.save(practice);
        Integer id = practice.getId();
        return CommonMethod.getReturnData(practiceService.getMapFromPractice(practice));
    }
    public DataResponse delete(DataRequest req) {
        Integer id = req.getInteger("id");
        practiceRepository.deleteById(id);
        return CommonMethod.getReturnMessageOK("实习记录删除成功！");
    }
    public  DataResponse update(DataRequest req) {
        Integer id = req.getInteger("id");
        Optional<Practice>  p= practiceRepository.findById(id);
        if (!p.isEmpty()) {
            Practice practice = p.get();
            practice.setId(req.getInteger("id"));
            practice.setName(req.getString("name"));
            practice.setTitle(req.getString("title"));
            practice.setTime(req.getString("time"));
            practice.setGender(req.getString("gender"));
            practice.setStudentName(req.getString("studentName"));;
            practiceRepository.save(practice);
            return CommonMethod.getReturnData(practiceService.getMapFromPractice(practice));
        }
        return CommonMethod.getReturnMessageError("实习经历不存在，请仔细检查");
    }
    public DataResponse add(DataRequest req) {
        Practice practice = new Practice();
        practice.setId(req.getInteger("id"));
        practice.setName(req.getString("name"));
        practice.setTitle(req.getString("title"));
        practice.setTime(req.getString("time"));
        practice.setGender(req.getString("gender"));
        practice.setStudentName(req.getString("studentName"));
        practiceRepository.save(practice);
        return CommonMethod.getReturnMessageOK();
    }
}
