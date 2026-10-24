package Jaydee8652.JaydeeProficiencies.patch;

import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_Stats;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.intel.misc.CommSnifferIntel;
import com.fs.starfarer.rpg.Person;
import patchlib.api.context.AfterContext;
import patchlib.api.match.ClassMatch;
import patchlib.api.match.MethodMatch;
import patchlib.api.patch.After;
import patchlib.api.patch.Patch;

@Patch(target = @ClassMatch(subtype = BaseIndustry.class))
public class IndustryPatch {
    @After(target = @MethodMatch(methodName = "getPatherInterest"))
    public static void getPatherInterest(AfterContext context) {
        var self = context.getSelf();
        if (self instanceof BaseIndustry baseindustry) {
            var market = (baseindustry).getMarket();
            var mult = market.getStats().getDynamic().getStat(jdp_Stats.PATHER_INTEREST_MULT).getModifiedValue();
            context.setReturnValue(((float) context.getReturnValue() * mult));
        }
    }
}
