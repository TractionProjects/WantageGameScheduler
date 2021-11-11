package com.github.tractionprojects.wgs.security;

import com.github.tractionprojects.wgs.Developers;
import com.github.tractionprojects.wgs.HttpHandler;
import com.github.tractionprojects.wgs.data.entity.Member;
import com.github.tractionprojects.wgs.data.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.net.URL;

@Service
public class UserTools
{
    private final MemberService memberService;
    private final OAuth2AuthorizedClientService clientService;

    public UserTools(@Autowired MemberService memberService, @Autowired OAuth2AuthorizedClientService clientService)
    {
        this.memberService = memberService;
        this.clientService = clientService;
    }

    public String getCurrentUsersName()
    {
        return getPrincipalAttribute("username");
    }

    public long getCurrentUsersID()
    {
        return Long.parseLong(getPrincipalAttribute("id"));
    }

    public Member getCurrentMember()
    {
        return memberService.getByDiscordID(getCurrentUsersID());
    }

    public boolean isAdmin()
    {
        return (getCurrentMember().getIsAdmin() || Developers.isDev(getCurrentUsersID()));
    }

    public OAuth2AccessToken getCurrentUsersToken()
    {
        OAuth2AuthenticationToken oauthToken = getAuthentication();
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());
        return client.getAccessToken();
    }

    public boolean getPartOfGuild()
    {
        if (Developers.isDev(getCurrentUsersID()))
            return true;
        try
        {
            BufferedReader guilds = HttpHandler.getRequest(new URL("https://discordapp.com/api/users/@me/guilds"), getCurrentUsersToken());
            String s;
            if ((s = guilds.readLine()) != null)
            {
                if (s.contains("\"id\": \"655855933225828382\""))
                {
                    return true;
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public String getEmailFromDiscord()
    {
        return getPrincipalAttribute("email");
    }

    public OAuth2AuthenticationToken getAuthentication()
    {
        return (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    }

    private String getPrincipalAttribute(String key)
    {
        String error = "ERROR please report to the Admin";
        OAuth2AuthenticationToken auth = getAuthentication();
        if (auth != null)
        {
            Object principal = auth.getPrincipal();
            if (principal instanceof DefaultOAuth2User)
            {
                return (String) ((DefaultOAuth2User) principal).getAttributes().get(key);
            }
        }
        return error;
    }
}
