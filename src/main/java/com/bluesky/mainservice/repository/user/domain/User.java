package com.bluesky.mainservice.repository.user.domain;

import com.bluesky.mainservice.repository.BaseTimeEntity;
import com.bluesky.mainservice.repository.user.constant.AccountType;
import com.bluesky.mainservice.repository.user.constant.RoleType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user",
        indexes = {
        @Index(name = "uuid", columnList = "uuid"),
        @Index(name = "email", columnList = "email")} )
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "social_login_id")
    private String socialLoginId;

    @NotNull
    @Column(columnDefinition = "binary(16)", unique = true)
    private UUID uuid;

    @NotNull
    private String email;

    private String password;

    @Column(unique = true)
    private String nickname;

    @Column(name = "profile_image")
    private String profileImage;

    @OneToMany(mappedBy = "user")
    private List<UserRole> userRoles = new ArrayList<>();

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    private AccountType accountType;

    public User(Long id) {
        this.id = id;
    }

    @Builder
    private User(String socialLoginId,
                 String email,
                 String password,
                 String nickname,
                 @NonNull AccountType accountType) {
        this.socialLoginId = socialLoginId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.accountType = accountType;
        uuid = UUID.randomUUID();
    }

    @PostLoad
    private void initRole() {
        log.info("사용자 권한 정보 초기화");
        Hibernate.initialize(userRoles);
        Hibernate.initialize(userRoles.get(0).getRole());
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateNickName(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public boolean isRegisteredUser() {
        return StringUtils.hasText(nickname);
    }

    public boolean isAdmin() {
        for (UserRole userRole : userRoles) {
            if (userRole.getRole().getRoleType() == RoleType.USER) {
                return false;
            }
        }
        return true;
    }
}