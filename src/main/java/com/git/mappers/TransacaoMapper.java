package com.git.mappers;

import com.git.data.Transacao;
import com.git.dtos.TransacaoDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransacaoMapper {

    Transacao toTransacao(TransacaoDTO transacaoDTO);

    TransacaoDTO toTransacaoDTO(Transacao transacao);

}
