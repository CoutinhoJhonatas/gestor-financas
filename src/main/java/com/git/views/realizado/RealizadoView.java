package com.git.views.realizado;

import com.git.data.User;
import com.git.dtos.TransacaoDTO;
import com.git.security.AuthenticatedUser;
import com.git.services.TransacaoService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

import java.time.LocalDate;
import java.util.List;

@PageTitle("Realizado")
@Route("realizado")
@Menu(order = 1, icon = LineAwesomeIconUrl.PENCIL_RULER_SOLID)
@PermitAll
public class RealizadoView extends Composite<VerticalLayout> {

    private final TransacaoService transacaoService;
    private final AuthenticatedUser authenticatedUser;
    private Grid<TransacaoDTO> grid;
    private List<TransacaoDTO> transacoes;
    private User user;

    public RealizadoView(TransacaoService transacaoService, AuthenticatedUser authenticatedUser) {
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

        MultiSelectComboBox<String> contasBancarias = new MultiSelectComboBox<>("Conta Bancária");
        contasBancarias.setItems("Itaú", "Nubank", "Inter", "Santander");
        contasBancarias.setPlaceholder("Escolha uma ou mais contas");
        contasBancarias.setEnabled(false);

        DatePicker startDate = new DatePicker("Data Inicial");
        startDate.setPlaceholder("dd/mm/aaaa");
        startDate.setMax(LocalDate.now());
        startDate.setI18n(new DatePicker.DatePickerI18n()
                .setBadInputErrorMessage("Formato de data inválida")
                .setMaxErrorMessage("Data não pode ser superior a hoje"));

        DatePicker endDate = new DatePicker("Data Final");
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
                transacoes = transacaoService.buscarTransacoesByPeriodo(user.getId(), startDate.getValue(), endDate.getValue());
                grid.setItems(transacoes);
                binder.readBean(null);
            } else {
                System.out.println(binder.validate().getValidationErrors());
                notification = Notification.show("Erro ao buscar dados");
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }

        });
        searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchBtn.getStyle().setMarginRight("20px");

        Button resetBtn = new Button("Limpar", e -> {
            startDate.clear();
            endDate.clear();
            contasBancarias.clear();
        });
        resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        btnsDiv.add(searchBtn, resetBtn);
        filtrosDiv.add(btnsDiv);

        Div gridDiv = new Div();
        gridDiv.setWidth("100%");
        gridDiv.setHeight("100%");
        gridDiv.setClassName("grid-div");

        grid = createGrid();
        gridDiv.add(grid);

        getContent().add(filtrosDiv, gridDiv);
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
        grid.addColumn("valor").setAutoWidth(true).setFlexGrow(1).setHeader("Valor");
        grid.addColumn("nomeInstituicao").setAutoWidth(true).setFlexGrow(1).setHeader("Conta Bancária");
        grid.addColumn(
                new ComponentRenderer<>(Div::new, (div, contaBancariaDTO) -> {
                    div.setWidthFull();
                    div.getStyle().setDisplay(Style.Display.FLEX);
                    div.getStyle().setJustifyContent(Style.JustifyContent.SPACE_AROUND);

                    Button buttonEdit = new Button();
                    buttonEdit.addThemeVariants(ButtonVariant.LUMO_ICON);
                    buttonEdit.addClickListener(e -> Notification.show("Button Edit"));
                    buttonEdit.setIcon(new Icon(VaadinIcon.PENCIL));

                    Button buttonDelete = new Button();
                    buttonDelete.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_ERROR,
                            ButtonVariant.LUMO_TERTIARY);
                    buttonDelete.addClickListener(e -> Notification.show("Button Trash"));
                    buttonDelete.setIcon(new Icon(VaadinIcon.TRASH));

                    div.add(buttonEdit, buttonDelete);
                })).setHeader("Opções");

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.addClassName(LumoUtility.Border.TOP);
        grid.getStyle().setDisplay(Style.Display.FLEX);
        grid.getStyle().setJustifyContent(Style.JustifyContent.CENTER);
        grid.getStyle().setAlignItems(Style.AlignItems.CENTER);

        //Todo verificar como deixar o heigth mais dinamico
        grid.setMaxHeight("500px");

        return grid;
    }

        /*private void refreshGrid() {
            grid.getDataProvider().refreshAll();
        }*/
}
