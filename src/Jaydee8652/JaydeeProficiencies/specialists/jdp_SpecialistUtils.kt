package Jaydee8652.JaydeeProficiencies.specialists

import Jaydee8652.JaydeeProficiencies.JaydeeProficienciesPlugin
import Jaydee8652.JaydeeProficiencies.specialists.data.SpecialistSkillSection
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_PanelData
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_Specialist
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_SpecialistData
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_ArrowElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_FactionElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_SkillSeperatorElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_SkillWidgetElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_SpecialistBackgroundElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_SpecialistPickerElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_TypeElement
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_UnderlineElement
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistScreen.recreateMain
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_DEFAULT_SPECIALIST
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_DUMMY_PROFICIENCY
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_ROOT_SECTION
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_SPECIALIST_DATA
import Jaydee8652.JaydeeProficiencies.specialists.plugins.jdp_ColorWheelPlugin
import Jaydee8652.JaydeeProficiencies.specialists.tooltips.SpecialistSkillTooltipCreator
import Jaydee8652.JaydeeProficiencies.specialists.tooltips.SpecialistTooltipCreator
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_IntelUtils.assignApprentice
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_IntelUtils.createApprenticePickerPanel
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_IntelUtils.fireRuleFromIntel
import Jaydee8652.JaydeeProficiencies.utils.jdp_SpecStore
import Jaydee8652.JaydeeProficiencies.utils.jdp_SpecStore.jdp_proficiencySpec
import com.fs.starfarer.api.Global
import Jaydee8652.JaydeeProficiencies.utils.refl.*;
import com.fs.starfarer.api.campaign.CargoAPI
import com.fs.starfarer.api.campaign.CargoStackAPI
import com.fs.starfarer.api.campaign.FactionAPI
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.characters.AdminData
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI
import com.fs.starfarer.api.characters.OfficerDataAPI
import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.api.characters.SkillSpecAPI
import com.fs.starfarer.api.impl.campaign.ids.Factions
import com.fs.starfarer.api.ui.CustomPanelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI.TooltipLocation
import com.fs.starfarer.api.ui.UIPanelAPI
import com.fs.starfarer.api.util.Misc
import com.fs.starfarer.ui.impl.StandardTooltipV2
import indevo.industries.academy.industry.Academy
import lunalib.lunaExtensions.addLunaElement
import lunalib.lunaUI.elements.LunaElement
import org.apache.log4j.Logger

object jdp_SpecialistUtils {
    val log: Logger = Global.getLogger(JaydeeProficienciesPlugin::class.java)

    @JvmStatic
    fun getSpecialistData(): jdp_SpecialistData {
        var data: jdp_SpecialistData

        if (Global.getSector().memoryWithoutUpdate.contains(JDP_SPECIALIST_DATA)) {
            data = Global.getSector().memoryWithoutUpdate.get(JDP_SPECIALIST_DATA) as jdp_SpecialistData
        } else {
            data = jdp_SpecialistData()
            data.init()
            Global.getSector().memoryWithoutUpdate.set(JDP_SPECIALIST_DATA, data)
        }
        return data
    }

    @JvmStatic
    fun invokeCreateSkillTooltip(spec: SkillSpecAPI, stats: MutableCharacterStatsAPI): Any? {
        var tooltip = ReflectionUtils.invokeStatic(8, "createSkillTooltip", StandardTooltipV2::class.java,
            spec, stats,
            800f, 10f, !spec.isAdminSkill(), spec.isAdminSkill(), 1000, null)
        return tooltip
    }

    @JvmStatic
    fun getBaseSpecialistPerson(): PersonAPI {
        var jdp_specialist_person = Global.getFactory().createPerson();

        jdp_specialist_person.setId(JDP_DEFAULT_SPECIALIST)
        jdp_specialist_person.setFaction(Factions.PLAYER)

        jdp_specialist_person.setPortraitSprite(Global.getSettings().getSpriteName("misc", "default_portrait"));

        if (!Global.getSector().importantPeople.containsPerson(jdp_specialist_person)) {
            Global.getSector().importantPeople.addPerson(jdp_specialist_person);
        }

        return Global.getSector().importantPeople.getPerson(JDP_DEFAULT_SPECIALIST)
    }

