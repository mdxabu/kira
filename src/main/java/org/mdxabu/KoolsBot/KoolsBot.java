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
import static net.dv8tion.jda.api.interactions.commands.build.Commands.*;


public class KoolsBot extends ListenerAdapter {

    static JDA KoolsBuilder;

    public static void run() {
        EnumSet<GatewayIntent> intents = EnumSet.noneOf(GatewayIntent.class);
        KoolsBuilder = JDABuilder.createLight(
                        System.getenv("BOT-TOKEN"), intents)
                .addEventListeners(new KoolsBot())
                .addEventListeners(new SlashCommands())
                .setActivity(Activity.playing("Genshin Impact"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setStatus(OnlineStatus.ONLINE)
                .build();

        CommandListUpdateAction commands = KoolsBuilder.updateCommands();

        commands.addCommands(slash("hello", "say hello to kools"),

                Commands.slash("say", "Makes the bot say what you tell it to")
                        .addOption(STRING, "content", "What the bot should say", true),

                Commands.slash("get-character-image", "Get Character image url")
                        .addOptions(new OptionData(STRING, "character", "Select a Genshin character")
                                .addChoice("Aether (Traveler)", "Aether")
                                .addChoice("Lumine (Traveler)", "Lumine")
                                .addChoice("Zhongli", "Zhongli")
                                .addChoice("Raiden Shogun", "Raiden Shogun")
                                .addChoice("Venti", "Venti")
                                .addChoice("Nahida", "Nahida")
                                .addChoice("Furina", "Furina")
                                .addChoice("Albedo", "Albedo")
                                .addChoice("Kamisato Ayaka", "Ayaka")
                                .addChoice("Kamisato Ayato", "Ayato")
                                .addChoice("Tartaglia (Childe)", "Tartaglia")
                                .addChoice("Diluc", "Diluc")
                                .addChoice("Jean", "Jean")
                                .addChoice("Eula", "Eula")
                                .addChoice("Ganyu", "Ganyu")
                                .addChoice("Xiao", "Xiao")
                                .addChoice("Hu Tao", "Hutao")
                                .addChoice("Alhaitham", "Alhaitham")
                                .addChoice("Tighnari", "Tighnari")
                                .addChoice("Collei", "Collei")
                                .addChoice("Cyno", "Cyno")
                                .addChoice("Yae Miko", "Yae")
                                .addChoice("Kuki Shinobu", "Shinobu")
                                .addChoice("Arataki Itto", "Itto")
                                .addChoice("Mona", "Mona")),

                Commands.slash("get-user-info", "Get Genshin Impact User Info by UID")
                        .addOption(STRING, "uid", "UID of the Player",true)
        ).queue();



    }
}
