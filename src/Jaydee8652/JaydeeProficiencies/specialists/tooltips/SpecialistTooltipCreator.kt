package Jaydee8652.JaydeeProficiencies.specialists.tooltips;

import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_Specialist
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_SpecialistData
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_HomeElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_SpecialistPickerElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_TypeElement
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getAdmin
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getBaseSpecialistPerson
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getPlayerGovernedColonies
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getSpecialistData
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.isAdmin
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.isApprentice
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.isBaseSpecialistPerson
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.isOfficer
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_IntelUtils.extractPlayerTypeData
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_IntelUtils.extractTypeData
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIReflectionUtils.EMPTY_COLOR
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.ui.BaseTooltipCreator
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.LabelAPI
import com.fs.starfarer.api.ui.UIComponentAPI
import lunalib.lunaExtensions.addLunaElement
import org.lazywizard.lazylib.MathUtils
import java.awt.Color

class SpecialistTooltipCreator(var picker: jdp_SpecialistPickerElement, var mentor: PersonAPI) : BaseTooltipCreator() {
    override fun getTooltipWidth(tooltipParam: Any?): Float {
        return 400f
    }

    fun addAdminTypePanel(tooltip: TooltipMakerAPI, previous: CustomPanelAPI, person: PersonAPI): CustomPanelAPI {
        var homeElementSize = 80f
        var gap = 6f
        var width = getTooltipWidth(null)

        var mainElement = tooltip.addLunaElement(width, homeElementSize + (2f * gap)).apply {
            enableTransparency = true
            renderBorder = false
            renderBackground = false
        }
        mainElement.position.belowLeft(previous, 3f)

        var homeElement = jdp_HomeElement(null, getAdmin(person.id)?.market, mainElement.innerElement, homeElementSize, homeElementSize)
        homeElement.position.inTL(gap, gap)

        var textElement = mainElement.innerElement.addLunaElement(width - homeElementSize -(4f * gap), homeElementSize + 2f * gap).apply {
            enableTransparency = true
            renderBorder = false
            renderBackground = false
        }
        textElement.position.rightOfTop(homeElement.elementPanel, 3f)

        if(getAdmin(person.id)?.market != null) {
            textElement.innerElement.addPara("Administrator - " + getAdmin(person.id)!!.market.name, 3f, Misc.getTextColor(), Misc.getHighlightColor(), "Administrator")
        } else {
            textElement.innerElement.addPara("Administrator - Unassigned", 3f, Misc.getGrayColor(), Misc.getHighlightColor(), "Administrator")
        }
        textElement.innerElement.addSpacer(3f)
        textElement.innerElement.addPara("Gains Proficiency Experience from the profit margin and defense value of their governed market.", 3f, Misc.getTextColor(), Misc.getHighlightColor())

        return mainElement.elementPanel
    }


    fun addPlayerAdminTypePanel(tooltip: TooltipMakerAPI, previous: CustomPanelAPI, person: PersonAPI): CustomPanelAPI {
        var homeElementSize = 80f
        var gap = 6f
        var width = getTooltipWidth(null)


        var mainElement = tooltip.addLunaElement(width, (MathUtils.clamp(getPlayerGovernedColonies().size, 1, 2) * (homeElementSize + gap)) + gap).apply {
            enableTransparency = true
            renderBorder = false
            renderBackground = false
        }
        mainElement.position.belowLeft(previous, 3f)

        var market: MarketAPI? = null
        if(!getPlayerGovernedColonies().isEmpty()) market = getPlayerGovernedColonies().get(0)

        var homeElement = jdp_HomeElement(null, market, mainElement.innerElement, homeElementSize, homeElementSize)
        homeElement.position.inTL(gap, gap)
        var previousInternalElement = homeElement.elementPanel

        var textElement = mainElement.innerElement.addLunaElement(width - homeElementSize -(4f * gap), homeElementSize + 2f * gap).apply {
            enableTransparency = true
            renderBorder = false
            renderBackground = false
        }
        textElement.position.rightOfTop(homeElement.elementPanel, 3f)

        if(market != null) {
            if(getPlayerGovernedColonies().size > 1) {
                textElement.innerElement.addPara("Administrator - Governing " + getPlayerGovernedColonies().size + " Markets", 3f, Misc.getTextColor(), Misc.getHighlightColor(), "Administrator")

                var homeElement = jdp_HomeElement(null, getPlayerGovernedColonies().get(1), mainElement.innerElement, homeElementSize, homeElementSize)
                homeElement.position.belowLeft(previousInternalElement, gap)
                previousInternalElement = homeElement.elementPanel

                for (i in 0 until getPlayerGovernedColonies().size) {
                    if(i > 5 || i < 2) continue
                    if(i == 5) {
                        var etcElement = mainElement.innerElement.addLunaElement(30f, 10f).apply {
                            enableTransparency = true
                            renderBorder = false
                            renderBackground = false
                        }
                        etcElement.position.rightOfMid(previousInternalElement, 3f)
                        etcElement.innerElement.addPara("+" + (getPlayerGovernedColonies().size - i),Misc.getBasePlayerColor(), 3f)
                        previousInternalElement = etcElement.elementPanel
                        continue
                    }

                    var market = getPlayerGovernedColonies().get(i)
                    var homeElement = jdp_HomeElement(null, market, mainElement.innerElement, homeElementSize, homeElementSize)
                    homeElement.position.rightOfTop(previousInternalElement, gap)
                    previousInternalElement = homeElement.elementPanel
                }
            } else {
                textElement.innerElement.addPara("Administrator - " + market.name, 3f, Misc.getTextColor(), Misc.getHighlightColor(), "Administrator")
            }
        } else {
            textElement.innerElement.addPara("Administrator - Unassigned", 3f, Misc.getGrayColor(), Misc.getHighlightColor(), "Administrator")
        }
        textElement.innerElement.addSpacer(3f)
        textElement.innerElement.addPara("Gains Proficiency Experience from the profit margin and defense value of personally governed markets, divided by the number of personally governed markets.", 3f, Misc.getTextColor(), Misc.getHighlightColor())

        return mainElement.elementPanel
    }


