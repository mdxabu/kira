package org.mdxabu.Commands.Labs;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.mdxabu.GenshinData.EnkaNetworkFetcher;

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

}
