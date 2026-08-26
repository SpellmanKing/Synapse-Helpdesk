package com.synapse.helpdesk.repository;

import com.synapse.helpdesk.model.Interacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InteracaoRepository extends JpaRepository<Interacao, Long> {
    List<Interacao> findByTicketIdOrderByDataCriacaoAsc(Long ticketId);
}
