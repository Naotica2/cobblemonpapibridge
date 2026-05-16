# Cobblemon PAPI Bridge by Naotica

A NeoForge 1.21.1 bridge mod that injects Cobblemon and Cobblemon Ranked data directly into the TAB plugin via PlaceholderAPI syntax.

**Current Version:** 1.0.2

## Features
- Bypasses traditional ForgePlaceholderAPI hooking issues on NeoForge 1.21.1 by directly injecting placeholders into the TAB API.
- Fully supports Cobblemon 1.7.3+ native API for Pokedex and Party data.
- Automatically handles `/tab reload` events to re-register placeholders dynamically without requiring a server restart.
- Native integration with Cobblemon Ranked for real-time ELO and competitive stats.

## Build Instructions

To compile the mod from source, follow these steps:

1. Ensure you have Java 21 installed.
2. Clone or download this repository.
3. Create a `libs/` folder in the project root if it doesn't exist, and place the required dependency mod files inside it. You can use newer versions, but the mod is built against:
   - `Cobblemon-neoforge` (1.7.3+)
   - `cobblemon_ranked ( neoforge )` (1.2.6+)
4. Open your terminal in the project directory and run the Gradle wrapper:

**Linux / macOS:**
```bash
./gradlew build
```

**Windows:**
```cmd
gradlew.bat build
```

5. The compiled `.jar` file will be generated in the `build/libs/` directory.

## Supported Placeholders

### Cobblemon Pokedex
- `%cobblemon_pokedex_caught%` - Total species caught.
- `%cobblemon_pokedex_seen%` - Total species seen.
- `%cobblemon_pokedex_total%` - Total species available on the server.
- `%cobblemon_pokedex_percent_caught%` - Percentage of species caught.
- `%cobblemon_pokedex_percent_seen%` - Percentage of species seen.
- `%cobblemon_pokedex_progress_bar%` - Visual progress bar of caught species.

### Cobblemon Party
- `%cobblemon_party_size%` - Number of Pokémon currently in the player's party.
- `%cobblemon_party_lead%` - Species name of the first Pokémon in the party.
- `%cobblemon_party_lead_level%` - Level of the first Pokémon.
- `%cobblemon_party_slot_<1-6>_species%` - Species name at slot N.
- `%cobblemon_party_slot_<1-6>_level%` - Level at slot N.
- `%cobblemon_party_slot_<1-6>_shiny%` - Shiny status at slot N (true/false).
- `%cobblemon_party_slot_<1-6>_iv_hp%` - HP IV at slot N (replace `hp` with `atk`, `def`, `spa`, `spd`, `spe`).

### Cobblemon Ranked
*Note: Ranked stats support format suffixes (`_singles`, `_doubles`, `_2v2singles`). Example: `%cobblemon_ranked_elo_singles%`*

- `%cobblemon_ranked_elo%` - Current ELO score.
- `%cobblemon_ranked_rank_title%` - Current rank title.
- `%cobblemon_ranked_win_rate%` - Win rate percentage.
- `%cobblemon_ranked_wins%` - Total wins.
- `%cobblemon_ranked_losses%` - Total losses.
- `%cobblemon_ranked_total_games%` - Total games played.
- `%cobblemon_ranked_streak%` - Current win streak.
- `%cobblemon_ranked_rank%` - Leaderboard position.
- `%cobblemon_ranked_season_name%` - Current season name.
- `%cobblemon_ranked_queue_status%` - Current matchmaking queue status.
