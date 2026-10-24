package Jaydee8652.JaydeeProficiencies.console;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.FactionDoctrineAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.RepairTrackerAPI;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.plog.PlaythroughLog;
import org.lazywizard.console.*;

import java.util.*;

public class AddFleet implements BaseCommandWithSuggestion {
    @Override
    public CommandResult runCommand(String args, CommandContext context) {
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        if (args.isEmpty()) {
            Console.showMessage("Unmodified officer cap");
            Global.getSector().getPlayerPerson().getStats().getOfficerNumber().unmodifyFlat("AddFleet");
            return CommandResult.SUCCESS;
        }

        String[] tmp = args.split(" ", 3);
        if (tmp.length != 2) {
            return CommandResult.BAD_SYNTAX;
        }

        final FactionAPI faction = CommandUtils.findBestFactionMatch(tmp[0]);
        if (faction == null) {
            Console.showMessage("Faction [" + tmp[0] + "] not found");
            return CommandResult.ERROR;
        }

        if (!CommandUtils.isInteger(tmp[1])) {
            Console.showMessage("Fleet points must be a whole number!");
            return CommandResult.ERROR;
        }
        try {
            // Create fleet
            final FactionDoctrineAPI doctrine = faction.getDoctrine();
            final int totalFP = Integer.parseInt(tmp[1]);
            final float freighterFP = totalFP * doctrine.getCombatFreighterProbability();
            final FleetParamsV3 params = new FleetParamsV3(
                    null, // Hyperspace location
                    faction.getId(), // Faction ID
                    null, // Quality override (null disables)
                    FleetTypes.PATROL_LARGE, // Fleet type
                    totalFP, // Combat FP
                    freighterFP * .3f, // Freighter FP
                    freighterFP * .3f, // Tanker FP
                    freighterFP * .1f, // Transport FP
                    freighterFP * .1f, // Liner FP
                    freighterFP * .1f, // Utility FP
                    0f); // Quality bonus
            final CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);

            var commander = fleet.getFleetData().getCommander();
            for (String commanderSkill : doctrine.getCommanderSkills()) {
                if (commander.getStats().hasSkill(commanderSkill)) {
                    commander.getStats().setSkillLevel(commanderSkill, 0f);
                    PlaythroughLog.getInstance().removeOfficerSkillRecord(commander.getId(), commanderSkill, false);
                    PlaythroughLog.getInstance().removeOfficerSkillRecord(commander.getId(), commanderSkill, true);
                }
            }

            fleet.getFleetData().sort();
            fleet.forceSync();

            var officers = 0;
            var crew = 0;
            for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {
                Global.getSector().getPlayerFleet().getFleetData().addFleetMember(member);
                crew += (int) member.getMinCrew();

                var captain = member.getCaptain();
                captain.setFaction(Global.getSector().getPlayerFaction().getId());
                if(!captain.isDefault()) {
                    Global.getSector().getPlayerFleet().getFleetData().addOfficer(captain);
                    officers += 1;
                }

                final RepairTrackerAPI repairs = member.getRepairTracker();
                var max = repairs.getMaxCR();
                repairs.setCR(max);
            }

            var mod = Global.getSector().getPlayerPerson().getStats().getOfficerNumber().getFlatStatMod("AddFleet");
            float previous = 0;
            if(mod != null) previous = mod.value;

            Global.getSector().getPlayerFleet().getCargo().addCrew(crew);
            Global.getSector().getPlayerPerson().getStats().getOfficerNumber().modifyFlat("AddFleet", previous + officers);

            Console.showMessage("Added the contents of a " + totalFP + "FP " + faction.getDisplayName() + " [" + faction.getId() + "] Fleet with " + officers + " officers and " + crew + " crew to the player.");
            return CommandResult.SUCCESS;
        }
        catch (Exception ex) {
            Console.showMessage("Unable to generate generic patrol fleet for faction [" + faction.getId() + "]");
            return CommandResult.ERROR;
        }
    }

    @Override
    public List<String> getSuggestions(int parameter, List<String> previous, CommandContext context) {
        List<String> suggestions = new ArrayList<>();
        if (parameter == 0) {
            suggestions.addAll(Global.getSettings().getAllFactionSpecs().stream().map(it -> it.getId()).toList());
        }
        return suggestions;
    }
}
