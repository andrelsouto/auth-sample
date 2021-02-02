package br.com.andre.authenticationsample.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/autenticacao")
public class AutenticacaoController {

    @PostMapping("/autenticado")
    public ResponseEntity usuarioAutorizado() {
        return ResponseEntity.ok().build();
    }

}
