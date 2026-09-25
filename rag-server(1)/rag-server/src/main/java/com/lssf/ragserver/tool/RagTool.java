package com.lssf.ragserver.tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RagTool {
    @Autowired
    private VectorStore vectorStore;

    /**
     * Tool:将这个方法暴露给AI代理作为可调用的工具
     * AI代理会根据description判断何时使用这个工具
     * @param query
     * @return
     */
    @Tool(description = "在知识库中搜索与用户查询相关的信息。当你需要从知识库中查找特定信息时使用此工具。")
    public String searchKnowledgeBase(@ToolParam(description = "用于查找相关文档的搜索查询") String query) {
        log.info("RAG 工具被调用,查询内容: {}", query);
        SearchRequest request = SearchRequest.builder()
                // 用户的搜索查询
                .query(query)
                // 返回最相似的4个文档
                .topK(4)
                // 相似度阈值50%,低于此值的结果会被过滤
                .similarityThreshold(0.0)
                .build();
        List<Document> documents = vectorStore.similaritySearch(request);
        if (documents.isEmpty()) {
            return "在知识库中未找到相关信息。";
        }
        return documents.stream()
                // 提取每个文档的文本内容
                .map(Document::getText)
                // 用分隔符合并所有文档
                .collect(Collectors.joining("\n\n---\n\n"));
    }
}