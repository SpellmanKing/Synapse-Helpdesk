package com.synapse.helpdesk.controller;

import com.synapse.helpdesk.model.ArtigoSolucao;
import com.synapse.helpdesk.service.ArtigoSolucaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artigos")
public class ArtigoSolucaoController {

    private final ArtigoSolucaoService artigoSolucaoService;

    public ArtigoSolucaoController(ArtigoSolucaoService artigoSolucaoService) {
        this.artigoSolucaoService = artigoSolucaoService;
    }

    @PostMapping
    public ResponseEntity<ArtigoSolucao> cadastrar(@RequestBody ArtigoSolucao artigo) {
        return ResponseEntity.ok(artigoSolucaoService.cadastrar(artigo));
    }

    @GetMapping
    public ResponseEntity<List<ArtigoSolucao>> listarTodos() {
        return ResponseEntity.ok(artigoSolucaoService.listarTodos());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ArtigoSolucao>> buscar(@RequestParam String termo) {
        return ResponseEntity.ok(artigoSolucaoService.buscarPorTermo(termo));
    }
}
