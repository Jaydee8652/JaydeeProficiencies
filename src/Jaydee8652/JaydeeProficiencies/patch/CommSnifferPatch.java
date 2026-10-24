package Jaydee8652.JaydeeProficiencies.patch;

import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_Stats;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.impl.campaign.BaseCampaignObjectivePlugin;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.intel.misc.CommSnifferIntel;
import patchlib.api.context.AfterContext;
import patchlib.api.match.ClassMatch;
import patchlib.api.match.MethodMatch;
import patchlib.api.patch.After;
import patchlib.api.patch.Patch;

@Patch(target = @ClassMatch(subtype = CommSnifferIntel.class))
public class CommSnifferPatch {
    @After(target = @MethodMatch(methodName = "getCurrLoseProb"))
    public static void getCurrLoseProb(AfterContext context) {
        var mult = Global.getSector().getPlayerPerson().getStats().getDynamic().getStat(jdp_Stats.COMM_SNIFFER_FAILURE_MULT).getModifiedValue();
        context.setReturnValue(((float) context.getReturnValue() * mult));
    }
}
