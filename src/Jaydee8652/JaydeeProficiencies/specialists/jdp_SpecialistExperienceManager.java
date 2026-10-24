package Jaydee8652.JaydeeProficiencies.specialists;

import Jaydee8652.JaydeeProficiencies.patch.ExperienceGainListenerPatch;
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_Specialist;
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_SpecialistData;
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_SpecialistExperienceData;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetEncounterContextPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import org.lazywizard.lazylib.MathUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getPlayerGovernedColonies;
import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getSpecialistData;

public class jdp_SpecialistExperienceManager extends BaseCampaignEventListener implements ExperienceGainListenerPatch.ExperienceGainListener {
	public static Map<ShipAPI.HullSize, Float> SHIP_SIZE_MULT = new HashMap<>();
	public static Float COLONY_XP_MULT = 100f;
	public static Float COMBAT_XP_MULT = 0.2f;

	public static Float COLONY_XP_MULT_PLAYER = 1f;
	public static Float COMBAT_XP_MULT_PLAYER = 0.5f;

	static {
		SHIP_SIZE_MULT.put(ShipAPI.HullSize.FIGHTER, 1.5f);
		SHIP_SIZE_MULT.put(ShipAPI.HullSize.FRIGATE, 1.5f);
		SHIP_SIZE_MULT.put(ShipAPI.HullSize.DESTROYER, 1.25f);
		SHIP_SIZE_MULT.put(ShipAPI.HullSize.CRUISER, 1.0f);
		SHIP_SIZE_MULT.put(ShipAPI.HullSize.CAPITAL_SHIP, 1.0f);
	}

	public jdp_SpecialistExperienceManager() {
		super(false);
	}

	public void init() {
		if (Global.getSector().getPersistentData().get("jdp_SpecialistExperienceManager") == null) {
			Global.getSector().getPersistentData().put("jdp_SpecialistExperienceManager", this);
			Global.getSector().getListenerManager().addListener(this);
		}
	}

	@Override
	public void reportEconomyMonthEnd() {
		jdp_SpecialistData data = getSpecialistData();

		List<MarketAPI> markets = Global.getSector().getEconomy().getMarketsCopy();
		markets = markets.stream().filter(m -> m.isPlayerOwned() || m.getFaction().isPlayerFaction()).toList();
		for (MarketAPI market : markets) {
			PersonAPI admin = market.getAdmin();
			if (admin != null) {
				jdp_Specialist specialist = data.getSpecialist(admin.getId());
				if(specialist != null && !admin.hasTag(jdp_SpecialistTags.PERSON_TAG_NO_XP_GAIN)) {
					var source = "Administration of " + market.getName();

					float profit = market.getNetIncome();
					float cost = market.getIndustryUpkeep();
					if (profit <= 0 || cost <= 0) continue;

					float margin = profit / cost;
					margin = MathUtils.clamp(margin, 0, 2);
					margin /= Global.getSettings().getFloat("economyIterPerMonth");

					float xp = (float) (COLONY_XP_MULT * (margin * Math.pow((market.getNetIncome()), 0.6) + (Math.pow(market.getStats().getDynamic().getStat(Stats.GROUND_DEFENSES_MOD).getModifiedValue(), 0.5))));
					if (admin.isPlayer()) {
						if(specialist.getProficiencySpec().getTags().contains(jdp_SpecialistTags.SPEC_TAG_PLAYER_NO_XP_AS_ADMIN)) continue;
						xp /= getPlayerGovernedColonies().size();
						xp *= COLONY_XP_MULT_PLAYER;
					}
					xp = specialist.getProficiencyPlugin().calculateExperience(specialist, xp, market);
					if (xp > 0) specialist.addExperience(new jdp_SpecialistExperienceData(source, xp, Global.getSector().getClock().getTimestamp()));
				}
			}
		}
	}

	@Override
	public void reportExperienceGain(FleetEncounterContextPlugin.DataForEncounterSide side, float xp, FleetEncounterContext context) {
		xp *= COMBAT_XP_MULT;
		jdp_SpecialistData data = getSpecialistData();
		var enemyFleets = context.getBattle().getNonPlayerSideSnapshot().stream().sorted(Comparator.comparingDouble(CampaignFleetAPI::getFleetSizeCount)).toList();
		if (enemyFleets.isEmpty()) return;

		var enemyFleet = (enemyFleets.get(enemyFleets.size() - 1));
		var source = "Combat against " + enemyFleet.getFullName().substring(0, 1).toUpperCase() + enemyFleet.getFullName().substring(1);
		float num = Math.max(1f, side.getMembersWithOfficerOrPlayerAsOrigCaptain().size());

		for (FleetMemberAPI member : side.getFleetMemberDeploymentData().keySet()) {
			var engagementData = side.getFleetMemberDeploymentData().get(member);
			if (engagementData != null) {
				var captain = member.getCaptain();
				jdp_Specialist specialist = data.getSpecialist(captain.getId());
				if (specialist != null && !captain.hasTag(jdp_SpecialistTags.PERSON_TAG_NO_XP_GAIN) && !specialist.getSpec().getTags().contains(jdp_SpecialistTags.SPEC_TAG_NO_XP_GAIN)) {
					float fraction = engagementData.timeDeployed / Math.max(1f, side.getMaxTimeDeployed());
					fraction = MathUtils.clamp(fraction, 0, 1);

					var gained = (SHIP_SIZE_MULT.get(member.getHullSpec().getHullSize()) * xp * fraction) / num;
					gained = specialist.getProficiencyPlugin().calculateExperience(specialist, gained, context);
					if (gained > 0) specialist.addExperience(new jdp_SpecialistExperienceData(source, gained, Global.getSector().getClock().getTimestamp()));
				}
			}
		}

		var playerPerson = Global.getSector().getPlayerPerson();
		jdp_Specialist specialist = data.getSpecialist(playerPerson.getId());

		FleetMemberAPI member = Global.getSector().getPlayerFleet().getFleetData().getMemberWithCaptain(playerPerson);
		if (member == null) return;

		var gained = (COMBAT_XP_MULT_PLAYER * SHIP_SIZE_MULT.get(member.getHullSpec().getHullSize()) * xp) / num;
		if (specialist != null && !specialist.getProficiencySpec().getTags().contains(jdp_SpecialistTags.SPEC_TAG_PLAYER_NO_XP_AS_OFFICER)) {
			gained = specialist.getProficiencyPlugin().calculateExperience(specialist, gained, context);
			if (gained > 0) specialist.addExperience(new jdp_SpecialistExperienceData(source, gained, Global.getSector().getClock().getTimestamp()));
		}
	}
}
