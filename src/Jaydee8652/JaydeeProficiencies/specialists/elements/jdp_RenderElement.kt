package Jaydee8652.JaydeeProficiencies.specialists.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.graphics.SpriteAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.FaderUtil
import lunalib.lunaUI.elements.LunaElement
import org.lazywizard.lazylib.MathUtils
import java.awt.Color

class jdp_RenderElement(var sprite: SpriteAPI, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {
    init {
        this.renderBorder = false
        this.renderBackground = false
    }
    override fun renderBelow(alphaMult: Float) {
        super.renderBelow(alphaMult)
        sprite.setNormalBlend()
        sprite.setSize(width, height)
        sprite.render(x, y)
    }
}