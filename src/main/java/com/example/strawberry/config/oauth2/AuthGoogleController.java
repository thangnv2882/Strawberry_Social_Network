package com.example.strawberry.config.oauth2;

import com.example.strawberry.adapter.web.base.VsResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login/oauth2/code/google")
public class AuthGoogleController {

  ///oauth2/authorization/google

  private final GoogleService googleService;

  public AuthGoogleController(GoogleService googleService) {
    this.googleService = googleService;
  }

  @GetMapping()
  public ResponseEntity<?> save(OAuth2AuthenticationToken token) {
    System.out.println("luu di");
    return VsResponseUtil.ok(googleService.saveUserWithFirstLogin(token));
  }

}
