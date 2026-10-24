package Jaydee8652.JaydeeProficiencies.specialists.plugins;

import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_PanelData;
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_Specialist;
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_SpecialistData;
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_DisplayElement;
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_SpecialistPickerElement;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.List;

import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_NEW_SPECIALIST;
import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.applyPickerOnClick;
import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getSpecialistData;

public class jdp_SpecialistTabsComponent implements jdp_ExtendUIPanelPlugin {
    private float calWidth;
    public CustomPanelAPI mainPanel;
    CustomPanelAPI dummy;
    jdp_RightMouseTooltipMoverV2 mover;
    TooltipMakerAPI tooltip;
    CustomPanelAPI content;
    jdp_RightMouseInterceptor interceptor = new jdp_RightMouseInterceptor();
    ButtonAPI left, right;
    jdp_PanelData panelData;

    public jdp_SpecialistTabsComponent(jdp_PanelData panelData) {
        mainPanel = Global.getSettings().createCustom(panelData.width(), 70f, this);
        interceptor = new jdp_RightMouseInterceptor();
        this.panelData = panelData;
        init();
    }

    public void init() {
        dummy = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), null);
        CustomPanelAPI blocker = Global.getSettings().createCustom(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), interceptor);
        tooltip = mainPanel.createUIElement(mainPanel.getPosition().getWidth(), mainPanel.getPosition().getHeight(), false);//true

        tooltip.addSpacer(mainPanel.getPosition().getHeight() * 2);
        createUI();

        dummy.getPosition().setSize(Math.max(content.getPosition().getWidth(), mainPanel.getPosition().getWidth()), dummy.getPosition().getHeight());

        tooltip.addCustom(dummy, 0f).getPosition().inTL(0, 0);

        mainPanel.addUIElement(tooltip).inTL(0, 0);
        interceptor.setPanelPos(blocker);
        mainPanel.addComponent(blocker).inTL(0, 0);

        mover = new jdp_RightMouseTooltipMoverV2();
        mover.init(dummy, mainPanel);
        tooltip.setHeightSoFar(0f);
        mover.setBorders(-dummy.getPosition().getWidth() + mainPanel.getPosition().getWidth(), 0);
        mover.setCurrOffset(0f);
    }

    public void createUI() {
        if (content != null) dummy.removeComponent(content);
        float gap = 6f;
        var pickerSize = 64f;

        jdp_SpecialistData data = getSpecialistData();
        List<jdp_Specialist> activeSpecialists = data.getMentors();

        //INFLATE LIST
/*
        jdp_Specialist generic = activeSpecialists.get(0);
        while (activeSpecialists.size() <= 60) {
            activeSpecialists.add(generic);
        }
*/
        calWidth = ((pickerSize + gap) * activeSpecialists.size()) - gap;

        //Not currently necessary, may be reimplemented if the framework demands it.
        /*if(calWidth > width) {
            left = tooltip.addButton("<<", "<<", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.NONE, 15, 10f, 0f);
            right = tooltip.addButton(">>", ">>", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.NONE, 15, 10f, 0f);
            left.getPosition().inBL(10f, 0f);
            right.getPosition().inBR(10f, 0f);
        }*/

        content = Global.getSettings().createCustom(calWidth, dummy.getPosition().getHeight(), null);
        TooltipMakerAPI element = content.createUIElement(calWidth, content.getPosition().getHeight(), false);

        CustomPanelAPI previous = null;
        for (jdp_Specialist specialist : activeSpecialists) {
            Color color = specialist.getProficiencySpec().getColor();
            jdp_SpecialistPickerElement specialistPickerElement = new jdp_SpecialistPickerElement(specialist.getPerson(), color, element, pickerSize, pickerSize);
            applyPickerOnClick(specialistPickerElement, specialist, this.panelData);

            if (previous != null) {
                specialistPickerElement.getPosition().rightOfTop(previous, gap);
            } else {
                specialistPickerElement.getPosition().inTL(0f, 0f);
            }
            previous = specialistPickerElement.getElementPanel();

            if (specialist.person().getMemoryWithoutUpdate().contains(JDP_NEW_SPECIALIST)) {
                jdp_DisplayElement newSpecialistIconElement = new jdp_DisplayElement(Global.getSettings().getSprite("graphics/icons/skills/jdp_iconnotification.png"), "", Misc.getTextColor(), element, pickerSize / 4f, pickerSize / 4f);
                newSpecialistIconElement.setHasFlash(true);
                newSpecialistIconElement.addTooltip(specialist.getPerson().getName().getFullName() + " is new!", 200f, TooltipMakerAPI.TooltipLocation.ABOVE);
                applyPickerOnClick(newSpecialistIconElement, specialist, this.panelData);

                newSpecialistIconElement.getPosition().rightOfTop(previous, -pickerSize);
            }
        }

        content.addUIElement(element).inTL(0, 0);
        dummy.addComponent(content).inTL(0, 0);
    }

    @Override
    public CustomPanelAPI getMainPanel() {
        return mainPanel;
    }

    @Override
    public void positionChanged(PositionAPI position) {
    }

    @Override
    public void renderBelow(float alphaMult) {
    }

    @Override
    public void render(float alphaMult) {
    }

    @Override
    public void advance(float amount) {

        mover.advance(amount);

        if (left != null) {
            if (mover.isMoving() && left.isEnabled()) {
                left.setEnabled(false);
            } else if (!mover.isMoving()) {
                left.setEnabled(true);
            }
            if (!mover.isMoving()) {
                if (left.isChecked()) {
                    left.setChecked(false);
                    mover.moveBy(calWidth);
                }
            }
        }
        if (right != null) {
            if (mover.isMoving() && right.isEnabled()) {
                right.setEnabled(false);

            } else if (!mover.isMoving()) {
                right.setEnabled(true);
            }
            if (!mover.isMoving()) {
                if (right.isChecked()) {
                    right.setChecked(false);
                    mover.moveBy(-calWidth);
                }
            }
        }
    }

    @Override
    public void processInput(List<InputEventAPI> events) {
    }

    @Override
    public void buttonPressed(Object buttonId) {}
}
