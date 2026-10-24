package Jaydee8652.JaydeeProficiencies.skills;

import Jaydee8652.JaydeeProficiencies.JaydeeProficienciesPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.*;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import org.apache.log4j.Logger;

import java.util.List;

import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.createCargoStack;


public class jdp_refit_applicator {
	/** Can be implemented by listeners to run code on all ships in the player fleet. See jdp_RefitApplicator in JaydeePiracy*/
	public interface RefitApplicatorImpl {
		void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize);
		void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize);
	}

	public static class Level1 implements ShipSkillEffect {
		public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {
			for (RefitApplicatorImpl RefitApplicator : Global.getSector().getListenerManager().getListeners(RefitApplicatorImpl.class)) RefitApplicator.apply(stats, hullSize);
		}

		public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {
			for (RefitApplicatorImpl RefitApplicator : Global.getSector().getListenerManager().getListeners(RefitApplicatorImpl.class)) RefitApplicator.unapply(stats, hullSize);
		}

		public String getEffectDescription(float level) {
			return "null";
		}

		public String getEffectPerLevelDescription() {
			return null;
		}

		public ScopeDescription getScopeDescription() {
			return ScopeDescription.PILOTED_SHIP;
		}
	}
}