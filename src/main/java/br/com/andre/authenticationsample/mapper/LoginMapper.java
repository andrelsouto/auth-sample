package br.com.andre.authenticationsample.mapper;

import br.com.andre.authenticationsample.entity.Usuario;
import br.com.andre.authenticationsample.entity.dto.request.LoginRequest;
import org.mapstruct.Mapper;

@Mapper
public interface LoginMapper {

    LoginRequest usuarioToLoginRequest(Usuario usuario);

}
