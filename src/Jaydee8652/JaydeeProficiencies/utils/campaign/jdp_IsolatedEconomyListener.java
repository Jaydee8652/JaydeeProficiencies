package Jaydee8652.JaydeeProficiencies.utils.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.EconomyAPI.EconomyUpdateListener;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.util.FlickerUtilV2;

public class jdp_IsolatedEconomyListener implements EconomyUpdateListener {
	protected Boolean ended = null;

	protected MarketAPI market;
	protected String justification;

	public jdp_IsolatedEconomyListener(MarketAPI market, String justification) {
		this.market = market;
		this.justification = justification;
	}

	public void economyUpdated() {}
	
	public void commodityUpdated(String commodityId) {
		CommodityOnMarketAPI com = market.getCommodityData(commodityId);
		int curr = 0;
		String modId = market.getId();
		MutableStat.StatMod mod = com.getAvailableStat().getFlatStatMod(modId);
		if (mod != null) {
			curr = Math.round(mod.value);
		}

		int avWithoutPenalties = (int) Math.round(com.getAvailableStat().getBaseValue());
		for (MutableStat.StatMod m : com.getAvailableStat().getFlatMods().values()) {
			if (m.value < 0) continue;
			avWithoutPenalties += (int) Math.round(m.value);
		}

		int a = com.getAvailable() - curr;
		a = avWithoutPenalties - curr;
		int d = com.getMaxDemand();
		if (d > a) {
			int supply = Math.max(1, d - a);
			//"Brought in by smugglers"
			com.getAvailableStat().modifyFlat(modId, supply, justification);
		}
	}

	public boolean isEconomyListenerExpired() {
		return isEnded();
	}

	public boolean isEnded() {
		return ended != null && ended;
	}

}