    @JvmStatic
    fun isBaseSpecialistPerson(person: PersonAPI): Boolean {
        return getBaseSpecialistPerson().id.equals(person.id)
    }

    @JvmStatic
    fun applyPickerOnClick(specialistPickerElement: LunaElement, specialist: jdp_Specialist, panelData: jdp_PanelData) {
        specialistPickerElement.onClick {
            var person = getOpenSpecialist();
            if (person.id.equals(specialist.person.getId())) {
                specialistPickerElement.playSound("ui_char_decrease_skill", 1f, 1f);
            } else {
                specialistPickerElement.playClickSound();
                setOpenSpecialist(specialist.person)
                recreateMain(panelData);
            }
        }
    }

    @JvmStatic
    fun createFakeSkill(id: String, level: Float): MutableCharacterStatsAPI.SkillLevelAPI {
        var fake = Global.getFactory().createPerson();
        fake.setFaction(Factions.SLEEPER);
        fake.stats.setSkillLevel(id, level);
        var skills = fake.stats.skillsCopy.filter { s -> s.skill.id == id && s.level == level }
        return skills.get(0)
    }

    @JvmStatic
    fun isAbsent(specialist: jdp_Specialist): Boolean {
        if (specialist.getProficiencySpec().tags.contains(jdp_SpecialistTags.SPEC_TAG_AI_PERSON)) return false

        var specialists = ArrayList<String>()
        specialists.addAll(Global.getSector().characterData.admins.map { it.person.id })
        specialists.addAll(Global.getSector().playerFleet.fleetData.officersCopy.map { it.person.id })
        specialists.add(Global.getSector().playerPerson.id)

        return !specialists.contains(specialist.person.id)
    }

    @JvmStatic
    fun getOfficers(): List<PersonAPI> {
        var officers = ArrayList<PersonAPI>()
        officers.addAll(Global.getSector().playerFleet.fleetData.officersCopy.map { it.person })

        if (Global.getSettings().getModManager().isModEnabled("IndEvo")) {
            for (market in Global.getSector().getEconomy().marketsCopy) {
                if (market.hasIndustry("IndEvo_Academy")) {
                    var academy = market.getIndustry("IndEvo_Academy") as Academy
                    if (!academy.officerStorage.isEmpty) officers.addAll(academy.officerStorage)
                    if (academy.officerInTraining != null) officers.add(academy.officerInTraining)
                }
            }
        }
        return officers
    }

    @JvmStatic
    fun isAdmin(person: PersonAPI): Boolean {
        return Global.getSector().characterData.admins.map { a -> a.person }.contains(person)
    }

    @JvmStatic
    fun isOfficer(person: PersonAPI): Boolean {
        return Global.getSector().playerFleet.fleetData.officersCopy.map { o -> o.person }.contains(person)
    }

    @JvmStatic
    fun getAdmins(): List<PersonAPI> {
        var admins = ArrayList<PersonAPI>()
        admins.addAll(Global.getSector().characterData.admins.map { it.person })

        if (Global.getSettings().getModManager().isModEnabled("IndEvo")) {
            for (market in Global.getSector().getEconomy().marketsCopy) {
                if (market.hasIndustry("IndEvo_Academy")) {
                    var academy = market.getIndustry("IndEvo_Academy") as Academy
                    if (!academy.adminStorage.isEmpty) admins.addAll(academy.adminStorage)
                    if (academy.adminInTraining != null) admins.add(academy.adminInTraining)
                }
            }
        }
        return admins
    }

