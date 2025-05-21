package cn.edu.sdu.java.server.controllers;

import cn.edu.sdu.java.server.payload.request.DataRequest;
import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.services.UserDetailsImpl;
import cn.edu.sdu.java.server.services.UserService;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")

public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/getUserRole")
    public DataResponse getUserRole(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            return CommonMethod.getReturnMessageError("❌ 用户未登录！");
        }

        String role = userDetails.getAuthorities().iterator().next().getAuthority(); // ✅ 获取用户角色
        System.out.println("🔍 当前用户角色: " + role);

        return CommonMethod.getReturnData(role);
    }


}
