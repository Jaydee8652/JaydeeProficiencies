package Jaydee8652.JaydeeProficiencies.utils;

import Jaydee8652.JaydeeProficiencies.JaydeeProficienciesPlugin
import Jaydee8652.JaydeeProficiencies.specialists.plugins.jdp_BaseProficiencyPlugin
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ModSpecAPI
import org.apache.log4j.Logger
import java.awt.Color
import java.util.Arrays

object jdp_SpecStore {
    val log: Logger = Global.getLogger(JaydeeProficienciesPlugin::class.java)

    data class jdp_normalAptitudeData(
        var id: String,
        var name: String,
        var color: Color,
        var description: String,
        var order: Int,
        var effect_skill_id: String,
        var sounds: HashMap<String, String>,
        var elite_overlay: String,
        var elite_overlay_medium: String,
        var elite_overlay_small: String) {

        fun getBannerAverageColor() : Color {
            return Global.getSettings().getSprite(elite_overlay).averageColor
        }
    }

    data class jdp_proficiencySpec(
        var id: String,
        var name: String,
        var color: Color,
        var tags: List<String>,
        var type: List<String>,
        var mentor: List<String>,
        var modSpec: ModSpecAPI,
        var maxLevel: Int,
        var apprenticeMaxLevel: Int,
        var pluginPath: String) {

        private var plugin: jdp_BaseProficiencyPlugin? = null

        fun getPlugin() : jdp_BaseProficiencyPlugin {
            if (plugin == null) {
                plugin = Global.getSettings().scriptClassLoader.loadClass(this.pluginPath).newInstance() as jdp_BaseProficiencyPlugin
                plugin!!.spec = this
            }
            return plugin!!
        }
        fun getColorTransparent() : Color {
            return Color(color.red, color.green, color.blue, 175)
        }
     }


    private var normalAptitudeSpecs = ArrayList<jdp_normalAptitudeData>()
    private var normalAptitudeSpecsMap = HashMap<String, jdp_normalAptitudeData>()

    @JvmStatic
    fun normalAptitudeSpecs() = normalAptitudeSpecs
    @JvmStatic
    fun getNormalAptitudeSpec(specId: String) = normalAptitudeSpecsMap.get(specId)
    @JvmStatic
    fun loadNormalAptitudeSpecsFromCSV() {
        var CSV = Global.getSettings().getMergedSpreadsheetDataForMod("id", "data/characters/skills/aptitude_data.csv", "JaydeeProficiencies")

        for (index in 0 until  CSV.length()) {
            val row = CSV.getJSONObject(index)

            val id = row.getString("id")
            if (id.startsWith("#") || id == "") continue

            val order = row.getInt("order")

            val name = row.getString("name")
            val description = row.getString("description")
            val effect_skill_id = row.getString("effect_skill_id")

            var sounds = HashMap<String, String>()
            sounds.put("sound1", row.getString("sound1"))
            sounds.put("sound2", row.getString("sound2"))
            sounds.put("sound3", row.getString("sound3"))
            sounds.put("sound4", row.getString("sound4"))
            sounds.put("sound5", row.getString("sound5"))
            sounds.put("sound_story", row.getString("sound_story"))

            val elite_overlay = row.getString("elite_overlay")
            val elite_overlay_medium = row.getString("elite_overlay_medium")
            val elite_overlay_small = row.getString("elite_overlay_small")

            val colorString = row.getString("color")
            val cs = colorString.split(",").map { it.trim().toInt() }
            val color = Color(cs[0], cs[1], cs[2], cs[3])

            var spec = jdp_normalAptitudeData(id, name, color, description, order, effect_skill_id, sounds, elite_overlay, elite_overlay_medium, elite_overlay_small)
            normalAptitudeSpecs.add(spec)
            normalAptitudeSpecsMap.put(id, spec)
            log.info("JaydeeProficiencies: Loaded " + name + " Aptitude")
        }

        log.info("JaydeeProficiencies: Loaded ${normalAptitudeSpecs.count()} Vanilla/Modded Aptitude Specs.")
    }

