package com;

// 카카오톡 간편 로그인

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.annotation.JsonProperty;

@Service
public class KakaoService {
    private final RestTemplate rest = new RestTemplate();
    private static final String TOKEN_URL   = "https://kauth.kakao.com/oauth/token";
    private static final String PROFILE_URL = "https://kapi.kakao.com/v2/user/me";

    @Value("${kakao.rest-api-key}")
    private String restApiKey;

    public static class KakaoTokenResponse {
        @JsonProperty("access_token")
        private String accessToken;
        public String getAccessToken() { return accessToken; }
    }

    public static class KakaoUserInfo {
        @JsonProperty("id")            private long id;
        @JsonProperty("properties")    private Properties props;
        @JsonProperty("kakao_account") private Account acct;

        public static class Properties { public String nickname; }
        public static class Account    { public String email;   }

        public long   getId()       { return id;            }
        public String getNickname() { return props.nickname; }
        public String getEmail()    { return acct.email;    }
    }

    public KakaoTokenResponse requestToken(String code, String redirectUri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("grant_type",   "authorization_code");
        params.add("client_id",    restApiKey);
        params.add("redirect_uri", redirectUri);
        params.add("code",         code);

        HttpEntity<MultiValueMap<String,String>> req = new HttpEntity<>(params, headers);
        return rest.postForObject(TOKEN_URL, req, KakaoTokenResponse.class);
    }

    public KakaoUserInfo requestUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> req = new HttpEntity<>(headers);
        ResponseEntity<KakaoUserInfo> resp = rest.exchange(PROFILE_URL, HttpMethod.GET, req, KakaoUserInfo.class);
        return resp.getBody();
    }
}
