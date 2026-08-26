package com.synapse.helpdesk.service;

import com.synapse.helpdesk.model.*;
import com.synapse.helpdesk.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
public class TicketServiceTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ArtigoSolucaoRepository artigoSolucaoRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private InteracaoRepository interacaoRepository;

    private Usuario cliente;
    private Usuario tecnico;

    @BeforeEach
    public void setup() {
        interacaoRepository.deleteAll();
        ticketRepository.deleteAll();
        artigoSolucaoRepository.deleteAll();
        usuarioRepository.deleteAll();

        cliente = usuarioRepository.save(Usuario.builder()
                .nome("Calebe Carvalho")
                .email("calebe.carvalho@synapse.com")
                .tipo(TipoUsuario.CLIENTE)
                .build());

        tecnico = usuarioRepository.save(Usuario.builder()
                .nome("Maria Silva")
                .email("maria.silva@synapse.com")
                .tipo(TipoUsuario.TECNICO)
                .build());
    }

    @Test
    public void testAbrirTicketComTriagemMock() {
        Ticket ticket = Ticket.builder()
                .titulo("Minha maquina virtual no VirtualBox nao liga")
                .descricao("Ocorre erro de virtualizacao VT-x desativada.")
                .cliente(cliente)
                .build();

        Ticket ticketSalvo = ticketService.abrirTicket(ticket);

        assertThat(ticketSalvo.getId()).isNotNull();
        assertThat(ticketSalvo.getStatus()).isEqualTo(StatusTicket.ABERTO);
        assertThat(ticketSalvo.getCategoria()).isEqualTo("Infraestrutura");
        assertThat(ticketSalvo.getPrioridade()).isEqualTo("Média");
    }

    @Test
    public void testAbrirTicketComSugestaoRAG() {
        artigoSolucaoRepository.save(ArtigoSolucao.builder()
                .titulo("VirtualBox e VT-x")
                .conteudo("Para resolver problemas de maquina virtual no VirtualBox, ative a virtualizacao VT-x na BIOS.")
                .palavrasChave("VirtualBox, maquina, virtual")
                .build());

        Ticket ticket = Ticket.builder()
                .titulo("Problema com maquina virtual no VirtualBox")
                .descricao("Nao consigo ligar minha maquina.")
                .cliente(cliente)
                .build();

        Ticket ticketSalvo = ticketService.abrirTicket(ticket);

        List<Interacao> interacoes = ticketService.listarInteracoes(ticketSalvo.getId());
        assertThat(interacoes).isNotEmpty();
        
        Interacao sugestao = interacoes.get(0);
        assertThat(sugestao.getAutor().getEmail()).isEqualTo("suporte.virtual@synapse.com");
        assertThat(sugestao.getMensagem()).contains("VirtualBox");
    }

    @Test
    public void testResumoTicket() {
        Ticket ticket = Ticket.builder()
                .titulo("Acesso negado ao portal")
                .descricao("Nao consigo entrar no sistema.")
                .cliente(cliente)
                .build();

        Ticket ticketSalvo = ticketService.abrirTicket(ticket);
        
        ticketService.adicionarInteracao(ticketSalvo.getId(), cliente.getId(), "Ainda nao consigo acessar.");
        ticketService.adicionarInteracao(ticketSalvo.getId(), tecnico.getId(), "Por favor, limpe o cache do seu navegador.");

        String resumo = ticketService.obterResumoTicket(ticketSalvo.getId());
        
        assertThat(resumo).isNotBlank();
        assertThat(resumo).contains("Resumo");
    }
}
