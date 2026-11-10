package Jaydee8652.JaydeeProficiencies.ui
import Jaydee8652.JaydeeProficiencies.utils.refl.ReflectionUtils
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.BaseCustomUIPanelPlugin
import com.fs.starfarer.api.ui.ButtonAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.ui.UIComponentAPI
import com.fs.starfarer.api.ui.UIPanelAPI

//Idea shamelessly stolen from Console so it isn't a hard dependency
fun UIPanelAPI.getChildrenCopy(): List<UIComponentAPI> {
    return ReflectionUtils.invoke("getChildrenCopy", this) as List<UIComponentAPI>
}

fun UIPanelAPI.getChildrenNonCopy(): List<UIComponentAPI> {
    return ReflectionUtils.invoke("getChildrenNonCopy", this) as List<UIComponentAPI>
}

fun UIComponentAPI.getParent(): UIPanelAPI? {
    return ReflectionUtils.invoke("getParent", this) as UIPanelAPI
}

fun TooltipMakerAPI.getParentWidget(): UIComponentAPI? {
    return ReflectionUtils.invoke("getParentWidget", this) as UIPanelAPI
}

fun UIComponentAPI.setOpacity(alpha: Float) {
    ReflectionUtils.invoke("setOpacity", this, alpha)
}

fun UIPanelAPI.clearChildren() {
    ReflectionUtils.invoke("clearChildren", this)
}

fun UIComponentAPI.getWidth(): Float {
    return ReflectionUtils.invoke("getWidth", this) as Float
}

fun UIComponentAPI.getHeight(): Float {
    return ReflectionUtils.invoke("getHeight", this) as Float
}

object jdp_UIExtensions {
    @JvmStatic
    fun getChildrenCopy(panel: UIPanelAPI): List<UIComponentAPI> {
        return panel.getChildrenCopy()
    }


    @JvmStatic
    fun getChildrenNonCopy(panel: UIPanelAPI): List<UIComponentAPI> {
        return panel.getChildrenNonCopy()
    }

    @JvmStatic
    fun getParent(component: UIComponentAPI): UIPanelAPI? {
        return component.getParent()
    }

    @JvmStatic
    fun getParentWidget(tooltip: TooltipMakerAPI): UIComponentAPI? {
        return tooltip.getParentWidget()
    }

    @JvmStatic
    fun setOpacity(component: UIComponentAPI, alpha: Float) {
        component.setOpacity(alpha)
    }

    @JvmStatic
    fun clearChildren(panel: UIPanelAPI) {
        return panel.clearChildren()
    }

    @JvmStatic
    fun getWidth(component: UIComponentAPI): Float {
        return component.getWidth()
    }

    @JvmStatic
    fun getHeight(component: UIComponentAPI): Float {
        return component.getHeight()
    }
}