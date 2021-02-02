package br.com.andre.authenticationsample.filters;

import br.com.andre.authenticationsample.config.WebSecurityConfig;
import br.com.andre.authenticationsample.enums.ValidacoesEnum;
import br.com.andre.authenticationsample.services.TokenAuthenticationService;
import br.com.andre.authenticationsample.utils.FilterUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final TokenAuthenticationService tokenAuthenticationService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        try {

            Authentication authentication = tokenAuthenticationService.getAuthentication(req);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            resp.setHeader("X-CNPJ", tokenAuthenticationService.obterCnpj(req.getHeader(WebSecurityConfig.HEADER_STRING)));
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (ExpiredJwtException e) {

            FilterUtils.tratarRespostaFilterException(resp, ValidacoesEnum.TOKEN_EXPIRADO, (req).getRequestURI());
        } catch (SignatureException e) {

            FilterUtils.tratarRespostaFilterException(resp, ValidacoesEnum.TOKEN_INVALIDO, (req).getRequestURI());
        }

    }
}
