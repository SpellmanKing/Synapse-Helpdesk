package com.synapse.helpdesk.repository;

import com.synapse.helpdesk.model.ArtigoSolucao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ArtigoSolucaoRepository extends JpaRepository<ArtigoSolucao, Long> {
    @Query("SELECT a FROM ArtigoSolucao a WHERE " +
           "LOWER(a.titulo) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.conteudo) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.palavrasChave) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ArtigoSolucao> searchByKeyword(@Param("keyword") String keyword);
}
