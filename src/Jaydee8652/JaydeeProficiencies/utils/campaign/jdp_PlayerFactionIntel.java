package Jaydee8652.JaydeeProficiencies.utils.campaign;

import Jaydee8652.JaydeeProficiencies.utils.refl.ReflectionUtilis;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Set;

public class jdp_PlayerFactionIntel extends BaseIntelPlugin {
	FactionAPI faction = Global.getSector().getPlayerFaction();

	public static String BUTTON_MANAGE = "jdp_ManageFactionButton";
	public String getName() {
		return "Faction: "+ jdp_IndustryUtils.capitaliseFirst(faction.getDisplayName());
	}
	
	@Override
	public FactionAPI getFactionForUIColors() {
		return faction;
	}

	public String getSmallDescriptionTitle() {
		return getName();
	}

	public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
		Color h = Misc.getHighlightColor();
		Color g = Misc.getGrayColor();
		Color tc = Misc.getTextColor();
		float pad = 3f;
		float opad = 10f;

		info.addImage(getFaction().getLogo(), width, 128, opad);
		info.addSpacer(opad);
		info.addPara("Manage the flag, name and color of your faction", pad);
		info.addSpacer(opad);
		ButtonAPI button = info.addButton("Manage Faction", BUTTON_MANAGE,
				getFactionForUIColors().getBaseUIColor(), getFactionForUIColors().getDarkUIColor(),
				(int)(width), 20f, opad * 1f);
		button.setShortcut(Keyboard.KEY_M, true);
	}
	
	
	public String getIcon() {
		return faction.getCrest();
	}

	public void init() {
		Global.getSector().getIntelManager().addIntel(this);
		this.setImportant(true);
	}


	public Set<String> getIntelTags(SectorMapAPI map) {
		Set<String> tags = super.getIntelTags(map);
		//tags.add(Tags.INTEL_IMPORTANT);
		tags.add(faction.getId());
		return tags;
	}
	
	@Override
	public String getImportantIcon() {
		return Global.getSettings().getSpriteName("intel", "important_accepted_mission");
	}

	@Override
	public SectorEntityToken getMapLocation(SectorMapAPI map) {
		return null;
	}

	public FactionAPI getFaction() {
		return faction;
	}

	@Override
	public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
		if (buttonId == BUTTON_MANAGE) {
			ReflectionUtilis.setPrivateVariableFromSuperclass("dialogType", Global.getSector().getCampaignUI(), null);
			Global.getSector().getCampaignUI().showPlayerFactionConfigDialog();
		}
		super.buttonPressConfirmed(buttonId, ui);
	}
}






