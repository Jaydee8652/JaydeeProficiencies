package Jaydee8652.JaydeeProficiencies.utils.campaign;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.coreui.S;

import java.util.HashSet;
import java.util.Set;

public class jdp_ConditionUtils {
	public static Set<String> ORES = new HashSet<String>();
	public static Set<String> RARE_ORES = new HashSet<String>();
	public static Set<String> ORGANICS = new HashSet<String>();
	public static Set<String> VOLATILES = new HashSet<String>();
	public static Set<String> FARMLAND = new HashSet<String>();
	public static Set<String> RUINS = new HashSet<String>();

	public static Set<String> TEMPERATURE = new HashSet<String>();
	public static Set<String> DECIVILIZED = new HashSet<String>();
	public static Set<String> ATMOSPHERE = new HashSet<String>();

	static {
		ORES.add(Conditions.ORE_SPARSE);
		ORES.add(Conditions.ORE_MODERATE);
		ORES.add(Conditions.ORE_ABUNDANT);
		ORES.add(Conditions.ORE_RICH);
		ORES.add(Conditions.ORE_ULTRARICH);

		RARE_ORES.add(Conditions.RARE_ORE_SPARSE);
		RARE_ORES.add(Conditions.RARE_ORE_MODERATE);
		RARE_ORES.add(Conditions.RARE_ORE_ABUNDANT);
		RARE_ORES.add(Conditions.RARE_ORE_RICH);
		RARE_ORES.add(Conditions.RARE_ORE_ULTRARICH);

		ORGANICS.add(Conditions.ORGANICS_TRACE);
		ORGANICS.add(Conditions.ORGANICS_COMMON);
		ORGANICS.add(Conditions.ORGANICS_ABUNDANT);
		ORGANICS.add(Conditions.ORGANICS_PLENTIFUL);

		VOLATILES.add(Conditions.VOLATILES_TRACE);
		VOLATILES.add(Conditions.VOLATILES_DIFFUSE);
		VOLATILES.add(Conditions.VOLATILES_ABUNDANT);
		VOLATILES.add(Conditions.VOLATILES_PLENTIFUL);

		FARMLAND.add(Conditions.FARMLAND_POOR);
		FARMLAND.add(Conditions.FARMLAND_ADEQUATE);
		FARMLAND.add(Conditions.FARMLAND_RICH);
		FARMLAND.add(Conditions.FARMLAND_BOUNTIFUL);

		RUINS.add(Conditions.RUINS_SCATTERED);
		RUINS.add(Conditions.RUINS_WIDESPREAD);
		RUINS.add(Conditions.RUINS_EXTENSIVE);
		RUINS.add(Conditions.RUINS_VAST);

		TEMPERATURE.add(Conditions.VERY_COLD);
		TEMPERATURE.add(Conditions.VERY_HOT);
		TEMPERATURE.add(Conditions.COLD);
		TEMPERATURE.add(Conditions.HOT);

		DECIVILIZED.add(Conditions.DECIVILIZED);
		DECIVILIZED.add(Conditions.DECIVILIZED_SUBPOP);

		ATMOSPHERE.add(Conditions.NO_ATMOSPHERE);
		ATMOSPHERE.add(Conditions.DENSE_ATMOSPHERE);
		ATMOSPHERE.add(Conditions.TOXIC_ATMOSPHERE);
		ATMOSPHERE.add(Conditions.THIN_ATMOSPHERE);
	}

	public static void removeConditions(MarketAPI market, Set<String> conditionClass) {
		for(String condition: conditionClass) market.removeCondition(condition);
	}

	public static Boolean hasConditions(MarketAPI market, Set<String> conditionClass) {
		for(String condition: conditionClass) if(market.hasCondition(condition)) return true;
		return false;
	}
}
