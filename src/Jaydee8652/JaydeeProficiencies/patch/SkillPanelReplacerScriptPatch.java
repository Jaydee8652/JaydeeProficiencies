package Jaydee8652.JaydeeProficiencies.patch;

import com.fs.starfarer.api.Global;
import patchlib.api.context.BeforeContext;
import patchlib.api.match.ClassMatch;
import patchlib.api.match.MethodMatch;
import patchlib.api.patch.Before;
import patchlib.api.patch.Patch;
import second_in_command.scripts.SkillPanelReplacerScript;

import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.JDP_SPECIALIST_TAB_OPEN;

// I can use the Lukas code to destroy the Lukas code
// Delightfully devilish Seymour...
@Patch(target = @ClassMatch(subtype = SkillPanelReplacerScript.class))
public class SkillPanelReplacerScriptPatch {
    @Before(target = @MethodMatch(methodName = "advance"))
    public static void advance(BeforeContext context) throws NoSuchMethodException {
        if (Global.getSector().getMemoryWithoutUpdate().contains(JDP_SPECIALIST_TAB_OPEN)) {
            context.skipOriginal(null);
        }
    }
}
