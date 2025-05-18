package com.example.aiinterview.module.interview.application.dto;

public record SseResponse(String event, String data) {
    public static SseResponse progress(String data) {
        return new SseResponse("progress", data);
    }

    public static SseResponse complete() {
        return new SseResponse("complete", "end");
    }

    public static SseResponse heartbeat() {
        return new SseResponse("heartbeat", "💓");
    }

    public static SseResponse terminated() {
        return new SseResponse("terminated", "종료");
    }
}
