package Jaydee8652.JaydeeProficiencies.console;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonAPI;
import org.lazywizard.console.BaseCommandWithSuggestion;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lazywizard.console.CommandUtils.*;

public class AddAdmin implements BaseCommandWithSuggestion
{
    @Override
    public CommandResult runCommand(String args, CommandContext context) {
        PersonAPI person = Global.getSector().getPlayerFaction().createRandomPerson(new Random());
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        String[] tmp = args.split(" ");

        if (tmp.length == 1) {
            return runCommand(args + " 1", context);
        }

        if (tmp.length >= 3) {
            return CommandResult.BAD_SYNTAX;
        }

        tmp = args.split(" ");
        if (isInteger(tmp[0]) || !isInteger(tmp[1])) {
            return CommandResult.BAD_SYNTAX;
        }

        final String skillId = findBestStringMatch(tmp[0], Global.getSettings().getSkillIds());
        if (skillId == null && tmp[0] != "") {
            Console.showMessage("No skill found with id '" + tmp[0] + "'!");
            return CommandResult.ERROR;
        } else if (skillId != null) {
            person.getStats().setSkillLevel(tmp[0], Float.parseFloat(tmp[1]));
            Console.showMessage("Added " + person.getName().getFullName() + " to the fleet with " + Global.getSettings().getSkillSpec(skillId).getName() + " level " + tmp[1]);
        } else {
            Console.showMessage("Added " + person.getName().getFullName() + " to the fleet.");
        }

        Global.getSector().getCharacterData().addAdmin(person);
        return CommandResult.SUCCESS;

    }

    @Override
    public List<String> getSuggestions(int parameter, List<String> previous, CommandContext context) {
        if (parameter != 0) return new ArrayList<>();
        return Global.getSettings().getSkillIds();
    }
}
