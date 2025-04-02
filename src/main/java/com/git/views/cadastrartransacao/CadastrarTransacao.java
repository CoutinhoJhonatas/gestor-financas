package com.git.views.cadastrartransacao;

import com.git.data.User;
import com.git.dtos.TransacaoDTO;
import com.git.projections.ContaBancariaProjection;
import com.git.repositories.ContaBancariaRepository;
import com.git.security.AuthenticatedUser;
import com.git.services.ExtratoPdfService;
import com.git.services.TransacaoService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@PageTitle("Cadastrar Transações")
@Route("cadastrar-transacoes")
@Menu(order = 1, icon = LineAwesomeIconUrl.CLIPBOARD)
@PermitAll
public class CadastrarTransacao extends Composite<VerticalLayout> {

    private final TransacaoService transacaoService;
    private final ExtratoPdfService extratoPdfService;
    private final ContaBancariaRepository contaBancariaRepository;
    private final AuthenticatedUser authenticatedUser;
    private User user;

    public CadastrarTransacao(TransacaoService transacaoService, ExtratoPdfService extratoPdfService,
                              ContaBancariaRepository contaBancariaRepository, AuthenticatedUser authenticatedUser) {
        this.transacaoService = transacaoService;
        this.extratoPdfService = extratoPdfService;
        this.contaBancariaRepository = contaBancariaRepository;
        this.authenticatedUser = authenticatedUser;

        if(authenticatedUser.get().isPresent()) {
            user = authenticatedUser.get().get();
        } else {
            throw new UsernameNotFoundException("User not found");
        }

        getContent().setWidthFull();

        Div divH4Form = new Div();
        divH4Form.setWidthFull();
        divH4Form.setClassName("div-h3-form");
        divH4Form.getStyle()
                .setDisplay(Style.Display.FLEX)
                .setJustifyContent(Style.JustifyContent.CENTER);

        H4 textH4Form = new H4();
        textH4Form.setText("Cadastro Manual");
        divH4Form.add(textH4Form);

        Div divLayoutForm = new Div();
        divLayoutForm.setWidthFull();
        divLayoutForm.setClassName("div-layout-form");

        TextField descricao = new TextField("Descrição");
        descricao.setPlaceholder("Digite a descrição");

        DatePicker date = new DatePicker("Data");
        date.setPlaceholder("dd/mm/aaaa");

        TextField valor = getTextFieldValor();

        List<ContaBancariaProjection> listContaBancaria = contaBancariaRepository.findByUsuarioId(user.getId());
        Select<ContaBancariaProjection> contaBancaria = getContaBancariaProjectionSelect(listContaBancaria);

        FormLayout form = new FormLayout();
        form.add(descricao, date, valor, contaBancaria);
        form.setColspan(descricao, 3);
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 3));

        Binder<TransacaoDTO> binder = new Binder<>(TransacaoDTO.class);
        binder.forField(descricao)
                .asRequired("Campo requerido")
                .bind(TransacaoDTO::getDescricao, TransacaoDTO::setDescricao);
        binder.forField(date)
                .asRequired("Campo requerido")
                .bind(TransacaoDTO::getData, TransacaoDTO::setData);

        Binder<TextField> binderValor = new Binder<>(TextField.class);
        binderValor.forField(valor)
                .asRequired("Campo requerido")
                .bind(TextField::getValue, TextField::setValue);

        Binder<Select<ContaBancariaProjection>> binderContaBancaria = new Binder<>();
        binderContaBancaria.forField(contaBancaria)
                .asRequired("Campo requerido")
                .bind(Select::getValue, Select::setValue);

        Div btnsDiv = new Div();
        btnsDiv.setWidth("100%");
        btnsDiv.setClassName("btns-dv");
        btnsDiv.getStyle()
                .setDisplay(Style.Display.FLEX)
                .setJustifyContent(Style.JustifyContent.CENTER)
                .setMarginTop("5px");

        Button saveBtn = new Button("Salvar", e -> {
            Notification notification;
            if (binder.validate().isOk()
                    && binderValor.validate().isOk()
                    && binderContaBancaria.validate().isOk()) {

                transacaoService.save(buildTransacaoDTO(
                        date.getValue(),
                        descricao.getValue(),
                        BigDecimal.valueOf(Double.parseDouble(
                                valor.getValue().replace(",", ".")
                        )),
                        contaBancaria.getValue().getNomeInstituicao(),
                        contaBancaria.getValue().getId()
                ));
                notification = Notification.show("Salvo com sucesso");
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                clearForm(descricao, date, valor, contaBancaria);
                binder.readBean(null);
                binderValor.readBean(null);
                binderContaBancaria.readBean(null);
            } else {
                System.out.println(binder.validate().getValidationErrors());
                notification = Notification.show("Erro ao salvar");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }

        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.getStyle().setMarginRight("20px");

        Button resetBtn = new Button("Limpar", e -> clearForm(descricao, date, valor, contaBancaria));
        resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        btnsDiv.add(saveBtn, resetBtn);
        divLayoutForm.add(form, btnsDiv);

        Div divH4Pdf = new Div();
        divH4Pdf.setWidthFull();
        divH4Pdf.setClassName("div-h4-pdf");
        divH4Pdf.getStyle()
                .setDisplay(Style.Display.FLEX)
                .setJustifyContent(Style.JustifyContent.CENTER)
                .setMarginTop("50px");

        H4 textH4UploadFiles = new H4();
        textH4UploadFiles.setText("Cadastro Automático via Extrato Bancário");
        divH4Pdf.add(textH4UploadFiles);

        Div divUploadPdf = new Div();
        divUploadPdf.setWidthFull();
        divUploadPdf.setClassName("div-upload-pdf");
        divUploadPdf.getStyle()
                .setDisplay(Style.Display.FLEX)
                .setJustifyContent(Style.JustifyContent.SPACE_AROUND);

        Select<ContaBancariaProjection> contaBancariaUpload = getContaBancariaProjectionSelect(listContaBancaria);
        divUploadPdf.add(contaBancariaUpload);

        MemoryBuffer buffer = new MemoryBuffer();
        Upload uploadPdf = new Upload(buffer);
        uploadPdf.setMaxFiles(1);
        uploadPdf.setAcceptedFileTypes("application/pdf", ".pdf");
        uploadPdf.getStyle()
                .setMarginTop("10px");

        uploadPdf.addFileRejectedListener(e -> {
            String errorMessage = e.getErrorMessage();
            Notification notification = Notification.show(errorMessage, 5000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });

        Div gridDiv = new Div();
        gridDiv.setMinWidth("300px");
        gridDiv.setMaxWidth("100%");
        gridDiv.setHeight("100%");
        gridDiv.setClassName("grid-div");

        Grid<TransacaoDTO> grid = createGrid();
        gridDiv.add(grid);

        Dialog dialog = new Dialog();
        dialog.setDraggable(true);
        dialog.setResizable(true);
        dialog.setHeaderTitle("Registros Encontrados");

        dialog.add(gridDiv);

        Button saveDialogButton = new Button("Salvar");
        saveDialogButton.getStyle()
                .set("cursor", "pointer")
                .set("margin-right", "auto");
        saveDialogButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialog.getFooter().add(saveDialogButton);

        Button cancelDialogButton = new Button("Cancelar", e -> dialog.close());
        cancelDialogButton.getStyle().set("cursor", "pointer");
        cancelDialogButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancelDialogButton);

        uploadPdf.addSucceededListener(e -> {
            try {
                InputStream inputStream = buffer.getInputStream();
                File tempFile = File.createTempFile("extrato", ".pdf");

                try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                    inputStream.transferTo(outputStream);
                }

                List<TransacaoDTO> transacoesPdf = extratoPdfService.extrairDados(
                        Integer.parseInt(contaBancariaUpload.getValue().getCodigoBanco()),
                        tempFile);

                transacoesPdf.forEach(transacao ->
                        transacao.setIdInstituicao(contaBancariaUpload.getValue().getId()));

                grid.setItems(transacoesPdf);
                dialog.open();

                saveDialogButton.addClickListener(event -> {
                    transacoesPdf.forEach(transacaoService::save);
                    Notification notification = Notification.show("SALVO COM SUCESSO", 4000, Notification.Position.MIDDLE);
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                    dialog.close();
                });
            } catch (IOException ex) {
                Notification notification = Notification.show("ERRO AO PROCESSAR ARQUIVO: " + ex.getMessage(), 4000,
                        Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        UploadI18N uploadI18N = buildUploadI18N();
        uploadPdf.setI18n(uploadI18N);

        contaBancariaUpload.addValueChangeListener(e -> divUploadPdf.add(uploadPdf));

        getContent().add(divH4Form, divLayoutForm, divH4Pdf, divUploadPdf);
    }

    private Grid<TransacaoDTO> createGrid() {
        Grid<TransacaoDTO> grid = new Grid<>(TransacaoDTO.class, false);
        grid.addColumn(new LocalDateRenderer<>(TransacaoDTO::getData, "dd/MM/yyyy"))
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setHeader("Data")
                .setSortable(true)
                .setComparator(TransacaoDTO::getData);
        grid.addColumn("descricao").setAutoWidth(true).setFlexGrow(2).setHeader("Descrição");

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
        grid.addColumn(
                        new ComponentRenderer<>(transacaoDTO -> {
                            Span span = new Span(currencyFormat.format(transacaoDTO.getValor()));

                            if (transacaoDTO.getValor().doubleValue() < 0) {
                                span.getStyle().set("color", "#E57373");
                            }
                            return span;
                        }))
                .setAutoWidth(true)
                .setFlexGrow(1)
                .setHeader("Valor");

        grid.setAllRowsVisible(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addClassName(LumoUtility.Border.TOP);
        grid.getStyle().setDisplay(Style.Display.FLEX);
        grid.getStyle().setJustifyContent(Style.JustifyContent.CENTER);
        grid.getStyle().setAlignItems(Style.AlignItems.CENTER);

        return grid;
    }

    private static Select<ContaBancariaProjection> getContaBancariaProjectionSelect(List<ContaBancariaProjection> listContaBancaria) {
        Select<ContaBancariaProjection> contaBancaria = new Select<>();
        contaBancaria.setLabel("Conta Bancária");
        contaBancaria.setPlaceholder("Selecione a conta bancária");
        contaBancaria.setItems(listContaBancaria);
        contaBancaria.setItemLabelGenerator(ContaBancariaProjection::getNomeInstituicao);
        return contaBancaria;
    }

    private static UploadI18N buildUploadI18N() {
        UploadI18N uploadI18N = new UploadI18N();
        uploadI18N.setAddFiles(new UploadI18N.AddFiles().setOne("Carregar Arquivo..."));
        uploadI18N.setDropFiles(new UploadI18N.DropFiles().setOne("Solte o arquivo PDF aqui"));
        uploadI18N.setError(new UploadI18N.Error()
                .setIncorrectFileType("O arquivo enviado não tem o formato correto (Documento em PDF)."));
        uploadI18N.setUploading(new UploadI18N.Uploading()
                .setStatus(new UploadI18N.Uploading.Status()
                        .setConnecting("Conectando...")
                        .setStalled("Parado")
                        .setProcessing("Processando Arquivo"))
                .setError(new UploadI18N.Uploading.Error()
                        .setServerUnavailable("Falha no carregamento, tente novamente mais tarde")
                        .setUnexpectedServerError("Erro inesperado no servidor durante o carregamento")));
        return uploadI18N;
    }

    private static void clearForm(TextField descricao, DatePicker date, TextField valor, Select<ContaBancariaProjection> contaBancaria) {
        descricao.clear();
        date.clear();
        valor.clear();
        contaBancaria.clear();
    }

    private static TextField getTextFieldValor() {
        TextField valor = new TextField("Valor");
        valor.setPlaceholder("Digite o valor");
        valor.setPrefixComponent(new Span("R$"));

        valor.addValueChangeListener(e -> {
            String value = e.getValue();

            // Ajuste no regex para permitir números negativos
            if (!value.matches("^-?\\d{1,3}(\\.\\d{3})*,\\d{2}$")) {
                // Remove caracteres inválidos e mantém apenas números, pontos, vírgulas e o sinal de "-"
                value = value.replaceAll("[^\\d.,-]", "");

                // Garante que o "-" só apareça no início, se existir
                if (value.contains("-")) {
                    value = "-" + value.replace("-", "");
                }

                // Corrige casos onde o usuário digitou um valor sem os centavos
                if (value.matches("^-?\\d+(\\.\\d{3})*$")) {
                    value += ",00";
                }

                valor.setValue(value);
            }
        });
        return valor;
    }

    private static TransacaoDTO buildTransacaoDTO(LocalDate data, String descricao, BigDecimal valor,
                                                  String nomeInstituicao, Long idInstituicao) {
        TransacaoDTO transacaoDTO = new TransacaoDTO();
        transacaoDTO.setData(data);
        transacaoDTO.setDescricao(descricao);
        transacaoDTO.setValor(valor);
        transacaoDTO.setNomeInstituicao(nomeInstituicao);
        transacaoDTO.setIdInstituicao(idInstituicao);
        return transacaoDTO;
    }

}
