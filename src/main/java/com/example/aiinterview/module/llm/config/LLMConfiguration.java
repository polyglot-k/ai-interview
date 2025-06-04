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
    ResponseFormat analysisResponseFormat() {
        Map<String, JsonSchemaElement> properties = Map.of(
                "evaluations", JsonArraySchema.builder()
                        .items(JsonObjectSchema.builder()
                                .addNumberProperty("messageId", "표기된 messageId 값을 그대로 포함해주세요. (제시된 모든 messageId에 대한 분석이 필요하므로 모두 포함해야 합니다.)")
                                .addNumberProperty("score", "LLM 질문에 대한 USER 답변 점수를 100점 만점으로 평가해주세요. 부족한 부분이 있다면 낮은 점수를 부여해주세요.")
                                .addStringProperty("coreQuestion","LLM 질문의 중점만 잡아서, 질문을 한줄로 요약해줘 (불필요한거 제거 messageId 당 1개임, 최대 45글자로 요약)")
                                .addStringProperty("feedback", "해당 답변에 대한 상세 피드백을 신입 면접 상황을 고려하여 한국어로 작성해주세요. 긍정적인 답변이라면 칭찬해주세요.")
                                .required("messageId", "score", "feedback")
                                .build())
                        .build()
        );
        return ResponseFormat.builder()
                .type(JSON)
                .jsonSchema(JsonSchema.builder()
                        .name("InterviewSessionResult")
                        .rootElement(JsonObjectSchema.builder()
                                .addProperties(properties)
                                .addNumberProperty("overallAverageScore", "evaluations 내의 score 값들의 평균을 계산하여 알려주세요.")
                                .addStringProperty("overallFeedback", "전반적인 답변에 대한 총평을 약점, 개선할 점, 잘한 점을 포함하여 한국어로 간결하고 명확하게 요약해주세요.")
                                .required("overallAverageScore", "overallFeedback", "evaluations")
                                .build())
                        .build())
                .build();
    }
}
