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

    /**
     * Fixed the first time the industry is applied, then carried in the save:
     * a captured market keeps making what its kitchen was built to make, no
     * matter who owns it afterwards.
     */
    private String specialtyId;

    @Override
    public void apply() {
        super.apply(true);

        int size = market.getSize();
        if (specialtyId == null) {
            specialtyId = FoodcFoods.getSpecialty(market.getFactionId());
        }
        String specialty = specialtyId;
        int mult = Math.max(1, size / 2); // size 4-5 -> x2, 6-7 -> x3, 8-9 -> x4

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
        supply(specialty, Math.max(1, Math.round(raw / divisor)) * mult);

        // production inputs, scaled the same way as the output
        List<String> inputs = getInputs(specialty);
        for (String commodityId : inputs) {
            demand(commodityId, getInputAmount(commodityId, size) * mult);
        }

        Pair<String, Integer> deficit = FoodcFoods.maxDeficit(this, inputs.toArray(new String[0]));
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
        return super.isAvailableToBuild();
    }

    @Override
    public String getUnavailableReason() {
        return super.getUnavailableReason();
    }

    @Override
    protected boolean canImproveToIncreaseProduction() {
        return true;
    }
}
