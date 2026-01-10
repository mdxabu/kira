package org.mdxabu.Commands.Labs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.mdxabu.GenshinData.EnkaNetworkFetcher;

import java.awt.*;
import java.rmi.server.UID;

public class CommandLab {




    public static void say(SlashCommandInteractionEvent event, String content){
        event.reply(content).queue();
    }

    public static void getCharacterImage(SlashCommandInteractionEvent event, String characterName) {
        event.deferReply(true).queue(hook -> {
            new Thread(() -> {
                EnkaNetworkFetcher enkaApi = new EnkaNetworkFetcher();
                String url = enkaApi.FetchCharacterImage(characterName);
                hook.editOriginal(url).queue();
            }).start();
        });
    }
    public static void getUserInfo(SlashCommandInteractionEvent event, String uid) {
        event.deferReply(false).queue(hook -> {
            EnkaNetworkFetcher enkaApi = new EnkaNetworkFetcher();
            enkaApi.FetchProfile(uid, hook,event);
        });
    }



}