    @JvmStatic
    fun getAdmin(id: String): AdminData? {
        var admins = ArrayList<AdminData>()
        admins.addAll(Global.getSector().characterData.admins.map { it })
        admins = admins.filter { a -> a.person.id == id } as ArrayList<AdminData>

        if(admins.size > 1) log.warn("JDP_DEBUG: Warning! Two admins with the same id encountered!");
        if(!admins.isEmpty) {
            return admins.get(0)
        } else {
            return null
        }
    }

    @JvmStatic
    fun getOfficer(id: String): OfficerDataAPI? {
        var officers = ArrayList<OfficerDataAPI>()
        officers.addAll(Global.getSector().playerFleet.fleetData.officersCopy)

        officers = officers.filter { o -> o.person.id == id } as ArrayList<OfficerDataAPI>
        if(officers.size > 1) log.warn("JDP_DEBUG: Warning! Two officers with the same id encountered!");
        if(!officers.isEmpty) {
            return officers.get(0)
        } else {
            return null
        }
    }

    @JvmStatic
    fun itemEquals(a: CargoStackAPI?, b: CargoStackAPI?): Boolean {
        if(a == null && b == null) return true
        if(a == null || b == null) return false
        return (a.data == b.data && a.size == b.size && a.type == b.type)
    }

    @JvmStatic
    fun itemAvailable(req: CargoStackAPI?): Boolean {
        if(req == null) return true
        return getNumAvailable(req) >= req.size
    }


    @JvmStatic
    fun itemAvailable(specialist: jdp_Specialist, section: SpecialistSkillSection, req: CargoStackAPI?): Boolean {
        return itemEquals(specialist.sectionSkills(section).map { s -> section.skillItemMap.get(s.key) }.firstOrNull(), req)
    }

    @JvmStatic
    fun getNumAvailable(req: CargoStackAPI): Int {
        val fleet = Global.getSector().playerFleet ?: return 0
        return kotlin.math.round(fleet.cargo.getQuantity(req.type, req.data)).toInt()
    }

    @JvmStatic
    fun consumeItem(req: CargoStackAPI?) {
        if (req == null) return

        val fleet = Global.getSector().playerFleet ?: return
        var cargo = fleet.cargo

        if (cargo.getQuantity(req.type, req.data) >= req.size) {
            cargo.removeItems(req.type, req.data, req.size)
        }
    }

    @JvmStatic
    fun returnItem(req: CargoStackAPI?) {
        if (req == null) return

        val fleet = Global.getSector().playerFleet ?: return
        var cargo = fleet.cargo
        cargo.addItems(req.type, req.data, req.size)
    }

    @JvmStatic
    fun createCargoStack(type: CargoAPI.CargoItemType, data: Object, count: Float): CargoStackAPI {
        var cargo = Global.getFactory().createCargoStack(type, data, null);
        cargo.setSize(count);
        return cargo;
    }

    @JvmStatic
    fun createCargoStack(type: CargoAPI.CargoItemType, data: Object): CargoStackAPI {
        return createCargoStack(type, data, 1f)
    }

    @JvmStatic
    fun getPerson(stats: MutableCharacterStatsAPI): PersonAPI? {
        var people = ArrayList<PersonAPI>()
        people.addAll(getAdmins())
        people.addAll(getOfficers())
        people.add(Global.getSector().playerPerson)
        people = people.filter { p -> p.stats == stats } as ArrayList<PersonAPI>

        if(people.size > 1) log.warn("JDP_DEBUG: Warning! Two people with the same stats encountered!");
        if(!people.isEmpty) {
            return people.get(0)
        } else {
            return null
        }
    }

    @JvmStatic
    fun getPersonAdmin(stats: MutableCharacterStatsAPI): AdminData? {
        var admins = ArrayList<AdminData>()
        admins.addAll(Global.getSector().characterData.admins.map { it })
        admins = admins.filter { a -> a.person.stats == stats } as ArrayList<AdminData>

        if(admins.size > 1) log.warn("JDP_DEBUG: Warning! Two admins with the same stats encountered!");
        if(!admins.isEmpty) {
            return admins.get(0)
        } else {
            return null
        }
    }

