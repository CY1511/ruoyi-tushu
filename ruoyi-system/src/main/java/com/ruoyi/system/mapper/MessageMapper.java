package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.Message;

@Mapper
public interface MessageMapper {
    List<Message> selectMessageList(Long userId);
    Message selectMessageById(Long id);
    int insertMessage(Message message);
    int updateMessage(Message message);
    int deleteMessageById(Long id);
    int updateMessageRead(Long userId);
    int countUnread(Long userId);
}