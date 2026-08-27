package com.synapse.helpdesk.service;

import com.synapse.helpdesk.model.*;
import com.synapse.helpdesk.repository.ArtigoSolucaoRepository;
import com.synapse.helpdesk.repository.InteracaoRepository;
import com.synapse.helpdesk.repository.TicketRepository;
import com.synapse.helpdesk.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private static final Logger log = LoggerFactory.getLogger(TicketService.class);

    private final TicketRepository ticketRepository;
    private final InteracaoRepository interacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArtigoSolucaoRepository artigoSolucaoRepository;

    private final TriadorAiService triadorAiService;
    private final ResolucaoAiService resolucaoAiService;
    private final ResumoAiService resumoAiService;

    public TicketService(TicketRepository ticketRepository,
                         InteracaoRepository interacaoRepository,
                         UsuarioRepository usuarioRepository,
                         ArtigoSolucaoRepository artigoSolucaoRepository,
                         TriadorAiService triadorAiService,
                         ResolucaoAiService resolucaoAiService,
                         ResumoAiService resumoAiService) {
        this.ticketRepository = ticketRepository;
        this.interacaoRepository = interacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.artigoSolucaoRepository = artigoSolucaoRepository;
        this.triadorAiService = triadorAiService;
        this.resolucaoAiService = resolucaoAiService;
        this.resumoAiService = resumoAiService;
    }

    @Transactional
    public Ticket abrirTicket(Ticket ticket) {
        log.info("Abrindo novo ticket: {}", ticket.getTitulo());

        // Configurações iniciais
        ticket.setStatus(StatusTicket.ABERTO);
        ticket.setDataCriacao(LocalDateTime.now());

        // Buscar cliente no banco para garantir consistência
        if (ticket.getCliente() == null || ticket.getCliente().getId() == null) {
            throw new IllegalArgumentException("Cliente do ticket é obrigatório");
        }
        Usuario cliente = usuarioRepository.findById(ticket.getCliente().getId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        ticket.setCliente(cliente);

        // Executar triagem automática com IA
        try {
            log.info("Iniciando triagem por IA do ticket...");
            TriagemResult triagem = triadorAiService.triar(ticket.getTitulo(), ticket.getDescricao());
            if (triagem != null) {
                ticket.setCategoria(triagem.getCategoria());
                ticket.setPrioridade(triagem.getPrioridade());
                log.info("Triagem finalizada por IA. Categoria: {}, Prioridade: {}", 
                        ticket.getCategoria(), ticket.getPrioridade());
            }
        } catch (Exception e) {
            log.error("Erro ao realizar triagem por IA. Usando valores padrão.", e);
            ticket.setCategoria("Software");
            ticket.setPrioridade("Média");
        }

        // Salvar ticket inicial
        Ticket ticketSalvo = ticketRepository.save(ticket);

        // Executar RAG para Resolução Sugerida (Nível 1 de Suporte)
        sugerirResolucaoAutomatica(ticketSalvo);

        return ticketSalvo;
    }

    private void sugerirResolucaoAutomatica(Ticket ticket) {
        log.info("Buscando artigos de solução relevantes para o ticket: {}", ticket.getId());
        List<ArtigoSolucao> artigos = buscarArtigosRelacionados(ticket.getTitulo(), ticket.getDescricao());

        if (artigos.isEmpty()) {
            log.info("Nenhum artigo de solução relevante encontrado.");
            return;
        }

        log.info("Encontrados {} artigos relacionados. Gerando sugestão por IA...", artigos.size());
        String contextoArtigos = artigos.stream()
                .map(a -> "Título: " + a.getTitulo() + "\nConteúdo: " + a.getConteudo())
                .collect(Collectors.joining("\n---\n"));

        try {
            String sugestao = resolucaoAiService.sugerirSolucao(contextoArtigos, ticket.getDescricao());
            
            // Salvar sugestão como interação do Suporte Virtual
            Usuario suporteVirtual = getOrCreateSuporteVirtual();
            Interacao interacao = Interacao.builder()
                    .ticket(ticket)
                    .autor(suporteVirtual)
                    .mensagem(sugestao)
                    .dataCriacao(LocalDateTime.now())
                    .build();
            
            interacaoRepository.save(interacao);
            log.info("Sugestão de suporte virtual adicionada com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao gerar sugestão de suporte por IA.", e);
        }
    }

    private List<ArtigoSolucao> buscarArtigosRelacionados(String titulo, String descricao) {
        Set<ArtigoSolucao> artigos = new HashSet<>();
        String textoCompleto = (titulo + " " + descricao).replaceAll("[^a-zA-Z0-9áéíóúâêîôûãõçÁÉÍÓÚÂÊÎÔÛÃÕÇ ]", " ");
        String[] palavras = textoCompleto.split("\\s+");
        for (String palavra : palavras) {
            if (palavra.length() >= 4) {
                List<ArtigoSolucao> matches = artigoSolucaoRepository.searchByKeyword(palavra);
                artigos.addAll(matches);
            }
        }
        return new ArrayList<>(artigos);
    }

    private Usuario getOrCreateSuporteVirtual() {
        return usuarioRepository.findByEmail("suporte.virtual@synapse.com")
                .orElseGet(() -> usuarioRepository.save(Usuario.builder()
                        .nome("Suporte Virtual (IA)")
                        .email("suporte.virtual@synapse.com")
                        .tipo(TipoUsuario.TECNICO)
                        .build()));
    }

    @Transactional
    public Interacao adicionarInteracao(Long ticketId, Long autorId, String mensagem) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket não encontrado"));
        Usuario autor = usuarioRepository.findById(autorId)
                .orElseThrow(() -> new IllegalArgumentException("Autor não encontrado"));

        Interacao interacao = Interacao.builder()
                .ticket(ticket)
                .autor(autor)
                .mensagem(mensagem)
                .dataCriacao(LocalDateTime.now())
                .build();

        return interacaoRepository.save(interacao);
    }

    public List<Ticket> listarTodos() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> buscarPorId(Long id) {
        return ticketRepository.findById(id);
    }

    public List<Interacao> listarInteracoes(Long ticketId) {
        return interacaoRepository.findByTicketIdOrderByDataCriacaoAsc(ticketId);
    }

    @Transactional
    public Ticket atribuirTecnico(Long ticketId, Long tecnicoId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket não encontrado"));
        Usuario tecnico = usuarioRepository.findById(tecnicoId)
                .orElseThrow(() -> new IllegalArgumentException("Técnico não encontrado"));

        if (tecnico.getTipo() != TipoUsuario.TECNICO) {
            throw new IllegalArgumentException("O usuário atribuído deve ser um TÉCNICO");
        }

        ticket.setTecnico(tecnico);
        ticket.setStatus(StatusTicket.EM_ANDAMENTO);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket atualizarStatus(Long ticketId, StatusTicket novoStatus) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket não encontrado"));
        ticket.setStatus(novoStatus);
        return ticketRepository.save(ticket);
    }

    public String obterResumoTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket não encontrado"));
        
        List<Interacao> interacoes = interacaoRepository.findByTicketIdOrderByDataCriacaoAsc(ticketId);

        StringBuilder sb = new StringBuilder();
        sb.append("Ticket #").append(ticket.getId()).append(": ").append(ticket.getTitulo()).append("\n");
        sb.append("Descrição: ").append(ticket.getDescricao()).append("\n");
        sb.append("Categoria: ").append(ticket.getCategoria()).append(" | Prioridade: ").append(ticket.getPrioridade()).append("\n");
        sb.append("Histórico de Conversação:\n");

        if (interacoes.isEmpty()) {
            sb.append("(Sem mensagens adicionais)");
        } else {
            for (Interacao in : interacoes) {
                sb.append(in.getAutor().getNome())
                  .append(" (")
                  .append(in.getAutor().getTipo())
                  .append("): ")
                  .append(in.getMensagem())
                  .append("\n");
            }
        }

        try {
            return resumoAiService.resumirConversa(sb.toString());
        } catch (Exception e) {
            log.error("Erro ao resumir conversa com IA", e);
            return "Não foi possível gerar o resumo automático. Por favor, revise as mensagens individualmente.";
        }
    }

    @Transactional
    public Ticket avaliarSolucao(Long ticketId, boolean aprovado) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket não encontrado"));
        
        if (aprovado) {
            ticket.setStatus(StatusTicket.FECHADO);
            log.info("Ticket #{} aprovado pelo cliente. Status alterado para FECHADO.", ticketId);
            
            // Adicionar uma interação automática do sistema
            Usuario suporteVirtual = getOrCreateSuporteVirtual();
            Interacao interacao = Interacao.builder()
                    .ticket(ticket)
                    .autor(suporteVirtual)
                    .mensagem("Chamado finalizado e fechado com a aprovação do cliente.")
                    .dataCriacao(LocalDateTime.now())
                    .build();
            interacaoRepository.save(interacao);
        } else {
            ticket.setStatus(StatusTicket.EM_ANDAMENTO);
            ticket.setPrioridade("Alta"); // Elevar prioridade
            log.info("Ticket #{} reprovado pelo cliente. Prioridade elevada para Alta.", ticketId);
            
            // Adicionar uma interação automática do sistema informando a reprovação
            Usuario suporteVirtual = getOrCreateSuporteVirtual();
            Interacao interacao = Interacao.builder()
                    .ticket(ticket)
                    .autor(suporteVirtual)
                    .mensagem("Atenção: O cliente reprovou a solução proposta. O chamado permanece ativo (Em Progresso) e sua prioridade foi elevada para ALTA.")
                    .dataCriacao(LocalDateTime.now())
                    .build();
            interacaoRepository.save(interacao);
        }
        
        return ticketRepository.save(ticket);
    }

}
