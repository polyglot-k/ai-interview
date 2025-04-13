package com.example.aiinterview.llm;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.FluxSink;

@RequiredArgsConstructor
public class LLMStreamingResponseHandler implements StreamingChatResponseHandler {

    private final FluxSink<String> sink;

    @Override
    public void onPartialResponse(String partialResponse) {
        // Emit partial response through the sink
        sink.next(partialResponse);
    }

    @Override
    public void onCompleteResponse(ChatResponse completeResponse) {
        // Complete the stream when the response is finished
        sink.complete();
    }

    @Override
    public void onError(Throwable error) {
        // Handle any errors and complete with error
        sink.error(error);
    }
}
