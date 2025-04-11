package com.git.views.realizado;

import com.git.data.User;
import com.git.dtos.TransacaoDTO;
import com.git.projections.ContaBancariaProjection;
import com.git.repositories.ContaBancariaRepository;
import com.git.security.AuthenticatedUser;
import com.git.services.TransacaoService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@PageTitle("Transações Realizadas")
@Route("realizadas")
@Menu(order = 2, icon = LineAwesomeIconUrl.PENCIL_RULER_SOLID)
@PermitAll
public class RealizadoView extends Composite<VerticalLayout> {

    private final ContaBancariaRepository contaBancariaRepository;
    private final TransacaoService transacaoService;
    private final AuthenticatedUser authenticatedUser;
    private Grid<TransacaoDTO> grid;
    private List<TransacaoDTO> transacoes;
    private User user;
    private MultiSelectComboBox<String> contasBancarias;
    private DatePicker startDate;
    private DatePicker endDate;
    private TextField totalEntradas;
    private TextField totalSaidas;


    public RealizadoView(ContaBancariaRepository contaBancariaRepository, TransacaoService transacaoService, AuthenticatedUser authenticatedUser) {
        this.contaBancariaRepository = contaBancariaRepository;
        this.transacaoService = transacaoService;
        this.authenticatedUser = authenticatedUser;

        if(authenticatedUser.get().isPresent()) {
            user = authenticatedUser.get().get();
        } else {
            throw new UsernameNotFoundException("User not found");
        }

        getContent().setWidthFull();

        Div filtrosDiv = new Div();
        filtrosDiv.setWidth("100%");
        filtrosDiv.setClassName("filtros-div");
        filtrosDiv.getStyle()
                .setDisplay(Style.Display.FLEX)
                .setFlexWrap(Style.FlexWrap.WRAP)
                .setJustifyContent(Style.JustifyContent.SPACE_AROUND);

        contasBancarias = new MultiSelectComboBox<>("Conta Bancária");
        contasBancarias.setItems("Itaú", "Nubank", "Inter", "Santander");
        contasBancarias.setPlaceholder("Escolha uma ou mais contas");
        contasBancarias.setEnabled(false);

        startDate = new DatePicker("Data Inicial");
        startDate.setPlaceholder("dd/mm/aaaa");
        startDate.setMax(LocalDate.now());
        startDate.setI18n(new DatePicker.DatePickerI18n()
                .setBadInputErrorMessage("Formato de data inválida")
                .setMaxErrorMessage("Data não pode ser superior a hoje"));

        endDate = new DatePicker("Data Final");
        endDate.setPlaceholder("dd/mm/aaaa");
        endDate.setMax(LocalDate.now());
        endDate.setI18n(new DatePicker.DatePickerI18n()
                .setBadInputErrorMessage("Formato de data inválida")
                .setMaxErrorMessage("Data não pode ser superior a hoje"));

        startDate.addValueChangeListener(e -> endDate.setMin(startDate.getValue()));
        endDate.addValueChangeListener(e -> startDate.setMax(endDate.getValue()));

        Binder<DatePicker> binder = new Binder<>(DatePicker.class);
        binder.forField(startDate)
                .asRequired("Data inicial requerida")
                .bind(DatePicker::getValue, DatePicker::setValue);

        filtrosDiv.add(startDate, endDate, contasBancarias);

        Div totaisDiv = new Div();
        totaisDiv.setWidth("100%");
        totaisDiv.setHeight("100%");
        totaisDiv.setClassName("totais-div");
        totaisDiv.getStyle()
                .setDisplay(Style.Display.FLEX)
                .setJustifyContent(Style.JustifyContent.CENTER);

        totalEntradas = new TextField("Total Entradas");
        totalEntradas.setReadOnly(true);
        totalEntradas.getStyle()
                .setMarginRight("25px");

        totalSaidas = new TextField("Total Saídas");
        totalSaidas.setReadOnly(true);

        totaisDiv.add(totalEntradas, totalSaidas);

        Div btnsDiv = new Div();
        btnsDiv.setWidth("100%");
        btnsDiv.setClassName("btns-dv");
        btnsDiv.getStyle()
                .setDisplay(Style.Display.FLEX)
                .setJustifyContent(Style.JustifyContent.CENTER)
                .setMarginTop("5px");

        Button searchBtn = new Button("Buscar", e -> {
            Notification notification;
            if (binder.validate().isOk()) {
                atualizarGrid();
                atualizarTotais();
                //clearFilters(startDate, endDate, contasBancarias);
                //binder.readBean(null);
            } else {
                System.out.println(binder.validate().getValidationErrors());
                notification = Notification.show("ERRO AO BUSCAR DADOS", 4000, Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }

        });
        searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchBtn.getStyle()
                .set("cursor", "pointer")
                .setMarginRight("20px");

        Button resetBtn = new Button("Limpar", e -> clearFilters(startDate, endDate, contasBancarias));
        resetBtn.getStyle().set("cursor", "pointer");
        resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        btnsDiv.add(searchBtn, resetBtn);
        filtrosDiv.add(btnsDiv);

        Div gridDiv = new Div();
        gridDiv.setWidth("100%");
        gridDiv.setHeight("100%");
        gridDiv.setClassName("grid-div");

        grid = createGrid();
        gridDiv.add(grid);

        getContent().add(filtrosDiv, gridDiv, totaisDiv);
    }

    private static void clearFilters(DatePicker startDate, DatePicker endDate, MultiSelectComboBox<String> contasBancarias) {
        startDate.clear();
        endDate.clear();
        contasBancarias.clear();
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

        grid.addColumn("nomeInstituicao").setAutoWidth(true).setFlexGrow(1).setHeader("Conta Bancária");
        grid.addColumn(
                new ComponentRenderer<>(Div::new, (div, transacaoDTO) -> {
                    div.setWidthFull();
                    div.getStyle().setDisplay(Style.Display.FLEX);
                    div.getStyle().setJustifyContent(Style.JustifyContent.SPACE_AROUND);

                    Button buttonEdit = new Button();
                    buttonEdit.addThemeVariants(ButtonVariant.LUMO_ICON);
                    buttonEdit.addClickListener(e -> handleButtonEdit(transacaoDTO));
                    buttonEdit.setIcon(new Icon(VaadinIcon.PENCIL));
                    buttonEdit.getStyle()
                                .set("cursor", "pointer");

                    Button buttonDelete = new Button();
                    buttonDelete.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_TERTIARY);
                    buttonDelete.addClickListener(e ->
                            handleButtonDelete(transacaoDTO.getId(), transacaoDTO.getDescricao()));
                    buttonDelete.setIcon(new Icon(VaadinIcon.TRASH));
                    buttonDelete.getStyle()
                                .set("cursor", "pointer");

                    div.add(buttonEdit, buttonDelete);
                })).setHeader("Opções");

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addClassName(LumoUtility.Border.TOP);
        grid.getStyle().setDisplay(Style.Display.FLEX);
        grid.getStyle().setJustifyContent(Style.JustifyContent.CENTER);
        grid.getStyle().setAlignItems(Style.AlignItems.CENTER);

        return grid;
    }

