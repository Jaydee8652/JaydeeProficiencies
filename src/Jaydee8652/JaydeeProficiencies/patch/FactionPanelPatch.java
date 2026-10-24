package Jaydee8652.JaydeeProficiencies.patch;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import patchlib.api.context.MethodCallContext;
import patchlib.api.match.ClassMatch;
import patchlib.api.match.MethodMatch;
import patchlib.api.patch.Patch;
import patchlib.api.patch.RedirectMethodCall;

@Patch(target = @ClassMatch(packageName = "com.fs.starfarer.campaign.ui.marketinfo", methodMatches = { @MethodMatch(methodName = "charTypeFailed") }))
public class FactionPanelPatch {
    @RedirectMethodCall(
            target = @MethodMatch(methodName = "advanceImpl", parameterCount = 1),
            call = @MethodMatch(methodName = "show", parameterCount = 2)
    )
    // Eats showing the "now you have static assets" blurb if it's a lie...
    public static void removeFirstTime(MethodCallContext context) {
        if (Global.getSector().getEconomy().getMarketsCopy().stream().anyMatch(MarketAPI::isPlayerOwned)) {
            var original = context.call();
            context.setResult(original);
        }
    }
}