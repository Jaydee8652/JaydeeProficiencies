package Jaydee8652.JaydeeProficiencies.specialists.data

import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_SkillWidgetElement
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_ROOT_SECTION
import com.fs.starfarer.api.campaign.CargoStackAPI

class SpecialistSkillSection(var previousSkillsRequirement: Int) {
    /**Add skill to section, can specify a max level and required item. Skills with a max level of one cannot be made elite, max level of two cn be made elite with
     * story points, higher are not supported.
     *
     * Create required items with the createCargoStack method in SpecialistUtils*/
    fun addSkill(id: String) {
        addSkill(id, 1f)
    }
    fun addSkill(id: String, maxLevel: Float) {
        skills.put(id, maxLevel)
    }
    fun addSkill(id: String, requiredItem: CargoStackAPI) {
        addSkill(id, 1f, requiredItem)
    }
    fun addSkill(id: String, maxLevel: Float, requiredItem: CargoStackAPI) {
        skills.put(id, maxLevel)

        if(requiredItem.size == 0f) requiredItem.add(1f)
        skillItemMap.put(id, requiredItem)
    }
    /**Set section as mutually exclusive, only one skill can be active at once, assigning a skill in a mutually exclusive section unassigns all others*/
    fun setMutuallyExclusive() {
        isMutuallyExclusive = true;
    }

    /**Internal Use Only** --------------------------------------------------------------------------------------------------------*/
    var sectionId: String = JDP_ROOT_SECTION
    var soundId: String = "sound1"

    var isMutuallyExclusive = false

    var isTierActive = true
    var isTierLocked = false
    var lockedBy: String? = null

    var previousSections = ArrayList<SpecialistSkillSection>()
    var nextSection: SpecialistSkillSection? = null

    var skills = HashMap<String, Float>()
    var skillItemMap = HashMap<String, CargoStackAPI>()

    var skillElements = ArrayList<jdp_SkillWidgetElement>()

    fun setLocked(locked: Boolean, source: String) {
        isTierLocked = locked;
        lockedBy = source;
    }

    fun setLocked(locked: Boolean) {
        isTierLocked = locked;
        lockedBy = null;
    }
}