    private void handleButtonEdit(TransacaoDTO dto) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Editar Transação");

        TextField descricao = new TextField("Descrição");
        descricao.setPlaceholder("Digite a descrição");
        descricao.setValue(dto.getDescricao());

        DatePicker date = new DatePicker("Data");
        date.setPlaceholder("dd/mm/aaaa");
        date.setValue(dto.getData());

        TextField valor = getTextFieldValor();
        valor.setValue(dto.getValor().toString().replace(".", ","));

        List<ContaBancariaProjection> listContaBancaria = contaBancariaRepository.findByUsuarioId(user.getId());
        Select<ContaBancariaProjection> contaBancaria = getContaBancariaProjectionSelect(listContaBancaria);
        contaBancaria.setValue(listContaBancaria.stream()
                .filter(c -> c.getNomeInstituicao().equals(dto.getNomeInstituicao()))
                .findFirst()
                .orElse(null));

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

        Button saveBtn = new Button("Salvar", e -> {
            Notification notification;
            if (binder.validate().isOk()
                    && binderValor.validate().isOk()
                    && binderContaBancaria.validate().isOk()) {

                transacaoService.update(buildTransacaoDTO(
                        dto.getId(),
                        date.getValue(),
                        descricao.getValue(),
                        BigDecimal.valueOf(Double.parseDouble(
                                valor.getValue().replace(",", ".")
                        )),
                        contaBancaria.getValue().getNomeInstituicao(),
                        contaBancaria.getValue().getId()
                ));
                notification = Notification.show("SALVO COM SUCESSO", 4000, Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                atualizarGrid();
                atualizarTotais();
                dialog.close();

                binder.readBean(null);
                binderValor.readBean(null);
                binderContaBancaria.readBean(null);
            } else {
                System.out.println(binder.validate().getValidationErrors());
                notification = Notification.show("ERRO AO SALVAR", 4000, Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }

        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.getStyle()
                .set("margin-right", "auto")
                .set("cursor", "pointer");
        dialog.getFooter().add(saveBtn);

        Button cancelButton = new Button("Cancelar", e -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.getStyle()
                .set("cursor", "pointer");
        dialog.getFooter().add(cancelButton);

        dialog.add(form);
        dialog.open();
    }

    private void handleButtonDelete(Long transacaoId, String descricao) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(String.format("Excluir transação \"%s\" ?", descricao));
        dialog.add("Você tem certeza que deseja deletar a transação permanentemente?");

        Button deleteButton = new Button("Excluir", e -> {
            transacaoService.excluir(transacaoId);
            Notification notification = Notification.show("DELETADO COM SUCESSO", 4000, Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

            atualizarGrid();
            atualizarTotais();
            dialog.close();
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_ERROR);
        deleteButton.getStyle().set("margin-right", "auto");
        dialog.getFooter().add(deleteButton);

        Button cancelButton = new Button("Cancelar", e -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(cancelButton);

        dialog.open();
    }

    private static TextField getTextFieldValor() {
        TextField valor = new TextField("Valor");
        valor.setPlaceholder("Digite o valor");
        valor.setPrefixComponent(new Span("R$"));

        valor.addValueChangeListener(e -> {
            String value = e.getValue();

            // Remove caracteres inválidos: só permite dígitos, vírgula e traço
            value = value.replaceAll("[^\\d,-]", "");

            // Garante que o "-" só apareça no início, se existir
            if (value.contains("-")) {
                value = "-" + value.replace("-", "");
            }

            // Corrige casos onde o usuário digitou um valor sem os centavos
            if (value.matches("^-?\\d+(\\.\\d{3})*$")) {
                value += ",00";
            }

            // Atualiza o valor somente se for diferente, para evitar loops infinitos
            if (!value.equals(e.getValue())) {
                valor.setValue(value);
            }
        });
        return valor;
    }

    private static Select<ContaBancariaProjection> getContaBancariaProjectionSelect(List<ContaBancariaProjection> listContaBancaria) {
        Select<ContaBancariaProjection> contaBancaria = new Select<>();
        contaBancaria.setLabel("Conta Bancária");
        contaBancaria.setPlaceholder("Selecione a conta bancária");
        contaBancaria.setItems(listContaBancaria);
        contaBancaria.setItemLabelGenerator(ContaBancariaProjection::getNomeInstituicao);
        return contaBancaria;
    }

    private static TransacaoDTO buildTransacaoDTO(Long idTransacao, LocalDate data, String descricao, BigDecimal valor,
                                                  String nomeInstituicao, Long idInstituicao) {
        TransacaoDTO transacaoDTO = new TransacaoDTO();
        transacaoDTO.setId(idTransacao);
        transacaoDTO.setData(data);
        transacaoDTO.setDescricao(descricao);
        transacaoDTO.setValor(valor);
        transacaoDTO.setNomeInstituicao(nomeInstituicao);
        transacaoDTO.setIdInstituicao(idInstituicao);
        return transacaoDTO;
    }

    private void atualizarGrid() {
        transacoes = transacaoService.buscarTransacoesByPeriodo(user.getId(), startDate.getValue(), endDate.getValue());
        grid.setItems(transacoes);
    }

    private void atualizarTotais() {
        totalEntradas.setValue(transacaoService.calcularTotalEntrada(transacoes));
        totalSaidas.setValue(transacaoService.calcularTotalSaida(transacoes));
    }
}
