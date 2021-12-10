package com.github.tractionprojects.wgs.data.service;

import com.github.tractionprojects.wgs.data.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

@Service
public class MemberService extends CrudService<Member, Integer>
{

    private final MemberRepository repository;
    private final ScheduledGameService scheduledGameService;

    public MemberService(@Autowired MemberRepository repository, @Autowired ScheduledGameService scheduledGameService)
    {
        this.repository = repository;
        this.scheduledGameService = scheduledGameService;
    }

    public Member getByDiscordID(long discordId)
    {
        return repository.findByDiscordId(discordId);
    }

    public Member getByEmail(String email)
    {
        return repository.findByEmail(email);
    }


    public void delete(Member member)
    {
        member.getOrganisedGames().forEach(game ->
        {
            game.removeAllPlayer();
            scheduledGameService.save(game);
        });
        scheduledGameService.getRepository().deleteInBatch(member.getOrganisedGames());
        super.delete(member.getId());
    }

    /**
     * Use Method that takes Member
     *
     * @param integer
     */
    @Override
    @Deprecated()
    public void delete(Integer integer)
    {
        delete(repository.getOne(integer));
    }

    @Override
    protected MemberRepository getRepository()
    {
        return repository;
    }

    public void save(Member member)
    {
        repository.save(member);
    }
}
