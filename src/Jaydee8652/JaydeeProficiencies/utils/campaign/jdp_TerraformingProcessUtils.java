package Jaydee8652.JaydeeProficiencies.utils.campaign;

import java.awt.*;
import java.util.*;
import java.util.List;

import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_ArrowElement;
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_DisplayElement;
import Jaydee8652.JaydeeProficiencies.specialists.tooltips.ConditionTooltipCreator;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.MarketConditionSpecAPI;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class jdp_TerraformingProcessUtils {
	public static class TerraformingProcessData {
		private final String product;
		private final String reagent;
		private final Float weight;
		private final List<String> blacklist;
		private final List<String> whitelist;
		private final String sideEffect;

		public TerraformingProcessData(Float weight, String product, String reagent, List<String> blacklist, List<String> whitelist, String sideEffect) {
			this.product = product;
			this.reagent = reagent;
			this.weight = weight;
			this.blacklist = blacklist;
			this.whitelist = whitelist;

			this.sideEffect = sideEffect;
		}

		public TerraformingProcessData(Float weight, String product, String reagent) {
			this.product = product;
			this.reagent = reagent;
			this.weight = weight;
			this.blacklist = null;
			this.whitelist = null;

			this.sideEffect = null;
		}

		public Boolean isAllowed(MarketAPI market) {
			return (!isBlacklisted(market) && isWhitelisted(market) && (reagent == null || market.hasCondition(reagent)) && (product == null || !market.hasCondition(product)));
		}

		public Boolean isBlacklisted(MarketAPI market) {
			boolean blacklisted = false;
			if (blacklist == null) return blacklisted;

			for (String condition : blacklist) {
				if (market.hasCondition(condition)) blacklisted = true;
			}
			return blacklisted;
		}

		public Boolean isWhitelisted(MarketAPI market) {
			boolean whitelisted = true;
			if (whitelist == null) return whitelisted;

			for (String condition : whitelist) {
				if (!market.hasCondition(condition)) whitelisted = false;
			}
			return whitelisted;
		}

		public String getReagent() {
			return reagent;
		}

		public String getProduct() {
			return product;
		}

		public String getSideEffect() {
			return sideEffect;
		}

		public Float getWeight() {
			return weight;
		}
	}

	public static MessageIntel generateTerraformingIntel(MessageIntel intel, MarketAPI market,TerraformingProcessData primaryProcess, TerraformingProcessData secondaryProcess) {
		if (secondaryProcess == null) secondaryProcess = new TerraformingProcessData(2f, null, null);

		if (primaryProcess.getProduct() != null && primaryProcess.getReagent() != null) {
			String added = Global.getSettings().getMarketConditionSpec(primaryProcess.getProduct()).getName().replace(" Deposits", "");
			String removed = Global.getSettings().getMarketConditionSpec(primaryProcess.getReagent()).getName().replace(" Deposits", "");

			intel.addLine(BaseIntelPlugin.BULLET + removed + " –> " + added, Misc.getTextColor(), new String[]{removed, added}, Misc.getHighlightColor(), Misc.getHighlightColor());
		}
		if (secondaryProcess.getProduct() != null && secondaryProcess.getReagent() != null) {
			String added = Global.getSettings().getMarketConditionSpec(secondaryProcess.getProduct()).getName().replace(" Deposits", "");
			String removed = Global.getSettings().getMarketConditionSpec(secondaryProcess.getReagent()).getName().replace(" Deposits", "");

			intel.addLine(BaseIntelPlugin.BULLET + removed + " –> " + added, Misc.getTextColor(), new String[]{removed, added}, Misc.getHighlightColor(), Misc.getHighlightColor());
		}
		if (primaryProcess.getProduct() != null && primaryProcess.getReagent() == null) {
			String added = Global.getSettings().getMarketConditionSpec(primaryProcess.getProduct()).getName().replace(" Deposits", "");

			intel.addLine(BaseIntelPlugin.BULLET + "Added: " + added, Misc.getTextColor(), new String[]{added}, Misc.getHighlightColor());
		}
		if (secondaryProcess.getProduct() != null && secondaryProcess.getReagent() == null) {
			String added = Global.getSettings().getMarketConditionSpec(secondaryProcess.getProduct()).getName().replace(" Deposits", "");

			intel.addLine(BaseIntelPlugin.BULLET + "Added: " + added, Misc.getTextColor(), new String[]{added}, Misc.getHighlightColor());
		}
		if (primaryProcess.getProduct() == null && primaryProcess.getReagent() != null) {
			String removed = Global.getSettings().getMarketConditionSpec(primaryProcess.getReagent()).getName().replace(" Deposits", "");

			intel.addLine(BaseIntelPlugin.BULLET + "Removed: " + removed, Misc.getTextColor(), new String[]{removed}, Misc.getHighlightColor());
		}
		if (secondaryProcess.getProduct() == null && secondaryProcess.getReagent() != null) {
			String removed = Global.getSettings().getMarketConditionSpec(secondaryProcess.getReagent()).getName().replace(" Deposits", "");

			intel.addLine(BaseIntelPlugin.BULLET + "Removed: " + removed, Misc.getTextColor(), new String[]{removed}, Misc.getHighlightColor());
		}
		String sideEffect = primaryProcess.getSideEffect();
		if (primaryProcess.getSideEffect() != null && market.hasCondition(sideEffect)) {
			market.removeCondition(sideEffect);

			String removed = Global.getSettings().getMarketConditionSpec(primaryProcess.getSideEffect()).getName().replace(" Deposits", "");
			intel.addLine(BaseIntelPlugin.BULLET + "Removed: " + removed, Misc.getTextColor(), new String[]{removed}, Misc.getHighlightColor());
		}
		return intel;
	}

	public static CustomPanelAPI addTerraformingDisplay(MarketAPI market, List<TerraformingProcessData> terraformingProcessMap, Color color, TooltipMakerAPI tooltip, Float width, Float height, Float gap) {
		CustomPanelAPI previousElement = null;
		TerraformingProcessData previousProcess = null;

		var displays = new ArrayList<jdp_DisplayElement>();

		ListIterator<TerraformingProcessData> iterator = new ArrayList<>(terraformingProcessMap).listIterator(terraformingProcessMap.size());
		while (iterator.hasPrevious()) {
			TerraformingProcessData data = iterator.previous();

			var sprite = Global.getSettings().getSprite("graphics/icons/skills/jdp_skillinactive.png");
			var imageWidth = height;

			String product = data.getProduct();
			String reagent = data.getReagent();
			if(product == reagent) return previousElement;
			String previousProduct = null;
			if (previousProcess != null) previousProduct = previousProcess.getProduct();

			//Reagent
			if (!Objects.equals(previousProduct, reagent) || previousProcess == null) {
				if (reagent != null) {
					MarketConditionSpecAPI reagentSpec = Global.getSettings().getMarketConditionSpec(reagent);
					sprite = Global.getSettings().getSprite(reagentSpec.getIcon());
				}
				imageWidth = (height / sprite.getHeight()) * sprite.getWidth();

				var reagentElement = new jdp_DisplayElement(sprite, "", color, tooltip, imageWidth, height);
				reagentElement.setHasBorder(true);
				displays.add(reagentElement);

				if (previousElement != null) {
					reagentElement.getPosition().rightOfTop(previousElement, gap * 4);
					reagentElement.setHasBorder(true);
					tooltip.setHeightSoFar(tooltip.getHeightSoFar() - height);
				}
				previousElement = reagentElement.getElementPanel();
			}

			//Arrow
			var full = false;
			if(market != null) full = data.isAllowed(market);
			var arrowElement = new jdp_ArrowElement(full, color, tooltip, 12f, 0f, height, 0f);
			if (previousElement != null) {
				arrowElement.getPosition().rightOfTop(previousElement, gap);
				tooltip.setHeightSoFar(tooltip.getHeightSoFar() - height);
			}
			previousElement = arrowElement.getElementPanel();

			//Product
			sprite = Global.getSettings().getSprite("graphics/icons/skills/jdp_skillinactive.png");
			if(product != null) {
				MarketConditionSpecAPI productSpec = Global.getSettings().getMarketConditionSpec(product);
				sprite = Global.getSettings().getSprite(productSpec.getIcon());
			}
			imageWidth = (height / sprite.getHeight()) * sprite.getWidth();

			var productElement = new jdp_DisplayElement(sprite, "", color, tooltip, imageWidth, height);

			displays.add(productElement);
			if (previousElement != null) {
				productElement.getPosition().rightOfTop(previousElement, gap);
				productElement.setHasBorder(true);
				tooltip.setHeightSoFar(tooltip.getHeightSoFar() - height);
			}
			previousElement = productElement.getElementPanel();
			previousProcess = data;
		}
		tooltip.addSpacer(gap).getPosition().belowLeft(displays.get(0).getElementPanel(), 0f);
		return previousElement;
	}
}



