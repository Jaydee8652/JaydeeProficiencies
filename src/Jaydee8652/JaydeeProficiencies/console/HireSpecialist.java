package Jaydee8652.JaydeeProficiencies.console;

import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_Specialist;
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_SpecialistData;
import com.fs.starfarer.api.Global;
import org.lazywizard.console.BaseCommandWithSuggestion;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

import java.util.ArrayList;
import java.util.List;

import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_DEFAULT_SPECIALIST;
import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getSpecialistData;
import static org.lazywizard.console.CommandUtils.isInteger;

public class HireSpecialist implements BaseCommandWithSuggestion {
    @Override
    public CommandResult runCommand(String args, CommandContext context) {
        jdp_SpecialistData data = getSpecialistData();

        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        String[] tmp = args.split(" ");

        if (tmp.length != 1) {
            return CommandResult.BAD_SYNTAX;
        }

        if (isInteger(tmp[0])) {
            return CommandResult.BAD_SYNTAX;
        }

        jdp_Specialist specialist = data.getSpecialist(tmp[0]);
        if (specialist == null) {
            Console.showMessage("No Specialist found with id '" + tmp[0] + "'!");
            return CommandResult.ERROR;
        } else {
            if (specialist.getHired()) {
                Console.showMessage("Specialist " + specialist.person().getName().getFullName() + "(" + specialist.getProficiencySpec().getName() + ") is already hired");
            } else {
                data.hireSpecialist(tmp[0]);
                Console.showMessage("Specialist " + specialist.getProficiencySpec().getName() + " " + specialist.person().getName().getFullName() + " hired");
            }
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
        }
        if (parameter != 0) return new ArrayList<>();
        return specialists;
    }
}
