package com.github.tractionprojects.wgs.discord;

import com.github.tractionprojects.wgs.data.entity.ScheduledGame;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class GameEmbed
{

    private final static long CHANNEL_ID = Long.parseLong(System.getenv("DISCORD_CHANNEL_ID"));
    private final DiscordBot bot;

    public GameEmbed(@Autowired DiscordBot bot)
    {
        this.bot = bot;
    }

    public void sendMessage(ScheduledGame game)
    {
        EmbedCreateSpec embed = EmbedCreateSpec.builder()
                .color(Color.BLUE)
                .title(String.format("A new %s game has been posted", game.getGame().getName()))
                .url(System.getenv("WGS_URL"))
                .author(game.getOrganiser().getFullName(), null, null)
                .description(game.getDetails())
                .addField("Number of Players", String.valueOf(game.getNoPlayers()), true)
                .addField("Points Limit", String.valueOf(game.getPointsLimit()), true)
                .addField("Date", game.getDate().format(DateTimeFormatter.ISO_DATE), true)
                .build();

        bot.getGateway().getChannelById(Snowflake.of(CHANNEL_ID))
                .ofType(GuildMessageChannel.class)
                .flatMap(channel -> channel.createMessage(embed))
                .subscribe();
    }
}
