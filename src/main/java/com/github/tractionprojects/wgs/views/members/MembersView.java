package com.github.tractionprojects.wgs.views.members;

import com.github.tractionprojects.wgs.Developers;
import com.github.tractionprojects.wgs.Form;
import com.github.tractionprojects.wgs.ReadForm;
import com.github.tractionprojects.wgs.data.entity.Game;
import com.github.tractionprojects.wgs.data.entity.Member;
import com.github.tractionprojects.wgs.data.service.GameService;
import com.github.tractionprojects.wgs.data.service.MemberService;
import com.github.tractionprojects.wgs.security.UserTools;
import com.github.tractionprojects.wgs.views.MenuLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vaadin.artur.helpers.CrudServiceDataProvider;
import org.vaadin.artur.spring.dataprovider.PageableDataProvider;

import java.util.List;
import java.util.Objects;

@PageTitle("Members")
@Route(value = "members", layout = MenuLayout.class)
public class MembersView extends Div
{
    private final Grid<Member> grid = new Grid<>(Member.class, false);
    private final MemberService memberService;
    private final UserTools userTools;
    private final GameService gameService;

    public MembersView(@Autowired MemberService memberService, @Autowired UserTools userTools, @Autowired GameService gameService)
    {
        addClassNames("members-view", "flex", "flex-col", "h-full");

        this.memberService = memberService;
        this.userTools = userTools;
        this.gameService = gameService;

        // Configure Grid
        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("lastName").setAutoWidth(true);
        grid.addColumn(new ComponentRenderer<>(data ->
                        new Button("Show Details", e ->
                        {
                            if (userTools.isAdmin() && !(userTools.getCurrentUsersID() == data.getDiscordId()))
                                new DetailPopupAdmin().open(data);
                            else
                                new DetailPopup().open(data);
                        })))
                .setHeader("Details").setFlexGrow(0).setAutoWidth(true);
        if (userTools.isAdmin())
            grid.addColumn(new ComponentRenderer<>(this::createDeleteButton))
                    .setHeader("Delete").setFlexGrow(0).setAutoWidth(true);
        grid.setDataProvider(new CrudServiceDataProvider<>(memberService));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();
        grid.recalculateColumnWidths();
        add(grid);
    }

    private Button createDeleteButton(Member member)
    {
        Button button = new Button("Delete", e ->
        {
            memberService.delete(member);
            grid.getDataProvider().refreshAll();
        }
        );
        if (Objects.equals(member.getId(), userTools.getCurrentMember().getId()))
            button.setEnabled(false);
        return button;
    }

    private class DetailPopup extends ReadForm<Member>
    {
        public DetailPopup()
        {
            super(Member.class);
            addField("First Name", new TextField(), "firstName").setWidthFull();
            addField("Last Name", new TextField(), "lastName").setWidthFull();
            MultiSelectListBox<Game> games = addField("Game Systems", new MultiSelectListBox<>(), "gameSystems");

            games.setDataProvider(new PageableDataProvider<Game, String>()
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
            games.setRenderer(new TextRenderer<>(Game::getName));
            games.setWidthFull();
        }
    }

    private class DetailPopupAdmin extends Form<Member>
    {
        private final Checkbox admin;

        public DetailPopupAdmin()
        {
            super(Member.class);
            addField("First Name", new TextField(), "firstName").setWidthFull();
            addField("Last Name", new TextField(), "lastName").setWidthFull();
            MultiSelectListBox<Game> games = addField("Game Systems", new MultiSelectListBox<>(), "gameSystems");

            games.setDataProvider(new PageableDataProvider<Game, String>()
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
            games.setRenderer(new TextRenderer<>(Game::getName));
            games.setWidthFull();

            admin = addField("Admin", new Checkbox(), "isAdmin");
            admin.setWidthFull();
        }

        @Override
        public void open(Member data)
        {
            if (Developers.isDev(data.getDiscordId()))
                admin.setEnabled(false);
            super.open(data);
        }

        @Override
        protected boolean saveData(Member data)
        {
            memberService.save(data);
            grid.getDataProvider().refreshAll();
            return true;
        }
    }
}
