package Jaydee8652.JaydeeProficiencies.specialists.elements

import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistPickerMenu
import Jaydee8652.JaydeeProficiencies.specialists.plugins.jdp_ColorWheelPlugin
import Jaydee8652.JaydeeProficiencies.ui.jdp_FactionDialogInjector.AUTOGENERATE_CHECKED
import Jaydee8652.JaydeeProficiencies.utils.JaydeeCommons.getColor
import Jaydee8652.JaydeeProficiencies.utils.JaydeeCommons.setColor
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_FactionUtils
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_FactionUtils.presetKey
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_IndustryUtils.capitaliseFirst
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIReflectionUtils.isBoxChecked
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.input.InputEventAPI
import com.fs.starfarer.api.ui.LabelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.UIComponentAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaUI.elements.LunaElement
import org.lwjgl.opengl.GL11

class jdp_PresetFactionElement(var presetID: String, var plugins: HashMap<String, jdp_ColorWheelPlugin>, tooltip: TooltipMakerAPI, width: Float, height: Float) : LunaElement(tooltip, width, height) {
    var displaySize = 20f / 2f;
    var gap = 3f;

    var baseUIColor = getColor(presetID, jdp_FactionUtils.baseUIColorKey)!!
    var gridUIColor = getColor(presetID, jdp_FactionUtils.gridUIColorKey)!!
    var brightUIColor = getColor(presetID, jdp_FactionUtils.brightUIColorKey)!!
    var darkUIColor = getColor(presetID, jdp_FactionUtils.darkUIColorKey)!!

    var displayY = y
    var baseUIColorX = x
    var gridUIColorX = x
    var brightUIColorX = x
    var darkUIColorX = x

    var label: LabelAPI? = null;

    init {
        recreateLabel()
        var saveElement = jdp_DisplayElement(
            Global.getSettings().getSprite("graphics/icons/skills/jdp_save.png"),
            "",
            Misc.getBasePlayerColor(),
            innerElement,
            displaySize * 2,
            displaySize * 2
        )
        saveElement.position.inTR(gap, height / 2 - displaySize)
        saveElement.onClick {
            baseUIColor = plugins.get(jdp_FactionUtils.baseUIColorKey)!!.currentColor
            gridUIColor = plugins.get(jdp_FactionUtils.gridUIColorKey)!!.currentColor
            brightUIColor = plugins.get(jdp_FactionUtils.brightUIColorKey)!!.currentColor
            darkUIColor = plugins.get(jdp_FactionUtils.darkUIColorKey)!!.currentColor

            backgroundColor = darkUIColor
            borderColor = gridUIColor

            setColor(presetID, jdp_FactionUtils.baseUIColorKey, plugins.get(jdp_FactionUtils.baseUIColorKey)!!.currentColor)
            setColor(presetID, jdp_FactionUtils.gridUIColorKey, plugins.get(jdp_FactionUtils.gridUIColorKey)!!.currentColor)
            setColor(presetID, jdp_FactionUtils.brightUIColorKey, plugins.get(jdp_FactionUtils.brightUIColorKey)!!.currentColor)
            setColor(presetID, jdp_FactionUtils.darkUIColorKey, plugins.get(jdp_FactionUtils.darkUIColorKey)!!.currentColor)

            recreateLabel()
        }

        enableTransparency = true
        renderBackground = true
        renderBorder = true

        backgroundColor = darkUIColor
        borderColor = gridUIColor
    }

    fun recreateLabel() {
        if(label != null) innerElement.removeComponent(label as UIComponentAPI)

        var name = capitaliseFirst(presetID)

        label = innerElement.createLabel(name, brightUIColor, width - ((((displaySize * 2) + gap) * 5) + gap * 3));
        innerElement.addCustom(label as UIComponentAPI?, 10f)
        label!!.position.inTL((((displaySize * 2) + gap) * 4) + gap * 2, height / 2 - label!!.computeTextHeight(label!!.text) / 2)
    }

    override fun onClick(input: InputEventAPI) {
        playClickSound()

        if(isInDisplayBounds(input.x, input.y, baseUIColorX, displayY)) {
            plugins.get(jdp_FactionUtils.baseUIColorKey)!!.setCurrentColor(baseUIColor)
        } else if(isInDisplayBounds(input.x, input.y, gridUIColorX, displayY)) {
            plugins.get(jdp_FactionUtils.gridUIColorKey)!!.setCurrentColor(gridUIColor)
        } else if(isInDisplayBounds(input.x, input.y, darkUIColorX, displayY)) {
            plugins.get(jdp_FactionUtils.darkUIColorKey)!!.setCurrentColor(darkUIColor)
        } else if(isInDisplayBounds(input.x, input.y, brightUIColorX, displayY)) {
            plugins.get(jdp_FactionUtils.brightUIColorKey)!!.setCurrentColor(brightUIColor)
        } else {
            Global.getSector().getPersistentData().set(presetKey, presetID)

            plugins.get(jdp_FactionUtils.baseUIColorKey)!!.setDefaultColor(baseUIColor)
            plugins.get(jdp_FactionUtils.gridUIColorKey)!!.setDefaultColor(gridUIColor)
            plugins.get(jdp_FactionUtils.darkUIColorKey)!!.setDefaultColor(darkUIColor)
            plugins.get(jdp_FactionUtils.brightUIColorKey)!!.setDefaultColor(brightUIColor)

            if(isBoxChecked(AUTOGENERATE_CHECKED, true)) {
                plugins.get(jdp_FactionUtils.baseUIColorKey)!!.setCurrentColor(baseUIColor)
                plugins.get(jdp_FactionUtils.gridUIColorKey)!!.setCurrentColor(gridUIColor)
                plugins.get(jdp_FactionUtils.darkUIColorKey)!!.setCurrentColor(darkUIColor)
                plugins.get(jdp_FactionUtils.brightUIColorKey)!!.setCurrentColor(brightUIColor)
            }
        }
        super.onClick(input)
    }

