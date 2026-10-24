package Jaydee8652.JaydeeProficiencies.utils.campaign;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;
import java.awt.*;

public class jdp_FactionUtils {
	public static Color colorDefault = new Color(170,222,255, 127);
	public static Color baseUIColorDefault = new Color(170,222,255,255);
	public static Color gridUIColorDefault = new Color(70,200,255,75);
	public static Color darkUIColorDefault = new Color(31,94,112,175);
	public static Color brightUIColorDefault = new Color(182,230,255,255);

	public static String presetKey = "jdp_presetFactionColorID";

	public static String colorKey = "jdp_playerFactionColor";
	public static String baseUIColorKey = "jdp_playerFactionBaseUIColor";
	public static String gridUIColorKey = "jdp_playerFactionGridUIColor";
	public static String darkUIColorKey = "jdp_playerFactionDarkUIColor";
	public static String brightUIColorKey = "jdp_playerFactionBrightUIColor";

	public static String presetSlotAKey = "Preset Slot A";
	public static String presetSlotBKey = "Preset Slot B";
	public static String presetSlotCKey = "Preset Slot C";

	public static Color createBrightUIColor (Color base) {
		var highlight = Misc.getTooltipTitleAndLightHighlightColor();

		float red =  base.getRed() + (highlight.getRed() - (base.getRed())) * 0.35f;
		float green =  base.getGreen() + (highlight.getGreen() - (base.getGreen())) * 0.35f;
		float blue =  base.getBlue() + (highlight.getBlue() - (base.getBlue())) * 0.35f;
		float alpha = base.getAlpha() + (255 - (base.getAlpha())) * 0.35f;

		return new Color(Math.round(MathUtils.clamp(red, 0, 255)), Math.round(MathUtils.clamp(green, 0, 255)), Math.round(MathUtils.clamp(blue, 0, 255)), Math.round(MathUtils.clamp(alpha, 0, 255)));
	}

	public static Color createDarkUIColor (Color base) {
		return new Color(Math.round(base.getRed() * 0.4f), Math.round(base.getGreen() * 0.4f), Math.round(base.getBlue() * 0.4f), 175);
	}

	public static Color createGridUIColor (Color base) {
		return new Color(base.getRed(), base.getGreen(), base.getBlue(), 75);
	}

	public static void updatePlayerFaction() {
		if(!Global.getSector().getPersistentData().containsKey(colorKey)) Global.getSector().getPersistentData().put(colorKey, colorDefault);
		if(!Global.getSector().getPersistentData().containsKey(baseUIColorKey)) Global.getSector().getPersistentData().put(baseUIColorKey, baseUIColorDefault);
		if(!Global.getSector().getPersistentData().containsKey(gridUIColorKey)) Global.getSector().getPersistentData().put(gridUIColorKey, gridUIColorDefault);
		if(!Global.getSector().getPersistentData().containsKey(darkUIColorKey)) Global.getSector().getPersistentData().put(darkUIColorKey, darkUIColorDefault);
		if(!Global.getSector().getPersistentData().containsKey(brightUIColorKey)) Global.getSector().getPersistentData().put(brightUIColorKey, brightUIColorDefault);
		if(!Global.getSector().getPersistentData().containsKey(presetKey)) Global.getSector().getPersistentData().put(presetKey, Global.getSector().getPlayerFaction().getId());

		var spec = Global.getSector().getPlayerFaction().getFactionSpec();

		spec.setColor((Color) Global.getSector().getPersistentData().get(colorKey));
		spec.setBaseUIColor((Color) Global.getSector().getPersistentData().get(baseUIColorKey));
		spec.setGridUIColor((Color) Global.getSector().getPersistentData().get(gridUIColorKey));
		spec.setDarkUIColor((Color) Global.getSector().getPersistentData().get(darkUIColorKey));
		spec.setBrightUIColor((Color) Global.getSector().getPersistentData().get(brightUIColorKey));
	}

	public static void resetPlayerFaction() {
		var spec = Global.getSector().getPlayerFaction().getFactionSpec();

		spec.setColor(colorDefault);
		spec.setBaseUIColor(baseUIColorDefault);
		spec.setGridUIColor(gridUIColorDefault);
		spec.setDarkUIColor(darkUIColorDefault);
		spec.setBrightUIColor(brightUIColorDefault);
	}
}
