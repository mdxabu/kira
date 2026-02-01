package org.mdxabu.Commands.Labs;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class CommandLab {




    public static void say(SlashCommandInteractionEvent event, String content){
        event.reply(content).queue();
    }





}
