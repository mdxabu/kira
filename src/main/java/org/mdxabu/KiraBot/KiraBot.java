package org.mdxabu.KiraBot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.mdxabu.Commands.SlashCommands;

import java.util.EnumSet;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;
import static net.dv8tion.jda.api.interactions.commands.build.Commands.*;


public class KiraBot extends ListenerAdapter {

    static JDA kiraBuilder;

    public static void run() {
        EnumSet<GatewayIntent> intents = EnumSet.noneOf(GatewayIntent.class);
        kiraBuilder = JDABuilder.createLight(
                        System.getenv("BOT-TOKEN"), intents)
                .addEventListeners(new KiraBot())
                .addEventListeners(new SlashCommands())
                .setActivity(Activity.watching("You..."))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .enableIntents(GatewayIntent.GUILD_EXPRESSIONS)
                .enableCache(CacheFlag.EMOJI)
                .setStatus(OnlineStatus.ONLINE)
                .build();

        CommandListUpdateAction commands = kiraBuilder.updateCommands();

        commands.addCommands(slash("hello", "say hello to Kira"),

                Commands.slash("say", "Makes the bot say what you tell it to")
                        .addOption(STRING, "content", "What the bot should say", true),

                Commands.slash("write","write the name you want to be in the deathnote :)")
                        .addOption(STRING, "name","Name of the person",true)



        ).queue();



    }
}
