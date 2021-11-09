package com.github.tractionprojects.wgs.views.login;

import com.github.tractionprojects.wgs.Developers;
import com.github.tractionprojects.wgs.data.entity.Member;
import com.github.tractionprojects.wgs.data.service.MemberService;
import com.github.tractionprojects.wgs.security.UserTools;
import com.github.tractionprojects.wgs.views.schedule.ScheduleView;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Login Process")
@Route(value = "loginprocess")
public class LoginProcessView extends Div implements BeforeEnterObserver
{
    public final UserTools userTools;
    public final MemberService members;
    private final TextField firstName = new TextField("First Name");
    private final TextField lastName = new TextField("Last Name");
    private final Button createAccount = new Button("Create Account");
    private final Button cancel = new Button("Cancel");
    private final Binder<Member> binder = new Binder<>(Member.class);

    public LoginProcessView(UserTools userTools, MemberService members)
    {
        this.userTools = userTools;
        this.members = members;

        addClassName("my-account-view");

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        binder.forField(firstName).asRequired("Please enter your name.").bind("firstName");
        binder.forField(lastName).bind("lastName");


        cancel.addClickListener(e -> getUI().ifPresent(ui -> ui.getPage().setLocation("/logout")));
        createAccount.addClickListener(e ->
        {
            Member newMember = new Member();
            try
            {
                binder.writeBean(newMember);
                long discordID = userTools.getCurrentUsersID();
                newMember.setDiscordId(discordID);
                if (Developers.isDev(discordID))
                    newMember.setIsAdmin(true);
                members.update(newMember);
                getUI().ifPresent(ui -> ui.navigate(ScheduleView.class));
            } catch (ValidationException ignore)
            {
            }
        });
    }

    private Component createTitle()
    {
        return new H3("Create Account");
    }

    private Component createFormLayout()
    {
        FormLayout formLayout = new FormLayout();
        formLayout.add(firstName, lastName);
        return formLayout;
    }

    private Component createButtonLayout()
    {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        createAccount.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(createAccount);
        buttonLayout.add(cancel);
        return buttonLayout;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        boolean isMemberOfClub = userTools.getPartOfGuild();
        if (!isMemberOfClub)
        {
            this.setVisible(false);
            Dialog dialog = new Dialog();
            dialog.add(new Text(userTools.getCurrentUsersName() + " is not part of the discord server."));
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);

            Button confirmButton = new Button("Ok", event ->
            {
                dialog.close();
                UI.getCurrent().navigate(LogoutProcessView.class);
            });
            Shortcuts.addShortcutListener(dialog, () ->
            {
                dialog.close();
                UI.getCurrent().navigate(LogoutProcessView.class);
            }, Key.ESCAPE);

            dialog.add(new Div(confirmButton));
            dialog.open();
            return;
        }
        long ourId = userTools.getCurrentUsersID();
        Member foundMember = members.getByDiscordID(ourId);
        if (foundMember != null)
            beforeEnterEvent.forwardTo(ScheduleView.class);
    }
}
