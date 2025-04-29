package org.example.langchain4j_springboot.config;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.*;
import org.example.langchain4j_springboot.service.ToolService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    public interface Assistant {
        String chat(String message);
        TokenStream stream(String message);

        @SystemMessage("""
                您是“XXX”航空公司的聊天助手。
                再提供有关预定活取消信息之前，您必须始终从用户处获取以下信息：
                预定号、客户姓名。
                今天的日志是{{current_date}}。
                """)
        TokenStream stream(@UserMessage String message, @V("current_date") String currentDate);
    }

    public interface AssistantUnique {
        String chat(@MemoryId int memoryId, @UserMessage String userMessage);
        TokenStream stream(@MemoryId int memoryId, @UserMessage String userMessage);
    }

    @Bean
    public Assistant assistant(ChatLanguageModel chatLanguageModel,
                               StreamingChatLanguageModel streamingChatModel,
                               ToolService toolService) {
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        return AiServices.builder(Assistant.class)
                .tools(toolService)
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

    @Bean
    public AssistantUnique assistantUniquePersistent(ChatLanguageModel chatLanguageModel,
                                                     StreamingChatLanguageModel streamingChatModel,
                                                     PersistentChatMemoryStore persistentChatMemoryStore) {

        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(100)
                .chatMemoryStore(persistentChatMemoryStore)
                .build();

        return AiServices.builder(AssistantUnique.class)
                .chatLanguageModel(chatLanguageModel)
                .streamingChatLanguageModel(streamingChatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .build();

    }

}
