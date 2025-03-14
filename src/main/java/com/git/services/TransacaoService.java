package com.git.services;

import com.git.dtos.TransacaoDTO;
import com.git.projections.ContaBancariaProjection;
import com.git.projections.TransacoesProjection;
import com.git.repositories.ContaBancariaRepository;
import com.git.repositories.TransacaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final ContaBancariaRepository contaBancariaRepository;

    public TransacaoService(TransacaoRepository transacaoRepository, ContaBancariaRepository contaBancariaRepository) {
        this.transacaoRepository = transacaoRepository;
        this.contaBancariaRepository = contaBancariaRepository;
    }

    public List<TransacaoDTO> buscarTransacoesByPeriodo(Long usuarioId, LocalDate dataInicial, LocalDate dataFinal) {
        List<ContaBancariaProjection> contasBancariasProjection = contaBancariaRepository.findByUsuarioId(usuarioId);
        List<TransacaoDTO> transacaoDTOS = new ArrayList<>();

        contasBancariasProjection.forEach(c -> {
            List<TransacoesProjection> list;
            if (dataFinal == null) {
                list = transacaoRepository.buscarPorPeriodo(c.getId(), dataInicial, LocalDate.now());
            } else {
                list = transacaoRepository.buscarPorPeriodo(c.getId(), dataInicial, dataFinal);
            }

            list.forEach(t -> {
                TransacaoDTO transacaoDTO = new TransacaoDTO();
                transacaoDTO.setId(t.getId());
                transacaoDTO.setData(t.getData());
                transacaoDTO.setDescricao(t.getDescricao());
                transacaoDTO.setValor(t.getValor());
                transacaoDTO.setNomeInstituicao(c.getNomeInstituicao());
                transacaoDTO.setIdInstituicao(c.getId());

                transacaoDTOS.add(transacaoDTO);
            });
        });

        return transacaoDTOS;
    }
}
