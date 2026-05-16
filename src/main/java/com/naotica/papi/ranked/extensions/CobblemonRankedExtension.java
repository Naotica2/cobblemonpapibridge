package com.naotica.papi.ranked.extensions;

import com.envyful.papi.api.manager.extensions.type.SimpleExtension;
import com.google.common.collect.Lists;
import net.minecraft.server.level.ServerPlayer;

import cn.kurt6.cobblemon_ranked.CobblemonRanked;
import cn.kurt6.cobblemon_ranked.config.RankConfig;
import cn.kurt6.cobblemon_ranked.data.PlayerRankData;
import cn.kurt6.cobblemon_ranked.data.RankDao;
import cn.kurt6.cobblemon_ranked.data.SeasonManager;
import cn.kurt6.cobblemon_ranked.matchmaking.MatchmakingQueue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CobblemonRankedExtension extends SimpleExtension<ServerPlayer> {

    private static final String NAME = "ranked";
    private static final int PRIORITY = 1;
    private static final List<String> DESCRIPTION = Lists.newArrayList(
            "Provides all Cobblemon Ranked competitive statistics.",
            "Supports format suffixes: _singles, _doubles, _2v2singles.",
            "Includes ELO, rank, wins, losses, streaks, season info, and queue status."
    );
    private static final List<String> EXAMPLES = Lists.newArrayList(
            "%cobblemon_ranked_elo%",
            "%cobblemon_ranked_rank_title%",
            "%cobblemon_ranked_elo_singles%",
            "%cobblemon_ranked_wins_doubles%",
            "%cobblemon_ranked_season_name%",
            "%cobblemon_ranked_queue_status%"
    );

    // Known format suffixes
    private static final String[] FORMAT_SUFFIXES = {"_singles", "_doubles", "_2v2singles"};

    // Known base stat keys that DON'T support format suffixes
    private static final String[] GLOBAL_STATS = {
            "season_name", "season_id", "season_days_left", "queue_status"
    };

    public CobblemonRankedExtension() {
        super(NAME, PRIORITY, DESCRIPTION, EXAMPLES);
    }

    @Override
    public String parse(ServerPlayer player, String placeholder) {
        try {
            // Check if it's a global stat (no format support)
            for (String global : GLOBAL_STATS) {
                if (placeholder.equals(global)) {
                    return resolveGlobalStat(player, global);
                }
            }

            // Extract base stat and format suffix
            String baseStat = placeholder;
            String format = "default";

            for (String suffix : FORMAT_SUFFIXES) {
                if (placeholder.endsWith(suffix)) {
                    baseStat = placeholder.substring(0, placeholder.length() - suffix.length());
                    format = suffix.substring(1); // Remove leading underscore
                    break;
                }
            }

            return resolvePlayerStat(player, baseStat, format);

        } catch (Exception e) {
            // Any unexpected error returns a safe fallback
            return "N/A";
        }
    }

    // =====================================================================
    // GLOBAL STATS (no format support)
    // =====================================================================

    private String resolveGlobalStat(ServerPlayer player, String stat) {
        try {
            switch (stat) {
                case "season_name": {
                    SeasonManager sm = CobblemonRanked.seasonManager;
                    if (sm == null) return "None";
                    String name = sm.getCurrentSeasonName();
                    return name != null ? name : "None";
                }
                case "season_id": {
                    SeasonManager sm = CobblemonRanked.seasonManager;
                    if (sm == null) return "0";
                    return String.valueOf(sm.getCurrentSeasonId());
                }
                case "season_days_left": {
                    SeasonManager sm = CobblemonRanked.seasonManager;
                    if (sm == null) return "0";
                    SeasonManager.SeasonRemainingTime remaining = sm.getRemainingTime();
                    if (remaining == null) return "0";
                    return String.valueOf(remaining.getDays());
                }
                case "queue_status": {
                    MatchmakingQueue queue = CobblemonRanked.matchmakingQueue;
                    if (queue == null) return "Inactive";
                    boolean inQueue = queue.getQueue().containsKey(player.getUUID());
                    return inQueue ? "Queued" : "Not Queued";
                }
                default:
                    return "N/A";
            }
        } catch (Exception e) {
            return "N/A";
        }
    }

    // =====================================================================
    // PLAYER STATS (with optional format support)
    // =====================================================================

    private String resolvePlayerStat(ServerPlayer player, String baseStat, String format) {
        try {
            RankDao rankDao = CobblemonRanked.rankDao;
            SeasonManager sm = CobblemonRanked.seasonManager;

            if (rankDao == null || sm == null) return "0";

            int seasonId = sm.getCurrentSeasonId();
            UUID uuid = player.getUUID();

            // Fetch the player's rank data for this season and format
            PlayerRankData data = rankDao.getPlayerData(uuid, seasonId, format);

            switch (baseStat) {
                case "elo": {
                    if (data == null) return "0";
                    return String.valueOf(data.getElo());
                }
                case "rank_title": {
                    if (data == null) return "Unranked";
                    String title = data.getRankTitle();
                    return title != null ? title : "Unranked";
                }
                case "win_rate": {
                    if (data == null) return "0.0%";
                    double rate = data.getWinRate();
                    return String.format("%.1f%%", rate);
                }
                case "wins": {
                    if (data == null) return "0";
                    return String.valueOf(data.getWins());
                }
                case "losses": {
                    if (data == null) return "0";
                    return String.valueOf(data.getLosses());
                }
                case "total_games": {
                    if (data == null) return "0";
                    return String.valueOf(data.getWins() + data.getLosses());
                }
                case "streak": {
                    if (data == null) return "0";
                    return String.valueOf(data.getWinStreak());
                }
                case "best_streak": {
                    if (data == null) return "0";
                    return String.valueOf(data.getBestWinStreak());
                }
                case "flee_count": {
                    if (data == null) return "0";
                    return String.valueOf(data.getFleeCount());
                }
                case "rank": {
                    // Leaderboard position
                    int rank = rankDao.getPlayerRank(uuid, seasonId, format);
                    if (rank <= 0) return "Unranked";
                    return "#" + rank;
                }
                case "next_rank_elo": {
                    return getNextRankElo(data);
                }
                case "next_rank_name": {
                    return getNextRankName(data);
                }
                default:
                    return "N/A";
            }
        } catch (Exception e) {
            return "N/A";
        }
    }

    // =====================================================================
    // RANK PROGRESSION HELPERS
    // =====================================================================

    /**
     * Get the ELO required for the next rank above the player's current rank.
     */
    private String getNextRankElo(PlayerRankData data) {
        try {
            RankConfig config = CobblemonRanked.config;
            if (config == null || data == null) return "0";

            int currentElo = data.getElo();
            Map<Integer, String> rankTitles = config.getRankTitles();

            if (rankTitles == null || rankTitles.isEmpty()) return "Max";

            // rankTitles is Map<Integer (elo threshold), String (title)>
            // Find the next elo threshold above current elo
            int nextElo = Integer.MAX_VALUE;
            for (Integer threshold : rankTitles.keySet()) {
                if (threshold > currentElo && threshold < nextElo) {
                    nextElo = threshold;
                }
            }

            if (nextElo == Integer.MAX_VALUE) return "Max";
            return String.valueOf(nextElo);
        } catch (Exception e) {
            return "0";
        }
    }

    /**
     * Get the name of the next rank above the player's current rank.
     */
    private String getNextRankName(PlayerRankData data) {
        try {
            RankConfig config = CobblemonRanked.config;
            if (config == null || data == null) return "None";

            int currentElo = data.getElo();
            Map<Integer, String> rankTitles = config.getRankTitles();

            if (rankTitles == null || rankTitles.isEmpty()) return "None";

            int nextElo = Integer.MAX_VALUE;
            String nextName = "None";
            for (Map.Entry<Integer, String> entry : rankTitles.entrySet()) {
                if (entry.getKey() > currentElo && entry.getKey() < nextElo) {
                    nextElo = entry.getKey();
                    nextName = entry.getValue();
                }
            }

            return nextName;
        } catch (Exception e) {
            return "None";
        }
    }
}
