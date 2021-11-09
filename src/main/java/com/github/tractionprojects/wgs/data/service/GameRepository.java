package com.github.tractionprojects.wgs.data.service;

import com.github.tractionprojects.wgs.data.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Integer>
{
    Game findByNameIgnoreCase(String name);
}