package com.example.aiinterview.module.interview.infrastructure.converter;

import com.example.aiinterview.module.interview.domain.vo.InterviewSessionStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class InterviewSessionStatusWriteConverter implements Converter<InterviewSessionStatus, String> {
    @Override
    public String convert(InterviewSessionStatus source) {
        return source.name();
    }
}