package Jaydee8652.JaydeeProficiencies.specialists.data

import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_DEFAULT_SPECIALIST
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_DUMMY_PROFICIENCY
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getSpecialistData
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.isApprentice
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.isUnassigned
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.returnItem
import Jaydee8652.JaydeeProficiencies.specialists.plugins.jdp_BaseProficiencyPlugin
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_IntelUtils.addSpecialistLevelIntel
import Jaydee8652.JaydeeProficiencies.utils.jdp_SpecStore
import Jaydee8652.JaydeeProficiencies.utils.jdp_SpecStore.jdp_proficiencySpec
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.characters.PersonAPI
import kotlin.collections.getOrNull
import kotlin.collections.iterator

class jdp_Specialist(var person: PersonAPI, var proficiencySpecId: String) {
    var hired = false

    var spec = jdp_SpecStore.getProficiencySpec(proficiencySpecId)
    var experience = 0f
    var level = 1

    var activeSkills = LinkedHashMap<String, Float>()
    var recentExperience = ArrayList<jdp_SpecialistExperienceData>()

    fun addExperience(data: jdp_SpecialistExperienceData) {
        if (proficiencySpecId == JDP_DUMMY_PROFICIENCY && mentor == JDP_DEFAULT_SPECIALIST) return
        if (level != getMaxLevel()) {
            recentExperience.add(data)
            if (recentExperience.size > 3) recentExperience.remove(recentExperience.first())
        }

        experience += data.xp()
        levelUpIfNeeded()
    }

    fun getRequiredXP(level: Int) : Float {
        if (isApprentice(this) || isUnassigned(this)) return (getProficiencySpec().getPlugin().getXPThresholds(true).getOrNull(level) ?: 0f).toFloat()
        return (getProficiencySpec().getPlugin().getXPThresholds(false).getOrNull(level) ?: 0f).toFloat()
    }

    fun getMaxLevel() : Int {
        if (isApprentice(this) || isUnassigned(this)) return getProficiencySpec().apprenticeMaxLevel
        return getProficiencySpec().maxLevel
    }

    fun disableSkills(){
        for (skill in activeSkills()) {
            if(!Global.getSettings().getSkillSpec(skill.key).tags.contains(jdp_SpecialistTags.SKILL_TAG_MARKER)) {
                person.stats.setSkillLevel(skill.key, 0f)
            } else {
                Global.getSettings().getSkillSpec(skill.key).effectsAPI.last().asStatsEffect.unapply(person.stats, jdp_SpecialistTags.SKILL_TAG_MARKER)
            }
        }
    }

    fun enableSkills(){
        for (skill in activeSkills()) {
            if(!Global.getSettings().getSkillSpec(skill.key).tags.contains(jdp_SpecialistTags.SKILL_TAG_MARKER)) {
                person.stats.setSkillLevel(skill.key, skill.value)
            } else {
                Global.getSettings().getSkillSpec(skill.key).effectsAPI.last().asStatsEffect.apply(person.stats, jdp_SpecialistTags.SKILL_TAG_MARKER, skill.value)
            }
        }
    }

    fun levelUpIfNeeded() {
        var required = getRequiredXP(level)
        if (experience < required) return

        if (level >= getMaxLevel()) {
            experience = required
            return
        }

        var diff = experience - required
        diff = diff.coerceAtLeast(0f)

        experience = diff
        level += 1
        var willLevelupAgainWithCurrentXP = (diff > getRequiredXP(level)) && (level != getMaxLevel())
        getProficiencySpec().getPlugin().notifyLevelup(this, willLevelupAgainWithCurrentXP)
        if(!willLevelupAgainWithCurrentXP) addSpecialistLevelIntel(this)

        levelUpIfNeeded()
    }

    fun resetExperience() {
        experience = 0f
        level = 1
        var sections = getProficiencySpec().getPlugin().getSections(false)
        for (section in sections) {
            for (skill in section.skills) {
                if (activeSkills.contains(skill.key)) {
                    if (section.skillItemMap.containsKey(skill.key)) returnItem(section.skillItemMap.get(skill.key))
                    if (skill.value > 1) Global.getSector().playerStats.storyPoints = (Global.getSector().playerStats.storyPoints + 1)
                    removeSkill(skill.key)
                }
            }
        }
    }

