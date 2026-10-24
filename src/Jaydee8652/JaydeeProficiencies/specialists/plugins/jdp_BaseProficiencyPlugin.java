package Jaydee8652.JaydeeProficiencies.specialists.plugins;

import Jaydee8652.JaydeeProficiencies.specialists.data.SpecialistSkillSection;
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_PanelData;
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_Specialist;
import Jaydee8652.JaydeeProficiencies.utils.jdp_SpecStore;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import org.lazywizard.lazylib.MathUtils;

import java.util.ArrayList;
import java.util.List;

/**Instantiated on application load and kept in memory, do not save class-scope variables within */
public abstract class jdp_BaseProficiencyPlugin {
    public jdp_SpecStore.jdp_proficiencySpec spec;

    /**Required, used to decide which skills are added, and how they are organised.
     * Mentee Sections are only available if Mentor Sections contains the Mentorship skill. Do not give the Mentee Sections Mentorship, it has no effect*/
    public abstract void createMentorSections();
    public abstract void createMenteeSections();

    /**Required, adds intel and hire screen description.
     * Ideally, the version displayed in the hire screen should be shorter and more digestible */
    public abstract void addDescription(PersonAPI person, TooltipMakerAPI tooltip, Boolean isHireScreen);

    /** Not required, added to the bottom of the intel
     * Can be used to explain more complicated mechanics */
    public abstract void addFooter(PersonAPI person, jdp_PanelData panelData, TooltipMakerAPI tooltip);

    /**Used for adding sections within the createMentorSections() and createMenteeSections() methods.
     * All skills in the root section are unlocked by the Specialist by default unless the section is marked as "Mutually Exclusive",
     * in this case only the first skill starts unlocked. The base level of the Specialist will be adjusted accordingly to the number of starting skills.
     * Only the first section should be marked as root*/
    public final void addSection(SpecialistSkillSection section, Boolean isRoot) {
        String number = sections.size() + 1 + "";
        if (!isRoot) section.setSectionId("section" + number);

        number = MathUtils.clamp(sections.size() + 1, 1, 5) + "";
        section.setSoundId("sound" + number);
        sections.add(section);
    }
    public final void addSection(SpecialistSkillSection section) {
        addSection(section, false);
    }

    /**Returns the XP Thresholds for each level, can be overridden by Proficiency Plugins
     * If a Proficiency does not start at level 1 it is recommended to adjust the experience curve accordingly*/
    public long [] getXPThresholds(Boolean apprentice) {
        return new long []{
                0,       // level  1
                12000,   // level  2
                25000,   // level  3
                35000,   // level  4
                45000,   // level  5
                55000,   // level  6
                65000,   // level  7
                75000,   // level  8
                90000,   // level  9
                100000,  // level 10
        };
    }

    /**Creates title in intel and hire screen, can be overridden by Proficiency Plugins for flavor*/
    public String createTitle(PersonAPI person, Boolean isHireScreen) {
        if(isHireScreen) return spec.getName() + " - " + person.getName().getFullName();
        return person.getName().getLast() + " Archive";
    }

    /**Intercept just before experience is added, returns final experience value that will be gained,
     * allows for finer control / custom mechanics. If a battle is provided, this was the source of the experience,
     * otherwise it was from colony governance*/
    public Float calculateExperience(jdp_Specialist specialist, Float experience, FleetEncounterContext context) {
        return experience;
    }
    public Float calculateExperience(jdp_Specialist specialist, Float experience, MarketAPI market) {
        return experience;
    }

    /**Gets called on level up by both mentors and their apprentices. Can can be overridden by Proficiency Plugins
     * If willLevelupAgainWithCurrentXP is true, the amount of XP gained at once was enough to level up multiple times, therefore notifyLevelup will be called
     * multiple times for this one XP gain. Only running code when this is false means it will only run once for a given XP gain*/
    public void notifyLevelup(jdp_Specialist specialist, Boolean willLevelupAgainWithCurrentXP) {}

    /**Internal Use Only** --------------------------------------------------------------------------------------------------------/
    /**Create sections, then immediately clear, to prevent memory leaks due to UI data in sections.**/
    public final List<SpecialistSkillSection> getSections(Boolean mentor) {
        if(mentor) {
            createMentorSections();
        } else {
            createMenteeSections();
        }
        List<SpecialistSkillSection> list = new ArrayList<>(sections);
        sections.clear();
        return list;
    }
    private final List<SpecialistSkillSection> sections = new ArrayList<>();
}
