package com.example.aiinterview.module.member.application.dto;

import lombok.Data;

public record CreateMemberRequest(String email, String name, String password) {
}
