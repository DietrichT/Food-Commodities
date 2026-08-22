package foodcMarketEdit.econ;

import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import foodcplatform.api.FactionFoodConfig;
import foodcplatform.api.FoodRegistry;
import java.util.List;

public class SpecialFoodProducer extends BaseIndustry {

    private static final float BASE_SUPPLY_PER_POP = 2f;
    private static final float EXTRA_SUPPLY = 5f;

    @Override
    public void apply() {
        //super.apply();

        // 获取该势力所有食品配置
        List<FactionFoodConfig> configs = FoodRegistry.getFoodConfigsForFaction(market.getFactionId());
        for (FactionFoodConfig config : configs) {
            // 检查必需条件
            boolean conditionsMet = true;
            for (String cond : config.getRequiredConditions()) {
                if (!market.hasCondition(cond)) {
                    conditionsMet = false;
                    break;
                }
            }
            if (!conditionsMet) continue;

            // 检查可选产业（至少有一个）
            if (!config.getOptionalIndustries().isEmpty()) {
                boolean hasIndustry = false;
                for (String indId : config.getOptionalIndustries()) {
                    if (market.hasIndustry(indId)) {
                        hasIndustry = true;
                        break;
                    }
                }
                if (!hasIndustry) continue;
            }

            // 计算产量：基础量 + 人口修正
            float supply = config.getBaseSupply() + market.getSize() * BASE_SUPPLY_PER_POP + EXTRA_SUPPLY;
            // 额外加成：如果星球有 farmlands，产量增加 30%
            if (market.hasCondition("farmland")) {
                supply *= 1.3f;
            }
            // 可根据需要添加更多条件修正

            // 获取商品数据，添加供应
            CommodityOnMarketAPI com = market.getCommodityData(config.getFoodId());
            if (com != null) {
                // 使用唯一的 modId 以便后续移除
                String modId = getModId() + "_" + config.getFoodId();
                com.getAvailableStat().modifyFlat(modId, supply);
            }
        }
    }

    @Override
    public void unapply() {
        super.unapply();
        // 移除所有由本产业添加的供应修正
        List<FactionFoodConfig> configs = FoodRegistry.getFoodConfigsForFaction(market.getFactionId());
        for (FactionFoodConfig config : configs) {
            CommodityOnMarketAPI com = market.getCommodityData(config.getFoodId());
            if (com != null) {
                String modId = getModId() + "_" + config.getFoodId();
                com.getAvailableStat().unmodifyFlat(modId);
            }
        }
    }

//    // 可选：重写 isAvailableToBuild() 来限制产业可建造的条件
//    @Override
//    public boolean isAvailableToBuild() {
//        // 例如，只有非气态巨行星且人口≥3才能建造
//        return market.getSize() >= 3 && !market.isGasGiant();
//    }

    // 可选：重写 getTooltip() 添加描述
}