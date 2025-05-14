package com;

// 카카오톡 로그인 하면 데이터 베이스랑 연결해주고 출력까지 흐름? 이라고,,,

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/kakao")
public class ApiAuthController {
    private final KakaoService service;
    private final UserDao       dao;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    public ApiAuthController(KakaoService service, UserDao dao) {
        this.service = service;
        this.dao     = dao;
    }

    @PostMapping("/login")
    public Map<String,Object> login(@RequestBody Map<String,String> body,
                                    HttpSession session) throws Exception {
        String code = body.get("code");
        var tokenResp = service.requestToken(code, redirectUri);
        var info      = service.requestUserInfo(tokenResp.getAccessToken());

        User user = dao.findByKakaoId(info.getId());
        if (user == null) {
            user = new User(info.getId(), info.getNickname(), info.getEmail());
            user = dao.insertKakaoUser(user);
        }

        session.setAttribute("loginUser", user);
        return Map.of(
          "id",          user.getId(),
          "nickname",    user.getNickname(),
          "email",       user.getEmail(),
          "accessToken", tokenResp.getAccessToken()
        );
    }
}