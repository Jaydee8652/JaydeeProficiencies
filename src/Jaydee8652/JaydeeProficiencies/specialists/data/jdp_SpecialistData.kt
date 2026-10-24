package Jaydee8652.JaydeeProficiencies.specialists.data


import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.*
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getBaseSpecialistPerson
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getOpenSpecialist
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.setOpenSpecialist
import Jaydee8652.JaydeeProficiencies.utils.jdp_SpecStore
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.characters.PersonAPI
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_IntelUtils.addHireSpecialistIntel;
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIReflectionUtils.*;

class jdp_SpecialistData {
    private var activeSpecialists = ArrayList<jdp_Specialist>()
    private var activeSpecialistsMap = HashMap<String, jdp_Specialist>()

    fun init() {
        if (getSpecialist(JDP_DEFAULT_SPECIALIST) == null) {
            createSpecialist(getBaseSpecialistPerson(), JDP_DUMMY_PROFICIENCY);
        }
    }

    fun getMentors(): ArrayList<jdp_Specialist> {
        return activeSpecialists.filter { s -> s.proficiencySpecId != JDP_DUMMY_PROFICIENCY && s.hired } as ArrayList<jdp_Specialist>
    }

    fun getAllHired(): ArrayList<jdp_Specialist> {
        return activeSpecialists.filter { s -> s.hired } as ArrayList<jdp_Specialist>
    }

    fun getAllSpecialists(): ArrayList<jdp_Specialist> {
        return activeSpecialists
    }

    fun getSpecialist(personId: String): jdp_Specialist? {
        return activeSpecialistsMap.get(personId)
    }

    fun getBaseSpecialist(): jdp_Specialist {
        return getSpecialist(JDP_DEFAULT_SPECIALIST)!!
    }

    fun createSpecialist(person: PersonAPI, specialistSpecId: String): jdp_Specialist {
        var spec = jdp_SpecStore.getProficiencySpec(specialistSpecId)
        var plugin = spec!!.getPlugin()
        var specialist = jdp_Specialist(person, specialistSpecId)

        for (section in plugin.getSections(true)) {
            if (section.sectionId == JDP_ROOT_SECTION) {
                if (section.isMutuallyExclusive) {
                    specialist.addSkill(section.skills.keys.first(), 1f)
                } else {
                    specialist.level = (section.skills.size)
                    for (skill in section.skills) {
                        specialist.addSkill(skill.key, 1f)
                    }
                }
            }
        }
        person.getMemoryWithoutUpdate().set("\$nex_noOfficerDeath", true);
        activeSpecialists.add(specialist)
        activeSpecialistsMap.put(person.id, specialist)
        return specialist
    }

    fun hireSpecialist(personId: String) {
        var specialist = getSpecialist(personId)
        if (specialist != null) {
            var spec = specialist.spec!!
            var person = specialist.person

            specialist.hired = true
            if(!spec.tags.contains(SPEC_TAG_AI_PERSON)) {
                if (spec.type.contains(SPECIALIST_TYPE_ADMIN)) Global.getSector().getCharacterData().addAdmin(person);
                if (spec.type.contains(SPECIALIST_TYPE_OFFICER)) Global.getSector().getPlayerFleet().getFleetData().addOfficer(person);
            }
            person.getMemoryWithoutUpdate().set(JDP_NEW_SPECIALIST, true);

            addHireSpecialistIntel(person, spec);
            if(!isBoxChecked(DONT_SHOW_EXPO_AGAIN, false)) showExpositionDialog("You have acquired a new Specialist! \n\nSpecialists can be managed from the Specialists Tab, located above the Character Tab when the Character Tab is open.", CONFIRM_DIALOG_WIDTH, CONFIRM_DIALOG_HEIGHT / 2, DONT_SHOW_EXPO_AGAIN);
            if(getOpenSpecialist() == getBaseSpecialistPerson()) setOpenSpecialist(person);
        }
    }

    fun createApprentice(person: PersonAPI) {
        var specialist = createSpecialist(person, JDP_DUMMY_PROFICIENCY)
        specialist.hired = true
    }
}