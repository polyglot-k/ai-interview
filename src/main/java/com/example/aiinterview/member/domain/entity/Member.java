//package com.example.aiinterview.member.model;
//
//import com.example.aiinterview.common.BaseEntityWithUpdate;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Entity(name="members")
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//public class Member extends BaseEntityWithUpdate {
//    @Column(unique = true)
//    private String email;
//
//    private String name;
//
//    public static Member create(String email, String name){
//        return new Member(email, name);
//    }
//}
