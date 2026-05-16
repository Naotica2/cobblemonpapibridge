package com.naotica.papi.cobblemon;

import com.envyful.papi.api.manager.AbstractPlaceholderManager;
import com.naotica.papi.cobblemon.extensions.CobblemonPartyExtension;
import com.naotica.papi.cobblemon.extensions.CobblemonPokedexExtension;
import net.minecraft.server.level.ServerPlayer;

public class CobblemonPlaceholders extends AbstractPlaceholderManager<ServerPlayer> {

    private static final String IDENTIFIER = "cobblemon";
    private static final String[] AUTHORS = new String[]{"Naotica"};
    private static final String VERSION = "1.0.0";
    private static final String NAME = "CobblemonPlaceholders";

    public CobblemonPlaceholders() {
        super(IDENTIFIER, AUTHORS, VERSION, NAME, ServerPlayer.class);

        // Party placeholders — single dynamic extension handles all party_* placeholders
        this.registerPlaceholder(new CobblemonPartyExtension());

        // Pokedex placeholders — single dynamic extension handles all pokedex_* placeholders
        this.registerPlaceholder(new CobblemonPokedexExtension());
    }
}
