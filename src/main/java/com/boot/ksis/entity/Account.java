package com.boot.ksis.entity;

import com.boot.ksis.constant.Gender;
import com.boot.ksis.constant.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "account")
@Getter
@Setter
public class Account implements UserDetails {
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
    private Boolean isActive;

    //권한
    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Role에서 권한을 가져와서 GrantedAuthority로 변환
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return accountId;
    }

    @Override
    public boolean isAccountNonExpired() {
        // 계정 만료 여부: 기본적으로 false를 반환
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠금 여부: 기본적으로 false를 반환
        return isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 자격 증명 만료 여부: 기본적으로 false를 반환
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 계정 활성화 여부: 기본적으로 false를 반환
        return isActive;
    }
}
