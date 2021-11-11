package com.github.tractionprojects.wgs.data.entity;

import com.github.tractionprojects.wgs.data.AbstractEntity;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Member extends AbstractEntity
{

    private String firstName;
    private String lastName;
    private String email;
    private long discordId;
    private boolean isAdmin;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "membersGames",
            joinColumns = @JoinColumn(name = "MemberId"),
            inverseJoinColumns = @JoinColumn(name = "GameId"))
    private Set<Game> gameSystems;

    @OneToMany(mappedBy = "organiser", fetch = FetchType.EAGER)
    private Set<ScheduledGame> organisedGames;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "scheduledGamePlayers",
            joinColumns = @JoinColumn(name = "MemberId"),
            inverseJoinColumns = @JoinColumn(name = "ScheduledGameId"))
    private Set<ScheduledGame> gamesJoined;

    public Set<Game> getGameSystems()
    {
        return gameSystems;
    }

    public void setGameSystems(Set<Game> gameSystems)
    {
        this.gameSystems = gameSystems;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public long getDiscordId()
    {
        return discordId;
    }

    public void setDiscordId(long discordId)
    {
        this.discordId = discordId;
    }

    public String getFullName()
    {
        return firstName + " " + lastName;
    }

    public boolean getIsAdmin()
    {
        return isAdmin;
    }

    public void setIsAdmin(boolean admin)
    {
        isAdmin = admin;
    }

    public Set<ScheduledGame> getOrganisedGames()
    {
        return organisedGames;
    }

    public void setOrganisedGames(Set<ScheduledGame> organisedGames)
    {
        this.organisedGames = organisedGames;
    }

    public Set<ScheduledGame> getGamesJoined()
    {
        return gamesJoined;
    }

    public void setGamesJoined(Set<ScheduledGame> gamesJoined)
    {
        this.gamesJoined = gamesJoined;
    }
}
