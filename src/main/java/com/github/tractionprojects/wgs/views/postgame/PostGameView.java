package com.github.tractionprojects.wgs.views.postgame;

import com.github.tractionprojects.wgs.Form;
import com.github.tractionprojects.wgs.data.entity.Game;
import com.github.tractionprojects.wgs.data.entity.Member;
import com.github.tractionprojects.wgs.data.entity.ScheduledGame;
import com.github.tractionprojects.wgs.data.service.GameService;
import com.github.tractionprojects.wgs.data.service.MemberService;
import com.github.tractionprojects.wgs.data.service.ScheduledGameService;
import com.github.tractionprojects.wgs.discord.GameEmbed;
import com.github.tractionprojects.wgs.security.UserTools;
import com.github.tractionprojects.wgs.views.MenuLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vaadin.artur.spring.dataprovider.PageableDataProvider;

import java.util.List;

@PageTitle("Post Game")
@Route(value = "post-game", layout = MenuLayout.class)
@Uses(Icon.class)
public class PostGameView extends Div
{

    private final DatePicker date = new DatePicker("Date");
    private final ComboBox<Game> game = new ComboBox<>("Game");
    private final IntegerField noPlayers = new IntegerField("Number of Players");
    private final IntegerField pointsLimit = new IntegerField("Points Limit");
    private final TextArea details = new TextArea("Details");
    private final MultiSelectListBox<Member> players = new MultiSelectListBox<>();
    private final IntegerField nonMemberPlayers = new IntegerField("Number of Non Member Players");

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Post");
    private final Button CreateGame = new Button("Add game system");

    private final Binder<ScheduledGame> binder = new Binder<>(ScheduledGame.class);

    private final GameService games;
    private final MemberService members;
    private final UserTools userTools;

    public PostGameView(ScheduledGameService scheduledGameService, GameService games, UserTools userTools, GameEmbed gameEmbed, MemberService members)
    {
        this.games = games;
        this.userTools = userTools;
        this.members = members;
        addClassName("post-game-view");

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        setupBindings();

        clearForm();

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e ->
        {
            if (binder.validate().isOk())
            {
                ScheduledGame game = binder.getBean();
                if(game.getPlayers().size() + game.getOtherPlayers() > game.getNoPlayers())
                {
                    noPlayers.setErrorMessage("The player limit must be at least the number of players playing");
                    noPlayers.setInvalid(true);
                    return;
                }
                scheduledGameService.update(game);
                Notification.show(game.getClass().getSimpleName() + " details stored.");
                if (game.getPlayers().size() + game.getOtherPlayers() < game.getNoPlayers())
                    gameEmbed.sendMessage(game);
                clearForm();
            }
        });
        CreateGame.addClickListener(e -> new DetailPopup().open(new Game()));
    }

    private void setupBindings()
    {
        binder.forField(date).asRequired("Date is required").bind("date");
        binder.forField(game).asRequired("Game is required").bind("game");
        binder.forField(noPlayers)
                .asRequired("Number of players is required")
                .withValidator(new IntegerRangeValidator(
                        "Games must have at lest 2 players", 2, Integer.MAX_VALUE))
                .bind("noPlayers");
        binder.bind(pointsLimit, "pointsLimit");
        binder.bind(details, "details");
        binder.bind(players, "players");
        binder.bind(nonMemberPlayers, "otherPlayers");
    }

    private void clearForm()
    {
        ScheduledGame game = new ScheduledGame();
        Member user = userTools.getCurrentMember();
        game.setOrganiser(user);
        game.addPlayer(user);
        binder.setBean(game);
    }

    private Component createTitle()
    {
        return new H3("Post a Game");
    }

    private Component createFormLayout()
    {
        FormLayout formLayout = new FormLayout();
        game.setDataProvider(new PageableDataProvider<Game, String>()
        {
            @Override
            protected Page<Game> fetchFromBackEnd(Query<Game, String> query, Pageable pageable)
            {
                return games.list(pageable);
            }

            @Override
            protected List<QuerySortOrder> getDefaultSortOrders()
            {
                return QuerySortOrder.asc("name").build();
            }

            @Override
            protected int sizeInBackEnd(Query<Game, String> query)
            {
                return games.count();
            }
        });
        game.setItemLabelGenerator(Game::getName);

        players.setDataProvider(new PageableDataProvider<Member, String>()
        {
            @Override
            protected Page<Member> fetchFromBackEnd(Query<Member, String> query, Pageable pageable)
            {
                return members.list(pageable);
            }

            @Override
            protected List<QuerySortOrder> getDefaultSortOrders()
            {
                return QuerySortOrder.asc("id").build();
            }

            @Override
            protected int sizeInBackEnd(Query<Member, String> query)
            {
                return members.count();
            }
        });
        players.setRenderer(new TextRenderer<>(Member::getFullName));

        formLayout.add(date, game, noPlayers, pointsLimit, details, nonMemberPlayers);
        formLayout.addFormItem(players, "Players");
        return formLayout;
    }

    private Component createButtonLayout()
    {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        CreateGame.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        if (userTools.isAdmin())
            buttonLayout.add(CreateGame);
        return buttonLayout;
    }

    private class DetailPopup extends Form<Game>
    {
        private final TextField nameField = new TextField();

        public DetailPopup()
        {
            super(Game.class);
            addField("Game", nameField, "name", true).setWidthFull();
        }

        @Override
        protected boolean saveData(Game data)
        {
            Game foundGame = games.getByName(data.getName());
            if (foundGame == null)
            {
                games.update(data);
                game.getDataProvider().refreshAll();
            } else
            {
                nameField.setErrorMessage("Game already exists");
                nameField.setInvalid(true);
            }

            return foundGame == null;
        }
    }

}
