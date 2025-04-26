package com.example.aiinterview.module.llm.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.json.JsonArraySchema;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.request.json.JsonSchemaElement;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiStreamingChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static dev.langchain4j.model.chat.request.ResponseFormatType.JSON;

@Configuration
public class LLMConfiguration {
    @Value("${llm.google.api-key}")
    private String API_KEY;
    @Value("${llm.google.model-name}")
    private String MODEL_NAME;

    @Bean
    public StreamingChatLanguageModel geminiStreamModel() {
        return GoogleAiGeminiStreamingChatModel.builder()
                .apiKey(API_KEY)
                .modelName(MODEL_NAME)
                .build();
    }
    @Bean
    ChatLanguageModel geminiModel(){
        return GoogleAiGeminiChatModel.builder()
                .apiKey(API_KEY)
                .modelName(MODEL_NAME) //PRO로 변경
                .logRequestsAndResponses(true)
                .build();
    }
    @Bean
    ResponseFormat analysisResponseFormat(){
        Map<String, JsonSchemaElement> properties = Map.of(
                "evaluations", JsonArraySchema.builder()
                        .items(JsonObjectSchema.builder()
                                .addStringProperty("problemContent","면접관(LLM)의 질문을 1개씩 넣어줘")
                                .addNumberProperty("score","답변에 대한 점수를 적어줘 (100점 만점), 잘못된 부분이 존재하면 점수를 엄청 낮게 줘")
                                .addStringProperty("feedback","해당 답변에 대한 피드백을 자세하게 한글로 해줘.(신입 면접 상황이야) 만약 피드백이 없다면, 칭찬해줘")
                                .required("problemContent","score","feedback")
                                .build())
                        .build()
        );
        return ResponseFormat.builder()
                .type(JSON) // type can be either TEXT (default) or JSON
                .jsonSchema(JsonSchema.builder()
                        .name("InterviewSessionResult") // OpenAI requires specifying the name for the schema
                        .rootElement(JsonObjectSchema.builder() // see [1] below
                                .addProperties(properties)
                                .addNumberProperty("averageScore","evaluations 의 score들을 기반으로 평균을 내줘")
                                .addIntegerProperty("totalFeedback", "전체적인 피드백을 한글로 해줘")

                                .required("averageScore", "totalFeedback", "evaluations") // see [2] below
                                .build())
                        .build())
                .build();
    }
}
