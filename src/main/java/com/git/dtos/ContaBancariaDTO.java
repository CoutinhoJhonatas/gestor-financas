package com.git.dtos;

import java.util.List;

public class ContaBancariaDTO {

    private Long id;
    private String nomeInstituicao;
    private String agencia;
    private String numero;
    private String digito;
    private List<TransacaoDTO> transacoes;

    public ContaBancariaDTO() {
    }

    public ContaBancariaDTO(Long id, String nomeInstituicao, String agencia, String numero, String digito) {
        this.id = id;
        this.nomeInstituicao = nomeInstituicao;
        this.agencia = agencia;
        this.numero = numero;
        this.digito = digito;
    }

    public ContaBancariaDTO(Long id, String nomeInstituicao, String agencia, String numero, String digito, List<TransacaoDTO> transacoes) {
        this.id = id;
        this.nomeInstituicao = nomeInstituicao;
        this.agencia = agencia;
        this.numero = numero;
        this.digito = digito;
        this.transacoes = transacoes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeInstituicao() {
        return nomeInstituicao;
    }

    public void setNomeInstituicao(String nomeInstituicao) {
        this.nomeInstituicao = nomeInstituicao;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDigito() {
        return digito;
    }

    public void setDigito(String digito) {
        this.digito = digito;
    }

    public List<TransacaoDTO> getTransacoes() {
        return transacoes;
    }

    public void setTransacoes(List<TransacaoDTO> transacoes) {
        this.transacoes = transacoes;
    }
}
