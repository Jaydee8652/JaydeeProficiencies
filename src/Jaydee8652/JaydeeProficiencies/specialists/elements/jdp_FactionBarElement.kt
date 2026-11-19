package Jaydee8652.JaydeeProficiencies.specialists.elements

import Jaydee8652.JaydeeProficiencies.ui.jdp_FactionDialogInjector.AUTOGENERATE_CHECKED
import Jaydee8652.JaydeeProficiencies.ui.jdp_ProgressBar
import Jaydee8652.JaydeeProficiencies.ui.jdp_ProgressBar.jdp_LabelData
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_FactionUtils
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_FactionUtils.presetKey
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_IndustryUtils.capitaliseFirst
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.ListenerFactory
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIReflectionUtils.isBoxChecked
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.UIComponentAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaUI.elements.LunaElement
import org.lwjgl.opengl.GL11
import java.awt.Font

class jdp_FactionBarElement(factionId: String, progress: Float, label: jdp_LabelData, var onClickPlugin: ListenerFactory.onClickScript, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {
    var faction = Global.getSector().getFaction(factionId)
    var spec = faction.factionSpec
    var gap = 3f;

    var baseUIColor = spec.baseUIColor
    var gridUIColor = spec.gridUIColor
    var brightUIColor = spec.brightUIColor
    var darkUIColor = spec.darkUIColor

    init {
        var name = innerElement.createLabel(capitaliseFirst(faction.displayName), brightUIColor, (width * 0.2f - gap));

        innerElement.addCustom(name as UIComponentAPI?, 10f)
        name.position.inTL(gap, height / 2 - name.computeTextHeight(name.text) / 2)

        val component = jdp_ProgressBar((width * 0.8f) - (gap * 2), height - gap * 2, baseUIColor, gridUIColor, label, progress)
        innerElement.addCustom(component.getMainPanel(), 5f).position.inTL((width * 0.2f) + gap, gap)

        enableTransparency = true
        renderBackground = true
        renderBorder = true

        backgroundColor = darkUIColor
        borderColor = gridUIColor
    }

    var hoverFade = 0f

    override fun advance(amount: Float) {
        super.advance(amount)

        if (isHovering) {
            hoverFade += 10f * amount
        } else {
            hoverFade -= 3f * amount
        }
        hoverFade = hoverFade.coerceIn(0.2f, 1f)
        onClickPlugin.checkCriteria(this)
    }

    override fun onClick(input: InputEventAPI) {
        onClickPlugin.onClick(input, this)
    }

    override fun render(alphaMult: Float) {
        super.render(alphaMult)
        backgroundAlpha = alphaMult * hoverFade
        borderAlpha = alphaMult * hoverFade
    }

    override fun renderBelow(alphaMult: Float) {
        super.renderBelow(alphaMult)
    }
}