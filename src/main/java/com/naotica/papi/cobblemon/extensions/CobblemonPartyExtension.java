package com.naotica.papi.cobblemon.extensions;

import com.envyful.papi.api.manager.extensions.type.SimpleExtension;
import com.google.common.collect.Lists;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class CobblemonPartyExtension extends SimpleExtension<ServerPlayer> {

    private static final String NAME = "party";
    private static final int PRIORITY = 1;
    private static final List<String> DESCRIPTION = Lists.newArrayList(
            "Provides party Pokémon data from Cobblemon.",
            "Supports slot-based access for species, level, ability, nature, IVs, EVs, moves, and more."
    );
    private static final List<String> EXAMPLES = Lists.newArrayList(
            "%cobblemon_party_size%",
            "%cobblemon_party_lead%",
            "%cobblemon_party_slot_1_species%",
            "%cobblemon_party_slot_1_level%",
            "%cobblemon_party_slot_1_shiny%",
            "%cobblemon_party_slot_1_iv_hp%",
            "%cobblemon_party_slot_1_move_1%"
    );

    public CobblemonPartyExtension() {
        super(NAME, PRIORITY, DESCRIPTION, EXAMPLES);
    }

    @Override
    public String parse(ServerPlayer player, String placeholder) {
        try {
            // Access Cobblemon storage via Kotlin singleton interop
            com.cobblemon.mod.common.api.storage.party.PlayerPartyStore party =
                    com.cobblemon.mod.common.Cobblemon.INSTANCE.getStorage().getParty(player);

            if (party == null) {
                return "0";
            }

            // ---- party_size ----
            if (placeholder.equals("size")) {
                int count = 0;
                for (int i = 0; i < 6; i++) {
                    if (party.get(i) != null) count++;
                }
                return String.valueOf(count);
            }

            // ---- party_lead (alias for slot 1 species) ----
            if (placeholder.equals("lead")) {
                com.cobblemon.mod.common.pokemon.Pokemon lead = party.get(0);
                if (lead == null) return "None";
                return lead.getSpecies().getName();
            }

            // ---- party_lead_level ----
            if (placeholder.equals("lead_level")) {
                com.cobblemon.mod.common.pokemon.Pokemon lead = party.get(0);
                if (lead == null) return "0";
                return String.valueOf(lead.getLevel());
            }

            // ---- party_slot_<N>_<property> ----
            if (placeholder.startsWith("slot_")) {
                return parseSlot(party, placeholder.substring(5)); // Remove "slot_"
            }

        } catch (Exception e) {
            // Graceful fallback for any API errors
        }
        return "N/A";
    }

    /**
     * Parse a slot-based placeholder like "1_species", "3_iv_hp", "2_move_1"
     */
    private String parseSlot(com.cobblemon.mod.common.api.storage.party.PlayerPartyStore party, String slotPlaceholder) {
        try {
            // Extract slot number (first character)
            if (slotPlaceholder.length() < 3) return "N/A"; // Need at least "1_x"
            int underscoreIdx = slotPlaceholder.indexOf('_');
            if (underscoreIdx < 1) return "N/A";

            int slotNum;
            try {
                slotNum = Integer.parseInt(slotPlaceholder.substring(0, underscoreIdx));
            } catch (NumberFormatException e) {
                return "N/A";
            }

            if (slotNum < 1 || slotNum > 6) return "N/A";

            com.cobblemon.mod.common.pokemon.Pokemon pokemon = party.get(slotNum - 1); // 0-indexed
            if (pokemon == null) return "Empty";

            String property = slotPlaceholder.substring(underscoreIdx + 1);

            return parsePokemonProperty(pokemon, property);
        } catch (Exception e) {
            return "N/A";
        }
    }

    /**
     * Parse a property from a Pokémon instance.
     */
    private String parsePokemonProperty(com.cobblemon.mod.common.pokemon.Pokemon pokemon, String property) {
        try {
            switch (property) {
                case "species":
                    return pokemon.getSpecies().getName();
                case "nickname":
                    return pokemon.getNickname() != null ? pokemon.getNickname().getString() : pokemon.getSpecies().getName();
                case "level":
                    return String.valueOf(pokemon.getLevel());
                case "ability":
                    return pokemon.getAbility().getName();
                case "nature":
                    return pokemon.getNature().getName().getPath();
                case "shiny":
                    return String.valueOf(pokemon.getShiny());
                case "form":
                    return pokemon.getForm().getName();
                case "gender":
                    return pokemon.getGender().name();
                case "ball":
                    return pokemon.getCaughtBall().getName().getPath();
                case "friendship":
                    return String.valueOf(pokemon.getFriendship());
                case "hp":
                    return String.valueOf(pokemon.getCurrentHealth());
                case "maxhp":
                    return String.valueOf(pokemon.getMaxHealth());
                case "held_item":
                    return pokemon.heldItem().getDisplayName().getString();

                // --- IVs ---
                case "iv_hp":
                    return String.valueOf(pokemon.getIvs().getOrDefault(com.cobblemon.mod.common.api.pokemon.stats.Stats.HP));
                case "iv_atk":
                    return String.valueOf(pokemon.getIvs().getOrDefault(com.cobblemon.mod.common.api.pokemon.stats.Stats.ATTACK));
                case "iv_def":
                    return String.valueOf(pokemon.getIvs().getOrDefault(com.cobblemon.mod.common.api.pokemon.stats.Stats.DEFENCE));
                case "iv_spa":
                    return String.valueOf(pokemon.getIvs().getOrDefault(com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_ATTACK));
                case "iv_spd":
                    return String.valueOf(pokemon.getIvs().getOrDefault(com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_DEFENCE));
                case "iv_spe":
                    return String.valueOf(pokemon.getIvs().getOrDefault(com.cobblemon.mod.common.api.pokemon.stats.Stats.SPEED));

                // --- EVs ---
                case "ev_hp":
                    return String.valueOf(pokemon.getEvs().getOrDefault(com.cobblemon.mod.common.api.pokemon.stats.Stats.HP));
                case "ev_atk":
                    return String.valueOf(pokemon.getEvs().getOrDefault(com.cobblemon.mod.common.api.pokemon.stats.Stats.ATTACK));
                case "ev_def":
                    return String.valueOf(pokemon.getEvs().getOrDefault(com.cobblemon.mod.common.api.pokemon.stats.Stats.DEFENCE));
                case "ev_spa":
                    return String.valueOf(pokemon.getEvs().getOrDefault(com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_ATTACK));
                case "ev_spd":
                    return String.valueOf(pokemon.getEvs().getOrDefault(com.cobblemon.mod.common.api.pokemon.stats.Stats.SPECIAL_DEFENCE));
                case "ev_spe":
                    return String.valueOf(pokemon.getEvs().getOrDefault(com.cobblemon.mod.common.api.pokemon.stats.Stats.SPEED));

                default:
                    break;
            }

            // --- Moves: move_1 through move_4 ---
            if (property.startsWith("move_")) {
                try {
                    int moveIdx = Integer.parseInt(property.substring(5)) - 1; // 0-indexed
                    if (moveIdx < 0 || moveIdx > 3) return "N/A";
                    Object moveSet = pokemon.getClass().getMethod("getMoveSet").invoke(pokemon);
                    if (moveSet == null) return "None";
                    java.util.List<?> moves = (java.util.List<?>) moveSet.getClass().getMethod("getMoves").invoke(moveSet);
                    if (moveIdx < moves.size()) {
                        Object move = moves.get(moveIdx);
                        if (move != null) {
                            Object moveTemplate = move.getClass().getMethod("getTemplate").invoke(move);
                            return (String) moveTemplate.getClass().getMethod("getName").invoke(moveTemplate);
                        }
                    }
                    return "None";
                } catch (Exception e) {
                    return "N/A";
                }
            }

        } catch (Exception e) {
            // Catch any NoSuchMethod or ClassNotFound from API changes
        }
        return "N/A";
    }
}
