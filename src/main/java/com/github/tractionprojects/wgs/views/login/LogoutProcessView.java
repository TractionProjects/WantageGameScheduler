package com.github.tractionprojects.wgs.views.login;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Route(value = "logoutprocess")
public class LogoutProcessView extends Div implements BeforeEnterObserver
{
    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        new SecurityContextLogoutHandler().logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }
}
