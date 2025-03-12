package com.git.dtos;

import java.math.BigDecimal;

public class TransacaoDTO {

    private Long id;
    private String data;
    private String descricao;
    private BigDecimal valor;
    private String nomeInstituicao;
    private Long idInstituicao;

    public TransacaoDTO() {
    }

    public TransacaoDTO(Long id, String data, String descricao, BigDecimal valor, String nomeInstituicao, Long idInstituicao) {
        this.id = id;
        this.data = data;
        this.descricao = descricao;
        this.valor = valor;
        this.nomeInstituicao = nomeInstituicao;
        this.idInstituicao = idInstituicao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getNomeInstituicao() {
        return nomeInstituicao;
    }

    public void setNomeInstituicao(String nomeInstituicao) {
        this.nomeInstituicao = nomeInstituicao;
    }

    public Long getIdInstituicao() {
        return idInstituicao;
    }

    public void setIdInstituicao(Long idInstituicao) {
        this.idInstituicao = idInstituicao;
    }
}
