package org.example.langchain4j_springboot.config;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class PersistentChatMemoryStore implements ChatMemoryStore {

    private final StringRedisTemplate stringRedisTemplate;
    private static final String CHAT_MEMORY_PREFIX = "chat:memory:";

    public PersistentChatMemoryStore(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String key = CHAT_MEMORY_PREFIX + memoryId.toString();
        String json = stringRedisTemplate.opsForValue().get(key);
        return ChatMessageDeserializer.messagesFromJson(json);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        String key = CHAT_MEMORY_PREFIX + memoryId.toString();
        String json = ChatMessageSerializer.messagesToJson(list);
        stringRedisTemplate.opsForValue().set(key, json, 7, TimeUnit.DAYS);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        String key = CHAT_MEMORY_PREFIX + memoryId.toString();
        stringRedisTemplate.delete(key);
    }

}
