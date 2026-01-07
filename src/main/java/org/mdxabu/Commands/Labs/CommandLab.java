package org.mdxabu.Commands.Labs;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.mdxabu.GenshinData.EnkaNetworkFetcher;

public class CommandLab {



    public static void say(SlashCommandInteractionEvent event, String content){
        event.reply(content).queue();
    }

    public static void getCharacterImage(SlashCommandInteractionEvent event, String CharacterName){
        EnkaNetworkFetcher enkaApi = new EnkaNetworkFetcher();

        String url = enkaApi.FetchCharacterImage(CharacterName);
        event.reply(url).queue();
    }
}
