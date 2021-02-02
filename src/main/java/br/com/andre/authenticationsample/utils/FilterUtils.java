package br.com.andre.authenticationsample.utils;

import br.com.andre.authenticationsample.config.WebSecurityConfig;
import br.com.andre.authenticationsample.entity.dto.response.ResponseError;
import br.com.andre.authenticationsample.enums.ValidacoesEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FilterUtils {

    public static void tratarRespostaFilterException(HttpServletResponse res, ValidacoesEnum validacoesEnum, String path) throws IOException {        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setStatus(validacoesEnum.getHttpStatus());
        ResponseError responseError = getResponseError(validacoesEnum.getDescricaoMenssagem(), path);
        res.getWriter().write(getJsonResponse(responseError));
        res.flushBuffer();
    }

    private static String getJsonResponse(ResponseError responseError) throws JsonProcessingException {
        JsonMapper jsonMapper = JsonMapper.builder().build();
        return jsonMapper.writeValueAsString(responseError);
    }

    private static ResponseError getResponseError(String mensagem, String path) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        return ResponseError.builder()
                .caminho(path)
                .data(LocalDateTime.now().format(dateTimeFormatter))
                .mensagem(mensagem).build();
    }

}
