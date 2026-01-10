/*
This class is the main entry to fetch things from the Enka.network API to get genshin impact API


 */
package org.mdxabu.GenshinData;

import me.kazury.enkanetworkapi.enka.EnkaNetworkAPI;
import me.kazury.enkanetworkapi.enka.EnkaNetworkBuilder;
import me.kazury.enkanetworkapi.games.genshin.data.GenshinAffix;
import me.kazury.enkanetworkapi.games.genshin.data.GenshinUserInformation;
import me.kazury.enkanetworkapi.util.GameType;
import me.kazury.enkanetworkapi.util.GlobalLocalization;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.awt.*;
import java.util.Random;

public class EnkaNetworkFetcher {
    EnkaNetworkAPI enkaNetworkAPI;

    public EnkaNetworkFetcher() {
        this.enkaNetworkAPI = new EnkaNetworkBuilder()
                .setDefaultLocalization(GlobalLocalization.ENGLISH)
                .setUserAgent("kools")
                .setHonkaiEnabled(false)
                .setZenlessEnabled(false)
                .setDefaultLocalization(GlobalLocalization.ENGLISH)
                .build();

        enkaNetworkAPI.setDefaultUIPath(GameType.GENSHIN, "https://enka.network/ui/");

    }

    public void FetchProfile(String UID, InteractionHook hook, SlashCommandInteractionEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        Guild guild = event.getGuild();

        // Fetch your custom emojis by name
        assert guild != null;


        String mondstadt = Emoji.fromCustom("mondstadt",1459543172391768266L,false).getAsMention();



        enkaNetworkAPI.fetchGenshinUser(UID, (user) -> {
            GenshinUserInformation info = user.toGenshinUser();


            builder.setTitle(info.getNickname()+"'s Stats");
            builder.setColor(Color.BLUE);
            builder.setThumbnail("https://yoolk.ninja/wp-content/uploads/2021/08/Games-GenshinImpact-1024x1024.png");

            builder.setDescription("AR: "+ info.getLevel() + "\nWorld Level: " + info.getWorldLevel());
            builder.addField("**World Exploration**",mondstadt+"Mondstadt",true);

            hook.editOriginalEmbeds(builder.build()).queue();
        });
    }

    public String FetchCharacterImage(String character) {
        String url;

        String character_identifier = "UI_AvatarIcon_" + character;

        url = enkaNetworkAPI.getGenshinIcon(character_identifier);

        return url;
    }

}

