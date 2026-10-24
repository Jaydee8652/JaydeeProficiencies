package Jaydee8652.JaydeeProficiencies.specialists.tooltips

import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_Specialist
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.isBaseSpecialistPerson
import com.fs.starfarer.api.ui.BaseTooltipCreator
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.UIComponentAPI
import lunalib.lunaExtensions.addLunaElement

class ExperienceBarTooltipCreator(var specialist: jdp_Specialist) : BaseTooltipCreator() {
    override fun getTooltipWidth(tooltipParam: Any?): Float {
        return 245f
    }

    override fun createTooltip(tooltip: TooltipMakerAPI, expanded: Boolean, tooltipParam: Any?) {
        var width = getTooltipWidth(null)
        var gap = 3f

        var titleElement = tooltip.addLunaElement(width, 8f * gap).apply {
            enableTransparency = true
            renderBorder = false
            renderBackground = false
        }

        if (isBaseSpecialistPerson(specialist.person)) {
            //titleElement.innerElement.addSectionHeading("No Apprentice Assigned", Misc.getGrayColor(), getEmptyColor(), Alignment.MID, 10f)


            var titleLabel = titleElement.innerElement.createLabel("No Apprentice Assigned", Misc.getGrayColor())
            titleElement.innerElement.addCustom(titleLabel as UIComponentAPI?, 0f).position.inTMid((8f * gap / 2) - titleLabel.computeTextHeight(titleLabel.text) / 2)
            titleElement.elementPanel.position.inTL(gap, 0f)
        } else {
            if(specialist.level != specialist.getMaxLevel()) {
                var threshold = Math.round(specialist.getRequiredXP(specialist.level)).toString()
                var experience = Math.round(specialist.experience).toString()

              //  titleElement.innerElement.addSectionHeading(experience + " / " + threshold, Misc.getTextColor(), getEmptyColor(), Alignment.MID, 10f)

                var titleLabel = titleElement.innerElement.createLabel(experience + " / " + threshold, Misc.getTextColor())
                titleElement.innerElement.addCustom(titleLabel as UIComponentAPI?, 0f).position.inTMid((8f * gap / 2) - titleLabel.computeTextHeight(titleLabel.text) / 2)
                titleElement.elementPanel.position.inTL(gap * 2f, gap)
            } else {
                //titleElement.innerElement.addSectionHeading("Maximum Level Reached", Misc.getTextColor(), getEmptyColor(), Alignment.MID, 10f)

                var titleLabel = titleElement.innerElement.createLabel("Maximum Level Reached", Misc.getTextColor())
                titleElement.innerElement.addCustom(titleLabel as UIComponentAPI?, 0f).position.inTMid((8f * gap / 2) - titleLabel.computeTextHeight(titleLabel.text) / 2)
                titleElement.elementPanel.position.inTL(gap, gap)
            }

            if(!specialist.recentExperience.isEmpty) {
                var gainElement = tooltip.addLunaElement(width, 4f * gap).apply {
                    enableTransparency = true
                    renderBorder = false
                    renderBackground = false
                }
                //gainElement.innerElement.addSectionHeading("Recent Experience Gains:", Misc.getGrayColor(), getEmptyColor(), Alignment.LMID, 10f)

                var gainLabel = gainElement.innerElement.createLabel("Recent Experience Gains:", Misc.getGrayColor())
                gainElement.innerElement.addCustom(gainLabel as UIComponentAPI?, 0f).position.inTMid((8f * gap / 2) - gainLabel.computeTextHeight(gainLabel.text) / 2)
                gainElement.elementPanel.position.belowLeft(titleElement.elementPanel, 0f)

                val iterator = ArrayList(specialist.recentExperience).listIterator(specialist.recentExperience.size)
                while (iterator.hasPrevious()) {
                    val data = iterator.previous()
                    tooltip.addSpacer(10f)
                    var plural = "s"
                    if (Math.round(Global.getSector().clock.getElapsedDaysSince(data.timeStamp())) == 1) plural = ""
                    tooltip.addPara(
                        Math.round(Global.getSector().clock.getElapsedDaysSince(data.timeStamp()))
                            .toString() + " day" + plural + " ago: " + "+" + Math.round(data.xp()).toString(),
                        3f,
                        Misc.getTextColor(),
                        Misc.getHighlightColor(),
                        "+" + Math.round(data.xp()).toString()
                    )
                    tooltip.addPara(data.source(), 3f, Misc.getGrayColor(), data.source())
                }
                tooltip.addSpacer(gap)
            }
        }
    }
}