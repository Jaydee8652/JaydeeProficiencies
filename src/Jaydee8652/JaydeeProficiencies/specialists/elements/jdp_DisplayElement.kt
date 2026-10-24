package Jaydee8652.JaydeeProficiencies.specialists.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.graphics.SpriteAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.FaderUtil
import lunalib.lunaUI.elements.LunaElement
import org.lazywizard.lazylib.MathUtils
import java.awt.Color

class jdp_DisplayElement(var sprite: SpriteAPI, var text: String, var color: Color, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {
    var flash = FaderUtil(1f, 0.75f, 0.75f, true, true);
    var border = Global.getSettings().getSprite("graphics/icons/skills/jdp_skillinactive.png")

    var hasBorder: Boolean = false
    var hasFlash: Boolean = false
    var render: Boolean = true
    var hoverable: Boolean = true

    init {
        if (sprite == Global.getSettings().getSprite("graphics/icons/skills/jdp_skillinactive.png")) render = false

        var label = innerElement.addPara(text, color, 3f)
        label.position.inTL(width / 2 - label.computeTextWidth(label.text) / 2, height / 2 - label.computeTextHeight(label.text) / 2)

        enableTransparency = true
        renderBackground = false
        renderBorder = false

        flash.state = FaderUtil.State.OUT
        if(hoverable) onHoverEnter {
            playSound("ui_button_mouseover")
        }
    }
    var hoverFade = 0f
    var time = 0f

    override fun advance(amount: Float) {
        super.advance(amount)
        if(!hoverable) return

        time += 1 * amount

        if (isHovering) {
            hoverFade += 10f * amount
        } else {
            hoverFade -= 3f * amount
        }
        hoverFade = hoverFade.coerceIn(0f, 1f)
        if(hasFlash) flash.advance(amount);
    }


    override fun render(alphaMult: Float) {
        super.render(alphaMult)
        if (hasBorder) {
            border.setNormalBlend()
            border.color = color//.brighter()
            border.setSize(width, height)
            border.alphaMult = alphaMult * (0.85f + (flash.getBrightness() * 0.15f))
            border.renderAtCenter(x + (width / 2).toInt(), y + (height / 2).toInt())

            border.setAdditiveBlend()
            border.color = color//.brighter()
            border.setSize(width, height)
            border.alphaMult = alphaMult * (0.85f + (flash.getBrightness() * 0.15f))
            border.renderAtCenter(x + (width / 2).toInt(), y + (height / 2).toInt())
        }
    }

    override fun renderBelow(alphaMult: Float) {
        super.renderBelow(alphaMult)
        if(render) {
            sprite.setNormalBlend()
            sprite.alphaMult = alphaMult * 0.7f * (0.85f + (flash.getBrightness() * 0.15f))
            sprite.setSize(width, height)
            sprite.render(x, y)

            sprite.setAdditiveBlend()
            sprite.alphaMult = alphaMult * 0.3f * hoverFade * (0.85f + (flash.getBrightness() * 0.15f))
            sprite.setSize(width, height)
            sprite.render(x, y)
        }
    }
}