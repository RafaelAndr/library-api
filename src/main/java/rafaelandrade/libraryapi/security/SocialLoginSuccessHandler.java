package rafaelandrade.libraryapi.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import rafaelandrade.libraryapi.model.Usuario;
import rafaelandrade.libraryapi.service.UsuarioService;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SocialLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final String PATTERN_PASSWORD = "123";
    private final UsuarioService usuarioService;

    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws ServletException, IOException {

        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();

        String email = oAuth2User.getAttribute("email");

        Usuario user = usuarioService.getByEmail(email);

        if (user == null){
            user = registerUser(email);
        }

        authentication = new CustomAuthentication(user);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private Usuario registerUser(String email) {
        Usuario user;
        user = new Usuario();
        user.setEmail(email);
        user.setLogin(getUserName(email));
        user.setSenha(PATTERN_PASSWORD);
        user.setRoles(List.of("OPERADOR"));
        usuarioService.salvar(user);
        return user;
    }

    private String getUserName(String email){
        return email.substring(0, email.indexOf("@"));
    }

}
