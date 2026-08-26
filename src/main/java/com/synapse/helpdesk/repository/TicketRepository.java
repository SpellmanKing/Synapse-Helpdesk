package com.synapse.helpdesk.repository;

import com.synapse.helpdesk.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByClienteId(Long clienteId);
    List<Ticket> findByTecnicoId(Long tecnicoId);
}
