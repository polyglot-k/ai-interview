package com.example.aiinterview.module.user.domain.entity;

import com.example.aiinterview.global.common.utils.CryptUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("users")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    @Id
    @Column("id")
    private Long id;

    @Column("email")
    private String email;

    @Column("name")
    private String name;

    @Column("password")
    private String password;
    /**
     * 직렬
     */

    @Column("created_at")
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    public User(String email, String name, String password, LocalDateTime createdAt) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.createdAt = createdAt;
    }

    public static User create(String email, String name, String password) {
        return new User(email, name, password, LocalDateTime.now());
    }
}