    fun getProficiencySpec() : jdp_proficiencySpec {
        var data: jdp_SpecialistData = getSpecialistData()

        if(isApprentice(this)) return data.getSpecialist(mentor)!!.spec!!
        if(isUnassigned(this)) return data.getSpecialist(previousMentor)!!.spec!!

        return jdp_SpecStore.getProficiencySpec(proficiencySpecId)!!
    }

    private var mentor: String = JDP_DEFAULT_SPECIALIST
    private var mentee: String = JDP_DEFAULT_SPECIALIST
    private var previousMentor: String = JDP_DEFAULT_SPECIALIST

    fun setMentor(mentor: String) {
        this.previousMentor = this.mentor
        this.mentor = mentor
    }
    fun getMentor(): String {
        return this.mentor
    }
    fun getPreviousMentor(): String {
        return this.previousMentor
    }
    fun setMentee(mentee: String) {
        this.mentee = mentee
    }
    fun getMentee(): String {
        return this.mentee
    }

    fun getProficiencyPlugin() : jdp_BaseProficiencyPlugin {
        return getProficiencySpec().getPlugin()
    }

    fun person() : PersonAPI {
        return person
    }

    fun activeSkills() : LinkedHashMap<String, Float> {
        return activeSkills
    }
    fun addSkill(id: String, level: Float) {
        if (!activeSkills.contains(id)) {
            activeSkills.put(id, level)
            if(!Global.getSettings().getSkillSpec(id).tags.contains(jdp_SpecialistTags.SKILL_TAG_MARKER)) {
                person.stats.setSkillLevel(id, level)
                person.stats.setLevel(person.stats.getLevel() + 1)
            } else {
                Global.getSettings().getSkillSpec(id).effectsAPI.last().asStatsEffect.apply(person.stats, jdp_SpecialistTags.SKILL_TAG_MARKER, level)
            }
        } else {
            activeSkills.replace(id, level)
            if (!Global.getSettings().getSkillSpec(id).tags.contains(jdp_SpecialistTags.SKILL_TAG_MARKER)) {
                person.stats.setSkillLevel(id, level)
            } else {
                Global.getSettings().getSkillSpec(id).effectsAPI.last().asStatsEffect.apply(person.stats, jdp_SpecialistTags.SKILL_TAG_MARKER, level)
            }
        }
    }
    fun removeSkill(id: String) {

        if (activeSkills.contains(id)) {
            activeSkills.remove(id)
            if(!Global.getSettings().getSkillSpec(id).tags.contains(jdp_SpecialistTags.SKILL_TAG_MARKER)) {
                person.stats.setSkillLevel(id, 0f)
                person.stats.setLevel(person.stats.getLevel() - 1)
            } else {
                Global.getSettings().getSkillSpec(id).effectsAPI.last().asStatsEffect.unapply(person.stats, jdp_SpecialistTags.SKILL_TAG_MARKER)
            }
        }
    }
    fun hasSkill(id: String) : Boolean {
        return activeSkills.contains(id)
    }
    fun getSkillLevel(id: String) : Float {
        return (if (activeSkills.get(id) != null) activeSkills.get(id) else 0f)!!
    }

    fun sectionSkills(sections: List<SpecialistSkillSection>) : LinkedHashMap<String, Float> {
        var skills = LinkedHashMap<String, Float>()
        for (section in sections) {
            skills.putAll(activeSkills.filter { s -> section.skills.contains(s.key) }.toList())
        }
        return skills;
    }
    fun sectionSkills(section: SpecialistSkillSection) : LinkedHashMap<String, Float> {
        var skills = LinkedHashMap<String, Float>()
        skills.putAll(activeSkills.filter { s -> section.skills.contains(s.key) }.toList())
        return skills;
    }
}