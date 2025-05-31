package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.PracticeService;
import cn.edu.sdu.java.server.util.CommonMethod;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/practice")
public class PracticeController {
    private final PracticeService practiceService;

    @Autowired
    public PracticeController(PracticeService practiceService) {
        this.practiceService = practiceService;
    }

    @PostMapping("/getPracticeById")
    public DataResponse getPracticeById(@RequestBody DataRequest req) {
        return practiceService.getPracticeById(req);
    }
    @PostMapping("/getPracticeList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getPracticeList(@Valid @RequestBody DataRequest dataRequest) {
        return practiceService.getPracticeList(dataRequest);
    }
    @PostMapping("/add")
    @PreAuthorize("hasRole('STUDENT')")
    public DataResponse add( @RequestBody DataRequest dataRequest) {
        System.out.println("准备添加");
        return practiceService.practiceSave(dataRequest);
    }
    @PostMapping("/delete")
    public DataResponse delete(@RequestBody DataRequest req) {
        return practiceService.delete(req);
    }
    @PostMapping("/update")
    public DataResponse update(@RequestBody DataRequest req) {
        Integer id = req.getInteger("id");
        if (id == null) {
            return CommonMethod.getReturnMessageError("实习记录 ID 不能为空！");
        }
        return practiceService.update(req);
    }

}
