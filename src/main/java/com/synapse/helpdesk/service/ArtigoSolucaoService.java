package com.synapse.helpdesk.service;

import com.synapse.helpdesk.model.ArtigoSolucao;
import com.synapse.helpdesk.repository.ArtigoSolucaoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ArtigoSolucaoService {
    private final ArtigoSolucaoRepository artigoSolucaoRepository;

    public ArtigoSolucaoService(ArtigoSolucaoRepository artigoSolucaoRepository) {
        this.artigoSolucaoRepository = artigoSolucaoRepository;
    }

    public ArtigoSolucao cadastrar(ArtigoSolucao artigo) {
        return artigoSolucaoRepository.save(artigo);
    }

    public List<ArtigoSolucao> listarTodos() {
        return artigoSolucaoRepository.findAll();
    }

    public List<ArtigoSolucao> buscarPorTermo(String termo) {
        return artigoSolucaoRepository.searchByKeyword(termo);
    }
}
