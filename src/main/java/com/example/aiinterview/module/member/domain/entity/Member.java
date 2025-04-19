package com.example.aiinterview.module.member.domain.entity;

//import com.example.aiinterview.common.utils.PasswordUtils;
import com.example.aiinterview.common.utils.CryptUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

@Table("members")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    private Long id;

    private String email;

    private String name;
    private String password;

    @Column("created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column("updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Member(String email, String name, String password,LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.email = email;
        this.name = name;
        this.password = hashPassword(password);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Member create(String email, String name, String password) {
        return new Member(email, name, password, LocalDateTime.now(), LocalDateTime.now());
    }
    private String hashPassword(String password){
        return CryptUtils.hashPassword(password);
    }
}