    @JvmStatic
    fun getPersonOfficer(stats: MutableCharacterStatsAPI): OfficerDataAPI? {
        var officers = ArrayList<OfficerDataAPI>()
        officers.addAll(Global.getSector().playerFleet.fleetData.officersCopy.map { it })
        officers = officers.filter { o -> o.person.stats == stats } as ArrayList<OfficerDataAPI>

        if(officers.size > 1) log.warn("JDP_DEBUG: Warning! Two officers with the same stats encountered!");
        if(!officers.isEmpty) {
            return officers.get(0)
        } else {
            return null
        }
    }

    @JvmStatic
    fun getPlayerGovernedColonies(): List<MarketAPI> {
        return Global.getSector().economy.marketsCopy.filter { m -> m.getAdmin().isPlayer() }.toList()
    }

    @JvmStatic
    fun isApprentice(specialist: jdp_Specialist): Boolean {
        return (specialist.proficiencySpecId == JDP_DUMMY_PROFICIENCY && specialist.getMentor() != JDP_DEFAULT_SPECIALIST)
    }

    @JvmStatic
    fun isUnassigned(specialist: jdp_Specialist): Boolean {
        return (specialist.proficiencySpecId == JDP_DUMMY_PROFICIENCY && specialist.getMentor() == JDP_DEFAULT_SPECIALIST && specialist.getPreviousMentor() != JDP_DEFAULT_SPECIALIST)
    }

