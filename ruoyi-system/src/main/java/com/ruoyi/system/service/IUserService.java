package com.ruoyi.system.service;

import com.ruoyi.system.domain.User;

import java.util.List;

public interface IUserService {

    User selectUserById(Long userId);

    User selectUserByUserName(String userName);

    User selectUserByPhone(String phone);

    List<User> selectUserList(User user);

    int insertUser(User user);

    int updateUser(User user);

    int deleteUserById(Long userId);

    User login(String username, String password);

    User register(User user);

    int verifyStudent(Long userId, String schoolName, String studentId, String department);

    int updateAvatar(Long userId, String avatar);

    int updatePassword(Long userId, String oldPassword, String newPassword);

    User getUserInfo(Long userId);
}