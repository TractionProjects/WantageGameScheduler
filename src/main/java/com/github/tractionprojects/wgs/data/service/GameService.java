package com.github.tractionprojects.wgs.data.service;

import com.github.tractionprojects.wgs.data.entity.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class GameService extends CrudService<Game, Integer>
{

    private final GameRepository repository;

    public GameService(@Autowired GameRepository repository)
    {
        this.repository = repository;
    }

    public Game getByName(String name)
    {
        return repository.findByNameIgnoreCase(name);
    }

    @Override
    protected GameRepository getRepository()
    {
        return repository;
    }

}
