package com.example.aiinterview.module.interview.infrastructure.converter;

import com.example.aiinterview.module.interview.domain.entity.InterviewSender;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class InterviewSenderWriteConverter implements Converter<InterviewSender, String> {
    @Override
    public String convert(InterviewSender source) {
        return source.name();
    }
}