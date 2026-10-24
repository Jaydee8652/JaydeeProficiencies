package Jaydee8652.JaydeeProficiencies.utils.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Misc.Token;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class jdp_AddDebug extends BaseCommandPlugin {

	//Script to add debug rules. Will only appear with devmode enabled.
	// Format:
	// jdp_AddDebug rule_id BLAH BLAH BLAH
	public boolean execute(String ruleId, final InteractionDialogAPI dialog, List<Token> params, final Map<String, MemoryAPI> memoryMap) {
		if (dialog == null) return false;
		if (!memoryMap.get("global").contains("$isDevMode")) return false;

		String id = params.get(0).string;
		String text = params.stream().skip(1).map(t -> t.string).collect(Collectors.joining(" "));

		OptionPanelAPI options = dialog.getOptionPanel();
		options.addOption(text, id);
		dialog.setOptionColor(id, Misc.getGrayColor());
		return true;
	}

	@Override
	public boolean doesCommandAddOptions() {
		return true;
	}
}
