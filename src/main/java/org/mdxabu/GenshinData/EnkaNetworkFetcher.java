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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
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

    public void FetchProfile(String UID, InteractionHook hook, SlashCommandInteractionEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        EnkaNetworkAPI api = new EnkaNetworkAPI();
        Guild guild = (Guild) event.getGuild();
        Emoji ohno  = (Emoji) guild.getEmojisByName(":ohno:",true);
//        api.getGenshinIcon(UID);

        enkaNetworkAPI.fetchGenshinUser(UID, (user) -> {
            GenshinUserInformation info = user.toGenshinUser();


            builder.setTitle(info.getNickname());
            builder.setDescription("AR "+ info.getLevel());
            builder.setColor(Color.CYAN);
            builder.setThumbnail("https://yoolk.ninja/wp-content/uploads/2021/08/Games-GenshinImpact-1024x1024.png");
            builder.addField("**World Level:** "+ info.getWorldLevel(),
                    "**Imaginary Theater Acts:** "+ info.getTheaterActs() +
                            "\n**Spiral Abyss:** " + info.getAbyssFloor() +
                    "\n**Total Achievements:** " + info.getAchievementsCompleted() +
                    "\n**Stygian Onslaught:** " + info.getStygianIndex() +
                    "\n:ohno:",true);
            builder.addField(":ohno:","Click to view",true);
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

