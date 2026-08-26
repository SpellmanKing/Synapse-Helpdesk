package com.synapse.helpdesk.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interacoes")
public class Interacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    @Column(length = 4000)
    private String mensagem;

    private LocalDateTime dataCriacao;

    public Interacao() {}

    public Interacao(Long id, Ticket ticket, Usuario autor, String mensagem, LocalDateTime dataCriacao) {
        this.id = id;
        this.ticket = ticket;
        this.autor = autor;
        this.mensagem = mensagem;
        this.dataCriacao = dataCriacao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }

    public Usuario getAutor() { return autor; }
    public void setAutor(Usuario autor) { this.autor = autor; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Ticket ticket;
        private Usuario autor;
        private String mensagem;
        private LocalDateTime dataCriacao;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder ticket(Ticket ticket) { this.ticket = ticket; return this; }
        public Builder autor(Usuario autor) { this.autor = autor; return this; }
        public Builder mensagem(String mensagem) { this.mensagem = mensagem; return this; }
        public Builder dataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; return this; }

        public Interacao build() {
            return new Interacao(id, ticket, autor, mensagem, dataCriacao);
        }
    }
}