package org.mdxabu.Commands.Labs;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandLab {

    public static void say(SlashCommandInteractionEvent event, String content){
        event.reply(content).queue();
    }
}
