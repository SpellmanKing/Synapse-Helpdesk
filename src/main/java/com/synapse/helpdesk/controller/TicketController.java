package com.synapse.helpdesk.controller;

import com.synapse.helpdesk.model.Interacao;
import com.synapse.helpdesk.model.StatusTicket;
import com.synapse.helpdesk.model.Ticket;
import com.synapse.helpdesk.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<Ticket> abrirTicket(@RequestBody Ticket ticket) {
        try {
            return ResponseEntity.ok(ticketService.abrirTicket(ticket));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> listarTodos() {
        return ResponseEntity.ok(ticketService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> buscarPorId(@PathVariable Long id) {
        return ticketService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/interacoes")
    public ResponseEntity<Interacao> adicionarInteracao(
            @PathVariable Long id,
            @RequestParam Long autorId,
            @RequestBody String mensagem) {
        try {
            String msgLimpa = mensagem;
            if (mensagem != null && mensagem.startsWith("\"") && mensagem.endsWith("\"")) {
                msgLimpa = mensagem.substring(1, mensagem.length() - 1);
            }
            return ResponseEntity.ok(ticketService.adicionarInteracao(id, autorId, msgLimpa));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/interacoes")
    public ResponseEntity<List<Interacao>> listarInteracoes(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.listarInteracoes(id));
    }

    @PutMapping("/{id}/atribuir")
    public ResponseEntity<Ticket> atribuirTecnico(
            @PathVariable Long id,
            @RequestParam Long tecnicoId) {
        try {
            return ResponseEntity.ok(ticketService.atribuirTecnico(id, tecnicoId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Ticket> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusTicket status) {
        try {
            return ResponseEntity.ok(ticketService.atualizarStatus(id, status));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}/resumo")
    public ResponseEntity<String> obterResumoTicket(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ticketService.obterResumoTicket(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/avaliar")
    public ResponseEntity<Ticket> avaliarSolucao(
            @PathVariable Long id,
            @RequestParam boolean aprovado) {
        try {
            return ResponseEntity.ok(ticketService.avaliarSolucao(id, aprovado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
