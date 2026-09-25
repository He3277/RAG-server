package com.lssf.ragserver.config;
import com.lssf.ragserver.tool.RagTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 向 Spring 容器注册一个配置好的 ChatClient
 * 该 ChatClient 具备系统提示词(System Prompt)和工具调用(Tool Calling)能力
 * 在 Service 中注入使用,实现带知识库检索的 AI 对话
 */
@Configuration
public class ChatClientConfig {
    @Autowired
    private ChatClient.Builder chatClientBuilder;
    @Autowired
    private RagTool ragTool;

    @Bean
    public ChatClient chatClient() {
        // 在application.yml中配置了Ollama LLM提供商后,Spring AI会自动装配LLM相关配置,并创建该Builder
        return chatClientBuilder
                .defaultSystem("""
                你是一个拥有知识库的 AI 助手。
                当用户提问时,使用工具查找相关信息。
                遵循以下流程:
                1. 思考:分析用户需求
                2. 行动:使用searchKnowledgeBase工具检索相关文档
                3. 观察:查看检索到的信息
                4. 回答:基于检索到的信息提供全面的回复
                始终基于知识库中的信息回答问题。
                如果未找到相关信息,请明确说明。
                """)
                .defaultTools(ragTool)
                .build();
    }
}