package org.mdxabu.KoolsBot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.IntegrationType;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.mdxabu.Commands.SlashCommands;

import java.util.EnumSet;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;


public class KoolsBot extends ListenerAdapter {

    static JDA KoolsBuilder;

    public static void run() {
        EnumSet<GatewayIntent> intents = EnumSet.noneOf(GatewayIntent.class);
        KoolsBuilder = JDABuilder.createLight(
                        System.getenv("BOT-TOKEN"),intents)
                .addEventListeners(new KoolsBot())
                .addEventListeners(new SlashCommands())
                .setActivity(Activity.playing("Genshin Impact"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setStatus(OnlineStatus.ONLINE)
                .build();

        CommandListUpdateAction commands = KoolsBuilder.updateCommands();

        commands.addCommands(Commands.slash("hello","say hello to kools"),

                Commands.slash("say","Makes the bot say what you tell it to")
                        .addOption(STRING, "content", "What the bot should say", true),

                Commands.slash("get-character-image","Get Character image url")
                        .addOptions(new OptionData(STRING, "character","genshin character")
                        .addChoice("Varesa","Varesa")
                        .addChoice("Diluc","Diluc")
                        .addChoice("Furina","Furina")
                        .addChoice("Mualani","Mualani"))
        ).queue();

    }
}
