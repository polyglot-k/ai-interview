package com.example.aiinterview.module.llm.interviewer.memory;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Repository
public class R2dbcChatMemoryStore implements ChatMemoryStore {

    private final DatabaseClient databaseClient;

    public R2dbcChatMemoryStore(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String sql = "SELECT messages_json FROM chat_memory WHERE memory_id = :memoryId";

        return Mono.fromCallable(() ->
                        databaseClient.sql(sql)
                                .bind("memoryId", memoryId)
                                .map(row -> row.get("messages_json", String.class))
                                .all()
                                .collectList()
                                .block()
                )
                .subscribeOn(Schedulers.boundedElastic())
                .map(jsonList -> {
                    if (jsonList == null) {
                        return List.<ChatMessage>of();
                    }
                    return jsonList.stream()
                            .flatMap(json -> ChatMessageDeserializer.messagesFromJson(json).stream())
                            .toList();
                })
                .block();
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        String json = ChatMessageSerializer.messagesToJson(messages);
        String upsertSql = "INSERT INTO chat_memory (memory_id, messages_json) VALUES (:memoryId, :messagesJson) " +
                "ON DUPLICATE KEY UPDATE messages_json = :messagesJson";

        Mono.fromRunnable(() ->
                        databaseClient.sql(upsertSql)
                                .bind("memoryId", memoryId)
                                .bind("messagesJson", json)
                                .then()
                                .block()
                )
                .subscribeOn(Schedulers.boundedElastic())
                .block();
    }

    @Override
    public void deleteMessages(Object memoryId) {
        String sql = "DELETE FROM chat_memory WHERE memory_id = :memoryId";

        Mono.fromRunnable(() ->
                        databaseClient.sql(sql)
                                .bind("memoryId", memoryId)
                                .then()
                                .block()
                )
                .subscribeOn(Schedulers.boundedElastic())
                .block();
    }
}
