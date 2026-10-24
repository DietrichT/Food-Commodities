package foodc.econ;

import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.util.Pair;

/**
 * Produces the specialty food of the owning faction out of locally sourced
 * staples, and imports the other factions' specialties for the tables of
 * officers and executives.
 *
 * Tagged as a structure in industries.csv, so it does not use up one of a
 * colony's industry slots.
 */
public class CentralKitchen extends BaseIndustry {

    public static final String ID = "foodc_central_kitchen";

    /** Output is divided by this before it hits the market. */
    public static final float OUTPUT_DIVISOR = 1.5f;

    @Override
    public void apply() {
        super.apply(true);

        int size = market.getSize();
        String specialty = FoodcFoods.getSpecialty(market.getFactionId());

        // output
        float raw = size - 1;
        if (market.hasCondition(Conditions.FARMLAND_RICH)
                || market.hasCondition(Conditions.FARMLAND_BOUNTIFUL)) {
            raw += 1;
        }
        float divisor = OUTPUT_DIVISOR;
        if (FoodcFoods.TRITACH_LOBSTER.equals(specialty)) {
            divisor *= 1.2f; // the blue lobster dish is scarcer than the rest
        }
        supply(specialty, Math.max(1, Math.round(raw / divisor)));

        // production inputs depend on what this kitchen makes
        List<String> inputs = getInputs(specialty);
        for (String commodityId : inputs) {
            demand(commodityId, getInputAmount(commodityId, size));
        }

        // imported delicacies - anything not already bought in as an ingredient
        for (String food : FoodcFoods.ALL) {
            if (food.equals(specialty)) continue;
            if (food.equals(FoodcFoods.BAIJIU)) continue; // salvage-only, nobody orders it in
            if (inputs.contains(food)) continue;
            demand(food, size >= 6 ? 1 : 0);
        }

        Pair<String, Integer> deficit = getMaxDeficit(inputs.toArray(new String[0]));
        applyDeficitToProduction(1, deficit, specialty);

        if (!isFunctional()) {
            supply.clear();
        }
    }

    private static List<String> getInputs(String specialty) {
        List<String> ids = new ArrayList<String>();

        if (FoodcFoods.HEGEMONY_HOTPOT.equals(specialty)
                || FoodcFoods.SPICE_STEW.equals(specialty)) {
            ids.add(Commodities.FOOD);
            ids.add(Commodities.ORGANICS);
            ids.add(Commodities.DOMESTIC_GOODS);
        } else if (FoodcFoods.ARTISANAL.equals(specialty)) {
            ids.add(Commodities.DOMESTIC_GOODS);
            ids.add(Commodities.ORGANICS);
        } else if (FoodcFoods.TRITACH_LOBSTER.equals(specialty)) {
            ids.add(Commodities.LOBSTER);
            ids.add(FoodcFoods.ARTISANAL);
            ids.add(Commodities.LUXURY_GOODS);
        } else if (FoodcFoods.PIRATE_MIX.equals(specialty)) {
            ids.add(Commodities.FOOD);
            ids.add(Commodities.ORGANICS);
        }

        return ids;
    }

    private static int getInputAmount(String commodityId, int size) {
        if (Commodities.FOOD.equals(commodityId) || Commodities.ORGANICS.equals(commodityId)) {
            return Math.max(1, Math.round((size - 2) / 2f));
        }
        if (Commodities.DOMESTIC_GOODS.equals(commodityId)
                || Commodities.LUXURY_GOODS.equals(commodityId)) {
            return Math.max(1, (size - 4) / 2);
        }
        // lobster and artisanal food: small quantities of expensive ingredients
        return Math.max(1, (size - 5) / 2);
    }

    @Override
    public boolean isAvailableToBuild() {
        return super.isAvailableToBuild() && market.getSize() >= 3;
    }

    @Override
    public String getUnavailableReason() {
        if (!super.isAvailableToBuild()) return super.getUnavailableReason();
        return "Requires a colony of size 3 or larger";
    }

    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }
}
