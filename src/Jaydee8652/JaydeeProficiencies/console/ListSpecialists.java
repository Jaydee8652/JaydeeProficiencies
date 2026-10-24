package Jaydee8652.JaydeeProficiencies.console;

import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_Specialist;
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_SpecialistData;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.getSpecialistData;

public class ListSpecialists implements BaseCommand {
    @Override
    public CommandResult runCommand(String args, CommandContext context) {
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        jdp_SpecialistData data = getSpecialistData();
        for (jdp_Specialist specialist : data.getAllSpecialists()) {
            Console.showMessage(specialist.person().getName().getFullName() + " ID: " + specialist.person().getId() + " Hired: " + specialist.getHired());
        }
        return CommandResult.SUCCESS;
    }
}


