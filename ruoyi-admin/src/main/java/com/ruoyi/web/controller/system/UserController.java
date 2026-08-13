package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.User;
import com.ruoyi.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Anonymous
    @PostMapping("/login")
    public R login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        User user = userService.login(username, password);
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("token", "token_" + user.getId());
        return R.ok(data);
    }

    @Anonymous
    @PostMapping("/register")
    public R register(@RequestBody User user) {
        userService.register(user);
        return R.ok("注册成功");
    }

    @PostMapping("/verify-student")
    public R verifyStudent(@RequestBody Map<String, Object> params) {
        Long userId = Long.parseLong(params.get("userId").toString());
        String schoolName = params.get("schoolName").toString();
        String studentId = params.get("studentId").toString();
        String department = params.get("department").toString();
        userService.verifyStudent(userId, schoolName, studentId, department);
        return R.ok("认证成功");
    }

    @GetMapping("/info")
    public R getUserInfo(@RequestParam Long userId) {
        User user = userService.getUserInfo(userId);
        return R.ok(user);
    }

    @PutMapping("/update")
    public R updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return R.ok("更新成功");
    }

    @PutMapping("/avatar")
    public R updateAvatar(@RequestBody Map<String, Object> params) {
        Long userId = Long.parseLong(params.get("userId").toString());
        String avatar = params.get("avatar").toString();
        userService.updateAvatar(userId, avatar);
        return R.ok("头像更新成功");
    }

    @PutMapping("/password")
    public R updatePassword(@RequestBody Map<String, Object> params) {
        Long userId = Long.parseLong(params.get("userId").toString());
        String oldPassword = params.get("oldPassword").toString();
        String newPassword = params.get("newPassword").toString();
        userService.updatePassword(userId, oldPassword, newPassword);
        return R.ok("密码更新成功");
    }
}