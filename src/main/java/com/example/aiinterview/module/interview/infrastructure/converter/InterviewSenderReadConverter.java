package com.example.aiinterview.module.interview.infrastructure.converter;

import com.example.aiinterview.module.interview.domain.entity.InterviewSender;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class InterviewSenderReadConverter implements Converter<String, InterviewSender> {
    @Override
    public InterviewSender convert(String source) {
        return InterviewSender.valueOf(source.toUpperCase());
    }
}