    @JvmStatic
    fun createDisplaySkillSection(specialistElement: TooltipMakerAPI, person: PersonAPI, isMentor: Boolean, backgroundWidth: Float, pickerSize: Float) {
        var skillSize = 64f / 1.5f
        var typeSize = 20f / 1.5f
        var gap = 3f / 1.5f;
        var backgroundHeight = (skillSize / 2f) * 0.9f;

        var data: jdp_SpecialistData = getSpecialistData()

        var mentor: jdp_Specialist = data.getSpecialist(person.id)!!
        var mentee: jdp_Specialist = data.getSpecialist(mentor.getMentee())!!

        var specialist = mentor
        if (!isMentor) specialist = mentee

        var sections = mentor.getProficiencyPlugin().getSections(isMentor)
        var spec = mentor.getProficiencySpec()
        var color = spec.color

        var specialistPickerElement = jdp_SpecialistPickerElement(specialist.person, color, specialistElement, pickerSize, pickerSize)
        specialistPickerElement.isLocked = true
        specialistElement.addTooltipTo(SpecialistTooltipCreator(specialistPickerElement, person), specialistPickerElement.elementPanel, TooltipLocation.RIGHT)

        specialistPickerElement.onHoverEnter {
            specialistPickerElement.playScrollSound()
        }

        specialistPickerElement.onClick {
            specialistPickerElement.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
            return@onClick
        }

        var previous: CustomPanelAPI? = specialistPickerElement.elementPanel

        var spacer = LunaElement(specialistElement, 0f, 0f);
        spacer.position.inTL(pickerSize + 12f - gap, 0f)
        previous = spacer.elementPanel

        if(isMentor) {
            for (type in spec.type) {
                var typeDisplay = jdp_TypeElement(type, person, true, specialistElement, typeSize, typeSize)
                typeDisplay.position.rightOfTop(previous, 3f)
                previous = typeDisplay.elementPanel
            }

            var level = 1;

            var root = sections.stream().filter { s -> s.sectionId.equals(JDP_ROOT_SECTION) }.toList()
            if (!root.isEmpty()) {
                if(!root.get(0).isMutuallyExclusive) level = root.get(0).skills.size
            }

            var text = "Level " + level + " / " + spec.maxLevel
            var name = specialistElement.addPara(text, Misc.getTextColor(), 3f)
            name.position.rightOfMid(previous, gap * 3f)
            specialistElement.setParaFont("graphics/fonts/victor14.fnt")
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

            var level = 1;

            var root = sections.stream().filter { s -> s.sectionId.equals(JDP_ROOT_SECTION) }.toList()
            if (!root.isEmpty()) {
                if(!root.get(0).isMutuallyExclusive) level = root.get(0).skills.size
            }

            var text = "Level " + level + " / " + spec.apprenticeMaxLevel
            var name = specialistElement.addPara(text, Misc.getGrayColor(), 3f)
            name.position.rightOfMid(previous, gap * 3f)
            specialistElement.setParaFont("graphics/fonts/victor14.fnt")
        }

        var offsetElement = specialistElement.addLunaElement(0f, 0f)
        offsetElement.elementPanel.position.rightOfTop(specialistPickerElement.elementPanel, 0f)

        var background = jdp_SpecialistBackgroundElement(color, backgroundWidth, backgroundHeight, specialistElement)
        background.elementPanel.position.belowLeft(offsetElement.elementPanel, (pickerSize / 2f) + (pickerSize * 0.125f) + ((skillSize / 2f) * 0.05f))

        //Offsets the skill widgets
        var offset = LunaElement(specialistElement, 0f, 0f).position.inTL(pickerSize + 12f, ((pickerSize / 2f) - skillSize / 2f) + pickerSize * 0.125f)

        previous = null

        //Add every section to the UI
        var skillDisplays = ArrayList<jdp_SkillWidgetElement>()
        for (section in sections) {
            var isLastSection = sections.last() == section
            var usedWidth = 0f

            for (skill in section.skills) {
                var skillId = skill.key;
                var skillMaxLevel = skill.value

                var aptitude = jdp_SpecStore.getNormalAptitudeSpec(Global.getSettings().getSkillSpec(skillId).governingAptitudeId)
                var isLast = section.skills.entries.last() == skill

                var skillDisplay = jdp_SkillWidgetElement(skillId, skillMaxLevel, true, true, section, specialistElement, skillSize, skillSize)
                usedWidth += skillSize

                section.skillElements.add(skillDisplay)
                skillDisplays.add(skillDisplay)

                SpecialistSkillTooltipCreator().createDecorativeTooltip(skillDisplay, skillId, skillMaxLevel, person)

                skillDisplay.onClick {
                    skillDisplay.playSound("ui_char_can_not_increase_skill_or_aptitude", 1f, 1f)
                    return@onClick
                }

                if (previous != null) {
                    skillDisplay.position.rightOfTop(previous, gap)
                    if(!isLast) usedWidth += gap;
                }

                previous = skillDisplay.elementPanel
                if (!isLast) {
                    var seperator = jdp_SkillSeperatorElement(color, specialistElement, skillSize)
                    seperator.elementPanel.position.rightOfTop(skillDisplay.elementPanel, gap)
                    previous = seperator.elementPanel
                } else {
                    if (section.isMutuallyExclusive) {
                        val underline = jdp_UnderlineElement(color, 2f, specialistElement, usedWidth)
                        underline.position.belowLeft(section.skillElements.get(0).elementPanel, 2f)
                    }
                }
            }

            if (!isLastSection) {
                var arrowElement = jdp_ArrowElement(false, color, specialistElement, 12f, 0f, skillSize, skillSize * 0.1f);
                arrowElement.position.rightOfTop(previous, gap);
                previous = arrowElement.elementPanel
            }
        }
    }

    @JvmStatic
    fun getOpenSpecialist(): PersonAPI {
        var person = Global.getSector().getPersistentData().get("jdp_OpenSpecialist")?: getBaseSpecialistPerson()
        return person as PersonAPI
    }

    @JvmStatic
    fun setOpenSpecialist(person: PersonAPI)  {
        Global.getSector().getPersistentData().put("jdp_OpenSpecialist", person)
    }

    @JvmStatic
    fun clearChildren(panel: Any)  {
        ReflectionUtils.invoke("clearChildren", panel)
    }

    fun UIPanelAPI.clearChildren() {
        ReflectionUtils.invoke("clearChildren", this)
    }

    fun UIPanelAPI.getHeight() : Float  {
        return ReflectionUtils.invoke("getHeight", this) as Float
    }
}