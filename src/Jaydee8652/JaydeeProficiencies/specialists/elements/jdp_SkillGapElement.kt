package Jaydee8652.JaydeeProficiencies.specialists.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.lunaUI.elements.LunaElement
import java.awt.Color

class jdp_SkillGapElement(var color: Color, tooltip: TooltipMakerAPI, var heightOffset: Float = 60f, var heightOverride: Float = 34f, var widthOverride: Float = 1f) : LunaElement(tooltip, heightOverride, widthOverride) {
    var render: Boolean = true
    var arrowSprite = Global.getSettings().getSprite("graphics/icons/skills/jdp_skillarrow.png")

    init {
        enableTransparency = true
        renderBackground = false
        renderBorder = false
    }

    override fun render(alphaMult: Float) {
        super.render(alphaMult)

        if(render) {
            arrowSprite.setNormalBlend()
            arrowSprite.alphaMult = alphaMult * 0.8f
            arrowSprite.color = color
            arrowSprite.renderAtCenter(x + width / 2 + 1, y - heightOffset / 2 - 1)
        }
    }

    override fun processInput(events: MutableList<InputEventAPI>?) {
        //super.processInput(events) Prevent Inputs from being consumed
    }
}