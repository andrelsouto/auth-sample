package br.com.andre.authenticationsample.exception;


import br.com.andre.authenticationsample.enums.ValidacoesEnum;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ValidacoesEnum validacoesEnum;

    public CustomException(ValidacoesEnum dafaultError) {
        super(dafaultError.getDescricaoMenssagem());
        this.validacoesEnum = dafaultError;
    }
}
