package br.com.andre.authenticationsample.services.impl;

import br.com.andre.authenticationsample.entity.Usuario;
import br.com.andre.authenticationsample.entity.dto.request.LoginRequest;
import br.com.andre.authenticationsample.entity.dto.response.UsuarioResponse;
import br.com.andre.authenticationsample.mapper.UsuarioMapper;
import br.com.andre.authenticationsample.repositories.UsuarioRepository;
import br.com.andre.authenticationsample.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public UserDetails loadUserByUsername(String clientId) throws UsernameNotFoundException {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByClientId(clientId);

        return usuarioOptional.orElseThrow(() -> {
            throw new UsernameNotFoundException(clientId);
        });
    }

    @Override
    public UsuarioResponse getUsuarioByClientId(String clientId) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByClientId(clientId);
        return usuarioMapper.usuarioParaUsuarioDto(usuarioOptional.orElseGet(Usuario::new));
    }

    @Override
    public UsuarioResponse findUsuarioByClientIdAndClientSecretAndCnpjAndRazaoSocial(LoginRequest loginRequest) throws UsernameNotFoundException {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByClientIdAndCnpjAndRazaoSocial(loginRequest.getClientId(), loginRequest.getCnpj(), loginRequest.getRazaoSocial());

        return usuarioMapper.usuarioParaUsuarioDto(usuarioOptional.orElseThrow(() -> {
            throw new UsernameNotFoundException("teste");
        }));
    }
}
