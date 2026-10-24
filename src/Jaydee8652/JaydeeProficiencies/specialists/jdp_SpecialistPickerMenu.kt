package Jaydee8652.JaydeeProficiencies.specialists


import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_PanelData
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_Specialist
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_SpecialistData
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_DisplayElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_HomeElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_SkillWidgetElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_TypeElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_UnderlineElement
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_DEFAULT_SPECIALIST
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_DUMMY_PROFICIENCY
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_UNAPPLIED_MARKER
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.clearChildren
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.createFakeSkill
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getBaseSpecialistPerson
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getOpenSpecialist
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getSpecialistData
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.invokeCreateSkillTooltip
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_IntelUtils.*
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_IntelUtils.confirmApprenticeAssignment
import Jaydee8652.JaydeeProficiencies.utils.jdp_SpecStore
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.characters.AdminData
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import lunalib.lunaExtensions.addLunaElement
import com.fs.starfarer.api.ui.UIComponentAPI
import com.fs.starfarer.ui.impl.StandardTooltipV2Expandable
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI.SkillLevelAPI
import com.fs.starfarer.api.characters.OfficerDataAPI
import com.fs.starfarer.api.characters.PersonAPI
import java.util.ArrayList
import Jaydee8652.JaydeeProficiencies.utils.refl.*;
import com.fs.starfarer.api.input.InputEventAPI

object jdp_SpecialistPickerMenu {
    var lastScroller = 0f
    var activeElement: TooltipMakerAPI? = null

