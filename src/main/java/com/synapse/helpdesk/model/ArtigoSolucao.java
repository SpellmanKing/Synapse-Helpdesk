package com.synapse.helpdesk.model;

import jakarta.persistence.*;

@Entity
@Table(name = "artigos_solucao")
public class ArtigoSolucao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(length = 4000)
    private String conteudo;

    private String palavrasChave;

    public ArtigoSolucao() {}

    public ArtigoSolucao(Long id, String titulo, String conteudo, String palavrasChave) {
        this.id = id;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.palavrasChave = palavrasChave;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public String getPalavrasChave() { return palavrasChave; }
    public void setPalavrasChave(String palavrasChave) { this.palavrasChave = palavrasChave; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String titulo;
        private String conteudo;
        private String palavrasChave;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder titulo(String titulo) { this.titulo = titulo; return this; }
        public Builder conteudo(String conteudo) { this.conteudo = conteudo; return this; }
        public Builder palavrasChave(String palavrasChave) { this.palavrasChave = palavrasChave; return this; }

        public ArtigoSolucao build() {
            return new ArtigoSolucao(id, titulo, conteudo, palavrasChave);
        }
    }
}