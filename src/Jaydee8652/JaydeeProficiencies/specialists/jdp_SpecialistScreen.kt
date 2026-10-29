package Jaydee8652.JaydeeProficiencies.specialists


import Jaydee8652.JaydeeProficiencies.specialists.data.SpecialistSkillSection
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_PanelData
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_Specialist
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_SpecialistData
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_DisplayElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_ExperienceBarElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_HomeElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_LineElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_SkillGapElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_SkillSeperatorElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_SkillWidgetElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_SpecialistBackgroundElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_SpecialistPickerElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_TypeElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_UnderlineElement
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_DEFAULT_SPECIALIST
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_DUMMY_PROFICIENCY
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_MENTORSHIP
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_NEW_SPECIALIST
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_ROOT_SECTION
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_UNAPPLIED_MARKER
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.clearChildren
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.consumeItem
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.createFakeSkill
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getBaseSpecialistPerson
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getOpenSpecialist
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getSpecialistData
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.invokeCreateSkillTooltip
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.isAbsent
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.isBaseSpecialistPerson
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.itemAvailable
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.itemEquals
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.returnItem
import Jaydee8652.JaydeeProficiencies.specialists.plugins.jdp_SpecialistTabsComponent
import Jaydee8652.JaydeeProficiencies.specialists.tooltips.ExperienceBarTooltipCreator
import Jaydee8652.JaydeeProficiencies.specialists.tooltips.SpecialistSkillTooltipCreator
import Jaydee8652.JaydeeProficiencies.specialists.tooltips.SpecialistTooltipCreator
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_IntelUtils.*
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_IntelUtils.confirmApprenticeAssignment
import Jaydee8652.JaydeeProficiencies.utils.jdp_SpecStore
import Jaydee8652.JaydeeProficiencies.utils.jdp_SpecStore.jdp_proficiencySpec
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.characters.AdminData
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaExtensions.addLunaElement
import java.util.ArrayList
import Jaydee8652.JaydeeProficiencies.utils.refl.*;
import com.fs.starfarer.api.campaign.CoreUITabId
import com.fs.starfarer.api.ui.TooltipMakerAPI.TooltipLocation
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.campaign.CampaignState
import com.fs.state.AppDriver
import lunalib.lunaUI.elements.LunaElement
import org.lazywizard.console.overlay.v2.misc.getChildrenCopy
import org.lazywizard.console.overlay.v2.misc.getParent
import org.lazywizard.console.overlay.v2.misc.getWidth

object jdp_SpecialistScreen {
    @JvmStatic
    fun initialise(panelData: jdp_PanelData) {
        var width = panelData.width()
        var height = panelData.height()
        var panel = panelData.panel()

        val main = panel.createUIElement(width, height, false) //TODO: Maybe allow to scroll?

        var tabsPanel = Global.getSettings().createCustom(width - PICKER_PANEL_OUTER_GAP * 2, 70f, null)
        var displayPanel = Global.getSettings().createCustom(width - PICKER_PANEL_OUTER_GAP * 2, height - 75f, null)

        var displayPanelData = jdp_PanelData(displayPanel, width - PICKER_PANEL_OUTER_GAP * 2, height - 75f)

        main.addCustom(tabsPanel, 0f)
        tabsPanel.position.inTL(PICKER_PANEL_OUTER_GAP, PICKER_PANEL_OUTER_GAP);

        var component = jdp_SpecialistTabsComponent(displayPanelData);
        tabsPanel.addComponent(component.getMainPanel()).inTL(0f, 0f);

        main.addCustom(displayPanel, 0f)
        displayPanel.position.belowLeft(tabsPanel, 0f)

        recreateMain(displayPanelData)
        panel.addUIElement(main).inTL(0f, 0f)
    }

