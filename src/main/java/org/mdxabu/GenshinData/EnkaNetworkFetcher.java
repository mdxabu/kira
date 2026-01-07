package org.mdxabu.GenshinData;

import me.kazury.enkanetworkapi.enka.EnkaCache;
import me.kazury.enkanetworkapi.enka.EnkaNetworkAPI;
import me.kazury.enkanetworkapi.enka.EnkaNetworkBuilder;
import me.kazury.enkanetworkapi.util.GameType;
import me.kazury.enkanetworkapi.util.GlobalLocalization;

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

                enkaNetworkAPI.setDefaultUIPath(GameType.GENSHIN,"https://enka.network/ui/");

    }

    public String FetchCharacterImage(String character){
        String url;

        String character_identifier = "UI_AvatarIcon_"+character;

        url = enkaNetworkAPI.getGenshinIcon(character_identifier);

        return url;

    }




}
