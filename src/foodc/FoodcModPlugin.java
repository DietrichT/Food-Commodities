package foodc;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.loading.IndustrySpecAPI;

import foodc.econ.CentralKitchen;
import foodc.econ.FoodcFoods;
import foodc.econ.FoodcMarketInstaller;

import org.apache.log4j.Logger;

public class FoodcModPlugin extends BaseModPlugin {

    private static final Logger LOG = Global.getLogger(FoodcModPlugin.class);

    @Override
    public void onApplicationLoad() {
        // Self-check: everything below is read straight from the CSV files, so if
        // the log shows no complaints here, the data files parsed correctly.
        for (String id : FoodcFoods.ALL) {
            CommoditySpecAPI spec = Global.getSettings().getCommoditySpec(id);
            if (spec == null) {
                LOG.error("commodity spec not found: " + id);
            } else {
                LOG.info("commodity ok: " + id + " (" + spec.getName()
                        + ", base price " + (int) spec.getBasePrice() + ")");
            }
        }

        IndustrySpecAPI industry = Global.getSettings().getIndustrySpec(CentralKitchen.ID);
        if (industry == null) {
            LOG.error("industry spec not found: " + CentralKitchen.ID);
        } else {
            LOG.info("industry ok: " + industry.getId()
                    + " (" + industry.getName() + ", plugin " + industry.getPluginClass() + ")");
        }
    }

    @Override
    public void onNewGameAfterEconomyLoad() {
        FoodcMarketInstaller.installOnce();
    }

    @Override
    public void onGameLoad(boolean newGame) {
        // covers saves created before the mod was enabled
        FoodcMarketInstaller.installOnce();
    }
}
