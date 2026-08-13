package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.Message;
import com.ruoyi.system.mapper.MessageMapper;
import com.ruoyi.system.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public List<Message> selectMessageList(Long userId) {
        return messageMapper.selectMessageList(userId);
    }

    @Override
    public Message selectMessageById(Long id) {
        return messageMapper.selectMessageById(id);
    }

    @Override
    @Transactional
    public int insertMessage(Message message) {
        message.setIsRead(0);
        message.setCreateTime(DateUtils.getNowDate());
        return messageMapper.insertMessage(message);
    }

    @Override
    @Transactional
    public int updateMessage(Message message) {
        return messageMapper.updateMessage(message);
    }

    @Override
    @Transactional
    public int deleteMessageById(Long id) {
        return messageMapper.deleteMessageById(id);
    }

    @Override
    @Transactional
    public int updateMessageRead(Long userId) {
        return messageMapper.updateMessageRead(userId);
    }

    @Override
    public int countUnread(Long userId) {
        return messageMapper.countUnread(userId);
    }

    @Override
    @Transactional
    public int sendOrderMessage(Long userId, String title, String content) {
        Message message = new Message();
        message.setUserId(userId);
        message.setType("order");
        message.setTitle(title);
        message.setContent(content);
        message.setIsRead(0);
        message.setCreateTime(DateUtils.getNowDate());
        return messageMapper.insertMessage(message);
    }
}