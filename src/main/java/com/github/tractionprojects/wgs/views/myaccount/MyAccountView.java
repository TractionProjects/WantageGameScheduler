package com.github.tractionprojects.wgs.views.myaccount;

import com.github.tractionprojects.wgs.data.entity.Game;
import com.github.tractionprojects.wgs.data.entity.Member;
import com.github.tractionprojects.wgs.data.service.GameService;
import com.github.tractionprojects.wgs.data.service.MemberService;
import com.github.tractionprojects.wgs.security.UserTools;
import com.github.tractionprojects.wgs.views.MenuLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vaadin.artur.spring.dataprovider.PageableDataProvider;

import java.util.List;

@PageTitle("My Account")
@Route(value = "my-account", layout = MenuLayout.class)
public class MyAccountView extends Div
{

    private final UserTools userTools;
    private final TextField firstName = new TextField("First Name");
    private final TextField lastName = new TextField("Last Name");
    private final MultiSelectListBox<Game> gameSystems = new MultiSelectListBox<>();
    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Binder<Member> binder = new Binder<>(Member.class);

    public MyAccountView(MemberService memberService, UserTools userTools, GameService gameService)
    {
        this.userTools = userTools;

        addClassName("my-account-view");

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        gameSystems.setDataProvider(new PageableDataProvider<Game, String>()
        {
            @Override
            protected Page<Game> fetchFromBackEnd(Query<Game, String> query, Pageable pageable)
            {
                return gameService.list(pageable);
            }

            @Override
            protected List<QuerySortOrder> getDefaultSortOrders()
            {
                return QuerySortOrder.asc("name").build();
            }

            @Override
            protected int sizeInBackEnd(Query<Game, String> query)
            {
                return gameService.count();
            }
        });
        gameSystems.setRenderer(new TextRenderer<>(Game::getName));

        binder.forField(firstName).asRequired("Please enter your name.").bind("firstName");
        binder.forField(lastName).bind("lastName");
        binder.forField(gameSystems).bind("gameSystems");

        LoadUserData();

        cancel.addClickListener(e -> LoadUserData());

        save.addClickListener(e ->
        {
            if (binder.validate().isOk())
            {
                memberService.update(binder.getBean());
                LoadUserData();
            }
        });
    }

    private Component createTitle()
    {
        return new H3("My Account");
    }

    private Component createFormLayout()
    {
        FormLayout formLayout = new FormLayout();
        formLayout.add(firstName, lastName);
        formLayout.addFormItem(gameSystems, "Games");
        return formLayout;
    }

    private Component createButtonLayout()
    {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save);
        buttonLayout.add(cancel);
        return buttonLayout;
    }

    private void LoadUserData()
    {
        this.binder.setBean(userTools.getCurrentMember());
    }

}
