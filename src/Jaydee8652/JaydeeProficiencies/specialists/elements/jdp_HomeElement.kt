package Jaydee8652.JaydeeProficiencies.specialists.elements

import Jaydee8652.JaydeeProficiencies.utils.refl.ReflectionUtilis
import Jaydee8652.JaydeeProficiencies.utils.refl.ReflectionUtils
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.ClassRefs.*
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIReflectionUtils.isInstance
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.graphics.SpriteAPI
import com.fs.starfarer.api.ui.ButtonAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.UIComponentAPI
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.campaign.CampaignProgressIndicator
import com.fs.starfarer.campaign.ui.fleet.FleetMemberView
import com.fs.starfarer.ui.n
import lunalib.lunaUI.elements.LunaElement
import java.util.Collections

class jdp_HomeElement(fleetMember: FleetMemberAPI?, market: MarketAPI?, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {
    var renderScanlines = true

    var sprite: SpriteAPI? = null
    var scanlines = Global.getSettings().getSprite("graphics/icons/skills/jdp_scanlines.png")
    init {
        enableTransparency = true
        renderBackground = false
        renderBorder = false

        if (fleetMember != null && market != null) {
            innerElement.addPara("ADMIN & OFFICER ERROR", 3f);
        } else if (fleetMember != null) {
            var fleetMemberView: FleetMemberView = ReflectionUtils.instantiate(
                FleetMemberView::class.java,
                fleetMember,
                Global.getSector().playerFleet.fleetData
            ) as FleetMemberView
            var fleetMemberButton = ReflectionUtils.invoke("getButton", fleetMemberView) as ButtonAPI
            var renderer = ReflectionUtils.invoke("getRenderer", fleetMemberButton)

            var variables= ReflectionUtilis.getAllVariables(renderer)
            variables.removeAll(Collections.singleton(null));
            for (variable in variables) {
                if (isInstance(variable,CRBarClass)) {
                    var bar: UIPanelAPI = variable as UIPanelAPI
                    bar.setOpacity(0f);
                }
            }

            var children: List<UIComponentAPI> = ReflectionUtilis.invokeMethodDirectly(uiPanelgetChildrenCopyMethod, fleetMemberView) as List<UIComponentAPI>

            for (child in children) {
                fleetMemberView.removeComponent(child)
                if (child == fleetMemberButton) break
            }

            ReflectionUtilis.invokeMethodDirectly(buttonSetActiveMethod, fleetMemberButton, false);
            ReflectionUtilis.invokeMethodDirectly(buttonSetSizeMethod, fleetMemberButton, width, height);

            innerElement.addComponent(fleetMemberButton)
        } else if (market != null) {
            if (market.planetEntity != null) {
                innerElement.showPlanetInfo(market.planetEntity, width - (width / 8), height - (height / 8), false, 10f)
            } else {
                sprite = Global.getSettings().getSprite(market.primaryEntity.customEntitySpec.spriteName)
            }

            var label = innerElement.createLabel(market.name, Global.getSector().playerFaction.baseUIColor, width - (width / 16))
            innerElement.addCustom(label as UIComponentAPI?, 10f)
            label.position.inTMid((height * 0.75f) - label.computeTextHeight(label.text) / 2)

            renderScanlines = false
        } else {
            var label = innerElement.createLabel("Unassigned", Misc.getGrayColor(), width - (width / 16))
            innerElement.addCustom(label as UIComponentAPI?, 10f)
            label.position.inTMid(height / 2 - label.computeTextHeight(label.text) / 2)

            renderScanlines = false
        }
    }

    override fun render(alphaMult: Float) {
        super.render(alphaMult)
        if(renderScanlines) {
            scanlines.setNormalBlend()
            scanlines.alphaMult = alphaMult * 0.025f
            scanlines.setSize(width, height)
            scanlines.alphaMult = alphaMult
            scanlines.render(x, y)
        }
    }

    override fun renderBelow(alphaMult: Float) {
        super.renderBelow(alphaMult)
        if (sprite != null) {
            sprite!!.setNormalBlend()
            sprite!!.alphaMult = alphaMult
            sprite!!.setSize(width - (width / 8), height - (height / 8))
            sprite!!.render(x + ((width / 8) / 2), y + ((height / 8) / 2))
        }
    }
}