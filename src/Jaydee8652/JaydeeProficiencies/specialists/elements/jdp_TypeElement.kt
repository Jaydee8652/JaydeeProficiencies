package Jaydee8652.JaydeeProficiencies.specialists.elements

import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.isBaseSpecialistPerson
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.api.graphics.SpriteAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI.TooltipLocation
import com.fs.starfarer.api.util.Misc
import lunalib.lunaUI.elements.LunaElement

import java.awt.Color

class jdp_TypeElement(var type: String, var person: PersonAPI, var active: Boolean, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {
    var typeData = jdp_SpecialistTags.SPECIALIST_TYPE_MAP.get(type)!!;
    var color = Misc.getGrayColor()

    init {
        if (active) color = Misc.getTextColor()

        if (isBaseSpecialistPerson(person) || !active) {
            this.addTooltip("Apprentice can be " + typeData.an() + typeData.name(), 100f, TooltipLocation.ABOVE)
        } else {
            if (Global.getSector().playerPerson.id.equals(person.id)) {
                this.addTooltip(typeData.name() + " are " + person.name.fullName, 100f, TooltipLocation.ABOVE)
            } else {
                this.addTooltip(person.name.fullName + " is " + typeData.an() + typeData.name(), 100f, TooltipLocation.ABOVE)
            }
        }

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
    }

    override fun renderBelow(alphaMult: Float) {
        super.renderBelow(alphaMult)
        var sprite = typeData.sprite();

        sprite.color = color
        sprite.setNormalBlend()
        sprite.alphaMult = alphaMult * 0.7f
        sprite.setSize(width, height)
        sprite.render(x, y)

        sprite.setAdditiveBlend()
        sprite.alphaMult = alphaMult * 0.3f * hoverFade
        sprite.setSize(width, height)
        sprite.render(x, y)
    }
}