    @JvmStatic
    fun recreatePanel(panelData: jdp_PanelData, panel: CustomPanelAPI) {
        if (activeElement != null) lastScroller = activeElement!!.externalScroller.yOffset
        panel.clearChildren()

        var person = getOpenSpecialist()

        var adminMap = HashMap<String, AdminData>()
        var officerMap = HashMap<String, OfficerDataAPI>()

        var data: jdp_SpecialistData = getSpecialistData()
        var intelSpec = data.getSpecialist(person.id)!!.getProficiencySpec()

        var mentor: jdp_Specialist = data.getSpecialist(person.id)!!
        var mentee: jdp_Specialist = data.getSpecialist(mentor.getMentee())!!

        var titlePanel = panel.createCustomPanel(PICKER_PANEL_WIDTH, TITLE_GAP, null)
        panel.addComponent(titlePanel)

        var titleElement = titlePanel.createUIElement(PICKER_PANEL_WIDTH - (PICKER_PANEL_OUTER_GAP * 2) - PICKER_PANEL_WIDTH_OFFSET, TITLE_GAP, false)
        titlePanel.addUIElement(titleElement)
        titlePanel.position.inTL(PICKER_PANEL_OUTER_GAP, PICKER_PANEL_OUTER_GAP)
        titleElement.addSectionHeading("Select an apprentice for " + person.nameString, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, 10f)

        var scrollerPanel = panel.createCustomPanel(PICKER_PANEL_WIDTH - (PICKER_PANEL_OUTER_GAP * 2) - PICKER_PANEL_WIDTH_OFFSET, PICKER_PANEL_HEIGHT - HEIGHT_CAP - TITLE_GAP, null)
        panel.addComponent(scrollerPanel)
        scrollerPanel.position.belowLeft(titlePanel, 0f)

        var scrollerElement = scrollerPanel.createUIElement(PICKER_PANEL_WIDTH - (PICKER_PANEL_OUTER_GAP * 2) - PICKER_PANEL_WIDTH_OFFSET, PICKER_PANEL_HEIGHT - HEIGHT_CAP - TITLE_GAP, true)
        scrollerElement.position.inTL(0f, 0f)
        activeElement = scrollerElement

        var potentialSpecialists = ArrayList<PersonAPI>()

        if (intelSpec.mentor.contains(jdp_SpecialistTags.SPECIALIST_TYPE_PLAYER)) potentialSpecialists.add(Global.getSector().playerPerson)

        if (intelSpec.mentor.contains(jdp_SpecialistTags.SPECIALIST_TYPE_OFFICER)) {
            potentialSpecialists.addAll(Global.getSector().playerFleet.fleetData.officersCopy.map { it.person })
            for (officer in Global.getSector().playerFleet.fleetData.officersCopy) {
                officerMap.put(officer.person.id, officer)
            }
        }

        if (intelSpec.mentor.contains(jdp_SpecialistTags.SPECIALIST_TYPE_ADMIN)) {
            potentialSpecialists.addAll(Global.getSector().characterData.admins.map { it.person })
            for (admin in Global.getSector().characterData.admins) {
                adminMap.put(admin.person.id, admin)
            }
        }

        var specialists = ArrayList<PersonAPI>()
        for(person in potentialSpecialists) {
            var specialist = data.getSpecialist(person.id)

            if (specialist != null && specialist.proficiencySpecId != JDP_DUMMY_PROFICIENCY) continue
            if (officerMap.contains(person.id) && adminMap.contains(person.id)) continue

            specialists.add(person)
        }

        var scrollerGap = 6f;
        if((specialists.size * (PICKER_PANEL_ENTRY_SIZE + PICKER_PANEL_ENTRY_GAP * 2 + 3f)) < PICKER_PANEL_HEIGHT - HEIGHT_CAP - TITLE_GAP) scrollerGap = 2f

        for (i in 0 until specialists.size) {
            var person = specialists[i]
            var specialist = data.getSpecialist(person.id)

            //Base (Scroller Element Panel)
            var specialistElement = scrollerElement.addLunaElement(
                PICKER_PANEL_WIDTH - (PICKER_PANEL_OUTER_GAP * 2) - PICKER_PANEL_WIDTH_OFFSET - scrollerGap,
                PICKER_PANEL_ENTRY_SIZE + PICKER_PANEL_ENTRY_GAP * 2
            ).apply {
                enableTransparency = true
                borderColor = Global.getSector().playerFaction.baseUIColor
                backgroundColor = Global.getSector().playerFaction.color.brighter()
                backgroundAlpha = 0.25f
                borderAlpha = 0f
            }
            if(i == 0) specialistElement.position.inTL(1f,1f)
            scrollerElement.addSpacer(3f)
            specialistElement.advance {
                if (person == mentee.person) {
                    specialistElement.borderColor = Misc.getBrightPlayerColor()
                    specialistElement.borderAlpha = 0.6f
                } else {
                    specialistElement.borderColor = Global.getSector().playerFaction.color
                    specialistElement.borderAlpha = 0f
                }

                if (specialistElement.isHovering) {
                    specialistElement.backgroundColor = Misc.getDarkPlayerColor().brighter()
                    specialistElement.backgroundAlpha = 0.25f
                } else {
                    if (i % 2 == 0) {
                        specialistElement.backgroundColor = Global.getSector().playerFaction.color.brighter()
                        specialistElement.backgroundAlpha = 0f
                    } else {
                        specialistElement.backgroundColor = Global.getSector().playerFaction.color.brighter()
                        specialistElement.backgroundAlpha = 0.25f
                    }
                }
            }
            specialistElement.onClick {
                onClick(it, mentee, person, panelData, panel)
            }

            //Ship / Market
            var homeElement = jdp_HomeElement(
                Global.getSector().playerFleet.getFleetData().getMemberWithCaptain(person),
                adminMap.get(person.id)?.market,
                specialistElement.innerElement,
                PICKER_PANEL_ENTRY_SIZE,
                PICKER_PANEL_ENTRY_SIZE
            )
            homeElement.position.inTL(PICKER_PANEL_OUTER_GAP, PICKER_PANEL_ENTRY_GAP)
            homeElement.onClick {
                onClick(it, mentee, person, panelData, panel)
            }

            //Portrait
            var portraitElement = jdp_DisplayElement(
                Global.getSettings().getSprite(person.portraitSprite),
                "",
                Misc.getBasePlayerColor(),
                specialistElement.innerElement,
                PICKER_PANEL_ENTRY_SIZE,
                PICKER_PANEL_ENTRY_SIZE
            )
            portraitElement.position.rightOfTop(homeElement.elementPanel, PICKER_PANEL_ENTRY_GAP)
            portraitElement.onClick {
                onClick(it, mentee, person, panelData, panel)
            }

            //Text / Skills
            var nameElement = specialistElement.innerElement.addLunaElement(
                PICKER_PANEL_WIDTH - PICKER_PANEL_OUTER_GAP * 2 - (PICKER_PANEL_ENTRY_SIZE * 2) - (PICKER_PANEL_ENTRY_GAP * 4),
                PICKER_PANEL_ENTRY_SIZE
            ).apply {
                renderBorder = false
                renderBackground = false
            }
            nameElement.position.rightOfTop(portraitElement.elementPanel, PICKER_PANEL_ENTRY_GAP)

            var nameString = person.nameString
            if (officerMap.contains(person.id)) nameString = nameString + " (Level " + person.stats.level + ")"

            var nameLabel = nameElement.innerElement.createLabel(
                nameString,
                Global.getSector().playerFaction.baseUIColor,
                nameElement.width
            )
            nameElement.innerElement.addCustom(nameLabel as UIComponentAPI?, 10f).position.inTL(0f, 0f)
            nameElement.onClick {
                onClick(it, mentee, person, panelData, panel)
            }

            //Assignment Text
            if (adminMap.contains(person.id)) {//Admin
                var typeDisplay = jdp_TypeElement(
                    jdp_SpecialistTags.SPECIALIST_TYPE_ADMIN,
                    person,
                    true,
                    specialistElement.innerElement,
                    TYPE_ELEMENT_SIZE,
                    TYPE_ELEMENT_SIZE
                )
                typeDisplay.position.inTL(PICKER_PANEL_ENTRY_GAP, PICKER_PANEL_ENTRY_GAP)
                typeDisplay.onClick {
                    onClick(it, mentee, person, panelData, panel)
                }

                var market = adminMap.get(person.id)!!.market
                if (market == null) {
                    var assigmnentLabel = nameElement.innerElement.createLabel(
                        "Unassigned (reduced salary)",
                        Misc.getGrayColor(),
                        PICKER_PANEL_ENTRY_SIZE * 4 - 5f
                    )
                    nameElement.innerElement.addCustom(assigmnentLabel as UIComponentAPI?, 10f)
                    assigmnentLabel.position.inTL(0f, nameLabel.computeTextHeight(nameLabel.text))
                } else {
                    var assigmnentLabel = nameElement.innerElement.createLabel(
                        "Assigned to govern " + market.name + " (size " + person.market.size + ")",
                        Misc.getTextColor(),
                        PICKER_PANEL_ENTRY_SIZE * 5 - 5f
                    )
                    nameElement.innerElement.addCustom(assigmnentLabel as UIComponentAPI?, 10f)
                    assigmnentLabel.position.inTL(0f, nameLabel.computeTextHeight(nameLabel.text))
                }
            } else if (officerMap.contains(person.id)) {//Officer
                var typeDisplay = jdp_TypeElement(
                    jdp_SpecialistTags.SPECIALIST_TYPE_OFFICER,
                    person,
                    true,
                    specialistElement.innerElement,
                    TYPE_ELEMENT_SIZE,
                    TYPE_ELEMENT_SIZE
                )
                typeDisplay.position.inTL(PICKER_PANEL_ENTRY_GAP, PICKER_PANEL_ENTRY_GAP)
                typeDisplay.onClick {
                    onClick(it, mentee, person, panelData, panel)
                }

                var fleetMember = Global.getSector().playerFleet.getFleetData().getMemberWithCaptain(person)
                if (fleetMember == null) {
                    var assigmnentLabel = nameElement.innerElement.createLabel(
                        person.personalityAPI.displayName + ", unassigned",
                        Misc.getGrayColor(),
                        PICKER_PANEL_ENTRY_SIZE * 4 - 5f
                    )
                    nameElement.innerElement.addCustom(assigmnentLabel as UIComponentAPI?, 10f)
                    assigmnentLabel.position.inTL(0f, nameLabel.computeTextHeight(nameLabel.text))
                } else {
                    var assigmnentLabel = nameElement.innerElement.createLabel(
                        person.personalityAPI.displayName + ", assigned to the " + fleetMember.shipName + " (" + fleetMember.hullSpec.hullName + "-class)",
                        Misc.getTextColor(),
                        PICKER_PANEL_ENTRY_SIZE * 7 - 5f
                    )
                    nameElement.innerElement.addCustom(assigmnentLabel as UIComponentAPI?, 10f)
                    assigmnentLabel.position.inTL(0f, nameLabel.computeTextHeight(nameLabel.text))
                }
            } else if (person == Global.getSector().playerPerson) {//Player
                var typeDisplay = jdp_TypeElement(
                    jdp_SpecialistTags.SPECIALIST_TYPE_PLAYER,
                    person,
                    true,
                    specialistElement.innerElement,
                    TYPE_ELEMENT_SIZE,
                    TYPE_ELEMENT_SIZE
                )
                typeDisplay.position.inTL(PICKER_PANEL_ENTRY_GAP, PICKER_PANEL_ENTRY_GAP)
                typeDisplay.onClick {
                    onClick(it, mentee, person, panelData, panel)
                }

                var fleetMember = Global.getSector().playerFleet.getFleetData().getMemberWithCaptain(person)
                if (fleetMember == null) {
                    var assigmnentLabel = nameElement.innerElement.createLabel(
                        "Unassigned",
                        Misc.getGrayColor(),
                        PICKER_PANEL_ENTRY_SIZE * 4 - 5f
                    )
                    nameElement.innerElement.addCustom(assigmnentLabel as UIComponentAPI?, 10f)
                    assigmnentLabel.position.inTL(0f, nameLabel.computeTextHeight(nameLabel.text))
                } else {
                    var assigmnentLabel = nameElement.innerElement.createLabel(
                        "Commanding from the " + fleetMember.shipName + " (" + fleetMember.hullSpec.hullName + "-class)",
                        Misc.getTextColor(),
                        PICKER_PANEL_ENTRY_SIZE * 7 - 5f
                    )
                    nameElement.innerElement.addCustom(assigmnentLabel as UIComponentAPI?, 10f)
                    assigmnentLabel.position.inTL(0f, nameLabel.computeTextHeight(nameLabel.text))
                }
            }

            //Preparing Skills
            var skills = person.stats.skillsCopy.filter { s ->
                !Global.getSettings().getSkillSpec(s.skill.id).isAptitudeEffect && s.level > 0
            }
            skills =
                skills.sortedByDescending { s -> jdp_SpecStore.getNormalAptitudeSpec(s.skill.governingAptitudeId)!!.order }

            var mentorshipSkills = mutableListOf<SkillLevelAPI>()
            var inherentSkills = mutableListOf<SkillLevelAPI>()

            //Mentorship text
            if (specialist != null) {
                var level = specialist.level
                var maxLevel = specialist.getMaxLevel()

                var mentor = data.getSpecialist(specialist.getMentor())
                if (specialist.getMentor() != JDP_DEFAULT_SPECIALIST) {
                    var spec = specialist.getProficiencySpec()

                    var mentorLabel = nameElement.innerElement.createLabel(
                        "Apprentice under " + mentor!!.person.nameString,
                        Global.getSector().playerFaction.baseUIColor,
                        PICKER_PANEL_ENTRY_SIZE * 7 - 5f
                    )
                    nameElement.innerElement.addCustom(mentorLabel as UIComponentAPI?, 10f)
                    mentorLabel.position.inTR(PICKER_PANEL_ENTRY_GAP + PICKER_PANEL_WIDTH_OFFSET, 0f)

                    var mentorTypeLabel = nameElement.innerElement.createLabel(
                        spec.name + " - Level " + level + " / " + maxLevel,
                        spec.color,
                        PICKER_PANEL_ENTRY_SIZE * 7 - 5f
                    )
                    nameElement.innerElement.addCustom(mentorTypeLabel as UIComponentAPI?, 10f)
                    mentorTypeLabel.position.inTR(
                        PICKER_PANEL_ENTRY_GAP + PICKER_PANEL_WIDTH_OFFSET,
                        mentorLabel.computeTextHeight(mentorLabel.text)
                    )

                    var skillIds = ArrayList<String>()
                    specialist.activeSkills.forEach { s -> skillIds.add(s.key) }
                    skills.forEach { s ->
                        if (skillIds.contains(s.skill.id)) {
                            mentorshipSkills.add(s)
                        } else
                            inherentSkills.add(s)
                    }

                    mentorshipSkills =
                        mentorshipSkills.sortedByDescending { s -> jdp_SpecStore.getNormalAptitudeSpec(s.skill.governingAptitudeId)!!.order }
                            .toMutableList()
                    inherentSkills =
                        inherentSkills.sortedByDescending { s -> jdp_SpecStore.getNormalAptitudeSpec(s.skill.governingAptitudeId)!!.order }
                            .toMutableList()
                    skills = inherentSkills + mentorshipSkills
                } else if (specialist.getPreviousMentor() != JDP_DEFAULT_SPECIALIST) {
                    var mentor = data.getSpecialist(specialist.getPreviousMentor())
                    var mentorSpec = mentor!!.getProficiencySpec()

                    var maxLevel = mentorSpec.apprenticeMaxLevel

                    var mentorLabel = nameElement.innerElement.createLabel(
                        "Was apprentice under " + mentor.person.nameString,
                        Misc.getGrayColor(),
                        PICKER_PANEL_ENTRY_SIZE * 7 - 5f
                    )
                    nameElement.innerElement.addCustom(mentorLabel as UIComponentAPI?, 10f)
                    mentorLabel.position.inTR(PICKER_PANEL_ENTRY_GAP + PICKER_PANEL_WIDTH_OFFSET, 0f)

                    var mentorTypeLabel = nameElement.innerElement.createLabel(
                        mentorSpec!!.name + " - Level " + level + " / " + maxLevel,
                        mentorSpec.color.darker(),
                        PICKER_PANEL_ENTRY_SIZE * 7 - 5f
                    )
                    nameElement.innerElement.addCustom(mentorTypeLabel as UIComponentAPI?, 10f)
                    mentorTypeLabel.position.inTR(
                        PICKER_PANEL_ENTRY_GAP + PICKER_PANEL_WIDTH_OFFSET,
                        mentorLabel.computeTextHeight(mentorLabel.text)
                    )

                    var skillIds = ArrayList<String>()
                    specialist.activeSkills.forEach { s -> skillIds.add(s.key) }
                    skills.forEach { s ->
                        if (skillIds.contains(s.skill.id)) {
                            mentorshipSkills.add(s)
                        } else
                            inherentSkills.add(s)
                    }

                    mentorshipSkills.add(createFakeSkill(JDP_UNAPPLIED_MARKER, 1f))
                    mentorshipSkills = mentorshipSkills.sortedByDescending { s -> jdp_SpecStore.getNormalAptitudeSpec(s.skill.governingAptitudeId)!!.order }.toMutableList()
                    inherentSkills = inherentSkills.sortedByDescending { s -> jdp_SpecStore.getNormalAptitudeSpec(s.skill.governingAptitudeId)!!.order }.toMutableList()
                    skills = inherentSkills + mentorshipSkills
                }
            }

            var previous: CustomPanelAPI? = null
            var skillDisplays = ArrayList<jdp_SkillWidgetElement>();
            var gap = 3f
            var perLine = Math.floor(((PICKER_PANEL_WIDTH - PICKER_PANEL_OUTER_GAP * 2 - (PICKER_PANEL_ENTRY_SIZE * 2) - (PICKER_PANEL_ENTRY_GAP * 4)) / (SKILL_ELEMENT_SIZE + gap)).toDouble())

            for (skill in skills) {
                val spec = Global.getSettings().getSkillSpec(skill.skill.id)
                if (spec.tags.contains("dummy") || spec.tags.contains("jdp_blacklist") || (!spec.isAdminSkill && !spec.isCombatOfficerSkill && !spec.isAdmiralSkill)) continue

                var skillDisplay = jdp_SkillWidgetElement(skill.skill.id, skill.level, true, true, null, nameElement.innerElement, SKILL_ELEMENT_SIZE, SKILL_ELEMENT_SIZE)
                if (specialist != null && specialist.getMentor() == JDP_DEFAULT_SPECIALIST && specialist.getPreviousMentor() != JDP_DEFAULT_SPECIALIST && skill.skill.id == JDP_UNAPPLIED_MARKER) skillDisplay.color = data.getSpecialist(specialist.getPreviousMentor())!!.getProficiencySpec().color.darker()

                var tooltip = invokeCreateSkillTooltip(spec, person.getStats());
                ReflectionUtils.invokeStatic(2, "addTooltipBelow", StandardTooltipV2Expandable::class.java, skillDisplay.elementPanel, tooltip)

                skillDisplays.add(skillDisplay);
                if ((skillDisplays.size - 1) != 0 && ((skillDisplays.size - 1) % perLine).toInt() == 0) {
                    previous = skillDisplays.get((skillDisplays.size - (perLine + 1)).toInt()).elementPanel
                    skillDisplay.position.belowLeft(previous, gap * 2);
                } else if (previous != null){
                    skillDisplay.position.rightOfTop(previous, gap);
                } else {
                    skillDisplay.position.inTL(0f, 32f)
                }

                if (mentorshipSkills.contains(skill) && mentorshipSkills.get(0) == skill) {
                    if(skillDisplays.size - 1 <= perLine) skillDisplay.position.inTL(0f, 32f + SKILL_ELEMENT_SIZE + gap * 2)
                    val underline = jdp_UnderlineElement(specialist!!.getProficiencySpec().color, 2f, nameElement.innerElement, ((gap + SKILL_ELEMENT_SIZE) * mentorshipSkills.size) - gap)
                    underline.position.belowLeft(skillDisplay.elementPanel, 1.5f)
                }
                if((skillDisplays.size + 1).toDouble() == (perLine * 2)) {
                    var etcElement = nameElement.innerElement.addLunaElement(SKILL_ELEMENT_SIZE, SKILL_ELEMENT_SIZE).apply {
                        enableTransparency = true
                        renderBorder = false
                        renderBackground = false
                    }
                    etcElement.position.rightOfTop(skillDisplay.elementPanel, gap)
                    etcElement.innerElement.addPara("+" + (skills.size - skillDisplays.size), Misc.getBasePlayerColor(), 3f).position.inTL(SKILL_ELEMENT_SIZE / 2 - 5, SKILL_ELEMENT_SIZE / 2 - 10f)
                    etcElement.onClick { onClick(it, mentee, person, panelData, panel) }
                    break
                }

                previous = skillDisplay.elementPanel
                skillDisplay.onClick { onClick(it, mentee, person, panelData, panel) }
            }
        }

        scrollerPanel.addUIElement(scrollerElement)
        scrollerElement.externalScroller.yOffset = lastScroller
    }

    fun onClick(it: InputEventAPI, currentApprentice: jdp_Specialist, apprentice: PersonAPI, panelData: jdp_PanelData, pickerPanel: CustomPanelAPI) {
        if (it.isRMBEvent) {
            Global.getSoundPlayer().playUISound("ui_char_decrease_skill", 1f, 1f)
            if(currentApprentice.person == apprentice) selectApprentice(getBaseSpecialistPerson(), panelData, pickerPanel)
        } else {
            Global.getSoundPlayer().playUISound("ui_button_pressed", 1f, 1f)
            selectApprentice(apprentice, panelData, pickerPanel)
        }
    }

    fun selectApprentice(apprentice: PersonAPI, panelData: jdp_PanelData, pickerPanel: CustomPanelAPI) {
        var data = getSpecialistData()
        if (data.getSpecialist(apprentice.id) == null) data.createApprentice(apprentice)
        confirmApprenticeAssignment(apprentice, panelData, pickerPanel)
    }
}