    fun addOfficerTypePanel(tooltip: TooltipMakerAPI, previous: CustomPanelAPI, person: PersonAPI): CustomPanelAPI {
        var homeElementSize = 80f
        var gap = 3f
        var width = getTooltipWidth(null)

        var mainElement = tooltip.addLunaElement(width, homeElementSize + (2f * gap)).apply {
            enableTransparency = true
            renderBorder = false
            renderBackground = false
        }
        mainElement.position.belowLeft(previous, 3f)

        var homeElement = jdp_HomeElement(Global.getSector().playerFleet.getFleetData().getMemberWithCaptain(person), null, mainElement.innerElement, homeElementSize, homeElementSize)
        homeElement.position.inTL(gap, gap)

        var textElement = mainElement.innerElement.addLunaElement(width - homeElementSize -(4f * gap), homeElementSize + 2f * gap).apply {
            enableTransparency = true
            renderBorder = false
            renderBackground = false
        }
        textElement.position.rightOfTop(homeElement.elementPanel, 3f)

        if(Global.getSector().playerFleet.getFleetData().getMemberWithCaptain(person) != null) {
            textElement.innerElement.addPara("Officer - " + Global.getSector().playerFleet.getFleetData().getMemberWithCaptain(person).hullSpec.hullName, 3f, Misc.getTextColor(), Misc.getHighlightColor(), "Officer")
        } else {
            textElement.innerElement.addPara("Officer - Unassigned", 3f, Misc.getGrayColor(), Misc.getHighlightColor(), "Officer")
        }
        textElement.innerElement.addSpacer(3f)
        textElement.innerElement.addPara("Gains Proficiency Experience from performance in combat, rate increased when piloting frigates and destroyers.", 3f, Misc.getTextColor())

        return mainElement.elementPanel
    }

