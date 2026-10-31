package foodc;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.PlayerColonizationListener;

import foodc.econ.ConsumerMarket;

import org.apache.log4j.Logger;

/**
 * Gives a brand new colony its Consumer Market the moment it is founded.
 * NPC markets are handled by FoodcMarketInstaller instead.
 */
public class FoodcColonizationListener implements PlayerColonizationListener {

    private static final Logger LOG = Global.getLogger(FoodcColonizationListener.class);

    @Override
    public void reportPlayerColonizedPlanet(PlanetAPI planet) {
        MarketAPI market = planet.getMarket();
        if (market == null) return;
        if (market.hasIndustry(ConsumerMarket.ID)) return;

        market.addIndustry(ConsumerMarket.ID);
        LOG.info("Consumer Market added to newly founded colony " + market.getName());
    }

    @Override
    public void reportPlayerAbandonedColony(MarketAPI colony) {
        // nothing to clean up: the industry goes away with the market
    }
}
