package com.git.projections;

public interface ContaBancariaProjection {

    Long getId();
    String getCodigoBanco();
    String getAgencia();
    String getNumero();
    String getDigito();
    String getNomeInstituicao();
    String getUsuarioId();

}
