package Jaydee8652.JaydeeProficiencies.patch;

import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_Stats;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.BaseCampaignObjectivePlugin;

import patchlib.api.context.BeforeContext;
import patchlib.api.match.ClassMatch;
import patchlib.api.match.MethodMatch;
import patchlib.api.patch.Before;
import patchlib.api.patch.Patch;

@Patch(target = @ClassMatch(subtype = BaseCampaignObjectivePlugin.class))
public class BaseCampaignObjectivePluginPatch {
    @Before(target = @MethodMatch(methodName = "setHacked", parameterCount = 2))
    public static void setHacked(BeforeContext context) {
        var mult = Global.getSector().getPlayerPerson().getStats().getDynamic().getStat(jdp_Stats.HACKING_MULT).getModifiedValue();
        context.setArg(1, (float) context.getArg(1) * mult);
    }
}
