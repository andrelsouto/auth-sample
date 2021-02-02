package br.com.andre.authenticationsample.controllers.handler;

import br.com.andre.authenticationsample.entity.dto.response.ObjectError;
import br.com.andre.authenticationsample.entity.dto.response.ResponseError;
import br.com.andre.authenticationsample.enums.ValidacoesEnum;
import br.com.andre.authenticationsample.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private final DateTimeFormatter formatar = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<ObjectError> erros = this.obterErros(ex);

        return ResponseEntity.status(status.value())
                .body(ResponseError
                        .builder()
                        .data(LocalDateTime.now().format(formatar))
                        .mensagem(status.getReasonPhrase())
                        .erros(erros)
                        .build());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseError> handleControllerExcpetion(CustomException e, HttpServletRequest httpServletRequest) {
        ValidacoesEnum validacoesEnum = e.getValidacoesEnum();

        return ResponseEntity.status(validacoesEnum.getHttpStatus())
                .body(ResponseError
                        .builder()
                        .caminho(httpServletRequest.getRequestURI())
                        .data(LocalDateTime.now().format(formatar))
                        .mensagem(e.getMessage())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> handleExceptionInternal(Exception ex, HttpServletRequest httpServletRequest) {
        ResponseError responseError = ResponseError.builder()
                .caminho(httpServletRequest.getRequestURI())
                .data(LocalDateTime.now().format(formatar))
                .mensagem(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseError);
    }

    private List<ObjectError> obterErros(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .map(error -> ObjectError.builder()
                        .mensagem(error.getDefaultMessage())
                        .campo(error.getField())
                        .parametro(error.getRejectedValue()).build())
                .collect(Collectors.toList());
    }
}
