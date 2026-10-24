package Jaydee8652.JaydeeProficiencies.skills;

import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.CharacterStatsSkillEffect;
import com.fs.starfarer.api.characters.MarketSkillEffect;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.SkillSpecAPI;
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class jdp_mentorship {
	public static class Level0 extends BaseSkillEffectDescription implements MarketSkillEffect {
		@Override
		public void createCustomDescription(
				MutableCharacterStatsAPI stats,
				SkillSpecAPI skill,
				TooltipMakerAPI info,
				float width) {
			Color high = Misc.getHighlightColor();

			info.addPara("This specialist can support an apprentice, teaching them a selection of skills from their proficiency.",
					3f, high, "This specialist can support an apprentice, teaching them a selection of skills from their proficiency.");
		}

		public String getEffectPerLevelDescription() {
			return null;
		}

		public ScopeDescription getScopeDescription() {
			return ScopeDescription.PILOTED_SHIP;
		}

		@Override
		public void apply(MarketAPI market, String id, float level) {

		}

		@Override
		public void unapply(MarketAPI market, String id) {

		}
	}

	public static class Level1 implements CharacterStatsSkillEffect {
		@Override
		public String getEffectDescription(float level) {
			return "";
		}

		public String getEffectPerLevelDescription() {
			return null;
		}

		public ScopeDescription getScopeDescription() {
			return ScopeDescription.CUSTOM;
		}

		@Override
		public void apply(MutableCharacterStatsAPI stats, String id, float level) {
			//Nothing to do. Mentorship is handled entirely within the system

		}

		@Override
		public void unapply(MutableCharacterStatsAPI stats, String id) {
			//Nothing to do. Mentorship is handled entirely within the system
		}
	}
}