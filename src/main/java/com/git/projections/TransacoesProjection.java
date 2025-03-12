package com.git.projections;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransacoesProjection {

    Long getId();
    LocalDate getData();
    String getDescricao();
    BigDecimal getValor();
    Long getContaBancariaId();

}
