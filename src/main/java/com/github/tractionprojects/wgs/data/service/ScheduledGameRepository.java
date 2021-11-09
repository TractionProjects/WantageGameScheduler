package com.github.tractionprojects.wgs.data.service;

import com.github.tractionprojects.wgs.data.entity.ScheduledGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Set;

public interface ScheduledGameRepository extends JpaRepository<ScheduledGame, Integer>
{
    Set<ScheduledGame> findByDateLessThan(LocalDate date);
}
