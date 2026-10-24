package Jaydee8652.JaydeeProficiencies.utils.campaign;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class jdp_IndustryUtils {
	public static Map<String, String> INDUSTRY_NAMES = new HashMap<String, String>();
	public static Set<String> MILITARY_TAGS = new HashSet<String>();
	static {
		MILITARY_TAGS.add(Industries.TAG_MILITARY);
		MILITARY_TAGS.add(Industries.TAG_COMMAND);

		INDUSTRY_NAMES.put(Industries.REFINING, "Refining");
		INDUSTRY_NAMES.put(Industries.MINING, "Mining");
		INDUSTRY_NAMES.put(Industries.FARMING, "Farming");
		INDUSTRY_NAMES.put(Industries.AQUACULTURE, "Aquaculture");
		INDUSTRY_NAMES.put(Industries.COMMERCE, "Commerce");
		INDUSTRY_NAMES.put(Industries.FUELPROD, "Fuel Production");
		INDUSTRY_NAMES.put(Industries.SPACEPORT, "Spaceport, Megaport");
		INDUSTRY_NAMES.put(Industries.HEAVYINDUSTRY, "Heavy Industry, Orbital Works");
		INDUSTRY_NAMES.put(Industries.TAG_POPULATION, "Population & Infrastructure");
		INDUSTRY_NAMES.put(Industries.PLANETARYSHIELD, "Planetary Shield");
		INDUSTRY_NAMES.put(Industries.GROUNDDEFENSES, "Ground Defences, Heavy Batteries");
		INDUSTRY_NAMES.put(Industries.LIGHTINDUSTRY, "Light Industry");
		INDUSTRY_NAMES.put(Industries.MILITARYBASE, "Military Base, High Command");
		INDUSTRY_NAMES.put(Industries.TECHMINING, "Tech-Mining");
	}

	public static String addBrackets(String string) {
		return "(" + string + ")";
	}

	public static String capitaliseFirst(String string) {
		return string.replaceFirst(string.substring(0, 1), string.substring(0, 1).toUpperCase());
	}

	public static Industry getIndustryByTag(MarketAPI market, String tag) {
		for (Industry ind : market.getIndustries()) if (ind.getSpec().hasTag(tag)) return ind;
		return null;
	}

	public static Industry getIndustryByTag(MarketAPI market, Set<String> tags, String blacklist) {
		for (Industry ind : market.getIndustries()) for (String tag: tags) if (ind.getSpec().hasTag(tag) && !ind.getSpec().hasTag(blacklist)) return ind;
		return null;
	}

	public static void updateMarket(MarketAPI market) {
		market.reapplyConditions();
		market.reapplyIndustries();

		for (Industry industry : market.getIndustries()) {
			industry.doPreSaveCleanup();
			industry.doPostSaveRestore();
		}
	}
}



