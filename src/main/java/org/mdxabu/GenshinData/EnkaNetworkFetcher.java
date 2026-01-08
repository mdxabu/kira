/*
This class is the main entry to fetch things from the Enka.network API to get genshin impact API


 */
package org.mdxabu.GenshinData;

import me.kazury.enkanetworkapi.enka.EnkaNetworkAPI;
import me.kazury.enkanetworkapi.enka.EnkaNetworkBuilder;
import me.kazury.enkanetworkapi.games.genshin.data.GenshinUserCharacter;
import me.kazury.enkanetworkapi.games.genshin.data.GenshinUserInformation;
import me.kazury.enkanetworkapi.util.GameType;
import me.kazury.enkanetworkapi.util.GlobalLocalization;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;

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

    public String FetchCharacterImage(String character) {
        String url;

        String character_identifier = "UI_AvatarIcon_" + character;

        url = enkaNetworkAPI.getGenshinIcon(character_identifier);

        return url;

    }

    public String getNickname(String UID) {
        final String[] nickName = new String[1];
        enkaNetworkAPI.fetchGenshinUser(UID, (user) -> {
            final GenshinUserInformation info = user.toGenshinUser();
            nickName[0] = info.getNickname();
        });
        return nickName[0];
    }

    public void FetchProfileCharacterData(String UID) {
        enkaNetworkAPI.fetchGenshinUser(UID, (user) -> {
            final GenshinUserInformation info = user.toGenshinUser();
            System.out.println(info.getNickname());
            for (GenshinUserCharacter character : info.getCharacters()) {
                System.out.println(character.getGameData().getName());
                System.out.println(character.getGameData().getElement());
            }
        });
    }


    public void addCharactersToEmbed(String uid, EmbedBuilder embed, SlashCommandInteractionEvent event) {
        enkaNetworkAPI.fetchGenshinUser(uid, (user) -> {
            GenshinUserInformation info = user.toGenshinUser();

            for (GenshinUserCharacter character : info.getCharacters()) {
                embed.addField(
                        character.getGameData().getName(),
                        "**Level:** " + character.getCurrentLevel() +
                                "\n**Element:** " + character.getGameData().getElement() +
                                "\nStar: " + character.getGameData().getStar(),
                        true
                );
            }

            event.replyEmbeds(embed.build()).queue();
        });
    }
}

