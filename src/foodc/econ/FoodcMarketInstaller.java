package foodc.econ;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;

import org.apache.log4j.Logger;

/**
 * Puts Central Kitchens on the sector's food-producing markets so the new
 * commodities exist in the economy without the player having to build them.
 *
 * Runs once per save (tracked in the sector memory) and does nothing on
 * subsequent loads, so it never fights a player who tore an industry down.
 */
public class FoodcMarketInstaller {

    public static final String KITCHENS_KEY = "$foodc.centralKitchensInstalled";
    public static final String CONSUMERS_KEY = "$foodc.consumerMarketsInstalled";
    private static final Logger LOG = Global.getLogger(FoodcMarketInstaller.class);

    public static void installOnce() {
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();

        if (!memory.getBoolean(KITCHENS_KEY)) {
            int added = installKitchens();
            memory.set(KITCHENS_KEY, true);
            LOG.info("Central Kitchens installed on " + added + " markets.");
        }

        if (!memory.getBoolean(CONSUMERS_KEY)) {
            int added = installConsumerMarkets();
            memory.set(CONSUMERS_KEY, true);
            LOG.info("Consumer Markets installed on " + added + " markets.");
        }
    }

    private static int installKitchens() {
        int added = 0;
        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            if (market == null) continue;
            if (market.isPlanetConditionMarketOnly()) continue;
            if (market.getFaction() == null) continue;
            if (market.getFaction().isPlayerFaction()) continue;
            if (!market.hasIndustry(Industries.POPULATION)) continue;
            if (market.hasIndustry(CentralKitchen.ID)) continue;
            if (market.getSize() < 4) continue;
            if (!isFoodMarket(market)) continue;

            market.addIndustry(CentralKitchen.ID);
            added++;

            if (Global.getSettings().isDevMode()) {
                LOG.info("  kitchen -> " + market.getName()
                        + " (" + market.getFactionId() + ", size " + market.getSize() + ")");
            }
        }
        return added;
    }

    private static boolean isFoodMarket(MarketAPI market) {
        return market.hasIndustry(Industries.FARMING)
                || market.hasIndustry(Industries.AQUACULTURE)
                || market.hasIndustry(Industries.LIGHTINDUSTRY);
    }

    private static int installConsumerMarkets() {
        int added = 0;
        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            if (market == null) continue;
            if (market.isPlanetConditionMarketOnly()) continue;
            if (!market.hasIndustry(Industries.POPULATION)) continue;
            if (market.hasIndustry(ConsumerMarket.ID)) continue;
            if (market.getSize() < 3) continue;

            market.addIndustry(ConsumerMarket.ID);
            added++;
        }
        return added;
    }
}
