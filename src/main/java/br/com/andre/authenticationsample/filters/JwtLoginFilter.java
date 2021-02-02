package br.com.andre.authenticationsample.filters;

import br.com.andre.authenticationsample.config.WebSecurityConfig;
import br.com.andre.authenticationsample.entity.Usuario;
import br.com.andre.authenticationsample.entity.dto.request.LoginRequest;
import br.com.andre.authenticationsample.entity.dto.response.UsuarioResponse;
import br.com.andre.authenticationsample.enums.ValidacoesEnum;
import br.com.andre.authenticationsample.mapper.LoginMapper;
import br.com.andre.authenticationsample.services.TokenAuthenticationService;
import br.com.andre.authenticationsample.services.UsuarioService;
import br.com.andre.authenticationsample.utils.FilterUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

@Slf4j
public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final UsuarioService usuarioService;
    private final TokenAuthenticationService tokenAuthenticationService;
    private final LoginMapper loginMapper;

    public JwtLoginFilter(String url, AuthenticationManager authenticationManager, UsuarioService usuarioService, TokenAuthenticationService tokenAuthenticationService, LoginMapper loginMapper) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authenticationManager);
        this.usuarioService = usuarioService;
        this.tokenAuthenticationService = tokenAuthenticationService;
        this.loginMapper = loginMapper;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        try {

            if (!req.getRequestURI().equals(WebSecurityConfig.LOGIN_URL)) {
                chain.doFilter(req, res);
                return;
            }

            Authentication authenticationResult = attemptAuthentication(req, res);

            if (Objects.nonNull(authenticationResult)) {

                successfulAuthentication(req, res, chain, authenticationResult);
            } else {

                chain.doFilter(request, response);
            }

        } catch (AuthenticationException e) {
            unsuccessfulAuthentication(req, res, e);
        } catch (IOException e) {
            FilterUtils.tratarRespostaFilterException(res, ValidacoesEnum.JSON_INVALIDO, WebSecurityConfig.LOGIN_URL);
        }

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        SecurityContextHolder.clearContext();
        log.trace("Falha no processo de autenticação", failed);
        log.trace("Spring SecurityContextHolder limpo");
        log.trace("Tratando falha de autenticação");

        FilterUtils.tratarRespostaFilterException(response, ValidacoesEnum.ERRO_CRENDENCIAL, WebSecurityConfig.LOGIN_URL);

    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        LoginRequest credentials = new ObjectMapper()
                .readValue(request.getInputStream(), LoginRequest.class);

        if(loginRequestIsValid(credentials)) {
            usuarioService.findUsuarioByClientIdAndClientSecretAndCnpjAndRazaoSocial(credentials);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(credentials.getClientId(), credentials.getClientSecret(), Collections.emptyList()));
        }

        return null;

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        LoginRequest loginRequest = loginMapper.usuarioToLoginRequest((Usuario) authResult.getPrincipal());
        UsuarioResponse usuarioResponse = usuarioService.getUsuarioByClientId(loginRequest.getClientId());
        tokenAuthenticationService.addAuthentication(response, authResult, usuarioResponse);
    }

    private boolean loginRequestIsValid(LoginRequest credentials) {
        return Objects.nonNull(credentials.getClientId()) &&
                Objects.nonNull(credentials.getClientSecret()) &&
                Objects.nonNull(credentials.getCnpj()) &&
                Objects.nonNull(credentials.getRazaoSocial());
    }
}
