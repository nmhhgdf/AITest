package com.ai.config;

import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreAutoConfiguration;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

@Configuration
@EnableAutoConfiguration(exclude = {RedisVectorStoreAutoConfiguration.class})
public class VectorStoreConfig {

    @Bean
    @ConditionalOnProperty(name = "ai.vectorstore.type", havingValue = "local")
    public VectorStore vectorEmbeddingStore(EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }

    @Bean
    @ConditionalOnProperty(name = "ai.vectorstore.type", havingValue = "redis")
    public VectorStore vectorRedisStore(
            EmbeddingModel embeddingModel,
            JedisPooled jedisPooled) {

        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName("airline-chat-index")
                .prefix("airline:vector:")
                .initializeSchema(true)
                .build();

    }

    @Bean
    @ConditionalOnProperty(name = "ai.vectorstore.type", havingValue = "redis")
    public JedisPooled jedisPooled(RedisProperties redisProperties) {
        return new JedisPooled(
                redisProperties.getHost(),
                redisProperties.getPort(),
                redisProperties.getUsername(),
                redisProperties.getPassword()
        );
    }

}
