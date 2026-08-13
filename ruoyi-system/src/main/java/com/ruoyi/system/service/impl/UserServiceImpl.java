package com.ruoyi.system.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.User;
import com.ruoyi.system.mapper.UserMapper;
import com.ruoyi.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User selectUserById(Long userId) {
        return userMapper.selectUserById(userId);
    }

    @Override
    public User selectUserByUserName(String userName) {
        return userMapper.selectUserByUserName(userName);
    }

    @Override
    public User selectUserByPhone(String phone) {
        return userMapper.selectUserByPhone(phone);
    }

    @Override
    public List<User> selectUserList(User user) {
        return userMapper.selectUserList(user);
    }

    @Override
    @Transactional
    public int insertUser(User user) {
        user.setCreateTime(DateUtils.getNowDate());
        user.setUpdateTime(DateUtils.getNowDate());
        return userMapper.insertUser(user);
    }

    @Override
    @Transactional
    public int updateUser(User user) {
        user.setUpdateTime(DateUtils.getNowDate());
        return userMapper.updateUser(user);
    }

    @Override
    @Transactional
    public int deleteUserById(Long userId) {
        return userMapper.deleteUserById(userId);
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectUserByUserName(username);
        if (user == null) {
            user = userMapper.selectUserByPhone(username);
        }
        if (user == null) {
            throw new ServiceException("账号不存在");
        }
        if (!password.equals(user.getPassword())) {
            throw new ServiceException("密码错误");
        }
        return user;
    }

    @Override
    @Transactional
    public User register(User user) {
        if (userMapper.selectUserByUserName(user.getUserName()) != null) {
            throw new ServiceException("用户名已存在");
        }
        if (userMapper.selectUserByPhone(user.getPhone()) != null) {
            throw new ServiceException("手机号已被注册");
        }
        user.setCreateTime(DateUtils.getNowDate());
        user.setUpdateTime(DateUtils.getNowDate());
        user.setRole("student");
        user.setStatus(1);
        userMapper.insertUser(user);
        return user;
    }

    @Override
    @Transactional
    public int verifyStudent(Long userId, String schoolName, String studentId, String department) {
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        user.setSchoolName(schoolName);
        user.setStudentId(studentId);
        user.setDepartment(department);
        user.setIsVerified(1);
        user.setUpdateTime(DateUtils.getNowDate());
        return userMapper.updateUser(user);
    }

    @Override
    @Transactional
    public int updateAvatar(Long userId, String avatar) {
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        user.setAvatar(avatar);
        user.setUpdateTime(DateUtils.getNowDate());
        return userMapper.updateUser(user);
    }

    @Override
    @Transactional
    public int updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectUserById(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        if (!oldPassword.equals(user.getPassword())) {
            throw new ServiceException("旧密码错误");
        }
        user.setPassword(newPassword);
        user.setUpdateTime(DateUtils.getNowDate());
        return userMapper.updateUser(user);
    }

    @Override
    public User getUserInfo(Long userId) {
        return userMapper.selectUserById(userId);
    }
}