package foodc.econ;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.Pair;

/**
 * Central place for the commodity ids this mod adds, and for the
 * faction -> specialty food mapping used by the Central Kitchen industry.
 */
public class FoodcFoods {

    public static final String HEGEMONY_HOTPOT = "foodc_heghotpot";
    public static final String TRITACH_LOBSTER = "foodc_tritaclobster";
    public static final String BAIJIU = "foodc_collectablebaijiu";
    public static final String ARTISANAL = "foodc_ludcartisian";
    public static final String SPICE_STEW = "foodc_permixspicestrew";
    public static final String PIRATE_MIX = "foodc_pirfoodmix";

    public static final List<String> ALL = Collections.unmodifiableList(Arrays.asList(
            HEGEMONY_HOTPOT, TRITACH_LOBSTER, BAIJIU, ARTISANAL, SPICE_STEW, PIRATE_MIX));

    /** Everything except baijiu - the goods that actually get bought and eaten. */
    public static final List<String> MARKET_FOODS = Collections.unmodifiableList(Arrays.asList(
            HEGEMONY_HOTPOT, TRITACH_LOBSTER, ARTISANAL, SPICE_STEW, PIRATE_MIX));

    /** The everyday foods, i.e. everything the market wants except the lobster dish. */
    public static final List<String> STAPLES = Collections.unmodifiableList(Arrays.asList(
            HEGEMONY_HOTPOT, ARTISANAL, SPICE_STEW, PIRATE_MIX));

    private static final Map<String, String> SPECIALTY = new HashMap<String, String>();

    static {
        SPECIALTY.put(Factions.HEGEMONY, HEGEMONY_HOTPOT);
        // the Diktat runs the same kind of sealed military ration programme
        SPECIALTY.put(Factions.DIKTAT, HEGEMONY_HOTPOT);
        SPECIALTY.put(Factions.TRITACHYON, TRITACH_LOBSTER);
        SPECIALTY.put(Factions.PERSEAN, SPICE_STEW);
        SPECIALTY.put(Factions.PIRATES, PIRATE_MIX);
        SPECIALTY.put(Factions.LUDDIC_CHURCH, ARTISANAL);
        SPECIALTY.put(Factions.LUDDIC_PATH, ARTISANAL);
        SPECIALTY.put(Factions.KOL, ARTISANAL);
        SPECIALTY.put(Factions.INDEPENDENT, ARTISANAL);
        SPECIALTY.put(Factions.PLAYER, ARTISANAL);
    }

    /**
     * The food a Central Kitchen produces on a market owned by the given faction.
     * Unknown (modded) factions and the player fall back to artisanal food.
     */
    public static String getSpecialty(String factionId) {
        String food = SPECIALTY.get(factionId);
        return food == null ? ARTISANAL : food;
    }

    /**
     * Same as BaseIndustry.getMaxDeficit, but tolerates a market that has no
     * commodity data for one of these ids - which happens when the mod is
     * enabled on a save that predates it.
     */
    public static Pair<String, Integer> maxDeficit(BaseIndustry industry, String... commodityIds) {
        Pair<String, Integer> result = new Pair<String, Integer>();
        result.two = 0;

        for (String id : commodityIds) {
            int demand = (int) industry.getDemand(id).getQuantity().getModifiedValue();
            CommodityOnMarketAPI com = industry.getMarket().getCommodityData(id);
            int available = com == null ? 0 : com.getAvailable();

            int deficit = Math.max(demand - available, 0);
            if (deficit > result.two) {
                result.one = id;
                result.two = deficit;
            }
        }

        return result;
    }
}
