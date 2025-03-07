package com.git.services;

import com.git.repositories.ContaBancariaRepository;
import org.springframework.stereotype.Service;

@Service
public class ContaBancariaService {

    private final ContaBancariaRepository contaBancariaRepository;

    public ContaBancariaService(ContaBancariaRepository contaBancariaRepository) {
        this.contaBancariaRepository = contaBancariaRepository;
    }
}
