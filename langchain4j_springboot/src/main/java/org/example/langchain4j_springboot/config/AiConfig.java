package org.example.langchain4j_springboot.config;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    public interface Assistant {
        String chat(String message);
        TokenStream stream(String message);
    }

    public interface AssistantUnique {
        String chat(@MemoryId int memoryId, @UserMessage String userMessage);
        TokenStream stream(@MemoryId int memoryId, @UserMessage String userMessage);
    }

    @Bean
    public Assistant assistant(ChatLanguageModel chatLanguageModel, StreamingChatLanguageModel streamingChatModel) {
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        return AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .streamingChatLanguageModel(streamingChatModel)
                .chatMemory(chatMemory)
                .build();
    }

    @Bean
    public AssistantUnique assistantUnique(ChatLanguageModel chatLanguageModel, StreamingChatLanguageModel streamingChatModel) {

        return AiServices.builder(AssistantUnique.class)
                .chatLanguageModel(chatLanguageModel)
                .streamingChatLanguageModel(streamingChatModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory
                        .builder()
                        .maxMessages(10)
                        .id(memoryId)
                        .build())
                .build();
    }

}
