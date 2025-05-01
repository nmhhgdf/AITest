package org.example.langchain4j_springboot.config;

import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByLineSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.example.langchain4j_springboot.Langchain4jSpringbootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class VectorStoreConfig {

    @Bean
    CommandLineRunner insertTermOfServiceToVectorStore(QwenEmbeddingModel qwenEmbeddingModel,
                                                       EmbeddingStore embeddingStore) throws URISyntaxException {

        Path documentPath = Paths.get(Langchain4jSpringbootApplication.class.getClassLoader().getResource("rag/terms-of-service.txt").toURI());

        return args -> {

            DocumentParser documentParser = new TextDocumentParser();
            Document document = FileSystemDocumentLoader.loadDocument(documentPath, documentParser);

            DocumentByLineSplitter splitter = new DocumentByLineSplitter(
                    80,
                    20
            );
            List<TextSegment> segments = splitter.split(document);

            List<Embedding> embeddings = qwenEmbeddingModel.embedAll(segments).content();

            embeddingStore.addAll(embeddings, segments);
        };
    }

}
