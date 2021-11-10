package com.github.tractionprojects.wgs.views.schedule;

import com.github.tractionprojects.wgs.Form;
import com.github.tractionprojects.wgs.components.SetConverter;
import com.github.tractionprojects.wgs.data.entity.Member;
import com.github.tractionprojects.wgs.data.entity.ScheduledGame;
import com.github.tractionprojects.wgs.data.service.ScheduledGameService;
import com.github.tractionprojects.wgs.security.UserTools;
import com.github.tractionprojects.wgs.views.MenuLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.helpers.CrudServiceDataProvider;

import java.util.Collections;

@PageTitle("Posted Games")
@Route(value = "postedgames", layout = MenuLayout.class)
@RouteAlias(value = "", layout = MenuLayout.class)
public class ScheduleView extends Div
{
    private final Grid<ScheduledGame> grid = new Grid<>(ScheduledGame.class, false);
    private final UserTools userTools;
    private final ScheduledGameService scheduledGameService;

    public ScheduleView(@Autowired ScheduledGameService scheduledGameService, @Autowired UserTools userTools)
    {
        this.scheduledGameService = scheduledGameService;
        this.userTools = userTools;
        addClassNames("members-view", "flex", "flex-col", "h-full");

        // Configure Grid
        grid.addColumn("date").setAutoWidth(true);
        grid.addColumn("organiser.fullName").setHeader("Organiser").setAutoWidth(true);
        grid.addColumn("game.name").setHeader("Game").setAutoWidth(true);
        grid.addColumn(g -> String.format("%d/%d", g.getPlayers().size(), g.getNoPlayers()))
                .setHeader("Number of Players").setAutoWidth(true);
        grid.addColumn("pointsLimit").setAutoWidth(true);
        grid.setDataProvider(new CrudServiceDataProvider<>(scheduledGameService));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();
        grid.setItemDetailsRenderer(createDetailsRenderer());
        grid.setDetailsVisibleOnClick(true);
        grid.recalculateColumnWidths();
        grid.sort(Collections.singletonList(new GridSortOrder<>(grid.getColumnByKey("date"), SortDirection.ASCENDING)));
        add(grid);
    }

    private ComponentRenderer<DetailsFormLayout, ScheduledGame> createDetailsRenderer()
    {
        return new ComponentRenderer<>(DetailsFormLayout::new, DetailsFormLayout::setScheduledGame);
    }

    private class DetailsFormLayout extends Div
    {
        private final Binder<ScheduledGame> binder = new Binder<>(ScheduledGame.class);
        private final TextArea details = new TextArea("Details");
        private final TextArea players = new TextArea("Players");

        private Button join;
        private Button leave;
        private Button edit;
        private Button delete;

        public DetailsFormLayout()
        {
            FormLayout form = createFormLayout();
            HorizontalLayout buttons = createButtonPanel();
            binder.bind(details, "details");
            binder.forField(players).withConverter(new SetConverter<>(Member::getFullName)).bind(ScheduledGame::getPlayers, null);
            add(form, buttons);
        }

        public void setScheduledGame(ScheduledGame game)
        {
            binder.setBean(game);
            updateButtonStates();
        }

        public FormLayout createFormLayout()
        {
            FormLayout layout = new FormLayout();
            details.setReadOnly(true);
            players.setReadOnly(true);

            layout.add(details, players);
            return layout;
        }

        private HorizontalLayout createButtonPanel()
        {
            HorizontalLayout buttons = new HorizontalLayout();
            join = new Button("Join Game", e ->
            {
                ScheduledGame game = binder.getBean();
                game.addPlayer(userTools.getCurrentMember());
                game = scheduledGameService.save(binder.getBean());
                binder.setBean(game);
                grid.getDataProvider().refreshItem(binder.getBean());
                updateButtonStates();
            });
            leave = new Button("Leave Game", e ->
            {
                ScheduledGame game = binder.getBean();
                game.removePlayer(userTools.getCurrentMember());
                game = scheduledGameService.save(binder.getBean());
                binder.setBean(game);
                grid.getDataProvider().refreshItem(binder.getBean());
                updateButtonStates();
            });
            edit = new Button("Edit Game", e ->
            {
                ScheduledGame game = binder.getBean();
                new EditForm().open(game);
            });
            delete = new Button("Delete Game", e ->
            {
                scheduledGameService.deleteGame(binder.getBean());
                grid.getDataProvider().refreshAll();
            });
            buttons.add(join, leave, edit, delete);
            return buttons;
        }

        private void updateButtonStates()
        {
            ScheduledGame game = binder.getBean();

            boolean gameFull = game.getPlayers().size() >= game.getNoPlayers();
            boolean playing = game.getPlayers().contains(userTools.getCurrentMember());
            boolean organiser = game.getOrganiser().equals(userTools.getCurrentMember());
            boolean admin = userTools.isAdmin();

            join.setEnabled(!playing && !gameFull);
            leave.setEnabled(playing);
            edit.setEnabled(organiser || admin);
            delete.setEnabled(organiser || admin);
        }
    }

    private class EditForm extends Form<ScheduledGame>
    {

        private final IntegerField editNoPlayers;

        public EditForm()
        {
            super(ScheduledGame.class);
            editNoPlayers = addField("Number Of Players", new IntegerField(), "noPlayers", true);
            addField("Points Limit", new IntegerField(), "pointsLimit");
            addField("Details", new TextArea(), "details").setWidthFull();
        }

        @Override
        protected boolean saveData(ScheduledGame data)
        {
            if (editNoPlayers.getValue() < 2)
            {
                editNoPlayers.setErrorMessage("Games must have at lest 2 players");
                editNoPlayers.setInvalid(true);
                return false;
            }
            if(editNoPlayers.getValue() < data.getPlayers().size())
            {
                editNoPlayers.setErrorMessage("You can not set the player limit lower than the current number of players");
                editNoPlayers.setInvalid(true);
                return false;
            }
            scheduledGameService.save(data);
            grid.getDataProvider().refreshItem(data);
            return true;
        }
    }
}