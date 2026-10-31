# Food Commodities

A Starsector 0.98a mod that adds faction-flavoured food commodities and the **Central Kitchen**, a structure industry that produces them, plus the **Consumer Market** that buys them.

Version 0.2.0 — experimental build.

## Requirements

- Starsector **0.98a-RC8**
- No other mods required. This mod does not use LazyLib, MagicLib, LunaLib, Nexerelin or any other library — only the game's own API and the log4j that ships with the game.

## Commodities

| Commodity | Id | Base price | Tags | Source |
|---|---|---|---|---|
| Unit Banquet Hotpot | `foodc_heghotpot` | 35 | military, food | Central Kitchen (Hegemony / Diktat) |
| Le Homard Bleu Impérial | `foodc_tritaclobster` | 32 | exotic, luxury | Central Kitchen (Tri-Tachyon) |
| Artisian Organic Food | `foodc_ludcartisian` | 50 | military, food | Central Kitchen (Luddic / Independent / player) |
| Mix Spice Stew | `foodc_permixspicestrew` | 24 | food | Central Kitchen (Persean League) |
| "Blast" Mix-them-All | `foodc_pirfoodmix` | 15 | food | Central Kitchen (pirates) |
| Antique White Bottle Baijiu | `foodc_collectablebaijiu` | 5000 | exotic, luxury, nonecon | Salvage only |

All six have codex descriptions in `data/strings/descriptions.csv`.

## Central Kitchen

