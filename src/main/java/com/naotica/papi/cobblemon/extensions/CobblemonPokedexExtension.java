package com.naotica.papi.cobblemon.extensions;

import com.envyful.papi.api.manager.extensions.type.SimpleExtension;
import com.google.common.collect.Lists;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokedex.PokedexManager;
import com.cobblemon.mod.common.api.pokedex.CaughtCount;
import com.cobblemon.mod.common.api.pokedex.SeenCount;
import com.cobblemon.mod.common.api.pokedex.CaughtPercent;
import com.cobblemon.mod.common.api.pokedex.SeenPercent;

import java.util.List;

public class CobblemonPokedexExtension extends SimpleExtension<ServerPlayer> {

    private static final String NAME = "pokedex";
    private static final int PRIORITY = 1;
    private static final List<String> DESCRIPTION = Lists.newArrayList(
            "Provides Pokédex statistics from Cobblemon.",
            "Supports caught count, seen count, percentages, and progress bar."
    );
    private static final List<String> EXAMPLES = Lists.newArrayList(
            "%cobblemon_pokedex_caught%",
            "%cobblemon_pokedex_seen%",
            "%cobblemon_pokedex_total%",
            "%cobblemon_pokedex_percent_caught%",
            "%cobblemon_pokedex_progress_bar%"
    );

    private static final ResourceLocation NATIONAL_DEX = ResourceLocation.parse("cobblemon:national");

    public CobblemonPokedexExtension() {
        super(NAME, PRIORITY, DESCRIPTION, EXAMPLES);
    }

    @Override
    public String parse(ServerPlayer player, String placeholder) {
        try {
            PokedexManager dex = Cobblemon.INSTANCE.getPlayerDataManager().getPokedexData(player);
            if (dex == null) return "0";

            switch (placeholder) {
                case "caught": {
                    Object val = dex.getDexCalculatedValue(NATIONAL_DEX, CaughtCount.INSTANCE);
                    return String.valueOf(val);
                }
                case "seen": {
                    Object val = dex.getDexCalculatedValue(NATIONAL_DEX, SeenCount.INSTANCE);
                    return String.valueOf(val);
                }
                case "total": {
                    return String.valueOf(com.cobblemon.mod.common.api.pokemon.PokemonSpecies.getImplemented().size());
                }
                case "percent_caught": {
                    Object val = dex.getDexCalculatedValue(NATIONAL_DEX, CaughtPercent.INSTANCE);
                    return String.format("%.1f%%", ((Number) val).doubleValue());
                }
                case "percent_seen": {
                    Object val = dex.getDexCalculatedValue(NATIONAL_DEX, SeenPercent.INSTANCE);
                    return String.format("%.1f%%", ((Number) val).doubleValue());
                }
                case "progress_bar": {
                    int caught = ((Number) dex.getDexCalculatedValue(NATIONAL_DEX, CaughtCount.INSTANCE)).intValue();
                    int total = com.cobblemon.mod.common.api.pokemon.PokemonSpecies.getImplemented().size();
                    if (total == 0) return "[░░░░░░░░░░]";
                    return buildProgressBar(caught, total, 10);
                }
                default:
                    return "N/A";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    private String buildProgressBar(int current, int total, int barLength) {
        double ratio = Math.min(1.0, (double) current / total);
        int filled = (int) Math.round(ratio * barLength);
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            bar.append(i < filled ? "█" : "░");
        }
        bar.append("]");
        return bar.toString();
    }
}
