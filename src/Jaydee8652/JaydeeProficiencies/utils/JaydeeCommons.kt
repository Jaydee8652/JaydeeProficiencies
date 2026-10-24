package Jaydee8652.JaydeeProficiencies.utils

import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_FactionUtils
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_FactionUtils.presetSlotAKey
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_FactionUtils.presetSlotBKey
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_FactionUtils.presetSlotCKey
import com.fs.starfarer.api.util.Misc
import org.json.JSONArray
import org.json.JSONObject
import org.lazywizard.lazylib.JSONUtils
import java.awt.Color

object JaydeeCommons {
    private var init = false
    private var masterObject: JSONUtils.CommonDataJSONObject = JSONUtils.CommonDataJSONObject("")

    @JvmStatic
    fun set(groupID: String, key: String, value: Any) {
        init()

        var modObject = try {
            masterObject.getJSONObject(groupID)
        } catch (e: Throwable) { JSONObject() }

        modObject.put(key, value)
        masterObject.put(groupID, modObject)
        masterObject.save()
    }

    @JvmStatic
    fun get(groupID: String, key: String) : Any? {
        init()

        var JSONObject = try {
            masterObject.getJSONObject(groupID)
        } catch (e: Throwable) { return null }

        try {
            return JSONObject.get(key)
        } catch (e: Throwable) { return null }
    }

    @JvmStatic
    fun getGroup(groupID: String) : JSONObject? {
        init()

        var JSONObject = try {
            masterObject.getJSONObject(groupID)
        } catch (e: Throwable) { return JSONObject() }
        return JSONObject
    }

    @JvmStatic
    fun getString(groupID: String, key: String) : String? {
        return get(groupID, key) as String?
    }

    @JvmStatic
    fun getColor(groupID: String, key: String) : Color? {
        return Misc.getColor(getGroup(groupID), key)
    }

    @JvmStatic
    fun getInt(groupID: String, key: String) : Int? {
        return get(groupID, key) as Int?
    }

    @JvmStatic
    fun getDouble(groupID: String, key: String) : Double? {
        return get(groupID, key) as Double?
    }

    @JvmStatic
    fun getBoolean(groupID: String, key: String) : Boolean? {
        return get(groupID, key) as Boolean?
    }

    @JvmStatic
    fun remove(groupID: String, key: String) {
        var JSONObject = try {
            masterObject.getJSONObject(groupID)
        } catch (e: Throwable) { return  }

        try {
            JSONObject.remove(key)
        } catch (e: Throwable) { return }

        masterObject.save()
    }

    @JvmStatic
    fun populate() {
        var presetA = Color(148,121,255,255)
        populateColor(presetSlotAKey, jdp_FactionUtils.baseUIColorKey, presetA)
        populateColor(presetSlotAKey, jdp_FactionUtils.gridUIColorKey, jdp_FactionUtils.createGridUIColor(presetA))
        populateColor(presetSlotAKey, jdp_FactionUtils.brightUIColorKey,  jdp_FactionUtils.createBrightUIColor(presetA))
        populateColor(presetSlotAKey, jdp_FactionUtils.darkUIColorKey,  jdp_FactionUtils.createDarkUIColor(presetA))

        var presetB = Color(108,200,25,255)
        populateColor(presetSlotBKey, jdp_FactionUtils.baseUIColorKey, presetB)
        populateColor(presetSlotBKey, jdp_FactionUtils.gridUIColorKey, jdp_FactionUtils.createGridUIColor(presetB))
        populateColor(presetSlotBKey, jdp_FactionUtils.brightUIColorKey,  jdp_FactionUtils.createBrightUIColor(presetB))
        populateColor(presetSlotBKey, jdp_FactionUtils.darkUIColorKey,  jdp_FactionUtils.createDarkUIColor(presetB))

        var presetC = Color(255,100,85,255)
        populateColor(presetSlotCKey, jdp_FactionUtils.baseUIColorKey, presetC)
        populateColor(presetSlotCKey, jdp_FactionUtils.gridUIColorKey, jdp_FactionUtils.createGridUIColor(presetC))
        populateColor(presetSlotCKey, jdp_FactionUtils.brightUIColorKey,  jdp_FactionUtils.createBrightUIColor(presetC))
        populateColor(presetSlotCKey, jdp_FactionUtils.darkUIColorKey,  jdp_FactionUtils.createDarkUIColor(presetC))
    }

    @JvmStatic
    fun populateColor(groupID: String, key: String, color: Color) {
        var modObject = try {
            masterObject.getJSONObject(groupID)
        } catch (e: Throwable) { JSONObject() }

        var array = JSONArray()
        array.put(color.red)
        array.put(color.green)
        array.put(color.blue)
        array.put(color.alpha)

        modObject.put(key, array)
        masterObject.put(groupID, modObject)
        masterObject.save()
    }

    @JvmStatic
    fun setColor(groupID: String, key: String, color: Color) {
        var array = JSONArray()
        array.put(color.red)
        array.put(color.green)
        array.put(color.blue)
        array.put(color.alpha)
        set(groupID, key, array)
    }

    @JvmStatic
    fun init() {
        if (init) return
        masterObject = JSONUtils.loadCommonJSON("JaydeeCommons.json")
        masterObject.save()

        if(!masterObject.has(presetSlotAKey)) populate()
        init = true
    }
}