`foodc_central_kitchen` — tagged `structure`, so it **does not use up an industry slot** (the same way a Waystation doesn't). Buildable by the player on any colony with a population industry.

It produces the specialty of the faction that owns the market **at the moment the industry is created**, and keeps making it from then on — capturing a world does not change what its kitchen produces. A kitchen built later on a new colony follows whoever founds that colony.

| Faction | Produces |
|---|---|
| Hegemony, Sindrian Diktat | Unit Banquet Hotpot |
| Tri-Tachyon | Le Homard Bleu Impérial |
| Persean League | Mix Spice Stew |
| Pirates | "Blast" Mix-them-All |
| Luddic Church, Luddic Path, Knights of Ludd, Independent, player, unknown factions | Artisian Organic Food |

### Output

```
units = max(1, round(((market size - 1) + farmland bonus) / divisor)) * size mult
```

- `farmland bonus` is +1 on `farmland_rich` / `farmland_bountiful`
- `divisor` is 1.5, and 1.8 for the blue lobster dish
- `size mult` is `market size / 2`: **x2 on size 4-5, x3 on size 6-7, x4 on size 8-9**
- the same `size mult` applies to every ingredient below, so the recipe ratios stay the same

### Inputs

Each specialty has its own recipe; a shortage of any ingredient cuts into production. The Central Kitchen does not create any other demand — the buying side comes from the Consumer Market.

| Produces | Requires |
|---|---|
| Unit Banquet Hotpot | food, organics, domestic goods |
| Mix Spice Stew | food, organics, domestic goods |
| Artisian Organic Food | domestic goods, organics |
| Le Homard Bleu Impérial | lobster (Volturnian), Artisian Organic Food, luxury goods |
| "Blast" Mix-them-All | food, organics |

Ingredient quantities scale with market size:

| Ingredient | Amount |
|---|---|
| food, organics | `max(1, round((size - 2) / 2))` |
| domestic goods, luxury goods | `max(1, (size - 4) / 2)` |
| lobster, Artisian Organic Food | `max(1, (size - 5) / 2)` |


## Consumer Market

`foodc_consumer_market` — the demand side of the loop. Also tagged `structure` (no industry slot), present on every colony, and **not buildable** by the player: it is a background abstraction, not a choice.

It is created automatically:

- on every existing colony the first time the mod runs on a save (NPC and player alike, size 3 or larger, any market with a population industry);
- on any new colony the moment it is founded, through a `PlayerColonizationListener`, so a fresh player colony gets one immediately without waiting for a reload.

It keeps a standing demand for all five foods, scaled by colony size:

| Colony size | Demand per food |
|---|---|
| 3 | 1 |
| 4 | 1.5 |
| 5 | 2 |
| 6 | 3 |
| 7 | 4 |
| 8 | 5 |

Stability follows what is actually on the shelves: **+1** while the four everyday foods are available, **+2** if the lobster dish is available as well, and nothing if the basics are missing. Because the lobster dish is only made in Tri-Tachyon space, the full +2 is genuinely hard to earn elsewhere.

## What gets added automatically

On a new game — and once on an existing save — the mod adds:

- a **Central Kitchen** to every non-player market that has a population industry, is size 4 or larger, and has farming, aquaculture or light industry;
- a **Consumer Market** to every market with a population industry, size 3 or larger, player-owned included.

Each pass is tracked separately in sector memory (`$foodc.centralKitchensInstalled`, `$foodc.consumerMarketsInstalled`), so the mod never re-adds an industry the player removed, and adding the Consumer Market later still works on saves that already had kitchens.

## Baijiu: salvage only

The Antique White Bottle Baijiu is not produced by any industry and has no market demand. It is tagged `nonecon`, which puts it in the same category as AI cores and blueprints: it exists as cargo but never appears in market trade.

It is found through salvage. `data/campaign/procgen/drop_groups.csv` adds it to two vanilla drop groups:

- `extended` — derelict salvage, weight 2
- `goods` — habitat and cargo wreck salvage, weight 2

## Notes on pricing

Market prices in 0.98 are calculated from local supply and demand. `base price` in `commodities.csv` is a valuation figure used for cargo value, salvage value, raid value and the codex — changing it does not by itself change what a market pays for the good. If a commodity has no demand on a market, its sell price there is 0 no matter what the base price is.

## Building from source

Requires a **JDK 17** (Starsector 0.98 runs on Java 17).

```
build.bat
```

This compiles everything under `src/` against `starsector-core/starfarer.api.jar` and `starsector-core/log4j-1.2.9.jar`, then packs the classes into `jars/FoodCommodities.jar`. Both paths are relative to the mod folder, so the mod must stay inside `Starsector/mods/`.

The jar is loaded through `mod_info.json`:

```json
"jars": ["jars/FoodCommodities.jar"],
"modPlugin": "foodc.FoodcModPlugin"
```

## Project layout

```
Food Commodities/
├── mod_info.json
├── build.bat
├── jars/FoodCommodities.jar
├── src/foodc/
│   ├── FoodcModPlugin.java           entry point, startup self-check
│   ├── FoodcColonizationListener.java gives new colonies a Consumer Market
│   └── econ/
│       ├── CentralKitchen.java       industry behaviour: output, recipes, imports
│       ├── ConsumerMarket.java       background demand for the mod's foods
│       ├── FoodcFoods.java           commodity ids and faction -> specialty map
│       └── FoodcMarketInstaller.java seeds kitchens on NPC markets, once per save
├── data/
│   ├── campaign/commodities.csv
│   ├── campaign/industries.csv
│   ├── campaign/procgen/drop_groups.csv
│   └── strings/descriptions.csv
└── graphics/
    ├── foodc_items/*.png             80x80 commodity icons
    └── industries/foodc_tritacfoodproducer.png   190x95 industry image
```

All data files use the standard Starsector merge: the mod only adds its own rows and never modifies a core file.

## Compatibility

- The mod is purely additive: new commodities, one new industry, and extra rows in existing drop groups.
- It can be enabled on an existing campaign; the game will show the usual "mods changed" warning and the kitchens are added on first load.
- Changing any CSV requires a **game restart** — commodity and industry specs are read once at startup.
- No known conflicts. Nothing here replaces vanilla industries, so mods that alter population or trade should coexist with it; the Central Kitchen only reads the market and adds its own supply and demand entries.

## Debugging

On startup the log (`starsector.log`) shows a self-check:

```
foodc.FoodcModPlugin - commodity ok: foodc_heghotpot (Unit Banquet Hotpot, base price 35)
...
foodc.FoodcModPlugin - industry ok: foodc_central_kitchen (Central Kitchen, plugin foodc.econ.CentralKitchen)
foodc.FoodcModPlugin - industry ok: foodc_consumer_market (Consumer Market, plugin foodc.econ.ConsumerMarket)
foodc.econ.FoodcMarketInstaller - Central Kitchens installed on N markets.
foodc.econ.FoodcMarketInstaller - Consumer Markets installed on M markets.
```

If a commodity or the industry spec is missing, the log says so explicitly instead.

## License

- **Code** (`src/`, and the compiled `jars/FoodCommodities.jar`) is released under the MIT License — see `LICENSE`.
- **Artwork** (`graphics/`) and the written commodity descriptions are licensed CC BY-NC-SA 4.0: credit the author, don't sell them, and share derivatives under the same terms.

In practice: fork it, change the numbers, rip out the parts you don't like. A message saying you did would be appreciated, and please credit the original — but that is a courtesy request, not a condition of the MIT license.

## Credits

Author: DietrichT.
