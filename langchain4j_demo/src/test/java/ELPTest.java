import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByCharacterSplitter;
import dev.langchain4j.data.document.splitter.DocumentByRegexSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.Test;

import java.util.List;

public class ELPTest {

    @Test
    public void test01() {
        Document document = ClassPathDocumentLoader.loadDocument("rag/terms-of-service.txt", new TextDocumentParser());
        System.out.println(document.text());
    }

    @Test
    public void test02() {
        Document document = ClassPathDocumentLoader.loadDocument("rag/terms-of-service.txt", new TextDocumentParser());
//        System.out.println(document.text());

        DocumentByCharacterSplitter splitter = new DocumentByCharacterSplitter(
                20,
                10
        );
        List<TextSegment> segments = splitter.split(document);
        System.out.println(segments);
    }

    @Test
    public void test03() {
        Document document = ClassPathDocumentLoader.loadDocument("rag/terms-of-service.txt", new TextDocumentParser());

        DocumentByRegexSplitter splitter = new DocumentByRegexSplitter(
                "\\n\\d+\\.",    // 匹配”1. 标题“格式
                "\n",   // 保留换行符段落连续符
                80,
                20,
                new DocumentByCharacterSplitter(100, 20)
        );
        List<TextSegment> segments = splitter.split(document);
        System.out.println(segments);
    }

    @Test
    public void test04() {
        Document document = ClassPathDocumentLoader.loadDocument("rag/terms-of-service.txt", new TextDocumentParser());

        DocumentByRegexSplitter splitter = new DocumentByRegexSplitter(
                "\\n\\d+\\.",    // 匹配”1. 标题“格式
                "\n",   // 保留换行符段落连续符
                80,
                20,
                new DocumentByCharacterSplitter(100, 20)
        );
        List<TextSegment> segments = splitter.split(document);
//        System.out.println(segments);

        QwenEmbeddingModel embeddingModel = QwenEmbeddingModel.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .build();

        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
//        System.out.println(embeddings);

        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.addAll(embeddings, segments);

        Response<Embedding> embed = embeddingModel.embed("退费费用");
        EmbeddingSearchRequest build = EmbeddingSearchRequest.builder()
                .queryEmbedding(embed.content())
                .build();

        EmbeddingSearchResult<TextSegment> results = embeddingStore.search(build);
        for (EmbeddingMatch<TextSegment> match : results.matches()) {
            System.out.println(match.embedded().text() + "\n分数：" + match.score());
        }

        ChatLanguageModel model = QwenChatModel.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .modelName("qwen-max")
                .build();

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(5)
                .minScore(0.6)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .contentRetriever(contentRetriever)
                .build();

        System.out.println(assistant.chat("退费费用"));
    }


}
