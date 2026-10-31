# Changelog

## 0.2 — 2026-10-31

First version with actual industry behaviour. Everything below is additive; the six commodities from 0.1 are unchanged in name and id.

### Added

- **Central Kitchen** (`foodc_central_kitchen`), a structure industry that produces the specialty food of the faction that owns the colony: Hegemony and Sindrian Diktat ration hotpot, Tri-Tachyon blue lobster, Persean League spice stew, pirate mix, artisanal organic food for Luddic, Independent, player and unknown factions.
- Per-specialty recipes. Hotpot and stew need food, organics and domestic goods; artisanal food needs domestic goods and organics; the lobster dish needs Volturnian lobster, artisanal food and luxury goods; the pirate mix needs food and organics. A shortage of any ingredient cuts into production.
- Output scales with colony size: x2 on size 4-5, x3 on 6-7, x4 on 8-9. Base output is divided by 1.5 (1.8 for the lobster dish), and ingredient demand is scaled by the same multiplier.
- The specialty is **fixed when the industry is created** and stored in the save, so a captured market keeps producing what its kitchen was built to make. A kitchen built on a new colony follows whoever founds it.
- Kitchens are seeded automatically on NPC markets that have a population industry, size 4 or larger, and farming, aquaculture or light industry. Once per save.
- **Consumer Market** (`foodc_consumer_market`), a second structure that provides the demand side: every colony of size 3 or larger demands each of the five foods, scaled 1 / 1.5 / 2 / 3 / 4 / 5 units by colony size. Automatically created on existing colonies at first load and on new colonies the moment they are founded.
- Consumer Market grants stability while its demand is met: **+1** for the four everyday foods, **+2** if the lobster dish is available as well.
- Both industries are tagged `structure`, so neither uses an industry slot.
- `README.md`, `LICENSE` (MIT for code, CC BY-NC-SA 4.0 for artwork and descriptions) and `build.bat` for rebuilding the jar against the game API.

### Changed

- Baijiu is no longer produced by any industry. It is now a salvage-only collectible, found through the `extended` (derelict) and `goods` (habitat cargo) drop groups at weight 2.
- Baijiu is tagged `nonecon`, like AI cores and blueprints: it exists as cargo but never appears in market trade and has no demand anywhere.
- The lobster dish uses `utility` 1 instead of 0, and its base price is 32.
- `mod_info.json` declares `jars` and `modPlugin` explicitly, targets game version 0.98a-RC8, and drops the `utility` flag now that the mod adds content.
- Logging goes through the game's own log4j logger instead of `System.out`, so the startup self-check shows up in `starsector.log`.

### Fixed

- Commodity icon paths used Windows backslashes; they now use forward slashes.
- The lobster dish and baijiu had `cargo space` 0, which made them weightless in the cargo hold. Both are 1 now.
- Removed the old size 6+ "imports other factions' specialties" demand from the Central Kitchen; the Consumer Market handles demand now.
- Loading a save that has no commodity data for the mod's ids no longer throws a null pointer in the deficit calculation.

## 0.1

Added some food items to the base game, no external lib used so it is free to add to the base game.
Did not alter any game files, shouldn't have any incompatibilities.