    fun isInDisplayBounds(mouseX: Int, mouseY: Int , centreX: Float, centreY: Float): Boolean {
        return mouseX >= centreX - displaySize && mouseX <= centreX + displaySize &&
                mouseY >= centreY - displaySize && mouseY <= centreY + displaySize
    }

    override fun render(alphaMult: Float) {
        super.render(alphaMult)
    }

    override fun renderBelow(alphaMult: Float) {
        super.renderBelow(alphaMult)
        var posX = x + (displaySize) + 3f;
        var posY = y + height / 2
        displayY = posY

        // Draw base color display
        GL11.glColor4f(0f, 0f, 0f, alphaMult);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(posX - displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY + displaySize);
        GL11.glVertex2f(posX - displaySize, posY + displaySize);
        GL11.glEnd();

        GL11.glColor4f(baseUIColor.red / 255f, baseUIColor.green / 255f, baseUIColor.blue / 255f, alphaMult * (baseUIColor.alpha / 255f));
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(posX - displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY + displaySize);
        GL11.glVertex2f(posX - displaySize, posY + displaySize);
        GL11.glEnd();

        GL11.glColor4f(0f, 0f, 0f, alphaMult);
        GL11.glLineWidth(1f);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(posX - displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY + displaySize);
        GL11.glVertex2f(posX - displaySize, posY + displaySize);
        GL11.glEnd();
        baseUIColorX = posX
        posX = posX + (displaySize * 2) + gap

        // Draw bright color display
        GL11.glColor4f(0f, 0f, 0f, alphaMult);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(posX - displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY + displaySize);
        GL11.glVertex2f(posX - displaySize, posY + displaySize);
        GL11.glEnd();

        GL11.glColor4f(brightUIColor.red / 255f, brightUIColor.green / 255f, brightUIColor.blue / 255f, alphaMult * (brightUIColor.alpha / 255f));
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(posX - displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY + displaySize);
        GL11.glVertex2f(posX - displaySize, posY + displaySize);
        GL11.glEnd();

        GL11.glColor4f(0f, 0f, 0f, alphaMult);
        GL11.glLineWidth(1f);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(posX - displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY + displaySize);
        GL11.glVertex2f(posX - displaySize, posY + displaySize);
        GL11.glEnd();
        brightUIColorX = posX
        posX = posX + (displaySize * 2) + gap

        // Draw grid color display
        GL11.glColor4f(0f, 0f, 0f, alphaMult);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(posX - displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY + displaySize);
        GL11.glVertex2f(posX - displaySize, posY + displaySize);
        GL11.glEnd();

        GL11.glColor4f(gridUIColor.red / 255f, gridUIColor.green / 255f, gridUIColor.blue / 255f, alphaMult * (gridUIColor.alpha / 255f));
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(posX - displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY + displaySize);
        GL11.glVertex2f(posX - displaySize, posY + displaySize);
        GL11.glEnd();

        GL11.glColor4f(0f, 0f, 0f, alphaMult);
        GL11.glLineWidth(1f);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(posX - displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY + displaySize);
        GL11.glVertex2f(posX - displaySize, posY + displaySize);
        GL11.glEnd();
        gridUIColorX = posX
        posX = posX + (displaySize * 2) + gap

        // Draw dark color display
        GL11.glColor4f(0f, 0f, 0f, alphaMult);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(posX - displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY + displaySize);
        GL11.glVertex2f(posX - displaySize, posY + displaySize);
        GL11.glEnd();

        GL11.glColor4f(darkUIColor.red / 255f, darkUIColor.green / 255f, darkUIColor.blue / 255f, alphaMult * (darkUIColor.alpha / 255f));
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(posX - displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY + displaySize);
        GL11.glVertex2f(posX - displaySize, posY + displaySize);
        GL11.glEnd();

        GL11.glColor4f(0f, 0f, 0f, alphaMult);
        GL11.glLineWidth(1f);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(posX - displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY - displaySize);
        GL11.glVertex2f(posX + displaySize, posY + displaySize);
        GL11.glVertex2f(posX - displaySize, posY + displaySize);
        GL11.glEnd();
        darkUIColorX = posX

        //Save element border
        GL11.glColor4f(0f, 0f, 0f, alphaMult);
        GL11.glLineWidth(1f);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(x + width - (gap + displaySize) - displaySize, posY - displaySize);
        GL11.glVertex2f(x + width - (gap + displaySize) + displaySize, posY - displaySize);
        GL11.glVertex2f(x + width - (gap + displaySize) + displaySize, posY + displaySize);
        GL11.glVertex2f(x + width - (gap + displaySize) - displaySize, posY + displaySize);
        GL11.glEnd();
    }
}