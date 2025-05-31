package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.HonorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/honor")
public class HonorController {
    private final HonorService honorService;

    @Autowired
    public HonorController(HonorService honorService) {
        this.honorService = honorService;
    }

    @PostMapping("/getHonorListAll")
    @PreAuthorize("hasRole('ADMIN')") // 假设只有管理员可以添加荣誉
    public DataResponse getHonorList(@Valid @RequestBody DataRequest dataRequest) {
        return honorService.getHonorListAll(dataRequest);
    }
    @PostMapping("/createHonor")
    @PreAuthorize("hasRole('ADMIN')") // 假设只有管理员可以添加荣誉
    public DataResponse createHonor(@Valid @RequestBody DataRequest dataRequest) {
        return honorService.createHonor(dataRequest);
    }

    @PostMapping("/getHonorListByHonorName")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getHonorByHonorId(@Valid @RequestBody DataRequest dataRequest) {
        return honorService.getHonorByHonorName(dataRequest);
    }

    @PostMapping("/getHonorListByStudentName")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public DataResponse getHonorsByPersonId(@Valid @RequestBody  DataRequest dataRequest) {
        return honorService.getHonorsByStudentName(dataRequest);
    }

    @PostMapping("/updateHonor")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse updateHonor(@Valid @RequestBody DataRequest req) {
        return honorService.updateHonor(req);
    }

    @PostMapping("/deleteHonor")
    @PreAuthorize("hasRole('ADMIN')")
    public DataResponse deleteHonor(@Valid @RequestBody DataRequest req) {
        return honorService.deleteHonor(req);
    }
}