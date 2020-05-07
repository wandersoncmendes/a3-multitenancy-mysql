package com.a3.controllers;

import com.a3.model.Produto;
import com.a3.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/produto")
public class LoginController {

    @Autowired
    private ProdutoService service;

    @PostMapping
    @ResponseBody
    public ResponseEntity<Produto> criar(@RequestBody Produto produto) {
        return ResponseEntity.ok(service.criar(produto));
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Produto>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }
}
