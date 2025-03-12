/*
package com.git.views.realizado;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class RealizadoFiltersView extends FlexLayout {

    private final MultiSelectComboBox<String> contasBancarias = new MultiSelectComboBox<>("Conta Bancária");
    private final DatePicker startDate = new DatePicker("Data Inicial");
    private final DatePicker endDate = new DatePicker("Data Final");
    private final TextField descricao = new TextField("Descrição");
    
    public RealizadoFiltersView() {
        setWidthFull();
        setFlexWrap(FlexWrap.WRAP);
        setJustifyContentMode(JustifyContentMode.AROUND);
        addClassName("filter-layout");

        contasBancarias.setItems("Itaú", "Nubank", "Inter", "Santander");
        contasBancarias.setPlaceholder("Escolha uma conta");
        descricao.setPlaceholder("Descrição da Transação");

        Button resetBtn = new Button("Limpar", e -> {
            descricao.clear();
            startDate.clear();
            endDate.clear();
            contasBancarias.clear();
        });
        resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button searchBtn = new Button("Buscar", e -> onSearch.run());
        searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout dateRange = new HorizontalLayout(startDate, new Text(" – "), endDate);
        dateRange.setAlignItems(Alignment.BASELINE);

        add(contasBancarias, dateRange, descricao);

        HorizontalLayout buttonLayout = new HorizontalLayout(resetBtn, searchBtn);
        buttonLayout.setSpacing(true);
        buttonLayout.setAlignItems(Alignment.CENTER);

        add(buttonLayout);
    }
    
}
*/