    @JvmStatic
    fun recreateMain(panelData: jdp_PanelData) {
        var panel = panelData.panel()
        var width = panelData.width()
        var height = panelData.height()

        var person = getOpenSpecialist()
        person.getMemoryWithoutUpdate().unset(JDP_NEW_SPECIALIST);
        panel.clearChildren()

        var data: jdp_SpecialistData = getSpecialistData()

        var mentor: jdp_Specialist = data.getSpecialist(person.id)!!
        var spec: jdp_proficiencySpec = mentor.getProficiencySpec()

        val element = panel.createUIElement(width, height, false) //TODO: Maybe allow to scroll?

        var top_line = jdp_LineElement(spec.getColorTransparent(), element, width, PICKER_PANEL_OUTER_GAP)
        top_line.position.inTL(0f, 0f)

        element.setTitleOrbitronVeryLarge()
        var title = element.addTitle(spec.getPlugin().createTitle(person, false), spec.color)
        title.position.inTL(PICKER_PANEL_OUTER_GAP, 20f)
        element.addSpacer(20f)

        spec.getPlugin().addDescription(person, element, false)
        element.addSpacer(20f)

        var bottom_line = jdp_LineElement(spec.getColorTransparent(), element, width, 0.5f)
        bottom_line.position.setXAlignOffset(-PICKER_PANEL_OUTER_GAP)

        jdp_LineElement(spec.getColorTransparent(), element, width, 0.5f).x -= PICKER_PANEL_OUTER_GAP
        element.addSpacer(20f)

        var skillHeight = 128f
        var skillPanel = Global.getSettings().createCustom(width, height - (element.heightSoFar + 10f), null)
        addSkillSection(jdp_PanelData(skillPanel, width, skillHeight))

        element.addCustomDoNotSetPosition(skillPanel).position.inTL(0f, element.heightSoFar + 5f + PICKER_PANEL_OUTER_GAP * 2)

        if(spec.mentor.get(0) != "") skillHeight *= 2
        element.addSpacer(skillHeight + 10f)
        jdp_LineElement(spec.getColorTransparent(), element, width, 0.5f).x -= PICKER_PANEL_OUTER_GAP

        var spacer = element.addSpacer(20f)
        spacer.position.setXAlignOffset(PICKER_PANEL_OUTER_GAP)
        spec.getPlugin().addFooter(person, panelData, element)

        panel.addUIElement(element).inTL(0f, 0f)
    }

    fun processSections(sections: List<SpecialistSkillSection>, specialist: jdp_Specialist) {
        var data: jdp_SpecialistData = getSpecialistData()

        var previousSections = ArrayList<SpecialistSkillSection>()

        for (i in 0 until sections.size) {
            var section = sections[i]
            if(sections.size > i + 1) section.nextSection = sections[i + 1]

            section.previousSections.clear()
            section.previousSections.addAll(previousSections)
            previousSections.add(section)

            section.isTierActive = (specialist.sectionSkills(section.previousSections).size >= section.previousSkillsRequirement) && specialist != data.getBaseSpecialist()

            if (!specialist.sectionSkills(section).isEmpty() && specialist.sectionSkills(section.previousSections).size - 1 < section.previousSkillsRequirement) {
                for (previousSection in section.previousSections) previousSection.setLocked(true, section.sectionId)
            } else if (!section.previousSections.isEmpty() && section.previousSections.last().lockedBy == section.sectionId) {
                section.previousSections.last().setLocked(false)
            }
            if (section.sectionId == JDP_ROOT_SECTION) section.setLocked(true, section.sectionId)
        }
    }

    fun addSkillSection(panelData: jdp_PanelData) {
        var skillPanel = panelData.panel()
        var width = panelData.width()
        var height = panelData.height()

        var person = getOpenSpecialist()

        var data: jdp_SpecialistData = getSpecialistData()
        var spec: jdp_proficiencySpec = data.getSpecialist(person.id)!!.getProficiencySpec()

        var mentorElement = skillPanel.createUIElement(width, height, false)
        skillPanel.addUIElement(mentorElement).inTL(0f, 0f)
        recreateSkillSection(panelData, mentorElement, true)

        if(spec.mentor.get(0) != "") {
            var menteeElement = skillPanel.createUIElement(width, height, false)
            skillPanel.addUIElement(menteeElement).belowLeft(mentorElement, 0f)
            recreateSkillSection(panelData, menteeElement, false)
        }
    }