    private var proficiencySpecs = ArrayList<jdp_proficiencySpec>()
    private var proficiencySpecsMap = HashMap<String, jdp_proficiencySpec>()
    @JvmStatic
    fun proficiencySpecs() = proficiencySpecs
    @JvmStatic
    fun getProficiencySpec(specId: String) = proficiencySpecsMap.get(specId)
    @JvmStatic
    fun loadProficiencySpecsFromCSV() {
        var CSV = Global.getSettings().getMergedSpreadsheetDataForMod("id", "data/config/JaydeeProficiencies/Proficiencies.csv", "JaydeeProficiencies")

        var order = 0
        for (index in 0 until  CSV.length()) {
            order++
            val row = CSV.getJSONObject(index)

            val id = row.getString("id")
            if (id.startsWith("#") || id == "") continue
            val name = row.getString("name")

            var type = row.getString("type").split(",").map { it.trim() }
            var mentor = row.getString("mentor").split(",").map { it.trim() }

            val maxLevel = row.getInt("maxLevel")
            val apprenticeMaxLevel = row.getInt("apprenticeMaxLevel")

            val colorString = row.getString("color")
            val cs = colorString.split(",").map { it.trim().toInt() }
            val color = Color(cs[0], cs[1], cs[2], cs[3])

            var tags = row.getString("tags").split(",").map { it.trim() }

            var modName = filterModPath(row.getString("fs_rowSource"))
            var modSpec = Global.getSettings().modManager.enabledModsCopy.find { it.dirName == modName }!!

            val pluginPath = row.getString("plugin")

            var spec = jdp_proficiencySpec(id, name, color, tags, type, mentor, modSpec, maxLevel, apprenticeMaxLevel, pluginPath)
            proficiencySpecs.add(spec)
            proficiencySpecsMap.put(id, spec)
            log.info("JaydeeProficiencies: Loaded " + name + " Proficiency")
        }
        log.info("JaydeeProficiencies: Loaded ${proficiencySpecs.count()} Vanilla/Modded Proficiency Specs.")
    }

    private var hullmodIncompatibilitiesMap = HashMap<String, ArrayList<String>>()
    @JvmStatic
    fun getHullmodIncompatibilities(id: String): ArrayList<String> {
        if (hullmodIncompatibilitiesMap.contains(id)) return hullmodIncompatibilitiesMap.get(id)!!
        return ArrayList<String>()
    }
    @JvmStatic
    fun loadHullmodIncompatibilitiesFromCSV() {
        var CSV = Global.getSettings().getMergedSpreadsheetDataForMod("id", "data/config/JaydeeProficiencies/HullmodIncompatibilities.csv", "JaydeeProficiencies")

        var order = 0
        for (index in 0 until  CSV.length()) {
            order++
            val row = CSV.getJSONObject(index)

            val id = row.getString("id")
            if (id.startsWith("#") || id == "") continue
            var incompatibilities = row.getString("incompatibilities").split(",").map { it.trim() } as ArrayList<String>
            if (incompatibilities.isEmpty()) continue
            if (incompatibilities.contains(id)) incompatibilities.remove(id)

            if(!hullmodIncompatibilitiesMap.contains(id)) {
                hullmodIncompatibilitiesMap.put(id, incompatibilities)
            } else {
                var existing = hullmodIncompatibilitiesMap.get(id)!!
                for (incomptibility in incompatibilities) {
                    if(!existing.contains(incomptibility)) existing.add(incomptibility)
                }
                hullmodIncompatibilitiesMap.replace(id, existing)
            }
            log.info("JaydeeProficiencies: Loaded hullmod incompatibility data for " + id)
        }


        var toAdd = HashMap<String, ArrayList<String>>()
        for (data in hullmodIncompatibilitiesMap) {
            var id = data.key
            var incomptibilities = data.value

            for (incomptibility in incomptibilities) {
                if(!toAdd.contains(incomptibility)) {
                    toAdd.put(incomptibility, ArrayList<String>(Arrays.asList(id)))
                } else {
                    var existing = toAdd.get(incomptibility)!!
                    if (!existing.contains(id)) existing.add(id)
                    toAdd.replace(incomptibility, existing)
                }
            }
        }
        for (new in toAdd) {
            var id = new.key
            var incompatibilities = new.value

            if(!hullmodIncompatibilitiesMap.contains(id)) {
                hullmodIncompatibilitiesMap.put(id, incompatibilities)
            } else {
                var existing = hullmodIncompatibilitiesMap.get(id)!!
                for (incomptibility in incompatibilities) {
                    if(!existing.contains(incomptibility)) existing.add(incomptibility)
                }
                hullmodIncompatibilitiesMap.replace(id, existing)
            }

        }

        var test2 = hullmodIncompatibilitiesMap

        log.info("JaydeeProficiencies: Loaded hullmod incompatibility data for ${hullmodIncompatibilitiesMap.count()} Vanilla/Modded hullmods.")
    }

    //From Console Commands by LazyWizard
    private fun filterModPath(fullPath: String): String {
        var modPath = fullPath.replace("/", "\\")
        modPath = modPath.substring(modPath.lastIndexOf("\\mods\\"))
        modPath = modPath.substring(0, modPath.indexOf('\\', 6)) + "\\"
        modPath = modPath.replace("\\mods\\", "")
        modPath = modPath.replace("\\", "")
        return modPath
    }
}