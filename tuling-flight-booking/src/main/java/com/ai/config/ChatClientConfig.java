package com.ai.config;

import com.ai.services.BookLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class ChatClientConfig {

    @Bean
    CommandLineRunner ingestTermOfServiceToVectorStore(
            EmbeddingModel embeddingModel,
            VectorStore vectorStore,
            @Value("classpath:rag/terms-of-service.txt") Resource termsOfServiceDocs
    ) {
        return args -> {
            vectorStore.write(
                    new TokenTextSplitter().transform(
                            new TextReader(termsOfServiceDocs).read()
                    )
            );
        };
    }

    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder,
                                 ChatMemory chatMemory,
                                 VectorStore vectorStore) {
        return builder
                .defaultSystem("""
                        您是航空公司的客户聊天支持代理。请以友好、乐于助人且愉快的方式来回复。
                        您正在通过在线聊天系统与客户互动。
                        在提供鱼贯预订或取消预订的信息之前，您必须始终
                        从用户处获取以下信息：预定号、客户姓名。
                        在询问用户之前，请检查消息历史记录以获取此信息。
                        在更改或退订之前，需要询问用户确认是否确认退订。
                        请讲中文。
                        今天的日志是 {current_date}
                        """)
                .defaultAdvisors(
                        new PromptChatMemoryAdvisor(chatMemory),
                        new BookLoggerAdvisor(),
                        new QuestionAnswerAdvisor(vectorStore)
                )
                .defaultTools(
                        "cancelBooking",
                        "bookingDetail",
                        "changeBooking"
                )
                .build();
    }

}
