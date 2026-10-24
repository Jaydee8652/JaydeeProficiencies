package Jaydee8652.JaydeeProficiencies.specialists.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.lunaUI.elements.LunaElement
import java.awt.Color
import kotlin.collections.plusAssign

class jdp_ArrowElement(var full: Boolean, var color: Color, tooltip: TooltipMakerAPI, width: Float, var Xoffset: Float, height: Float, var Yoffset: Float) : LunaElement(tooltip, width, height) {
    var sprite = Global.getSettings().getSprite("graphics/icons/skills/jdp_skillarrow.png")
    var render = true

    init {
        if(full) sprite = Global.getSettings().getSprite("graphics/icons/skills/jdp_skillarrow_full.png")

        enableTransparency = true
        renderBackground = false
        renderBorder = false
    }
    var hoverFade = 0f
    var time = 0f

    override fun advance(amount: Float) {
        super.advance(amount)

        time += 1 * amount

        if (isHovering) {
            hoverFade += 10f * amount
        } else {
            hoverFade -= 3f * amount
        }
        hoverFade = hoverFade.coerceIn(0f, 1f)
    }

    override fun render(alphaMult: Float) {
        super.render(alphaMult)
        if (render) {
            sprite.setNormalBlend()
            sprite.color = color
            sprite.setSize(width - Xoffset * 2, height - Yoffset * 2)
            sprite.alphaMult = alphaMult * 0.8f
            sprite.render(x + Xoffset, y + Yoffset)
        }
    }

    override fun processInput(events: MutableList<InputEventAPI>?) {
    }

    override fun renderBelow(alphaMult: Float) {
        super.renderBelow(alphaMult)
    }
}