package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.domain.Message;
import com.ruoyi.system.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private IMessageService messageService;

    @GetMapping("/list")
    public R getMessageList(@RequestParam Long userId) {
        List<Message> messages = messageService.selectMessageList(userId);
        return R.ok(messages);
    }

    @GetMapping("/{id}")
    public R getMessageById(@PathVariable Long id) {
        Message message = messageService.selectMessageById(id);
        return R.ok(message);
    }

    @PostMapping("/add")
    public R addMessage(@RequestBody Message message) {
        messageService.insertMessage(message);
        return R.ok("发送成功");
    }

    @DeleteMapping("/{id}")
    public R deleteMessage(@PathVariable Long id) {
        messageService.deleteMessageById(id);
        return R.ok("删除成功");
    }

    @PutMapping("/read")
    public R updateMessageRead(@RequestParam Long userId) {
        messageService.updateMessageRead(userId);
        return R.ok("已读");
    }

    @GetMapping("/unread")
    public R countUnread(@RequestParam Long userId) {
        int count = messageService.countUnread(userId);
        return R.ok(count);
    }
}