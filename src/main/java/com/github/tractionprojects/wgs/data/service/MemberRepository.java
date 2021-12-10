package com.github.tractionprojects.wgs.data.service;

import com.github.tractionprojects.wgs.data.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer>
{

    Member findByDiscordId(long discordId);

    Member findByEmail(String email);
}