package com.git.services;

import com.git.dtos.TransacaoDTO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ExtratoPdfService {

    private static final String ITAU_REGEX = "(?m)^(\\d{2}/\\d{2}/\\d{4})\\s+(.+?)\\s+(-?\\d{1,3}(?:\\.\\d{3})*,\\d{2})(?:\\s+(-?\\d{1,3}(?:\\.\\d{3})*,\\d{2}))?\\s*$";

    public List<TransacaoDTO> extrairDados(int codBanco, File file) {
        String regex = "";
        switch (codBanco) {
            /*case 33 -> regex = SANTANDER_REGEX;
            case 77 -> regex = INTER_REGEX;
            case 260 -> regex = NUBANK_REGEX;*/
            case 341 -> regex = ITAU_REGEX;
        }

        try (PDDocument document = Loader.loadPDF(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String textoPDF = stripper.getText(document);

            //String regex = "(?m)^(\\d{2}/\\d{2}/\\d{4})\\s+(.+?)\\s+(-?\\d{1,3}(?:\\.\\d{3})*,\\d{2})(?:\\s+(-?\\d{1,3}(?:\\.\\d{3})*,\\d{2}))?\\s*$";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(textoPDF);

            List<TransacaoDTO> transacoes = new ArrayList<>();
            while (matcher.find()) {
                String data = matcher.group(1);
                String descricao = matcher.group(2);
                String valor = matcher.group(3);
                String saldo = matcher.group(4); // Pode ser null se não houver saldo na linha

                if (!descricao.equals("SALDO DO DIA")) {
                    TransacaoDTO transacao = new TransacaoDTO();
                    transacao.setData(convertStringToLocalDate(data));
                    transacao.setDescricao(descricao);
                    transacao.setValor(BigDecimal.valueOf(
                            Double.parseDouble(valor.replace(".", "")
                                    .replace(",", "."))));

                    transacoes.add(transacao);

                    System.out.println("Data: " + data);
                    System.out.println("Lançamento: " + descricao);
                    System.out.println("Valor: " + valor);
                    System.out.println("Saldo: " + (saldo != null ? saldo : "não informado"));
                    System.out.println("-------------");
                }
            }

            return transacoes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private LocalDate convertStringToLocalDate(String dataStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            return LocalDate.parse(dataStr, formatter);
        } catch (DateTimeParseException e) {
            System.out.println("Erro ao converter a data: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
