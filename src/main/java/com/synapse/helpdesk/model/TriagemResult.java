package com.synapse.helpdesk.model;

public class TriagemResult {
    private String categoria;
    private String prioridade;

    public TriagemResult() {}

    public TriagemResult(String categoria, String prioridade) {
        this.categoria = categoria;
        this.prioridade = prioridade;
    }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }
}