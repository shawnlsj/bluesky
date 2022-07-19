package com.bluesky.mainservice.config.security.oauth2;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultAuthorizationRequestResolver;

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        defaultAuthorizationRequestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest =
                this.defaultAuthorizationRequestResolver.resolve(request);

        return authorizationRequest == null ? null : customAuthorizationRequest(authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        return defaultAuthorizationRequestResolver.resolve(request, clientRegistrationId);
    }

    private OAuth2AuthorizationRequest customAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest) {

        //application-oauth.yml 에 설정한 registration 과 매핑된 값을 가져온다
        Map<String, Object> additionalParameters = new LinkedHashMap<>(authorizationRequest.getAdditionalParameters());
        String registrationId = authorizationRequest.getAttributes().get("registration_id").toString();

        //각 리소스 서버에 알맞는 재인증 프로퍼티를 추가한다
        switch (registrationId) {
            case "google":
                additionalParameters.put("prompt", "consent");
                break;
            case "kakao":
                additionalParameters.put("prompt", "login");
                break;
            case "naver":
                additionalParameters.put("auth_type", "reauthenticate");
                break;
        }

        return OAuth2AuthorizationRequest
                .from(authorizationRequest)
                .additionalParameters(additionalParameters)
                .build();
    }
}
