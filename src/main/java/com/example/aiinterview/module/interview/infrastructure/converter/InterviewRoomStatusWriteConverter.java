package com.example.aiinterview.module.interview.infrastructure.converter;

import com.example.aiinterview.module.interview.domain.entity.InterviewRoomStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class InterviewRoomStatusWriteConverter implements Converter<InterviewRoomStatus, String> {
    @Override
    public String convert(InterviewRoomStatus source) {
        return source.name();
    }
}