    @JvmStatic
    fun getCharacterPanel(): UIPanelAPI? {
        if (!Global.getSector().isPaused) return null
        if (Global.getSector().campaignUI.currentCoreTab != CoreUITabId.CHARACTER) return null

        var state = AppDriver.getInstance().currentState
        if (state !is CampaignState) return null

        var core: UIPanelAPI? = null

        var dialog = ReflectionUtils.invoke("getEncounterDialog", state)
        if (dialog != null) core = ReflectionUtils.invoke("getCoreUI", dialog) as UIPanelAPI?
        if (core == null) core = ReflectionUtils.invoke("getCore", state) as UIPanelAPI?
        if (core == null) return null

        var corePanels = core.getChildrenCopy().filter { it is UIPanelAPI } as List<UIPanelAPI>
        var innerPanels = corePanels.map { it.getChildrenCopy().find { children -> ReflectionUtils.hasMethodOfName("canReassign", children) } }

        if (Global.getSettings().getModManager().isModEnabled("second_in_command")) {
            corePanels = corePanels.stream().filter { p -> p.getChildrenCopy().size == 1 }.toList()
            innerPanels = corePanels.map { it.getChildrenCopy().find { children -> ReflectionUtilis.getMethods(children).size == 15 } } as List<UIPanelAPI>
            innerPanels = innerPanels.filterNotNull().filter{ p -> p.getChildrenCopy().size == 1 }
        }

        return innerPanels.filterNotNull().lastOrNull() as UIPanelAPI?
    }

    @JvmStatic
    fun recreateSections(panelData: jdp_PanelData) {
        var skillPanel = panelData.panel()

        skillPanel.clearChildren()
        addSkillSection(panelData)
    }

