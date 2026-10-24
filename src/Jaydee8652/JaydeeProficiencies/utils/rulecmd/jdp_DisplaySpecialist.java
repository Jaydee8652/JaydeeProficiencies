package Jaydee8652.JaydeeProficiencies.utils.rulecmd;

import java.awt.*;
import java.util.List;
import java.util.Map;

import Jaydee8652.JaydeeProficiencies.utils.jdp_SpecStore;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc.Token;

import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.*;
import static Jaydee8652.JaydeeProficiencies.utils.jdp_SpecStore.getProficiencySpec;

public class jdp_DisplaySpecialist extends BaseCommandPlugin {

	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
		String personId = params.get(0).string;

		PersonAPI person = Global.getSector().getImportantPeople().getPerson(personId);
		var data = getSpecialistData();
		var spec = data.getSpecialist(person.getId()).getProficiencySpec();

		var textPanel = dialog.getTextPanel();
		var tooltip = textPanel.beginTooltip();

		tooltip.addSectionHeading(spec.getPlugin().createTitle(person, true), spec.getColor().brighter(), spec.getColorTransparent().darker(), Alignment.MID, 10f);
		tooltip.addSpacer(10f);
		spec.getPlugin().addDescription(person, tooltip, true);
		tooltip.addSpacer(10f);

		var width = 500f;
		var height = 96f / 1.5f;
		var panel = Global.getSettings().createCustom(width, height, null);
		tooltip.addCustom(panel, 0f);

		TooltipMakerAPI mentorElement = panel.createUIElement(width, height, false);
		panel.addUIElement(mentorElement);
		createDisplaySkillSection(mentorElement, person, true, width, height);

		if (!spec.getMentor().get(0).equals("")) {
			TooltipMakerAPI menteeElement = panel.createUIElement(width, height, false);
			panel.addUIElement(menteeElement).belowLeft(mentorElement, 6f);
			createDisplaySkillSection(menteeElement, person, false, width, height);
			tooltip.setHeightSoFar(height + 6f + tooltip.getHeightSoFar());
		}
		textPanel.addTooltip();
		return true;
	}
}








