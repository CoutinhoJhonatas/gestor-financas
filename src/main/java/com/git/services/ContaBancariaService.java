package com.git.services;

import com.git.repositories.ContaBancariaRepository;
import org.springframework.stereotype.Service;

@Service
public class ContaBancariaService {

    private final ContaBancariaRepository contaBancariaRepository;
    private final UserService userService;

    public ContaBancariaService(ContaBancariaRepository contaBancariaRepository, UserService userService) {
        this.contaBancariaRepository = contaBancariaRepository;
        this.userService = userService;
    }
}
