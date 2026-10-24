package Jaydee8652.JaydeeProficiencies.patch;

import com.fs.starfarer.api.Global;
import patchlib.api.context.BeforeContext;
import patchlib.api.match.ClassMatch;
import patchlib.api.match.MethodMatch;
import patchlib.api.patch.Before;
import patchlib.api.patch.Patch;

import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_SPECIALIST_TAB_OPEN;

@Patch(target = @ClassMatch(packageName = "com.fs.starfarer.coreui", methodMatches = { @MethodMatch(methodName = "resetOrConfirmMode") }))
public class PlayerPanelPatch {
    @Before(target = @MethodMatch(methodName = "sizeChanged"))
    public static void sizeChanged(BeforeContext context) {
        Global.getSector().getMemoryWithoutUpdate().unset(JDP_SPECIALIST_TAB_OPEN);
    }
}