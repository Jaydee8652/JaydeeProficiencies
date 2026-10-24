package Jaydee8652.JaydeeProficiencies.specialists.elements

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import lunalib.lunaUI.elements.LunaElement
import org.lwjgl.opengl.GL11
import org.magiclib.kotlin.setAlpha
import java.awt.Color

class jdp_LineElement(var color: Color, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {

    init {
        enableTransparency = true
        renderBackground = true
        renderBorder = false

        backgroundColor = color
        backgroundAlpha = color.alpha / 255f
    }

    override fun renderBelow(alphaMult: Float) {
        super.renderBelow(alphaMult)
    }

    override fun processInput(events: MutableList<InputEventAPI>?) {
        //super.processInput(events) Prevent Inputs from being consumed
    }
}