package br.com.andre.authenticationsample.services.impl;

import br.com.andre.authenticationsample.config.WebSecurityConfig;
import br.com.andre.authenticationsample.entity.dto.response.LoginResponse;
import br.com.andre.authenticationsample.entity.dto.response.UsuarioResponse;
import br.com.andre.authenticationsample.services.TokenAuthenticationService;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;

@Service
public class TokenAuthenticationServiceImpl implements TokenAuthenticationService {

    private static final long EXPIRATION_TIME = 3600L;
    private static final String SECRET = "3ZS3LL4PP";
    private static final String TOKEN_PREFIX = "Bearer";
    private static final String CLAIM_ID = "ID";
    private static final String CLAIM_NOME = "NOME";
    private static final String CLAIM_CNPJ = "CNPJ";

    @Override
    public void addAuthentication(HttpServletResponse response, Authentication auth, UsuarioResponse usuario) throws IOException {

        LocalDateTime expire = LocalDateTime.now().plusSeconds(EXPIRATION_TIME);
        String jwt = Jwts.builder()
                .setSubject(auth.getName())
                .claim(CLAIM_ID, usuario.getId())
                .claim(CLAIM_NOME, usuario.getRazaoSocial())
                .claim(CLAIM_CNPJ, usuario.getCnpj())
                .setExpiration(Date.from(expire.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();


        setResponseBody(response, jwt, expire, "Bearer");

        response.setHeader(WebSecurityConfig.HEADER_STRING, TOKEN_PREFIX + " " + jwt);
    }

    @Override
    public Authentication getAuthentication(HttpServletRequest request) {
            String token = request.getHeader(WebSecurityConfig.HEADER_STRING);

            if (token != null) {

                String user = obterCorpoToken(token).getSubject();

                return user != null ? new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList())
                        : null;
            }

            return null;
    }

    @Override
    public String obterCnpj(String token) {
        String cnpj = (String) obterCorpoToken(token).get(CLAIM_CNPJ);

        return cnpj;
    }

    private Claims obterCorpoToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                .getBody();
    }

    private void setResponseBody(HttpServletResponse response, String token, LocalDateTime expirationDate, String escopo) throws IOException {

        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(token)
                .expiresIn(expirationDate)
                .tokenType(escopo)
                .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeCustomSerializer());
        JsonMapper jsonMapper = new
                JsonMapper();
        jsonMapper.registerModule(module);
        response.getWriter().write(jsonMapper.writeValueAsString(loginResponse));
        response.getWriter().flush();
    }
}
