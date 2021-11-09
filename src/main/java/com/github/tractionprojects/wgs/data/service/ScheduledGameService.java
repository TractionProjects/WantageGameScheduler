package com.github.tractionprojects.wgs.data.service;

import com.github.tractionprojects.wgs.data.entity.ScheduledGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.time.LocalDate;
import java.util.Set;

@Service
public class ScheduledGameService extends CrudService<ScheduledGame, Integer>
{

    private final ScheduledGameRepository repository;

    public ScheduledGameService(@Autowired ScheduledGameRepository repository)
    {
        this.repository = repository;
    }

    @Override
    protected JpaRepository<ScheduledGame, Integer> getRepository()
    {
        return repository;
    }

    public ScheduledGame save(ScheduledGame scheduledGame)
    {
        return repository.save(scheduledGame);
    }

    @Scheduled(cron = "@midnight")
    public void deleteOldGames()
    {
        Set<ScheduledGame> games = repository.findByDateLessThan(LocalDate.now());
        games.forEach(game ->
        {
            game.removeAllPlayer();
            save(game);
        });
        repository.deleteInBatch(games);
    }

    public void deleteGame(ScheduledGame game)
    {
        game.removeAllPlayer();
        save(game);
        repository.delete(game);
    }
}
