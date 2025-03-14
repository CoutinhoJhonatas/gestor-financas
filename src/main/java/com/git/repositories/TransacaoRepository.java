package com.git.repositories;

import com.git.data.Transacao;
import com.git.projections.TransacoesProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Integer> {

    @Query(nativeQuery = true,
            value = "SELECT tt.id, " +
                    "tt.data, " +
                    "tt.descricao, " +
                    "tt.valor, " +
                    "tt.conta_bancaria_id " +
                    "FROM tb_transacao tt " +
                    "WHERE tt.conta_bancaria_id = :contaBancariaId " +
                    "AND tt.data BETWEEN :dataInicial AND :dataFinal")
    List<TransacoesProjection> buscarPorPeriodo(Long contaBancariaId, LocalDate dataInicial, LocalDate dataFinal);
}
