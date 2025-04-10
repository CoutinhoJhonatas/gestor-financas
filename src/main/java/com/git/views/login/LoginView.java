package com.git.views.login;

import com.git.security.AuthenticatedUser;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class LoginView extends LoginOverlay implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;

    public LoginView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        setAction(RouteUtil.getRoutePath(VaadinService.getCurrent().getContext(), getClass()));

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Gestor Financeiro");
        i18n.getHeader().setDescription("Faça o login abaixo utilizando suas credenciais");

        i18n.getForm().setTitle(null);
        i18n.getForm().setUsername("Usuário");
        i18n.getForm().setPassword("Senha");
        i18n.getForm().setSubmit("Entrar");

        i18n.getErrorMessage().setTitle("Usuário e/ou senha inválidos");
        i18n.getErrorMessage().setMessage("Confira se os dados inseridos estão corretos e tente novamente");
        i18n.getErrorMessage().setUsername("Usuário requerido");
        i18n.getErrorMessage().setPassword("Senha requerida");

        i18n.setAdditionalInformation(null);
        setI18n(i18n);

        setForgotPasswordButtonVisible(false);
        setOpened(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (authenticatedUser.get().isPresent()) {
            // Already logged in
            setOpened(false);
            event.forwardTo("");
        }

        setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}
