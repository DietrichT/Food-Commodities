package foodcplatform.api;

import java.util.*;

// use FoodRegistry.registerFactionFood("myFaction", config) to register

public class FoodRegistry {
    private static final Map<String, List<FactionFoodConfig>> FACTORY_FOOD_MAP = new HashMap<>();

    /**
     * register a food config for a faction
     * @param factionId factionID（e.g "hegemony"）
     * @param config food config, refer to FactionFoodConfig.java for structure
     * @return if registered properly
     */
    public static boolean registerFactionFood(String factionId, FactionFoodConfig config) {
        if (factionId == null || config == null) return false;
        FACTORY_FOOD_MAP.computeIfAbsent(factionId, k -> new ArrayList<>());    //prevent duplicate faction
        FACTORY_FOOD_MAP.get(factionId).add(config);
        return true;
    }

    /**
     * Check for all food configs under a faction
     */
    public static List<FactionFoodConfig> getFoodConfigsForFaction(String factionId) {
        return FACTORY_FOOD_MAP.getOrDefault(factionId, Collections.emptyList());   //returns empty list if empty
    }

    /**
     * Get the first Food item for the faction
     */
    public static String getFirstFoodIdForFaction(String factionId) {
        List<FactionFoodConfig> configs = getFoodConfigsForFaction(factionId);
        return configs.isEmpty() ? null : configs.get(0).getFoodId();
    }
}
