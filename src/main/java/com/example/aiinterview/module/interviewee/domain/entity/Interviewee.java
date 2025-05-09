package com.example.aiinterview.module.interviewee.domain.entity;

import com.example.aiinterview.global.common.utils.CryptUtils;
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
public class Interviewee {

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
    private LocalDateTime createdAt;

    @Column("updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Interviewee(String email, String name, String password, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.email = email;
        this.name = name;
        this.password = hashPassword(password);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Interviewee create(String email, String name, String password) {
        return new Interviewee(email, name, password, LocalDateTime.now(), LocalDateTime.now());
    }
    private String hashPassword(String password){
        return CryptUtils.hashPassword(password);
    }
}
