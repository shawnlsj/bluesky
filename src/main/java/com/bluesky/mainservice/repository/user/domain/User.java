package com.bluesky.mainservice.repository.user.domain;

import com.bluesky.mainservice.repository.BaseTimeEntity;
import com.bluesky.mainservice.repository.user.constant.AccountType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user",
        indexes = {
        @Index(name = "uuid", columnList = "uuid"),
        @Index(name = "email", columnList = "email")} )
public class User extends BaseTimeEntity {

    @Builder
    public User(String socialLoginId, String email, String password, String nickname, AccountType accountType) {
        this.socialLoginId = socialLoginId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.accountType = accountType;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "social_login_id")
    private String socialLoginId;

    @NotNull
    @Column(columnDefinition = "binary(16)", unique = true)
    private UUID uuid = UUID.randomUUID();

    @NotNull
    private String email;

    private String password;

    @Column(unique = true)
    private String nickname;

    @OneToMany(mappedBy = "user")
    private List<UserRole> userRoles = new ArrayList<>();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    private AccountType accountType;

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateNickName(String nickname) {
        this.nickname = nickname;
    }
}