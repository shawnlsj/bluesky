package com.bluesky.mainservice.service.user.security;

import com.bluesky.mainservice.repository.user.RoleRepository;
import com.bluesky.mainservice.repository.user.UserRepository;
import com.bluesky.mainservice.repository.user.UserRoleRepository;
import com.bluesky.mainservice.repository.user.constant.AccountType;
import com.bluesky.mainservice.repository.user.constant.RoleType;
import com.bluesky.mainservice.repository.user.domain.Role;
import com.bluesky.mainservice.repository.user.domain.User;
import com.bluesky.mainservice.repository.user.domain.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //api 를 호출하여 유저 정보를 가져온다
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        //가져온 유저 정보를 매핑해줄 객체를 만든다
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        AttributeMapper attributeMapper = new AttributeMapper(registrationId, attributes);

        //유저를 DB 에서 찾을 때 필요한 값들을 가져온다
        String id = attributeMapper.getId();
        AccountType accountType = AccountType.valueOf(registrationId.toUpperCase());


        //유저 조회
        User user = userRepository.findBySocialLoginIdAndAccountType(id, accountType);

        //DB 에 존재 하지 않는 유저일 경우
        if (user == null) {
            user = User.builder()
                    .socialLoginId(id)
                    .email(attributeMapper.getEmail())
                    .accountType(accountType)
                    .build();
            userRepository.save(user);

            //중간 테이블에 다대다 연관관계를 저장
            Role role = roleRepository.findByRoleType(RoleType.USER);
            userRoleRepository.save(new UserRole(user, role));

        } else {
            //유저 정보를 새로 가져온 정보로 업데이트
            updateUser(user, attributeMapper);
        }

        //인증 정보를 담을 객체를 생성
        return OAuth2UserDetails.builder()
                .oAuth2User(oAuth2User)
                .user(user)
                .build();
    }

    private void updateUser(User user, AttributeMapper attributeMapper) {
        user.updateEmail(attributeMapper.getEmail());
    }

    /**
     * api 로 조회해온 정보에서 원하는 필드 값을 꺼내기 위해 사용하는 클래스입니다.
     */
    @Getter
    private static class AttributeMapper {
        private final String registrationId;
        private final Map<String, Object> attributes;
        private final String email;
        private final String id;

        private AttributeMapper(String registrationId, Map<String, Object> attributes) {
            this.registrationId = registrationId;
            this.attributes = attributes;
            this.email = findEmail();
            this.id = findId();
        }

        private String findEmail() {
            switch (registrationId) {
                case "google":
                    return (String) attributes.get("email");
                case "kakao":
                    Map<String, Object> kakaoMap = (Map) attributes.get("kakao_account");
                    return (String) kakaoMap.get("email");
                case "naver":
                    Map<String, Object> naverMap = (Map) attributes.get("response");
                    return (String) naverMap.get("email");
                default:
                    return "";
            }
        }

        private String findId() {
            switch (registrationId) {
                case "google":
                    return (String) attributes.get("sub");
                case "kakao":
                    return String.valueOf(attributes.get("id"));
                case "naver":
                    Map<String, Object> naverMap = (Map) attributes.get("response");
                    return (String) naverMap.get("id");
                default:
                    return "";
            }
        }
    }
}
