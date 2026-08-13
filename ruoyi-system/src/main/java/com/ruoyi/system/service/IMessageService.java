package com.ruoyi.system.service;

import com.ruoyi.system.domain.Message;

import java.util.List;

public interface IMessageService {

    List<Message> selectMessageList(Long userId);

    Message selectMessageById(Long id);

    int insertMessage(Message message);

    int updateMessage(Message message);

    int deleteMessageById(Long id);

    int updateMessageRead(Long userId);

    int countUnread(Long userId);

    int sendOrderMessage(Long userId, String title, String content);
}