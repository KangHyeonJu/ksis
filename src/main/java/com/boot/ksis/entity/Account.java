package com.boot.ksis.entity;

import com.boot.ksis.constant.Gender;
import com.boot.ksis.constant.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "account")
@Getter
@Setter
public class Account {
    //계정 아이디
    @Id
    @Column(name = "account_id")
    private String accountId;

    //비밀번호
    @Column(nullable = false)
    private String password;

    //이름
    @Column(nullable = false, length = 20)
    private String name;

    //생년월일
    private String birthDate;

    //업무전화번호
    @Column(nullable = false)
    private String businessTel;

    //긴급연락처
    private String emergencyTel;

    //이메일
    @Column(nullable = false, length = 50)
    private String email;

    //직책
    @Column(length = 20)
    private String position;

    //성별
    @Enumerated(EnumType.STRING)
    private Gender gender;

    //활성화
    @Column(nullable = false, columnDefinition = "TINYINT(0)")
    @ColumnDefault("false") //활성화 O
    private boolean isActive;

    //권한
    @Enumerated(EnumType.STRING)
    private Role role;

}
