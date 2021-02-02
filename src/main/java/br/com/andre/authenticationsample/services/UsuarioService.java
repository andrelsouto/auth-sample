package br.com.andre.authenticationsample.services;

import br.com.andre.authenticationsample.entity.dto.request.LoginRequest;
import br.com.andre.authenticationsample.entity.dto.response.UsuarioResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UsuarioService extends UserDetailsService {
    UsuarioResponse getUsuarioByClientId(String clientId);
    UsuarioResponse findUsuarioByClientIdAndClientSecretAndCnpjAndRazaoSocial(LoginRequest loginRequest) throws UsernameNotFoundException;
}
