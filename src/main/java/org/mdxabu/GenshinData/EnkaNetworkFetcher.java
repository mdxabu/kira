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
//        event.getGuild().getEmojis().forEach(emoji -> System.out.println("Emoji Found: " + emoji.getName()));
        String arEmoji = getEmoji(guild, "adventurerank");
        String abyssEmoji = getEmoji(guild, "spiralabyss");
        String Mondstadt = event.getJDA().getEmojisByName("mondstadt",true).toString();
        String theaterEmoji = getEmoji(guild, "theatre");

        enkaNetworkAPI.fetchGenshinUser(UID, (user) -> {
            GenshinUserInformation info = user.toGenshinUser();

            builder.setTitle(info.getNickname()+"'s Stats");
            builder.setColor(Color.BLUE);
            builder.setThumbnail("https://yoolk.ninja/wp-content/uploads/2021/08/Games-GenshinImpact-1024x1024.png");

            builder.setDescription("AR: "+ info.getLevel() + "\nWorld Level: " + info.getWorldLevel());

            hook.editOriginalEmbeds(builder.build()).queue();
        });
    }

    public String FetchCharacterImage(String character) {
        String url;

        String character_identifier = "UI_AvatarIcon_" + character;

        url = enkaNetworkAPI.getGenshinIcon(character_identifier);

        return url;
    }

    private String getEmoji(Guild guild, String name) {
        return guild.getEmojisByName(name, true)
                .stream()
                .findFirst()
                .map(emoji -> emoji.getAsMention() + " ") // Returns <:name:id>
                .orElse(""); // Returns empty string if emoji isn't found
    }





}

