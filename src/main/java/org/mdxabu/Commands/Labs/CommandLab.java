package org.mdxabu.Commands.Labs;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class CommandLab {

    public static void say(SlashCommandInteractionEvent event, String content) {
        event.reply(content).queue();
    }

    public static void writeName(
        SlashCommandInteractionEvent event,
        String name
    ) {
        ArrayList<String> titles = new ArrayList<>();

        titles.add("Do you hear that?");
        titles.add("Your name is enough.");
        titles.add("Justice doesn’t blink.");
        titles.add("No courtroom. No appeal.");
        titles.add("Every letter is a verdict.");
        titles.add("History will thank me.");
        titles.add("One breath too late.");
        titles.add("This is judgment.");
        titles.add("Another name.");
        titles.add("The world grows quieter.");

        Random random = new Random();
        String randomTitle = titles.get(random.nextInt(titles.size()));

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(randomTitle);
        embed.setImage(
            "https://media.tenor.com/0AEB38Hz1PMAAAAM/death-note-light.gif"
        );
        embed.setColor(Color.RED);

        event.replyEmbeds(embed.build()).queue();
    }
}
