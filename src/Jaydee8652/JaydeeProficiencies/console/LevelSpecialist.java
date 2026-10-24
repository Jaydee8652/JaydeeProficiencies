package Jaydee8652.JaydeeProficiencies.console;

import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_Specialist;
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_SpecialistData;
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_SpecialistExperienceData;
import com.fs.starfarer.api.Global;
import org.lazywizard.console.BaseCommandWithSuggestion;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_DEFAULT_SPECIALIST;
import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.*;
import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getSpecialistData;
import static org.lazywizard.console.CommandUtils.*;

public class LevelSpecialist implements BaseCommandWithSuggestion {
    @Override
    public CommandResult runCommand(String args, CommandContext context) {
        jdp_SpecialistData data = getSpecialistData();

        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        String[] tmp = args.split(" ");

        if (tmp.length == 1) {
            return CommandResult.BAD_SYNTAX;
        }

        if (tmp.length >= 3) {
            return CommandResult.BAD_SYNTAX;
        }

        tmp = args.split(" ");
        if (isInteger(tmp[0]) || !isInteger(tmp[1])) {
            return CommandResult.BAD_SYNTAX;
        }

        if (Objects.equals(tmp[0], "player")) tmp[0] = Global.getSector().getPlayerPerson().getId();
        if (Objects.equals(tmp[0], "all")) {
            if(data.getAllSpecialists().isEmpty()) {
                Console.showMessage("No active Specialists found!");
                return CommandResult.ERROR;
            }

            for (jdp_Specialist specialist : data.getAllSpecialists()) {
                specialist.addExperience(new jdp_SpecialistExperienceData("Console Command", Float.parseFloat(tmp[1]), Global.getSector().getClock().getTimestamp()));
                Console.showMessage("Experience of " + specialist.person().getName().getFullName() + " increased by " + tmp[1]);
            }
            return CommandResult.SUCCESS;
        }

        jdp_Specialist specialist = data.getSpecialist(tmp[0]);
        if (specialist == null) {
            Console.showMessage("No Specialist found with id '" + tmp[0] + "'!");
            return CommandResult.ERROR;
        } else {
            specialist.addExperience(new jdp_SpecialistExperienceData("Console Command", Float.parseFloat(tmp[1]), Global.getSector().getClock().getTimestamp()));
            Console.showMessage("Experience of " + specialist.person().getName().getFullName() + " increased by " + tmp[1]);
        }

        return CommandResult.SUCCESS;
    }

    @Override
    public List<String> getSuggestions(int parameter, List<String> previous, CommandContext context) {
        jdp_SpecialistData data = getSpecialistData();
        ArrayList<String> specialists = new ArrayList<>();
        data.getAllSpecialists().forEach(s -> specialists.add(s.person().getId()));
        specialists.remove(JDP_DEFAULT_SPECIALIST);

        if (specialists.contains(Global.getSector().getPlayerPerson().getId())) {
            specialists.remove(Global.getSector().getPlayerPerson().getId());
            specialists.add("player");
        }
        if(!specialists.isEmpty()) specialists.add("all");
        if (parameter != 0) return new ArrayList<>();
        return specialists;
    }
}
