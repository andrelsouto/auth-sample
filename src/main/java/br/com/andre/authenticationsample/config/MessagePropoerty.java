package br.com.andre.authenticationsample.config;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessagePropoerty {

    private static MessageSource messageSource;

    public MessagePropoerty(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public static String obterMensagem(String codigoErro) {
        return messageSource.getMessage(codigoErro, new Object[0], new Locale("pt", "br"));
    }

}
