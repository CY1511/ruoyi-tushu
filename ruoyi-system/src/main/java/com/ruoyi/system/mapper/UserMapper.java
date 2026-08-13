package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.User;

@Mapper
public interface UserMapper {
    User selectUserById(Long userId);
    User selectUserByUserName(String userName);
    User selectUserByPhone(String phone);
    List<User> selectUserList(User user);
    int insertUser(User user);
    int updateUser(User user);
    int deleteUserById(Long userId);
}