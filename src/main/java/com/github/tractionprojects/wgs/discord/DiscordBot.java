package com.github.tractionprojects.wgs.discord;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import org.springframework.stereotype.Service;

@Service
public class DiscordBot
{
    private Thread thread;
    private BotThread botThread;

    public DiscordBot()
    {
        start();
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void start()
    {
        if (botThread == null)
        {
            botThread = new BotThread();
            thread = new Thread(botThread);
            thread.setName("DiscordBot");
            thread.start();
        }
    }

    public void stop()
    {
        if (botThread != null && botThread.gateway != null)
        {
            botThread.gateway.logout().block();
        }
        botThread = null;
        thread = null;
    }

    public DiscordClient getClient()
    {
        return botThread.client;
    }

    public GatewayDiscordClient getGateway()
    {
        return botThread.gateway;
    }

    public class BotThread implements Runnable
    {
        private DiscordClient client;
        private GatewayDiscordClient gateway;

        @Override
        public void run()
        {
            client = DiscordClient.create(System.getenv("DISCORD_BOT_TOKEN"));
            gateway = client.gateway().login().block();
            if (gateway == null)
                stop();
            else
            {
                gateway.onDisconnect().block();
            }
        }
    }
}
