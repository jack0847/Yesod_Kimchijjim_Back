package org.example.yesodkimchijjimback.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import org.example.yesodkimchijjimback.domain.User;
import org.example.yesodkimchijjimback.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {
    private final UserRepository userRepository;

    @Value("${google.client-id}")
    private String googleClientId;

    public User loginWithGoogleIdToken(String idTokenString) {
        GoogleIdToken.Payload payload = verify(idTokenString);

        // 필요한 정보 뽑기
        String sub = payload.getSubject();   // google 고유 ID
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        //  구글 sub 기준으로 기존 유저 찾기, 예전에 로그인 했던 사람이면 googlesub이 저장되있음.
        User user = userRepository.findByGoogleSub(sub).orElse(null);
        if (user != null) {
            user.updateName(name);
            return userRepository.save(user);
        }

        User existingUserByEmail = userRepository.findByEmail(email).orElse(null);

        if (existingUserByEmail != null) {
            existingUserByEmail.updateGoogleSub(sub);
            existingUserByEmail.updateName(name);
            return userRepository.save(existingUserByEmail);
        }

        // 완전 신규 회원
        User newUser = User.builder()
                .name(name != null ? name : "google-user")
                .email(email)
                .googleSub(sub)
                .build();

        return userRepository.save(newUser);
    }

    // id_token이 진짜인지 검증
    private GoogleIdToken.Payload verify(String idTokenString) {
        try {
            //GoogleIdTokenVerifier: 구글이 제공하는 검증 도구
            GoogleIdTokenVerifier verifier =
                    new GoogleIdTokenVerifier.Builder(
                            GoogleNetHttpTransport.newTrustedTransport(),
                            GsonFactory.getDefaultInstance())
                            .setAudience(Collections.singletonList(googleClientId))
                            .build();

            //검증 성공하면 token 객체 반환
            GoogleIdToken token = verifier.verify(idTokenString);
            if (token == null) {
                throw new IllegalArgumentException("Invalid Google ID Token");
            }
            return token.getPayload();
        } catch (Exception e) {
            throw new IllegalArgumentException("Google ID Token verification failed", e);
        }
    }
}
