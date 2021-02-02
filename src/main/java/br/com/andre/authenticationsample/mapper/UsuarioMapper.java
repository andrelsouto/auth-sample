package br.com.andre.authenticationsample.mapper;

import br.com.andre.authenticationsample.entity.Usuario;
import br.com.andre.authenticationsample.entity.dto.response.UsuarioResponse;
import org.mapstruct.Mapper;

@Mapper
public interface UsuarioMapper {

    UsuarioResponse usuarioParaUsuarioDto(Usuario usuario);
    Usuario usuarioDtoParaUsuario(UsuarioResponse usuario);

}
