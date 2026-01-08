/*
This class is the main entry to fetch things from the Enka.network API to get genshin impact API


 */
package org.mdxabu.GenshinData;

import me.kazury.enkanetworkapi.enka.EnkaNetworkAPI;
import me.kazury.enkanetworkapi.enka.EnkaNetworkBuilder;
import me.kazury.enkanetworkapi.games.genshin.data.GenshinUserInformation;
import me.kazury.enkanetworkapi.util.GameType;
import me.kazury.enkanetworkapi.util.GlobalLocalization;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.awt.*;

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

    public void FetchUserInformation(String UID, InteractionHook hook) {
        EmbedBuilder builder = new EmbedBuilder();

        enkaNetworkAPI.fetchGenshinUser(UID, (user) -> {
            GenshinUserInformation info = user.toGenshinUser();

            builder.setTitle(info.getNickname());
            builder.setDescription(String.valueOf(info.getLevel()));
            builder.setColor(Color.CYAN);
            builder.setThumbnail(info.getNamecards().getFirst().getNamecardUrl());
            builder.addField("**Spiral Abyss:** ",info.getAbyssFloor() +"\n**Imaginary Theater Acts:** \n"+ info.getTheaterActs(),true);
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

