package com.github.tractionprojects.wgs.data.entity;

import com.github.tractionprojects.wgs.data.AbstractEntity;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ScheduledGame extends AbstractEntity
{
    private LocalDate date;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")

    private Member organiser;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(referencedColumnName = "id")
    private Game game;
    private int noPlayers = 2;
    @ColumnDefault("0")
    private int otherPlayers = 0;
    private int pointsLimit;
    private String details;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "scheduledGamePlayers",
            joinColumns = @JoinColumn(name = "ScheduledGameId"),
            inverseJoinColumns = @JoinColumn(name = "MemberId"))
    private Set<Member> players;

    public LocalDate getDate()
    {
        return date;
    }

    public void setDate(LocalDate date)
    {
        this.date = date;
    }

    public Member getOrganiser()
    {
        return organiser;
    }

    public void setOrganiser(Member organiser)
    {
        this.organiser = organiser;
    }

    public Game getGame()
    {
        return game;
    }

    public void setGame(Game game)
    {
        this.game = game;
    }

    public int getNoPlayers()
    {
        return noPlayers;
    }

    public void setNoPlayers(int noPlayers)
    {
        this.noPlayers = noPlayers;
    }

    public int getOtherPlayers()
    {
        return otherPlayers;
    }

    public void setOtherPlayers(int otherPlayers)
    {
        this.otherPlayers = otherPlayers;
    }

    public int getPointsLimit()
    {
        return pointsLimit;
    }

    public void setPointsLimit(int pointsLimit)
    {
        this.pointsLimit = pointsLimit;
    }

    public String getDetails()
    {
        return details;
    }

    public void setDetails(String details)
    {
        this.details = details;
    }

    public Set<Member> getPlayers()
    {
        return players;
    }

    public void setPlayers(Set<Member> players)
    {
        this.players = players;
    }

    public void addPlayer(Member member)
    {
        if (players == null)
            players = new HashSet<>();
        players.add(member);
    }

    public void removePlayer(Member member)
    {
        if (players != null)
            players.remove(member);
    }

    public void removeAllPlayer()
    {
        if (players != null)
            players.clear();
    }
}
