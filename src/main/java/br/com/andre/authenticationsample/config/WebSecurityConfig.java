package br.com.andre.authenticationsample.config;

import br.com.andre.authenticationsample.entity.Usuario;
import br.com.andre.authenticationsample.filters.JwtAuthenticationFilter;
import br.com.andre.authenticationsample.filters.JwtLoginFilter;
import br.com.andre.authenticationsample.mapper.LoginMapper;
import br.com.andre.authenticationsample.repositories.UsuarioRepository;
import br.com.andre.authenticationsample.services.TokenAuthenticationService;
import br.com.andre.authenticationsample.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter implements CommandLineRunner {

    public static final String LOGIN_URL = "/login";
    public static final String HEADER_STRING = "Authorization";

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final TokenAuthenticationService tokenAuthenticationService;
    private  final LoginMapper loginMapper;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            Usuario usuarioAdmin = Usuario.builder()
                    .cnpj("71336083000132")
                    .clientId("teste")
                    .razaoSocial("Razao Social")
                    .clientSecret(new Pbkdf2PasswordEncoder().encode("123"))
                    .build();

            usuarioRepository.save(usuarioAdmin);
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers(LOGIN_URL).permitAll()
                    .anyRequest().authenticated()
                .and()
                    .addFilterBefore(configLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                    .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(usuarioService)
                .passwordEncoder(new Pbkdf2PasswordEncoder());
    }

    private JwtLoginFilter configLoginFilter() throws Exception {
        JwtLoginFilter jwtLoginFilter = new JwtLoginFilter(LOGIN_URL, authenticationManager(), usuarioService, tokenAuthenticationService, loginMapper);

        return jwtLoginFilter;
    }

    private JwtAuthenticationFilter authenticationFilter() {
        JwtAuthenticationFilter jwtAuthenticationFilter= new JwtAuthenticationFilter(tokenAuthenticationService);

        return jwtAuthenticationFilter;
    }
}
