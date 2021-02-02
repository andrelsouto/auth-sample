package br.com.andre.authenticationsample.repositories;

import br.com.andre.authenticationsample.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByClientId(String username);

    Optional<Usuario> findByClientIdAndCnpjAndRazaoSocial(String clientId, String cnpj, String razaoSocial);
}
