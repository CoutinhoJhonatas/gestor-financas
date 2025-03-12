package com.git.views.realizado;

import com.git.dtos.TransacaoDTO;
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
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.time.LocalDate;
import java.util.List;

@PageTitle("Realizado")
@Route("realizado")
@Menu(order = 1, icon = LineAwesomeIconUrl.PENCIL_RULER_SOLID)
@PermitAll
public class RealizadoView extends Composite<VerticalLayout> {

    private Grid<TransacaoDTO> grid;
    private final TransacaoService transacaoService;
    private List<TransacaoDTO> transacoes;

    public RealizadoView(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;

        getContent().setWidthFull();

        Div filtrosDiv = new Div();
        filtrosDiv.setWidth("100%");
        filtrosDiv.setClassName("filtros-div");
        filtrosDiv.getStyle()
                .setDisplay(Style.Display.FLEX)
                .setFlexWrap(Style.FlexWrap.WRAP)
                .setJustifyContent(Style.JustifyContent.SPACE_AROUND)
                .setAlignItems(Style.AlignItems.CENTER);

        MultiSelectComboBox<String> contasBancarias = new MultiSelectComboBox<>("Conta Bancária");
        contasBancarias.setItems("Itaú", "Nubank", "Inter", "Santander");
        contasBancarias.setPlaceholder("Escolha uma ou mais contas");
        contasBancarias.setEnabled(false);

        DatePicker startDate = new DatePicker("Data Inicial");
        DatePicker endDate = new DatePicker("Data Final");

        filtrosDiv.add(startDate, endDate, contasBancarias);

        Div btnsDiv = new Div();
        btnsDiv.setWidth("100%");
        btnsDiv.setClassName("btns-dv");
        btnsDiv.getStyle()
                .setDisplay(Style.Display.FLEX)
                .setJustifyContent(Style.JustifyContent.CENTER)
                .setMarginTop("20px");

        Button searchBtn = new Button("Buscar", e -> {
            transacoes = transacaoService.buscarTransacoesByPeriodo(1L, LocalDate.now(), LocalDate.now());
            grid.setItems(transacoes);
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
        gridDiv.setClassName("grid-div");

        grid = createGrid();
        gridDiv.add(grid);

        getContent().add(filtrosDiv, gridDiv);
    }

    private Grid<TransacaoDTO> createGrid() {
        Grid<TransacaoDTO> grid = new Grid<>(TransacaoDTO.class, false);
        grid.addColumn("data").setAutoWidth(true).setFlexGrow(1).setHeader("Data");
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

        //grid.setItems(new TransacaoDTO(1L, "07/03/2025", "Pag Tit teste010203", BigDecimal.valueOf(2000.00)));

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassName(LumoUtility.Border.TOP);
        grid.getStyle().setDisplay(Style.Display.FLEX);
        grid.getStyle().setJustifyContent(Style.JustifyContent.CENTER);
        grid.getStyle().setAlignItems(Style.AlignItems.CENTER);

        return grid;
    }

        /*private void refreshGrid() {
            grid.getDataProvider().refreshAll();
        }*/
}
