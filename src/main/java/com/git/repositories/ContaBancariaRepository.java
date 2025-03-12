package com.git.repositories;

import com.git.data.ContaBancaria;
import com.git.projections.ContaBancariaProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContaBancariaRepository extends JpaRepository<ContaBancaria, Long> {

    @Query(nativeQuery = true,
            value = "SELECT tcb.id, " +
                    "tcb.agencia, " +
                    "tcb.numero, " +
                    "tcb.digito, " +
                    "tcb.nome_instituicao, " +
                    "tcb.usuario_id " +
                    "FROM tb_conta_bancaria tcb " +
                    "WHERE tcb.usuario_id = :usuarioId")
    List<ContaBancariaProjection> findByUsuarioId(Long usuarioId);

}
