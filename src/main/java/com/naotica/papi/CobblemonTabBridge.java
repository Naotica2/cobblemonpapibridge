package com.naotica.papi;

import com.naotica.papi.cobblemon.extensions.CobblemonPokedexExtension;
import com.naotica.papi.ranked.extensions.CobblemonRankedExtension;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.placeholder.PlaceholderManager;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("cobblemon_papi_bridge")
public class CobblemonTabBridge {
    private static final Logger LOGGER = LoggerFactory.getLogger(CobblemonTabBridge.class);

    private final CobblemonPokedexExtension pokedexExtension = new CobblemonPokedexExtension();
    private final CobblemonRankedExtension rankedExtension = new CobblemonRankedExtension();

    public CobblemonTabBridge() {
        NeoForge.EVENT_BUS.register(this);
        LOGGER.info("Cobblemon TAB Bridge initialized.");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        try {
            if (TabAPI.getInstance() != null) {
                registerTabPlaceholders();
                TabAPI.getInstance().getEventBus().register(me.neznamy.tab.api.event.plugin.TabLoadEvent.class, e -> registerTabPlaceholders());
                LOGGER.info("Successfully registered Cobblemon placeholders to TAB API.");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to hook into TAB API. Is TAB installed?", e);
        }
    }

    private void registerTabPlaceholders() {
        PlaceholderManager manager = TabAPI.getInstance().getPlaceholderManager();

        // Pokedex placeholders
        String[] pokedexPlaceholders = {"caught", "seen", "total", "percent_caught", "percent_seen", "progress_bar"};
        for (String p : pokedexPlaceholders) {
            manager.registerPlayerPlaceholder("%cobblemon_pokedex_" + p + "%", 1000, tabPlayer -> {
                if (tabPlayer.getPlayer() instanceof ServerPlayer) {
                    return pokedexExtension.parse((ServerPlayer) tabPlayer.getPlayer(), p);
                }
                return "0";
            });
        }

        // Ranked placeholders
        String[] rankedPlaceholders = {"rank_title", "elo", "wins", "losses"};
        for (String p : rankedPlaceholders) {
            manager.registerPlayerPlaceholder("%cobblemon_ranked_" + p + "%", 1000, tabPlayer -> {
                if (tabPlayer.getPlayer() instanceof ServerPlayer) {
                    return rankedExtension.parse((ServerPlayer) tabPlayer.getPlayer(), p);
                }
                return "0";
            });
        }
    }
}
