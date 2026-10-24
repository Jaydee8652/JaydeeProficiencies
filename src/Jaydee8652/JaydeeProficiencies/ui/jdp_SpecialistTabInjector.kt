package Jaydee8652.JaydeeProficiencies.ui

import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_PanelData
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_RenderElement
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistScreen.getCharacterPanel
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistScreen.initialise
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.DONT_SHOW_EXPO_AGAIN
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_SPECIALIST_TAB_OPEN
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getBaseSpecialistPerson
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getHeight
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getSpecialistData
import Jaydee8652.JaydeeProficiencies.utils.refl.ReflectionUtilis
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.ClassRefs
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.ClassRefs.buttonSetTooltipMethod
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.ClassRefs.buttonSetTooltipPositionRelativeToAnchorMethod
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.ListenerFactory.ActionListener
import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CoreUITabId
import com.fs.starfarer.api.ui.UIPanelAPI
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIPanel
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIReflectionUtils
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIReflectionUtils.CONFIRM_DIALOG_HEIGHT
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIReflectionUtils.CONFIRM_DIALOG_WIDTH
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIReflectionUtils.createTextTooltip
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIReflectionUtils.getCoreUI
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIReflectionUtils.isBoxChecked
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIReflectionUtils.showExpositionDialog
import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.CutStyle
import com.fs.starfarer.api.util.FaderUtil
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.ui.impl.StandardTooltipV2Expandable
import com.fs.starfarer.ui.interfacenew
import org.lazywizard.console.overlay.v2.misc.getParent
import org.lazywizard.console.overlay.v2.misc.getWidth
import org.lwjgl.input.Keyboard

class jdp_SpecialistTabInjector : EveryFrameScript {
    class jdp_SpecialistTabTrigger: ActionListener() {
        override fun trigger(vararg args: Any?) {
            Global.getSector().memoryWithoutUpdate.set(JDP_SPECIALIST_TAB_OPEN, true)
            /*
            if (Global.getSettings().getModManager().isModEnabled("second_in_command")) {
                createSiCPanel()
            } else {*/

            var panel = getCharacterPanel()
            var parent = panel?.getParent() ?: return
            parent.removeComponent(panel)

            var width = parent.getWidth()
            var height = parent.getHeight()

            var newPanel = Global.getSettings().createCustom(width, height, null)
            parent.addComponent(newPanel)

            initialise(jdp_PanelData(newPanel, width, height))
        }
    }

    var sliderSpeedIn = 4f;
    var sliderSpeedOut = 4f;
    var slider = FaderUtil(0f, 1 / sliderSpeedIn, 1 / sliderSpeedOut, false, false);

    var buttonWidth = 123f
    var buttonHeight = 23f

    var decor: CustomPanelAPI? = null
    var decorWidth = 280f;
    var decorHeight = 32f;

    var person: PersonAPI = getBaseSpecialistPerson()

    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return true
    }

    fun addSpecialistButton() {
        if(getSpecialistData().getMentors().isEmpty) return
        var trigger = jdp_SpecialistTabTrigger();
        if(decor == null) {
            decor = Global.getSettings().createCustom(decorWidth, decorHeight, null);
            var tooltipMaker = decor!!.createUIElement(decorWidth, decorHeight, false);
            decor!!.addUIElement(tooltipMaker);

            var sprite = "jdp_tripad_bot_left_decor_extended"
            if (Global.getSettings().getModManager().isModEnabled("aotd_ashpad")) sprite = "jdp_tripad_bot_left_decor_extended_ash"
            var display = jdp_RenderElement(Global.getSettings().getSprite(Global.getSettings().getSpriteName("ui", sprite)), tooltipMaker, decorWidth, decorHeight);
            display.position.inTL(0f, 0f);

            var button = UIReflectionUtils.makeButton(
                "Specialists",
                trigger,
                Misc.getBasePlayerColor(),
                Misc.getDarkPlayerColor(),
                Alignment.MID,
                CutStyle.ALL,
                buttonWidth,
                buttonHeight,
                Keyboard.KEY_S,
                false
            );

            var tooltip = createTextTooltip(390.0f, "Third party Tri-Pad extension provided by JaydeeProficiencies", 0f, Misc.getGrayColor(), Misc.getBasePlayerColor(), "JaydeeProficiencies")
            ReflectionUtilis.invokeMethodDirectly(buttonSetTooltipMethod, button.instance, 0f, tooltip);
            ReflectionUtilis.invokeMethodDirectly(buttonSetTooltipPositionRelativeToAnchorMethod, button.instance, -2.0f, tooltip.getHeight() + (buttonHeight / 2f), button.instance);

            var pos = UIPanel(decor).add(button)
            pos.instance.inTL(13f, 6f)

            var main = ReflectionUtilis.getPrivateVariable("screenPanel", Global.getSector().getCampaignUI())?: return
            main = main as UIPanelAPI
            main.addComponent(decor)

            decor!!.position.inBL((slider.brightness * (decorWidth)) - decorWidth, 34f)
            slider.state = FaderUtil.State.IN
        }
    }

    fun removeSpecialistButton() {
        if(decor != null) {
            slider.state = FaderUtil.State.OUT
            if(slider.brightness <= 0.4) {
                var main = ReflectionUtilis.getPrivateVariable("screenPanel", Global.getSector().getCampaignUI())?: return
                main = main as UIPanelAPI
                main.removeComponent(decor)

                slider = FaderUtil(0f, 1 / sliderSpeedIn, 1 / sliderSpeedOut, false, false);
                decor = null
                return
            }
        }
    }

    var previousTopAncestor: Any? = null;
    override fun advance(amount: Float) {
        var main = ReflectionUtilis.getPrivateVariable("screenPanel", Global.getSector().getCampaignUI()) ?: return
        main = main as UIPanelAPI

        slider.advance(amount)
        if (decor != null) decor!!.position.inBL((slider.brightness * (decorWidth)) - decorWidth, 34f)

        if (Global.getSector().campaignUI.currentCoreTab == CoreUITabId.CHARACTER) {
            addSpecialistButton()

            var currentTab = ReflectionUtilis.invokeMethodDirectly(ClassRefs.coreUIgetCurrentTabMethod, getCoreUI());
            if (currentTab == null) return;

            var topAncestor = ReflectionUtilis.invokeMethodDirectly(ClassRefs.uiPanelfindTopAncestorMethod, currentTab)
            if (previousTopAncestor != topAncestor) removeSpecialistButton()
            previousTopAncestor = topAncestor
        } else {
            removeSpecialistButton()
        }
        if (Global.getSector().getMemoryWithoutUpdate().contains(JDP_SPECIALIST_TAB_OPEN)) {
            removeSpecialistButton()
        }
    }
}