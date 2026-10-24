package Jaydee8652.JaydeeProficiencies.patch;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FleetEncounterContextPlugin;
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext;
import patchlib.api.context.AfterContext;
import patchlib.api.match.ClassMatch;
import patchlib.api.match.MethodMatch;
import patchlib.api.patch.After;
import patchlib.api.patch.Patch;

import java.util.ArrayList;

@Patch(target = @ClassMatch(subtype = FleetEncounterContext.class))
public class ExperienceGainListenerPatch {
    public interface ExperienceGainListener {
        void reportExperienceGain(FleetEncounterContextPlugin.DataForEncounterSide side, float xp, FleetEncounterContext context);
    }

    @After(target = @MethodMatch(methodName = "gainOfficerXP"))
    public static void gainOfficerXP(AfterContext context) {
        var side = (FleetEncounterContextPlugin.DataForEncounterSide) context.getArg(0);
        var xp = (float) context.getArg(1);

        if (context.getSelf() instanceof FleetEncounterContext encounterContext) {
            for (ExperienceGainListener listener : new ArrayList<ExperienceGainListener>(Global.getSector().getListenerManager().getListeners(ExperienceGainListener.class))) {
                listener.reportExperienceGain(side, xp, encounterContext);
            }
        }
    }
}
