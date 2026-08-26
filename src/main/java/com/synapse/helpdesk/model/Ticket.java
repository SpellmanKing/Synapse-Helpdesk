package com.synapse.helpdesk.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @Column(length = 2000)
    private String descricao;

    private String categoria;

    private String prioridade;

    @Enumerated(EnumType.STRING)
    private StatusTicket status;

    private LocalDateTime dataCriacao;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Usuario cliente;

    @ManyToOne
    @JoinColumn(name = "tecnico_id")
    private Usuario tecnico;

    public Ticket() {}

    public Ticket(Long id, String titulo, String descricao, String categoria, String prioridade, StatusTicket status, LocalDateTime dataCriacao, Usuario cliente, Usuario tecnico) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.prioridade = prioridade;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.cliente = cliente;
        this.tecnico = tecnico;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }

    public StatusTicket getStatus() { return status; }
    public void setStatus(StatusTicket status) { this.status = status; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public Usuario getCliente() { return cliente; }
    public void setCliente(Usuario cliente) { this.cliente = cliente; }

    public Usuario getTecnico() { return tecnico; }
    public void setTecnico(Usuario tecnico) { this.tecnico = tecnico; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String titulo;
        private String descricao;
        private String categoria;
        private String prioridade;
        private StatusTicket status;
        private LocalDateTime dataCriacao;
        private Usuario cliente;
        private Usuario tecnico;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder titulo(String titulo) { this.titulo = titulo; return this; }
        public Builder descricao(String descricao) { this.descricao = descricao; return this; }
        public Builder categoria(String categoria) { this.categoria = categoria; return this; }
        public Builder prioridade(String prioridade) { this.prioridade = prioridade; return this; }
        public Builder status(StatusTicket status) { this.status = status; return this; }
        public Builder dataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; return this; }
        public Builder cliente(Usuario cliente) { this.cliente = cliente; return this; }
        public Builder tecnico(Usuario tecnico) { this.tecnico = tecnico; return this; }

        public Ticket build() {
            return new Ticket(id, titulo, descricao, categoria, prioridade, status, dataCriacao, cliente, tecnico);
        }
    }
}