package com.naotica.papi.ranked;

import com.envyful.papi.api.manager.AbstractPlaceholderManager;
import com.naotica.papi.ranked.extensions.CobblemonRankedExtension;
import net.minecraft.server.level.ServerPlayer;

public class CobblemonRankedPlaceholders extends AbstractPlaceholderManager<ServerPlayer> {

    private static final String IDENTIFIER = "cobblemon_ranked";
    private static final String[] AUTHORS = new String[]{"Naotica"};
    private static final String VERSION = "1.0.0";
    private static final String NAME = "CobblemonRankedPlaceholders";

    public CobblemonRankedPlaceholders() {
        super(IDENTIFIER, AUTHORS, VERSION, NAME, ServerPlayer.class);

        // Single dynamic extension handles ALL cobblemon_ranked_* placeholders
        this.registerPlaceholder(new CobblemonRankedExtension());
    }
}
