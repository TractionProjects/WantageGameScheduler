package com.github.tractionprojects.wgs.data.entity;

import com.github.tractionprojects.wgs.data.AbstractEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.Set;

@Entity
public class Game extends AbstractEntity
{
    @ManyToMany(mappedBy = "gameSystems", fetch = FetchType.EAGER)
    private Set<Member> users;
    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Set<Member> getUsers()
    {
        return users;
    }

    public void setUsers(Set<Member> users)
    {
        this.users = users;
    }
}
