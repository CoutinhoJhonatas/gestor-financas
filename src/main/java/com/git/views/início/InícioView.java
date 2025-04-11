package com.git.views.início;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.List;

@PageTitle("Início")
@Route("")
@Menu(order = 0, icon = LineAwesomeIconUrl.HOME_SOLID)
@PermitAll
public class InícioView extends VerticalLayout {

    public InícioView() {
        setSizeFull();
        setSpacing(false);
        setPadding(false);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        add(buildHeader(), buildWelcomeCard(), buildNavigation(), buildFooter());
    }

    private Component buildHeader() {
        H1 title = new H1("Meu Controle Financeiro");
        title.getStyle()
                .set("font-size", "2.8rem")
                .set("color", "#1f2937")
                .set("margin", "32px 0 8px 0");
        return title;
    }

    private Component buildWelcomeCard() {
        Span description = new Span("Organize suas finanças de forma prática! Importe extratos e acompanhe seus gastos.");
        description.getStyle()
                .set("font-size", "1.1rem")
                .set("color", "#4b5563");

        VerticalLayout card = new VerticalLayout(description);
        card.setWidth("500px");
        card.setPadding(true);
        card.setSpacing(true);
        card.setAlignItems(Alignment.CENTER);
        card.getStyle()
                .set("background-color", "white")
                .set("border-radius", "12px")
                .set("box-shadow", "0 10px 25px rgba(0, 0, 0, 0.08)")
                .set("margin-bottom", "25px")
                .set("margin-top", "30px");

        return card;
    }

    private Component buildNavigation() {
        HorizontalLayout navButtons = new HorizontalLayout();
        navButtons.setSpacing(true);
        navButtons.setJustifyContentMode(JustifyContentMode.CENTER);

        Button btnImportar = new Button("Cadastrar Transações", new Icon(VaadinIcon.UPLOAD));
        btnImportar.addClickListener(e -> btnImportar.getUI().ifPresent(ui -> ui.navigate("cadastrar-transacoes")));

        Button btnTransacoes = new Button("Transações Realizadas", new Icon(VaadinIcon.LIST));
        btnTransacoes.addClickListener(e -> btnTransacoes.getUI().ifPresent(ui -> ui.navigate("realizadas")));

        /*Button btnCategorias = new Button("Categorias", new Icon(VaadinIcon.TAGS));
        btnCategorias.addClickListener(e -> btnCategorias.getUI().ifPresent(ui -> ui.navigate("categorias")));

        Button btnRelatorios = new Button("Relatórios", new Icon(VaadinIcon.CHART));
        btnRelatorios.addClickListener(e -> btnRelatorios.getUI().ifPresent(ui -> ui.navigate("relatorios")));
*/
        for (Button btn : List.of(btnImportar, btnTransacoes)) {
            btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
            btn.getStyle()
                    .set("cursor", "pointer")
                    .set("border-radius", "8px");
        }

        navButtons.add(btnImportar, btnTransacoes);
        return navButtons;
    }

    private Component buildFooter() {
        Span footer = new Span("Desenvolvido por Jhonatas Coutinho - 2025");
        footer.getStyle()
                .set("margin-top", "auto")
                .setMarginBottom("20px")
                .set("font-size", "0.9rem")
                .set("color", "gray");
        return footer;
    }

}
