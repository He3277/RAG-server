package com.lssf.ragserver.config;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {
    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        // Spring AI 框架提供的一个内存向量存储实现
        // embeddingModel从yaml自动配置引入
        return SimpleVectorStore.builder(embeddingModel).build();
    }
}