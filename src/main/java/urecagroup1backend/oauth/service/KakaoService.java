package urecagroup1backend.oauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import urecagroup1backend.oauth.dto.AccessTokenDto;
import urecagroup1backend.oauth.dto.KakaoProfileDto;

@Service
@Transactional
public class KakaoService {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    public AccessTokenDto getAccessToken(String code) {
        // 인가 코드, clientId, client_secret, redirect_uri, grant_type

        RestClient restClient = RestClient.create();

        // MultiValueMap을 통해 자동으로 form-data 형식으로 body 조립 가능
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("grant_type", "authorization_code");

        ResponseEntity<AccessTokenDto> response = restClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                // ?code=xxx&client_id=yyy
                .body(params )
                // retrieve: 응답 body값만을 추출
                .retrieve()
                .toEntity(AccessTokenDto.class);

        System.out.println("응답 JSON " + response.getBody());
        return response.getBody();
    }

    public KakaoProfileDto getKakaoProfile(String token) {
        RestClient restClient = RestClient.create();

        ResponseEntity<KakaoProfileDto> response = restClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(KakaoProfileDto.class);

        System.out.println("profile JSON" + response.getBody());

        return response.getBody();
    }
}
