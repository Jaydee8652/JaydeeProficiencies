package Jaydee8652.JaydeeProficiencies.utils.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import exerelin.campaign.AllianceManager;

public class jdp_CampaignEntityUtils {
	public static boolean validateFleet(CampaignFleetAPI fleet, SectorEntityToken entity) {
		if (fleet.getFaction() == entity.getFaction()) return true;
		if (Global.getSettings().getModManager().isModEnabled("nexerelin") && AllianceManager.areFactionsAllied(fleet.getFaction().getId(), entity.getFaction().getId())) return true;
		return false;
	}

	public static boolean isInverted() {
		return Global.getSector().getPlayerPerson().getMemoryWithoutUpdate().contains(jdp_Stats.HACKING_INVERSION_ACTIVE);
	}
}
