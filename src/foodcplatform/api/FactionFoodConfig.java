package foodcplatform.api;

import java.util.Collections;
import java.util.List;


public class FactionFoodConfig {
    private final String foodId;                    //ID defined in commodities.csv
    private final float baseSupply;                 //base supply amount
    private final List<String> requiredConditions;  // mandatory planet conditions
    private final List<String> optionalIndustries;  // at least 1 industry for production output

    public FactionFoodConfig(String foodId, float baseSupply,
                             List<String> requiredConditions,
                             List<String> optionalIndustries) {
        this.foodId = foodId;
        this.baseSupply = baseSupply;
        this.requiredConditions = requiredConditions != null ? requiredConditions : Collections.emptyList();
        this.optionalIndustries = optionalIndustries != null ? optionalIndustries : Collections.emptyList();
    }

    public String getFoodId() { return foodId; }
    public float getBaseSupply() { return baseSupply; }
    public List<String> getRequiredConditions() { return requiredConditions; }
    public List<String> getOptionalIndustries() { return optionalIndustries; }
}