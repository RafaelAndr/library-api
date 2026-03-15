package rafaelandrade.libraryapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import rafaelandrade.libraryapi.security.CustomUserDetailsService;
import rafaelandrade.libraryapi.service.UsuarioService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .formLogin(configurer -> {
                    configurer.loginPage("/login");
                })
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers("/login").permitAll();
                    authorize.requestMatchers(HttpMethod.POST, "/usuarios").permitAll();
//                    authorize.requestMatchers(HttpMethod.POST, "/autores/**").hasAuthority("CADASTRAR_AUTOR");
//                    authorize.requestMatchers(HttpMethod.DELETE, "/autores/**").hasRole("ADMIN");
//                    authorize.requestMatchers(HttpMethod.PUT, "/autores/**").hasRole("ADMIN");
//                    authorize.requestMatchers(HttpMethod.POST, "/livros/**").hasRole("ADMIN");
//                    authorize.requestMatchers(HttpMethod.DELETE, "/livros/**").hasRole("ADMIN");
//                    authorize.requestMatchers(HttpMethod.PUT, "/livros/**").hasRole("ADMIN");
//                    authorize.requestMatchers(HttpMethod.GET, "/livros/**").hasAnyRole("USER", "ADMIN");
//                    authorize.requestMatchers(HttpMethod.GET, "/autores/**").hasAnyRole("USER", "ADMIN");
                    authorize.anyRequest().authenticated();
                })
                .oauth2Login(oauth2 -> {
                    oauth2
                            .loginPage("/login");
                })
                .oauth2ResourceServer(oauth2RS -> oauth2RS.jwt(Customizer.withDefaults()))
                .build();
    }

    public GrantedAuthorityDefaults grantedAuthorityDefaults(){
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(){
        var authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("");

        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return converter;
    }




//    @Bean
//    public UserDetailsService userDetailsServiceMemory(PasswordEncoder passwordEncoder) {
//
//        UserDetails user1 = User.builder()
//                .username("usuario")
//                .password(passwordEncoder.encode("123"))
//                .roles("USER")
//                .build();
//
//        UserDetails user2 = User.builder()
//                .username("admin")
//                .password(passwordEncoder.encode("321"))
//                .authorities("CADASTRAR_AUTOR")
//                .roles("ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(user1, user2);
//    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioService usuarioService) {

        return new CustomUserDetailsService(usuarioService);

    }
}
