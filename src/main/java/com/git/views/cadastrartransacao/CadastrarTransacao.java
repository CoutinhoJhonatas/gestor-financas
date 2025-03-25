package com.git.views.cadastrartransacao;

import com.git.data.User;
import com.git.dtos.TransacaoDTO;
import com.git.projections.ContaBancariaProjection;
import com.git.repositories.ContaBancariaRepository;
import com.git.security.AuthenticatedUser;
import com.git.services.TransacaoService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.math.BigDecimal;
import java.time.LocalDate;

@PageTitle("Cadastrar Transações")
@Route("cadastrar-transacoes")
@Menu(order = 1, icon = LineAwesomeIconUrl.CLIPBOARD)
@PermitAll
public class CadastrarTransacao extends Composite<VerticalLayout> {

    private final TransacaoService transacaoService;
    private final ContaBancariaRepository contaBancariaRepository;
    private final AuthenticatedUser authenticatedUser;
    private User user;

    public CadastrarTransacao(TransacaoService transacaoService, ContaBancariaRepository contaBancariaRepository,
                              AuthenticatedUser authenticatedUser) {
        this.transacaoService = transacaoService;
        this.contaBancariaRepository = contaBancariaRepository;
        this.authenticatedUser = authenticatedUser;

        if(authenticatedUser.get().isPresent()) {
            user = authenticatedUser.get().get();
        } else {
            throw new UsernameNotFoundException("User not found");
        }

        getContent().setWidthFull();

        Div divLayoutForm = new Div();
        divLayoutForm.setWidthFull();
        divLayoutForm.setClassName("div-layout-form");

        TextField descricao = new TextField("Descrição");
        descricao.setPlaceholder("Digite a descrição");

        DatePicker date = new DatePicker("Data");
        date.setPlaceholder("dd/mm/aaaa");

        TextField valor = getTextFieldValor();

        Select<ContaBancariaProjection> contaBancaria = new Select<>();
        contaBancaria.setLabel("Conta Bancária");
        contaBancaria.setPlaceholder("Selecione a conta bancária");
        contaBancaria.setItems(contaBancariaRepository.findByUsuarioId(user.getId()));
        contaBancaria.setItemLabelGenerator(ContaBancariaProjection::getNomeInstituicao);

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
        getContent().add(divLayoutForm);
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
