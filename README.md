# Food Commodities

A Starsector 0.98a mod that adds faction-flavoured food commodities and the **Central Kitchen**, a structure industry that produces, imports and distributes them across the sector.

Version 0.1.0 — experimental build.

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

`foodc_central_kitchen` — tagged `structure`, so it **does not use up an industry slot** (the same way a Waystation doesn't). Available to the player on any colony of size 3 or larger.

It produces the specialty of the faction that owns the market:

| Faction | Produces |
|---|---|
| Hegemony, Sindrian Diktat | Unit Banquet Hotpot |
| Tri-Tachyon | Le Homard Bleu Impérial |
| Persean League | Mix Spice Stew |
| Pirates | "Blast" Mix-them-All |
| Luddic Church, Luddic Path, Knights of Ludd, Independent, player, unknown factions | Artisian Organic Food |

### Output

```
units = max(1, round(((market size - 1) + farmland bonus) / divisor))
```

- `farmland bonus` is +1 on `farmland_rich` / `farmland_bountiful`
- `divisor` is 1.5, and 1.8 for the blue lobster dish

### Inputs

Each specialty has its own recipe; a shortage of any ingredient cuts into production.

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

On top of that, markets of size 6 or larger import the other factions' specialties — one unit each, except baijiu — so the goods keep moving between factions instead of piling up where they are made.

### Where kitchens appear

On a new game (and once on an existing save) the mod adds a Central Kitchen to every non-player market that:

- has a population industry,
- is size 4 or larger,
- has farming, aquaculture or light industry.

This runs once per save and is tracked in the sector memory (`$foodc.centralKitchensInstalled`), so it never re-adds an industry the player removed.

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
│   └── econ/
│       ├── CentralKitchen.java       industry behaviour: output, recipes, imports
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
foodc.econ.FoodcMarketInstaller - Central Kitchens installed on N markets.
```

If a commodity or the industry spec is missing, the log says so explicitly instead.

## Credits

Author: DietrichT.
