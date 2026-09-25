package com.lssf.ragserver.loader;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class KnowledgeBaseLoader {
    @Value("${app.knowledge.dir}")
    private String knowledgeDir;
    @Autowired
    private VectorStore vectorStore;

    // Spring 在 Bean 的所有依赖注入完成后、正式提供服务前,自动调用此方法。
    @PostConstruct
    public void init() {
        try {
            List<Document> documents = loadDocuments();
            List<Document> splitDocuments = splitDocuments(documents);
            vectorStore.add(splitDocuments);
            log.info("已加载 {} 个文档,分割为 {} 个文本块", documents.size(), splitDocuments.size());
        } catch (Exception e) {
            log.error("加载知识库失败", e);
        }
    }

    private List<Document> loadDocuments() throws Exception {
        List<Document> documents = new ArrayList<>();
        // Spring 的资源解析器,支持 classpath:、file:、http: 等前缀,以及 *、** 等通配符。
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(knowledgeDir + "/*");
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                // 基于 Apache Tika 的文档读取器,自动识别 PDF、Word、Excel、PPT、TXT、HTML 等格式并提取纯文本。
                TikaDocumentReader reader = new TikaDocumentReader(resource);
                // reader.get() 返回 List<Document>
                // Document 包含:content:提取的文本内容;metadata:文件名、路径、页码等元数据
                documents.addAll(reader.get());
                log.info("已加载文档: {}", resource.getFilename());
            }
        }
        return documents;
    }

    private List<Document> splitDocuments(List<Document> documents) {
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                // 每个块的目标Token数量。Splitter会尽量靠近这个值,并在合适位置截断
                .withChunkSize(500)
                // 截断点必须超出此字符数时才会执行截断,避免切出太短的碎片化片段
                .withMinChunkSizeChars(200)
                .build();
        return splitter.apply(documents);
    }
}