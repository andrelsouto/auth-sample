package br.com.andre.authenticationsample.services;

import br.com.andre.authenticationsample.entity.dto.response.UsuarioResponse;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface TokenAuthenticationService {

    void addAuthentication(HttpServletResponse response, Authentication auth, UsuarioResponse usuario) throws IOException;
    Authentication getAuthentication(HttpServletRequest request);
    String obterCnpj(String token);

}