    override fun createTooltip(tooltip: TooltipMakerAPI?, expanded: Boolean, tooltipParam: Any?) {
        var homeElementSize = 80f
        var gap = 3f

        var data: jdp_SpecialistData = getSpecialistData()
        var person = picker.person

        var mentorData: jdp_Specialist = data.getSpecialist(mentor.id)!!
        var specialist: jdp_Specialist = data.getSpecialist(picker.person.id)!!

        var plugin = mentorData.getProficiencyPlugin()
        var spec = mentorData.getProficiencySpec()
        var specName = spec.name.toString() + " Proficiency"

        if (isBaseSpecialistPerson(person)) {
            var explaination = tooltip!!.addPara(
                "This slot can be used to assign an apprentice to " + mentor.name.fullName + ". An apprentice can learn a selection of skills based on their mentors Proficiency, but they do not necessarily have access to the exact same pool of skills.",
                0f,
                Misc.getTextColor(),
                Misc.getHighlightColor()
            )

            explaination.setHighlight(mentor.name.fullName)
            explaination.setHighlightColors(spec.color)

            tooltip.addSpacer(10f)

            var personelle = ""
            val highlights = mutableListOf<String>()
            for (i in 0 until spec.mentor.size) {
                val type: String = spec.mentor.get(i)
                val string: String = jdp_SpecialistTags.SPECIALIST_TYPE_MAP.get(type)!!.name() + jdp_SpecialistTags.SPECIALIST_TYPE_MAP.get(type)!!.plural()
                highlights += string

                val spacer: String = when (i) {
                    spec.mentor.size - 1 -> ""
                    spec.mentor.size - 2 -> " and "
                    else -> ", "
                }
                personelle += string + spacer
            }
            if (personelle == "You") {
                tooltip.addPara("The " + specName + " can only select " + personelle + " as an apprentice", 0f, Misc.getTextColor(), Misc.getHighlightColor(), *highlights.toTypedArray())
            } else {
                tooltip.addPara("The " + specName + " can select apprentices from your " + personelle, 0f, Misc.getTextColor(), Misc.getHighlightColor(), *highlights.toTypedArray())
            }
        } else {
            var width = getTooltipWidth(null)

            var titleElement = tooltip!!.addLunaElement(width, 24f).apply {
                enableTransparency = true
                renderBorder = false
                renderBackground = false
            }
            var player = ""
            if(picker.person.isPlayer) player = " (You)"
            titleElement.innerElement.addSectionHeading(picker.person.name.fullName + player, spec.color, EMPTY_COLOR, Alignment.MID, 10f)
            titleElement.elementPanel.position.inTL(gap * 2f, 0f)

            var previous = titleElement.elementPanel

            if (!isApprentice(specialist)) {
                //titleElement.innerElement.addSectionHeading(extractTypeData(spec.type), spec.color.brighter(), spec.getColorTransparent().darker(), Alignment.MID, 10f)

                if (spec.type.contains(jdp_SpecialistTags.SPECIALIST_TYPE_OFFICER)) previous = addOfficerTypePanel(tooltip, previous, person)
                if (spec.type.contains(jdp_SpecialistTags.SPECIALIST_TYPE_ADMIN)) previous = addAdminTypePanel(tooltip, previous, person)
            } else if (person.isPlayer) {
                //titleElement.innerElement.addSectionHeading(extractPlayerTypeData(spec.tags), spec.color.brighter(), spec.getColorTransparent().darker(), Alignment.MID, 10f)

                if(!spec.tags.contains(jdp_SpecialistTags.SPEC_TAG_PLAYER_NO_XP_AS_OFFICER)) previous = addOfficerTypePanel(tooltip, previous, person)
                if(!spec.tags.contains(jdp_SpecialistTags.SPEC_TAG_PLAYER_NO_XP_AS_ADMIN)) previous = addPlayerAdminTypePanel(tooltip, previous, person)
            } else {
                if (isOfficer(person)) {
                    //titleElement.innerElement.addSectionHeading(jdp_SpecialistTags.SPECIALIST_TYPE_MAP.get(jdp_SpecialistTags.SPECIALIST_TYPE_OFFICER)!!.name(), spec.color.brighter(), spec.getColorTransparent().darker(), Alignment.MID, 10f)
                    previous = addOfficerTypePanel(tooltip, previous, person)
                }
                if (isAdmin(person)) {
                    //titleElement.innerElement.addSectionHeading(jdp_SpecialistTags.SPECIALIST_TYPE_MAP.get(jdp_SpecialistTags.SPECIALIST_TYPE_ADMIN)!!.name(), spec.color.brighter(), spec.getColorTransparent().darker(), Alignment.MID, 10f)
                    previous = addAdminTypePanel(tooltip, previous, person)
                }
            }
        }
        tooltip.addSpacer(10f)
        tooltip.addPara("The skills of Specialists can be freely assigned and unassigned, as long as unassigning a skill would not mean the skill threshold for skills in higher tiers is no longer met. Only one skill in a mutually exclusive section can be assigned at a time, and attempting to assign a new skill will automatically unassign the current skill.", 0f, Misc.getTextColor(), Misc.getNegativeHighlightColor(), "reset")

        if(person.id != mentor.id) {
            tooltip.addSpacer(10f)
            tooltip.addPara("Unassigned apprentices maintain their experience and level, but their skills are deactivated. Reassigning an apprentice to their previous mentor will reactivate these skills, but assigning them to a new mentor will reset all experience and levels.", 0f, Misc.getTextColor(), Misc.getNegativeHighlightColor(), "reset")
            tooltip.addSpacer(10f)

            if(!picker.isLocked) {
                tooltip.addPara("Left-click to select a new apprentice to assign.", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "Left-click")
                tooltip.addPara("Right-click to unassign an apprentice.", 0f, Misc.getTextColor(), Misc.getHighlightColor(), "Right-click")

            } else {
                var doesNotHaveMentorship = tooltip.addPara(mentor.name.fullName + " must have the Mentorship skill active to assign an apprentice.", 0f, Misc.getNegativeHighlightColor(), Misc.getHighlightColor())
                doesNotHaveMentorship.setHighlight(mentor.name.fullName, "Mentorship")
                doesNotHaveMentorship.setHighlightColors(spec.color, Misc.getHighlightColor())
            }
        } else {
            if(!spec.tags.contains(jdp_SpecialistTags.SPEC_TAG_NO_COMMLINK)) {
                tooltip.addSpacer(10f)
                tooltip.addPara("Left/Right-click to open a commlink with " + person.name.fullName, 0f, Misc.getTextColor(), Misc.getHighlightColor(), "Left/Right-click")
            }
        }
        tooltip.addSpacer(3f)
        return
    }
}