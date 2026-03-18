package rafaelandrade.libraryapi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rafaelandrade.libraryapi.model.Usuario;
import rafaelandrade.libraryapi.service.UsuarioService;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UsuarioService userService;
    private final PasswordEncoder encoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String login = authentication.getName();
        String typedPassword = authentication.getCredentials().toString();

        Usuario userFound = userService.obterPorLogin(login);

        if (userFound == null){
            throw new UsernameNotFoundException("Username and/or Password incorrect");
        }

        String encryptedPassword = userFound.getSenha();

        boolean passwordsMatch = encoder.matches(typedPassword, encryptedPassword);

        if (passwordsMatch) {
            return new CustomAuthentication(userFound);
        }
        
        throw new UsernameNotFoundException("Username and/or Password incorrect");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
