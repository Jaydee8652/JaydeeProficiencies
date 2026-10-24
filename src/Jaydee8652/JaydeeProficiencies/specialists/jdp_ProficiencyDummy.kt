package Jaydee8652.JaydeeProficiencies.specialists

import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_PanelData
import Jaydee8652.JaydeeProficiencies.specialists.plugins.jdp_BaseProficiencyPlugin
import Jaydee8652.JaydeeProficiencies.utils.jdp_SpecStore.jdp_proficiencySpec
import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc


//Proficiency Dummy is a dummy proficiency,
//Used when a specialist doesn't need a proficiency (like the generic apprentice specialist)
class jdp_ProficiencyDummy : jdp_BaseProficiencyPlugin() {
    override fun addDescription(person: PersonAPI, tooltip: TooltipMakerAPI, isHireScreen: Boolean) {
        tooltip.addPara("UNOBTAINABLE! Please contact Jaydee8652 with details", 0f, Misc.getTextColor(), Misc.getHighlightColor())
    }

    override fun addFooter(person: PersonAPI?, panelData: jdp_PanelData, tooltip: TooltipMakerAPI?) {
    }

    override fun createMentorSections() {
    }
    override fun createMenteeSections() {
    }
}