package Jaydee8652.JaydeeProficiencies.specialists.elements

import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_Specialist
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_DEFAULT_SPECIALIST
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils
import Jaydee8652.JaydeeProficiencies.utils.jdp_SpecStore
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaUI.elements.LunaElement
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.opengl.GL11
import java.awt.Color

class jdp_ExperienceBarElement(var spec: jdp_SpecStore.jdp_proficiencySpec, var specialist: jdp_Specialist, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {
    var bar = Global.getSettings().getSprite("graphics/icons/skills/jdp_xp.png")
    var barFill = Global.getSettings().getSprite("graphics/icons/skills/jdp_xp_fill.png")

    var current = 0f
    var color = spec.color

    var hoverFade = 0f
    var time = 0f

    init {
        renderBorder = false
        renderBackground = false
        enableTransparency = true

        onHoverEnter {
            playScrollSound()
        }

        var threshold = specialist.getRequiredXP(specialist.level)
        current = specialist.experience / threshold
        if(specialist.level == specialist.getMaxLevel()) current = 1f;

        var level = specialist.level
        var maxLevel = specialist.getMaxLevel()
        var skillpoints = (specialist.level - specialist.activeSkills().size).toString()

        if (specialist.person.id == JDP_DEFAULT_SPECIALIST) {
            current = 0f;
            maxLevel = spec.apprenticeMaxLevel
            level = 0
            skillpoints = "0"

            var text = " SP - Level " + level + " / " + maxLevel
            var label = innerElement.addPara(skillpoints + text, 3f, Misc.getGrayColor(), skillpoints + text)
            label.position.inTL(width / 2 - label.computeTextWidth(label.text) / 2, 1f - label.computeTextHeight(label.text))
        } else {
            var text = " SP - Level " + level + " / " + maxLevel
            if (skillpoints == "0") {
                var label = innerElement.addPara(skillpoints + text, 3f, Misc.getTextColor(), Misc.getGrayColor(), skillpoints, text)
                label.position.inTL(width / 2 - label.computeTextWidth(label.text) / 2, 1f - label.computeTextHeight(label.text))
            } else {
                var label = innerElement.addPara(skillpoints + text, 3f, Misc.getTextColor(), Misc.getHighlightColor(), skillpoints, text)
                label.position.inTL(width / 2 - label.computeTextWidth(label.text) / 2, 1f - label.computeTextHeight(label.text))
            }
        }

        innerElement.setParaFont("graphics/fonts/victor14.fnt")
    }

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
        bar.color = color

        bar.alphaMult = alphaMult
        bar.setSize(width, height)
        bar.render(x, y)

        barFill.color = color
        current = MathUtils.clamp(current, 0f, 1f)

        startBarStencil(x, y, width, height, current)
        barFill.alphaMult = alphaMult
        barFill.setSize(width, height)
        barFill.render(x, y)
        endStencil()
    }

    fun startBarStencil(x: Float, y: Float, width: Float, height: Float, percent: Float) {
        GL11.glClearStencil(0);
        GL11.glStencilMask(0xff);
        //set everything to 0
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);

        //disable drawing colour, enable stencil testing
        GL11.glColorMask(false, false, false, false); //disable colour
        GL11.glEnable(GL11.GL_STENCIL_TEST); //enable stencil

        // ... here you render the part of the scene you want masked, this may be a simple triangle or square, or for example a monitor on a computer in your spaceship ...
        //begin masking
        //put 1s where I want to draw
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xff); // Do not test the current value in the stencil buffer, always accept any value on there for drawing
        GL11.glStencilMask(0xff);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE); // Make every test succeed

        // <draw a quad that dictates you want the boundaries of the panel to be>

        GL11.glRectf(x+3, y, x + 3 + ((width-6) * percent), y + height)

        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP); // Make sure you will no longer (over)write stencil values, even if any test succeeds
        GL11.glColorMask(true, true, true, true); // Make sure we draw on the backbuffer again.

        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xFF); // Now we will only draw pixels where the corresponding stencil buffer value equals 1
        //Ref 0 causes the content to not display in the specified area, 1 causes the content to only display in that area.
    }

    fun endStencil() {
        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }
}