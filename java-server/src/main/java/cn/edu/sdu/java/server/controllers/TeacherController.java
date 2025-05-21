package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teacher")

public class TeacherController {
    private final TeacherService teacherService;  //密码服务自动注入
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }
    
    @PostMapping("/getTeacherByNum")
    public DataResponse getTeacherByNum(@RequestBody DataRequest dataRequest) {

        System.out.println(dataRequest);
        return teacherService.getTeacherByNum(dataRequest);
    }

    @PostMapping("/getTeacherList")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherList(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.getTeacherList(dataRequest);
    }

    @PostMapping("/teacherDelete")
    public DataResponse teacherDelete(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.teacherDelete(dataRequest);
    }

    @PostMapping("/getTeacherInfo")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse getTeacherInfo(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.getTeacherInfo(dataRequest);
    }
    @PostMapping("/teacherEditSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse teacherEditSave(@Valid @RequestBody DataRequest dataRequest) {

        return teacherService.teacherEditSave(dataRequest);
    }

    @PostMapping("/getTeacherListExcl")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<StreamingResponseBody> getTeacherListExcl(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.getTeacherListExcl(dataRequest);
    }

    @PostMapping("/getTeacherPageData")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse getTeacherPageData(@Valid @RequestBody DataRequest dataRequest) {
        return teacherService.getTeacherPageData(dataRequest);
    }
    
    
    

}
