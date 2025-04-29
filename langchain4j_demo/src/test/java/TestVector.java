import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.Test;

public class TestVector {

    @Test
    public void testVector() {

        QwenEmbeddingModel embeddingModel = QwenEmbeddingModel.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .build();

        Response<Embedding> embed = embeddingModel.embed("你好");
        System.out.println(embed.content().toString());
        System.out.println(embed.content().vector().length);

    }

    @Test
    public void test02() {
        // --------------------------embedding------------------

        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        QwenEmbeddingModel embeddingModel = QwenEmbeddingModel.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .build();

        TextSegment segment1 = TextSegment.from("""
                预定航班：
                - 通过我们的网站或移动应用程序预订。
                - 预定时需要全额付款。
                - 确保个人信息（姓名、ID等）准确性，因为更正可能会产生25费用。
                """);

        Embedding embedding1 = embeddingModel.embed(segment1).content();
        embeddingStore.add(embedding1, segment1);

        TextSegment segment2 = TextSegment.from("""
                取消预订：
                - 最晚再航班起飞前 48 小时取消。
                - 取消费用： 经济舱 75 美元，豪华经济舱 50 美元，商务舱25美元。
                - 退款将在 7 哥工作日内处理。
                """);
        Embedding embedding2 = embeddingModel.embed(segment2).content();
        embeddingStore.add(embedding2, segment2);

        // -----------------------数据检索阶段--------------------------
        Embedding queryEmbedding = embeddingModel.embed("退票要多少钱").content();

        EmbeddingSearchRequest build = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
//                .maxResults(1)
                .minScore(0.7)
                .build();

        EmbeddingSearchResult<TextSegment> segmentEmbeddingSearchResult = embeddingStore.search(build);
        segmentEmbeddingSearchResult.matches().forEach(embedding -> {
            System.out.println(embedding.score());
            System.out.println(embedding.embedded().text());
        });

    }

}
