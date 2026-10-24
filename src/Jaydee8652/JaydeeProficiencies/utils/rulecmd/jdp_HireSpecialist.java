package Jaydee8652.JaydeeProficiencies.utils.rulecmd;

import java.util.List;
import java.util.Map;

import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_SpecialistData;
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags;
import Jaydee8652.JaydeeProficiencies.utils.jdp_SpecStore;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc.Token;

import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.DONT_SHOW_EXPO_AGAIN;
import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_NEW_SPECIALIST;
import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.*;
import static Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_IntelUtils.addHireSpecialistIntel;
import static Jaydee8652.JaydeeProficiencies.utils.jdp_SpecStore.getProficiencySpec;
import static Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIReflectionUtils.*;

public class jdp_HireSpecialist extends BaseCommandPlugin {
	public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
		String personId = params.get(0).string;
		var data = getSpecialistData();

		data.hireSpecialist(personId);
		return true;
	}
}