    fun recreateSkillSection(panelData: jdp_PanelData, specialistElement: TooltipMakerAPI, isMentor: Boolean) {
        var skillPanel = panelData.panel()

        var data: jdp_SpecialistData = getSpecialistData()
        var person = getOpenSpecialist()

        var mentor: jdp_Specialist = data.getSpecialist(person.id)!!
        var mentee: jdp_Specialist = data.getSpecialist(mentor.getMentee())!!

        var specialist = mentor
        if (!isMentor) specialist = mentee

        var skillSize = 64f
        var typeSize = 20f
        var pickerSize = 96f//80f

        var sections = mentor.getProficiencyPlugin().getSections(isMentor)
        var spec = mentor.getProficiencySpec()
        var color = spec.color

        var specialistPickerElement = jdp_SpecialistPickerElement(specialist.person, color, specialistElement, pickerSize, pickerSize)
        val underline = jdp_UnderlineElement(color, 2f, specialistElement, pickerSize)
        underline.position.belowLeft(specialistPickerElement.elementPanel, 2f)

        if (isMentor || !mentor.hasSkill(JDP_MENTORSHIP)) specialistPickerElement.isLocked = true
        if (isAbsent(specialist)) specialistPickerElement.isAbsent = true

        var xpBar = jdp_ExperienceBarElement(spec, specialist, specialistElement, 265f, 24f)
        xpBar.elementPanel.position.rightOfTop(specialistPickerElement.elementPanel, 10f)
        specialistElement.addTooltipTo(ExperienceBarTooltipCreator(specialist), xpBar.elementPanel, TooltipLocation.BELOW)
        var previous: CustomPanelAPI? = xpBar.elementPanel

        if(isMentor) {
            for (type in spec.type) {
                var typeDisplay = jdp_TypeElement(type, person, true, specialistElement, typeSize, typeSize)
                typeDisplay.position.rightOfTop(previous, 3f)
                previous = typeDisplay.elementPanel
            }
        } else {
            if (spec.mentor.contains(jdp_SpecialistTags.SPECIALIST_TYPE_OFFICER)) {
                var typeDisplay = jdp_TypeElement(jdp_SpecialistTags.SPECIALIST_TYPE_OFFICER, specialist.person, jdp_SpecialistUtils.getOfficers().contains(specialist.person), specialistElement, typeSize, typeSize)
                typeDisplay.position.rightOfTop(previous, 3f)
                previous = typeDisplay.elementPanel
            }
            if (spec.mentor.contains(jdp_SpecialistTags.SPECIALIST_TYPE_ADMIN)) {
                var typeDisplay = jdp_TypeElement(jdp_SpecialistTags.SPECIALIST_TYPE_ADMIN, specialist.person, jdp_SpecialistUtils.getAdmins().contains(specialist.person), specialistElement, typeSize, typeSize)
                typeDisplay.position.rightOfTop(previous, 3f)
                previous = typeDisplay.elementPanel
            }
            if (spec.mentor.contains(jdp_SpecialistTags.SPECIALIST_TYPE_PLAYER)) {
                var typeDisplay = jdp_TypeElement(jdp_SpecialistTags.SPECIALIST_TYPE_PLAYER, specialist.person, specialist.person == Global.getSector().playerPerson, specialistElement, typeSize, typeSize)
                typeDisplay.position.rightOfTop(previous, 3f)
                previous = typeDisplay.elementPanel
            }
        }
        var name = specialist.person.name.fullName
        var nameColor = Misc.getTextColor()
        if(isBaseSpecialistPerson(specialist.person)) {
            name = "No Apprentice Assigned"
            nameColor = Misc.getGrayColor()
        }

        var namePara = specialistElement.addPara(name, nameColor, 3f)
        namePara.position.rightOfMid(previous, 10f)
        specialistElement.setParaFont("graphics/fonts/victor21.fnt")

        specialistPickerElement.onHoverEnter {
            specialistPickerElement.playScrollSound()
        }

        specialistPickerElement.onClick {
            if (isMentor && !mentor.getProficiencySpec().tags.contains(jdp_SpecialistTags.SPEC_TAG_NO_COMMLINK)) {
                specialistPickerElement.playSound("ui_channel_comm_secure", 1f, 1f)

                Global.getSector().getPlayerMemoryWithoutUpdate().set(jdp_SpecialistTags.SPECIALIST_COMMLINK_ID, person.id, 0f);
                fireRuleFromIntel(jdp_SpecialistTags.SPECIALIST_COMMLINK_TRIGGER)
                return@onClick
            }

            if (specialistPickerElement.isLocked) {
                specialistPickerElement.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
                return@onClick
            }

            if (it.isRMBEvent) {
                specialistPickerElement.playSound("ui_char_decrease_skill", 1f, 1f)

                if(mentee != data.getBaseSpecialist()) {
                    assignApprentice(getBaseSpecialistPerson(), panelData, null)
                }
            } else {
                specialistPickerElement.playClickSound()
                createApprenticePickerPanel(panelData)
            }
        }

        specialistElement.addTooltipTo(SpecialistTooltipCreator(specialistPickerElement, person), specialistPickerElement.elementPanel, TooltipLocation.RIGHT)

        var offsetElement = specialistElement.addLunaElement(0f, 0f)
        offsetElement.elementPanel.position.rightOfTop(specialistPickerElement.elementPanel, 0f)

        var background = jdp_SpecialistBackgroundElement(color, skillPanel.getWidth() - pickerSize + 4, 28f, specialistElement)
        background.elementPanel.position.belowLeft(offsetElement.elementPanel, (pickerSize / 2f) + pickerSize * 0.125f)

        //Offsets the skill widgets
        var offset = LunaElement(specialistElement, 0f, 0f).position.inTL(pickerSize + 16f, ((pickerSize / 2f) - skillSize / 2f) + pickerSize * 0.125f)

        previous = null

        //Add every section to the UI
        var skillDisplays = ArrayList<jdp_SkillWidgetElement>()
        processSections(sections, specialist)

        for (section in sections) {
            var isLastSection = sections.last() == section
            var usedWidth = 0f

            for (skill in section.skills) {
                var skillId = skill.key;
                var skillMaxLevel = skill.value

                var aptitude = jdp_SpecStore.getNormalAptitudeSpec(Global.getSettings().getSkillSpec(skillId).governingAptitudeId)
                var isLast = section.skills.entries.last() == skill

                var skillDisplay = jdp_SkillWidgetElement(skillId, specialist.getSkillLevel(skillId), specialist.hasSkill(skillId), section.isTierActive && (specialist.activeSkills().size < specialist.level), section, specialistElement, skillSize, skillSize)
                usedWidth += skillSize;

                section.skillElements.add(skillDisplay)
                skillDisplays.add(skillDisplay)

                SpecialistSkillTooltipCreator().createTooltip(skillDisplay, skillId, skillMaxLevel, specialist)

                skillDisplay.onClick {
                    it

                    var storyPoints = Global.getSector().playerStats.storyPoints
                    var specialistPoints = specialist.level - specialist.activeSkills().size

                    processSections(sections, specialist)
                    if (!skillDisplay.section!!.isTierActive || specialistPickerElement.isAbsent || (skillId == JDP_MENTORSHIP && mentee != data.getBaseSpecialist())) {
                        skillDisplay.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
                        return@onClick
                    }

                    if (specialist.hasSkill(skillId)) {
                        if (specialist.getSkillLevel(skillId) < skillMaxLevel) {
                            // ----------------------------------------------------- Make Elite
                            if (storyPoints > 0f && !it.isRMBEvent) {
                                Global.getSector().playerStats.storyPoints = (storyPoints - 1) //Take story point

                                skillDisplay.playSound(aptitude!!.sounds.get("sound_story").toString());
                                specialist.addSkill(skillId, skillMaxLevel)

                                recreateSections(panelData)
                            // ----------------------------------------------------- Unassign
                            } else if (!skillDisplay.section!!.isTierLocked) {
                                if (section.skillItemMap.containsKey(skillId)) returnItem(section.skillItemMap.get(skillId)) //Return consumed item
                                skillDisplay.playSound("ui_char_decrease_skill", 1f, 1f)
                                specialist.removeSkill(skillId)

                                recreateSections(panelData)
                            } else {
                                skillDisplay.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
                            }
                        } else {
                            // ----------------------------------------------------- Downgrade elite
                            if (skillMaxLevel != 1f && (it.isRMBEvent || skillDisplay.section!!.isTierLocked)) {
                                Global.getSector().playerStats.storyPoints = (storyPoints + 1) //Return story point

                                skillDisplay.playSound("ui_char_decrease_skill", 1f, 1f)
                                specialist.addSkill(skillId, 1f)

                                recreateSections(panelData)
                            // ----------------------------------------------------- Unassign Elite
                            } else if (!skillDisplay.section!!.isTierLocked) {
                                if (section.skillItemMap.containsKey(skillId)) returnItem(section.skillItemMap.get(skillId)) //Return consumed item
                                if (skillMaxLevel != 1f) Global.getSector().playerStats.storyPoints = (storyPoints + 1) //Return story point

                                skillDisplay.playSound("ui_char_decrease_skill", 1f, 1f)
                                specialist.removeSkill(skillId)

                                recreateSections(panelData)
                            } else {
                                skillDisplay.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
                            }
                        }
                    // -------------------------------------- Switch mutually exclusive skills
                    } else if (section.isMutuallyExclusive && !specialist.sectionSkills(section).isEmpty()) {
                        // ---------- Normal
                        if (itemAvailable(section.skillItemMap.get(skillId))) {
                            if (specialist.sectionSkills(section).contains(JDP_MENTORSHIP) && mentee != data.getBaseSpecialist()) { //Block switching away from Mentorship if in use
                                skillDisplay.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
                            } else {
                                if (section.skillItemMap.containsKey(skillId)) consumeItem(section.skillItemMap.get(skillId)) //Take consumed item

                                skillDisplay.playSound(aptitude!!.sounds.get(skillDisplay.section!!.soundId).toString());
                                for (otherSkill in specialist.sectionSkills(section)) {
                                    if (section.skillItemMap.containsKey(otherSkill.key)) returnItem(section.skillItemMap.get(otherSkill.key)) //Return consumed item
                                    if (otherSkill.value > 1f) Global.getSector().playerStats.storyPoints = (storyPoints + 1) //Return story point
                                    specialist.removeSkill(otherSkill.key)
                                }

                                specialist.addSkill(skillId, 1f)

                                recreateSections(panelData)
                            }
                        // ---------- Same item is needed for both
                        } else if (itemAvailable(specialist, section, section.skillItemMap.get(skillId))) {
                            if (specialist.sectionSkills(section).contains(JDP_MENTORSHIP) && mentee != data.getBaseSpecialist()) { //Block switching away from Mentorship if in use
                                skillDisplay.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
                            } else {
                                skillDisplay.playSound(
                                    aptitude!!.sounds.get(skillDisplay.section!!.soundId).toString()
                                );
                                for (otherSkill in specialist.sectionSkills(section)) {
                                    if (otherSkill.value > 1f) Global.getSector().playerStats.storyPoints = (storyPoints + 1) //Return story point
                                    specialist.removeSkill(otherSkill.key)
                                }

                                specialist.addSkill(skillId, 1f)

                                recreateSections(panelData)
                            }
                        } else {
                            skillDisplay.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
                        }
                    // ----------------------------------------------------- Assign
                    } else if (specialistPoints > 0f && itemAvailable(section.skillItemMap.get(skillId))) {
                        if (section.skillItemMap.containsKey(skillId)) consumeItem(section.skillItemMap.get(skillId)) //Take consumed item

                        skillDisplay.playSound(aptitude!!.sounds.get(skillDisplay.section!!.soundId).toString());
                        specialist.addSkill(skillId, 1f)

                        recreateSections(panelData)
                    } else {
                        skillDisplay.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
                    }
                }

                if (previous != null) {
                    var gap = 3f
                    skillDisplay.position.rightOfTop(previous, gap)
                    if(!isLast) usedWidth += gap;
                }

                previous = skillDisplay.elementPanel
                if (!isLast) {
                    var gap = 3f
                    var seperator = jdp_SkillSeperatorElement(color, specialistElement, skillSize)
                    seperator.elementPanel.position.rightOfTop(skillDisplay.elementPanel, gap)
                    previous = seperator.elementPanel
                    usedWidth += gap;
                } else {
                    if (section.isMutuallyExclusive) {
                        val underline = jdp_UnderlineElement(color, 2f, specialistElement, usedWidth)
                        underline.position.belowLeft(section.skillElements.get(0).elementPanel, 2f)
                    }
                }
            }

            if (!isLastSection) {
                var gap = jdp_SkillGapElement(color, specialistElement)
                gap.elementPanel.position.rightOfTop(previous, 3f)//3f
                if ((section.nextSection != null && !section.nextSection!!.isTierActive) || specialist.activeSkills().size >= specialist.level) gap.render = false
                previous = gap.elementPanel
            }
        }

        if (isMentor) {
            var paraElement = specialistElement.addLunaElement(pickerSize, 20f).apply {
                renderBorder = false
                renderBackground = false
            }

            paraElement.position.aboveLeft(specialistPickerElement.elementPanel, 0f)
            paraElement.innerElement.setParaFont("graphics/fonts/victor14.fnt")

            var specialistPara = paraElement.innerElement.addPara(
                spec.name,
                0f,
                spec.color,
                spec.color
            )
            specialistPara.position.inTL(
                paraElement.width / 2 - specialistPara.computeTextWidth(specialistPara.text) / 2,
                paraElement.height - specialistPara.computeTextHeight(specialistPara.text) - 5
            )
        }
    }
}