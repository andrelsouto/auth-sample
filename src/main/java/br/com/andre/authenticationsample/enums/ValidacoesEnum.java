package br.com.andre.authenticationsample.enums;

import br.com.andre.authenticationsample.config.MessagePropoerty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ValidacoesEnum {
    ERRO_CRENDENCIAL("erro_credencial", HttpStatus.UNAUTHORIZED.value()),
    JSON_INVALIDO("json_invalido", HttpStatus.BAD_REQUEST.value()),
    TOKEN_EXPIRADO("token_expirado", HttpStatus.UNAUTHORIZED.value()),
    TOKEN_INVALIDO("token_invalido", HttpStatus.FORBIDDEN.value());

    private final String codigo;
    private final int httpStatus;

    public String getDescricaoMenssagem() {
        return MessagePropoerty.obterMensagem(this.codigo);
    }
}
