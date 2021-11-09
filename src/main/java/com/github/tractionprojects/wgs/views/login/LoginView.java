package com.github.tractionprojects.wgs.views.login;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Login")
@Route(value = "login")
public class LoginView extends VerticalLayout
{
    public LoginView()
    {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        add(new Button("Login", e -> getUI().ifPresent(ui -> ui.getPage().setLocation("/oauth2/authorization/discord"))));
    }
}
