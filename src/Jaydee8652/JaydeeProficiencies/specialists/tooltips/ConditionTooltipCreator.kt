package Jaydee8652.JaydeeProficiencies.specialists.tooltips

import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_Specialist
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils
import com.fs.starfarer.api.ui.BaseTooltipCreator
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI
import com.fs.starfarer.api.campaign.econ.MarketConditionPlugin
import com.fs.starfarer.campaign.CampaignPlanet
import com.fs.starfarer.campaign.Faction
import com.fs.starfarer.campaign.econ.Economy
import com.fs.starfarer.campaign.econ.Market

class ConditionTooltipCreator(var condition: String?) : BaseTooltipCreator() {
    override fun getTooltipWidth(tooltipParam: Any?): Float {
        return 245f
    }

    override fun createTooltip(tooltip: TooltipMakerAPI, expanded: Boolean, tooltipParam: Any?) {
        if(condition == null) return

        var campaignPlanet = CampaignPlanet("", "", "barren", 100.0F)
        var fakeMarket = Market("", "the planet", 3, null)
        fakeMarket.setPrimaryEntity(campaignPlanet)
        fakeMarket.setPlanetConditionMarketOnly(true)
        fakeMarket.setCachedFaction(Faction("neutral"))
        fakeMarket.addCondition(condition)
        var conditionAPI = fakeMarket.getCondition(condition);

        var plugin = conditionAPI.getPlugin();
        plugin.createTooltip(tooltip, true)
    }
}