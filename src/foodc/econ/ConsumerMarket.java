package foodc.econ;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.util.Pair;

/**
 * The parts of a colony that actually eat: street kitchens, company canteens,
 * officers' messes and the shops that supply them. Keeps a standing demand for
 * the sector's specialty foods so the Central Kitchens have somewhere to sell.
 *
 * Auto-added to NPC markets by FoodcMarketInstaller; not buildable, and tagged
 * as a structure so it does not use up an industry slot.
 */
public class ConsumerMarket extends BaseIndustry {

    public static final String ID = "foodc_consumer_market";

    /** Stability granted while all five foods are available. */
    public static final float STABILITY_BONUS_WITH_LOBSTER = 2f;
    /** Stability granted while only the everyday foods are available. */
    public static final float STABILITY_BONUS_BASE = 1f;

    @Override
    public void apply() {
        super.apply(true);

        float qty = demandFor(market.getSize());
        for (String food : FoodcFoods.MARKET_FOODS) {
            getDemand(food).getQuantity().modifyFlat(getModId(0), qty, "Consumer demand");
        }

        updateStability();

        if (!isFunctional()) {
            demand.clear();
            market.getStability().unmodifyFlat(getModId(1));
        }
    }

    @Override
    public void unapply() {
        super.unapply();
        if (market != null) {
            market.getStability().unmodifyFlat(getModId(1));
        }
    }

    /**
     * +2 stability while every food is available including the lobster dish,
     * +1 if only the everyday foods are. Nothing at all if the basics are
     * missing.
     */
    private void updateStability() {
        Pair<String, Integer> all = FoodcFoods.maxDeficit(this,
                FoodcFoods.MARKET_FOODS.toArray(new String[0]));

        float bonus = 0f;
        String reason = "";
        if (all.two <= 0) {
            bonus = STABILITY_BONUS_WITH_LOBSTER;
            reason = "Consumer market demand met, lobster included";
        } else {
            Pair<String, Integer> staples = FoodcFoods.maxDeficit(this,
                    FoodcFoods.STAPLES.toArray(new String[0]));
            if (staples.two <= 0) {
                bonus = STABILITY_BONUS_BASE;
                reason = "Consumer market demand met";
            }
        }

        if (bonus > 0) {
            market.getStability().modifyFlat(getModId(1), bonus, reason);
        } else {
            market.getStability().unmodifyFlat(getModId(1));
        }
    }

    /** Demand per food on a market of the given size. */
    private static float demandFor(int size) {
        switch (size) {
            case 3: return 1f;
            case 4: return 1.5f;
            case 5: return 2f;
            case 6: return 3f;
            case 7: return 4f;
            case 8: return 5f;
            default: return size > 8 ? 5f + (size - 8) : 1f;
        }
    }

    @Override
    public boolean isAvailableToBuild() {
        return false;
    }

    @Override
    public boolean showWhenUnavailable() {
        return false;
    }
}
