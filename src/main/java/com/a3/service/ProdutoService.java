package com.a3.service;

import com.a3.model.Produto;
import com.a3.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    public Produto criar(Produto produto) {
        produto.setId(null);
        return repository.save(produto);
    }

    public List<Produto> listarTodos() {
        return repository.findAll();
    }

}
