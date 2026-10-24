package Jaydee8652.JaydeeProficiencies.specialists.tooltips;


import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_Specialist;
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_SkillWidgetElement;
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_SpecialistPickerElement;
import Jaydee8652.JaydeeProficiencies.utils.refl.ReflectionUtilis;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.CharacterStats;
import com.fs.starfarer.loading.SkillSpec;
import com.fs.starfarer.ui.impl.StandardTooltipV2;

import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_MENTORSHIP;
import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_ROOT_SECTION;
import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.*;
import static Jaydee8652.JaydeeProficiencies.utils.refl.ui.ClassRefs.*;

public class SpecialistSkillTooltipCreator {
    //Tooltips for skills in the hire display panel, at this stage the specialist doesn't exist, so their level and active skills do not matter
    public void createDecorativeTooltip(jdp_SkillWidgetElement skillElement, String id, Float skillMaxLevel, PersonAPI person) {
        var spec = Global.getSettings().getSkillSpec(id);
        var elementPanel = skillElement.getElementPanel();

        var tooltip = (StandardTooltipV2) invokeCreateSkillTooltip(spec, person.getStats());
/*
        ButtonAPI fakeUIElement = Global.getSettings().createCustom(0f, 0f, null).createUIElement(0f, 0f, false).addButton("", "", 0f, 0f, 0f);

        Object setTooltip = ReflectionUtilis.getMethod("setTooltip", fakeUIElement.getClass());
        Object setTooltipPositionRelativeToAnchor = ReflectionUtilis.getMethod("setTooltipPositionRelativeToAnchor", fakeUIElement.getClass());
        Object setSize = ReflectionUtilis.getMethod("setSize", tooltip.getClass());
        Object addComponent = ReflectionUtilis.getMethod("addComponent", tooltip.getClass());
*/
        tooltip.setBeforeShowing(() -> {
            var panel = Global.getSettings().createCustom(tooltip.getWidth(), 0f, null);
            TooltipMakerAPI main = panel.createUIElement(tooltip.getWidth(), 0f, false);
            main.setParaFont(Fonts.DEFAULT_SMALL);

            if(skillElement.getSection().getSkillItemMap().containsKey(id)) {
                var item = skillElement.getSection().getSkillItemMap().get(id);
                if (item.getSize() == 1) {
                    main.addPara("Requires item: " + item.getDisplayName(), Misc.getNegativeHighlightColor(), 3f);
                } else {
                    main.addPara("Requires items: " + item.getDisplayName() + " x" + Math.round(item.getSize()), Misc.getNegativeHighlightColor(), 3f);
                }
            }

            var addedS = "";
            if (skillElement.getSection().getPreviousSkillsRequirement() >= 2) addedS = "s";

            if (skillElement.getSection().getPreviousSkillsRequirement() > 0) {
                main.addPara("Requires at least " + skillElement.getSection().getPreviousSkillsRequirement() + " lower tier skill" + addedS, Misc.getNegativeHighlightColor(), 3f);
            } else if (isBaseSpecialistPerson(person)) {
                main.addPara("An apprentice must be assigned before their skills can be selected", Misc.getNegativeHighlightColor(), 3f);
            }
            if (skillElement.getSection().isMutuallyExclusive()) {
                main.addPara("Only one skill in this section can be active at a time", Misc.getNegativeHighlightColor(), 3f);
            }

            var gap = 10f;
            var height = main.getHeightSoFar();
            if(main.getHeightSoFar() != 0f) height += gap;
            panel.addUIElement(main).inBL(12f, 6f);
            try {
                ReflectionUtilis.invokeMethodDirectly(standardTooltipV2SetSize, tooltip, tooltip.getWidth(), tooltip.getHeight() + height);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            try {
                PositionAPI pos = (PositionAPI) ReflectionUtilis.invokeMethodDirectly(standardTooltipV2AddComponent, tooltip, panel);
                pos.inBL(0f, 0f);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }

            //tooltip.setSize(tooltip.getWidth(), tooltip.getHeight() + height);
            //tooltip.addComponent(panel).inBL(0f, 0f);

            var children = tooltip.getChildrenNonCopy();
            for (UIComponentAPI child : children) {
                if (child instanceof LabelAPI codexLabel) {
                    if (codexLabel.getText().contains("F2 to open Codex") || codexLabel.getText().contains("F2 open codex") || codexLabel.getText().contains("F2 codex")) {
                        codexLabel.getPosition().inBL(13f, -18f);
                    }
                }
            }

            //Avoids accumulation if the codex tooltip, which gets re-added every frame if the game detects it has been futzed with for whatever reason
            if (children.size() > 3f) children.remove(children.size() - 1);
            //Set below
            try {
                ReflectionUtilis.invokeMethodDirectly(buttonSetTooltipPositionRelativeToAnchorMethod, elementPanel, -2.0F, -tooltip.getHeight(), elementPanel);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        try {
            ReflectionUtilis.invokeMethodDirectly(buttonSetTooltipMethod, elementPanel, 0f, tooltip);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    //Tooltips for real skills for a real specialist, assumes that the specialists level and active skills matter
    public void createTooltip(jdp_SkillWidgetElement skillElement, String id, Float skillMaxLevel, jdp_Specialist specialist) {
        var spec = Global.getSettings().getSkillSpec(id);
        var elementPanel = skillElement.getElementPanel();

        int requiredSkillPoints = skillElement.getSection().getPreviousSkillsRequirement() - specialist.sectionSkills(skillElement.getSection().getPreviousSections()).size();

        StandardTooltipV2 tooltip = (StandardTooltipV2) invokeCreateSkillTooltip(spec, specialist.getPerson().getStats());

        tooltip.setBeforeShowing(() -> {
            var panel = Global.getSettings().createCustom(tooltip.getWidth(), 0f, null);
            TooltipMakerAPI main = panel.createUIElement(tooltip.getWidth(), 0f, false);
            main.setParaFont(Fonts.DEFAULT_SMALL);

            if(specialist.hasSkill(id)) {
                if(skillElement.getSection().getSkillItemMap().containsKey(id)) {
                    var item = skillElement.getSection().getSkillItemMap().get(id);
                    if (item.getSize() == 1) {
                        main.addPara("Using item: " + item.getDisplayName(), Misc.getPositiveHighlightColor(), 3f);
                    } else {
                        main.addPara("Using items: " + item.getDisplayName() + " x" + Math.round(item.getSize()), Misc.getPositiveHighlightColor(), 3f);
                    }
                }

                if (skillElement.getSection().isTierLocked() && !skillElement.getSection().getSectionId().equals(JDP_ROOT_SECTION)) {
                    main.addPara("Higher tier skills must be unassigned before this skill can be unassigned", Misc.getNegativeHighlightColor(), 3f);
                }
                if (id.equals(JDP_MENTORSHIP) && !specialist.getMentee().equals(getBaseSpecialistPerson().getId())) {
                    main.addPara("This skill cannot be unassigned while " + specialist.person().getName().getFullName() + " has an apprentice", Misc.getNegativeHighlightColor(), 3f);
                }
                if (specialist.getSkillLevel(id) < skillMaxLevel && specialist.getSkillLevel(id) != 0f) {
                    if (Global.getSector().getPlayerPerson().getStats().getStoryPoints() < 1f) {
                        main.addPara("Requires a story point to make elite", Misc.getNegativeHighlightColor(), 3f);
                    } else {
                        main.addPara("Spend a story point to make elite (" + Global.getSector().getPlayerPerson().getStats().getStoryPoints() + " available)", 3f, Misc.getGrayColor(), Misc.getStoryOptionColor(), "story point", Global.getSector().getPlayerPerson().getStats().getStoryPoints() + "");
                    }
                }
            } else {
                if(skillElement.getSection().getSkillItemMap().containsKey(id)) {
                    var item = skillElement.getSection().getSkillItemMap().get(id);

                    var color = Misc.getNegativeHighlightColor();
                    if(itemAvailable(item)) color = Misc.getPositiveHighlightColor();
                    if (item.getSize() == 1) {
                        main.addPara("Requires item: " + item.getDisplayName() + " (" + getNumAvailable(item) + " available)", color, 3f);
                    } else {
                        main.addPara("Requires items: " + item.getDisplayName() + " x" + Math.round(item.getSize()) + " (" + getNumAvailable(item) + " available)", color, 3f);
                    }
                }

                if (requiredSkillPoints > 0) {
                    var addedS = "";
                    if (requiredSkillPoints >= 2) addedS = "s";
                    main.addPara("Requires at least " + requiredSkillPoints + " more lower tier skill" + addedS, Misc.getNegativeHighlightColor(), 3f);
                } else if (isBaseSpecialistPerson(specialist.person())) {
                    main.addPara("An apprentice must be assigned before their skills can be selected", Misc.getNegativeHighlightColor(), 3f);
                } else if (skillElement.getSection().isMutuallyExclusive() && specialist.getLevel() - specialist.activeSkills().size() < 1 && skillElement.getSection().getSkillElements().stream().noneMatch(jdp_SkillWidgetElement::getActivated)) {
                    main.addPara("Requires a skill point to learn", Misc.getNegativeHighlightColor(), 3f);
                } else if (!skillElement.getSection().isMutuallyExclusive() && specialist.getLevel() - specialist.activeSkills().size() < 1) {
                    main.addPara("Requires a skill point to learn", Misc.getNegativeHighlightColor(), 3f);
                }
            }

            if (skillElement.getSection().isMutuallyExclusive()) {
                main.addPara("Only one skill in this section can be active at a time", Misc.getNegativeHighlightColor(), 3f);
            }
            if (specialist.person() != getBaseSpecialistPerson() && isAbsent(specialist)) {
                main.addPara("Skill cannot be changed while " + specialist.person().getName().getFullName() + " is not present", Misc.getNegativeHighlightColor(), 3f);
            }

            var gap = 10f;
            var height = main.getHeightSoFar();
            if(main.getHeightSoFar() != 0f) height += gap;
            panel.addUIElement(main).inBL(12f, 6f);

            try {
                ReflectionUtilis.invokeMethodDirectly(standardTooltipV2SetSize, tooltip, tooltip.getWidth(), tooltip.getHeight() + height);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            try {
                PositionAPI pos = (PositionAPI) ReflectionUtilis.invokeMethodDirectly(standardTooltipV2AddComponent, tooltip, panel);
                pos.inBL(0f, 0f);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            //tooltip.setSize(tooltip.getWidth(), tooltip.getHeight() + height);
            //tooltip.addComponent(panel).inBL(0f, 0f);

            var children = tooltip.getChildrenNonCopy();
            for (UIComponentAPI child : children) {
                if (child instanceof LabelAPI codexLabel) {
                    if (codexLabel.getText().contains("F2 to open Codex") || codexLabel.getText().contains("F2 open codex") || codexLabel.getText().contains("F2 codex")) {
                        codexLabel.getPosition().inBL(13f, -18f);

                    }
                }
            }
            //Avoids accumulation if the codex tooltip, which gets re-added every frame if the game detects it has been futzed with for whatever reason
            if (children.size() > 3f) children.remove(children.size() - 1);
            //Set below
            try {
                ReflectionUtilis.invokeMethodDirectly(buttonSetTooltipPositionRelativeToAnchorMethod, elementPanel, -2.0F, -tooltip.getHeight(), elementPanel);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        try {
            ReflectionUtilis.invokeMethodDirectly(buttonSetTooltipMethod, elementPanel, 0f, tooltip);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}