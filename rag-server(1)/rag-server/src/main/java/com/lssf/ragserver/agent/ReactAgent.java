package com.lssf.ragserver.agent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReactAgent {
    @Autowired
    private ChatClient chatClient;

    /**
     * 创建一个新的提示词构建器(Prompt Builder)
     * 设置用户消息(User Message),即用户的问题/输入
     * 发送请求给 LLM,等待响应;如果 LLM 决定调用工具,框架会自动处理
     * 从响应中提取文本内容(LLM 的最终回答)
     * @param userMessage
     * @return
     */
    public String chat(String userMessage) {
        return chatClient.prompt()
                .user(userMessage)
                .call()
                .content();
    }
}