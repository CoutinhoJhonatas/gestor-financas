package com.git.services;

import com.git.data.Transacao;
import com.git.dtos.TransacaoDTO;
import com.git.exceptions.NotFoundException;
import com.git.mappers.TransacaoMapper;
import com.git.projections.ContaBancariaProjection;
import com.git.projections.TransacoesProjection;
import com.git.repositories.ContaBancariaRepository;
import com.git.repositories.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final ContaBancariaRepository contaBancariaRepository;
    private final TransacaoMapper transacaoMapper;

    public TransacaoService(TransacaoRepository transacaoRepository, ContaBancariaRepository contaBancariaRepository, TransacaoMapper transacaoMapper) {
        this.transacaoRepository = transacaoRepository;
        this.contaBancariaRepository = contaBancariaRepository;
        this.transacaoMapper = transacaoMapper;
    }

    @Transactional
    public void save(TransacaoDTO transacaoDTO) {
        Transacao transacao = transacaoMapper.toTransacao(transacaoDTO);
        transacao.setContaBancaria(contaBancariaRepository.findById(
                transacaoDTO.getIdInstituicao()).orElseThrow(() -> new NotFoundException("Conta bancária não encontrada"))
        );
        transacaoRepository.save(transacao);
    }

    @Transactional
    public void update(TransacaoDTO transacaoDTO) {
        Transacao transacao = transacaoRepository.findById(transacaoDTO.getId())
                .orElseThrow(() -> new NotFoundException("Transação não encontrada"));

        transacao.setDescricao(transacaoDTO.getDescricao());
        transacao.setValor(transacaoDTO.getValor());
        transacao.setData(transacaoDTO.getData());
        transacao.setContaBancaria(contaBancariaRepository.findById(
                transacaoDTO.getIdInstituicao()).orElseThrow(() -> new NotFoundException("Conta bancária não encontrada"))
        );

        transacaoRepository.save(transacao);
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public TransacaoDTO buscarTransacaoById(Long transacaoId) {
        Transacao transacao = transacaoRepository.findById(transacaoId)
                .orElseThrow(() -> new NotFoundException("Transação não encontrada"));
        return transacaoMapper.toTransacaoDTO(transacao);
    }

    @Transactional
    public void excluir(Long transacaoId) {
        transacaoRepository.deleteById(transacaoId);
    }

    public String calcularTotalEntrada(List<TransacaoDTO> transacoes) {
        double totalEntrada = 0.0;
        for (TransacaoDTO transacaoDTO : transacoes) {
            if (transacaoDTO.getValor().doubleValue() > 0) {
                totalEntrada += transacaoDTO.getValor().doubleValue();
            }
        }

        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
        return formatter.format(totalEntrada);
    }

    public String calcularTotalSaida(List<TransacaoDTO> transacoes) {
        double totalSaida = 0.0;
        for (TransacaoDTO transacaoDTO : transacoes) {
            if (transacaoDTO.getValor().doubleValue() < 0) {
                totalSaida += transacaoDTO.getValor().doubleValue();
            }
        }

        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
        return formatter.format(totalSaida);
    }
}
