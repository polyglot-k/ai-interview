package com.example.aiinterview.module.interview.infrastructure.converter;

import com.example.aiinterview.module.interview.domain.entity.InterviewSessionStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class InterviewSessionStatusReadConverter implements Converter<String, InterviewSessionStatus> {
    @Override
    public InterviewSessionStatus convert(String source) {
        return InterviewSessionStatus.valueOf(source.toUpperCase());
    }
}
