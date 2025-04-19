package com.example.aiinterview.module.interview.infrastructure.converter;

import com.example.aiinterview.module.interview.domain.entity.InterviewRoomStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class InterviewRoomStatusReadConverter implements Converter<String, InterviewRoomStatus> {
    @Override
    public InterviewRoomStatus convert(String source) {
        return InterviewRoomStatus.valueOf(source.toUpperCase());
    }
}
