package Jaydee8652.JaydeeProficiencies.patch;

import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_Stats;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import patchlib.api.context.MethodCallContext;
import patchlib.api.match.ClassMatch;
import patchlib.api.match.MethodMatch;
import patchlib.api.patch.Patch;
import patchlib.api.patch.RedirectMethodCall;

import java.util.Arrays;
import java.util.List;

@Patch(target = @ClassMatch(subtype = MarketCMD.class))
public class MarketCommandPatch {
    @RedirectMethodCall(
            target = @MethodMatch(methodName = "bombardSaturation"),
            call = @MethodMatch(methodName = "getAllFactions")
    )
    public static void modifyFactions(MethodCallContext context) {
        var original = context.call();
        if (isOverlooked()) {
            context.getField("faction");
            var faction = (FactionAPI) context.getField("faction").get();//getPrivateVariable("faction", self);
            List<FactionAPI> modified = Arrays.asList(faction);
            context.setResult(modified);
        } else {
            context.setResult(original);
        }
    }

    @RedirectMethodCall(
            target = @MethodMatch(methodName = "bombardSaturation"),
            call = @MethodMatch(methodName = "addPara", parameterTypes = { String.class })
    )
    public static void modifyText(MethodCallContext context) {
        var market = (MarketAPI) context.getField("market").get();
        var string = context.getCallArg(0);
        if (isOverlooked()) {

            if (string == "An atrocity of this scale can not be hidden, but any factions that would " +
                    "be dismayed by such actions are already hostile to you.") {
                context.setCallArg(0, "An atrocity of this scale can not be hidden, " +
                        "but it can be overlooked. With the right ideas, planted in the right places" +
                        ", the Sector will find its opinion of the people of " + market.getName() + " quite malleable indeed." +
                        "In time they will learn to forget, if not forgive.");
            } else if (string == "An atrocity of this scale can not be hidden, " +
                    "and will make the following factions hostile:") {
                context.setCallArg(0, "An atrocity of this scale can not be hidden, " +
                        "but it can be overlooked.\n\nWith the right ideas, planted in the right places" +
                        ", the Sector will find its opinion of the people of " + market.getName() + " quite malleable indeed. " +
                        "In time they will learn to forget, if not forgive." +
                        "\n\nOnly the market owner will become hostile:");
            }
        }
        if (shouldDeciv()) {
            if (string == "A saturation bombardment of a colony this size will destroy it utterly.") {
                context.setCallArg(0, "A saturation bombardment of a colony this size, coupled with a "+
                        "decapitation strike of critical colonial leadership, is projected to cause decivilization within hours.");
            }
        }

        var original = context.call();
        context.setResult(original);
    }

    @RedirectMethodCall(
            target = @MethodMatch(methodName = "bombardConfirm"),
            call = @MethodMatch(methodName = "decivilize", parameterTypes = { MarketAPI.class, boolean.class })
    )
    public static void modifyDeciv(MethodCallContext context) {
        if (shouldDeciv()) context.setCallArg(1, false);
        var original = context.call();
        context.setResult(original);
    }

    public static boolean isOverlooked () {
        return Global.getSector().getPlayerPerson().getMemoryWithoutUpdate().contains(jdp_Stats.SATURATION_BOMBARDMENT_OVERLOOK);
    }
    public static boolean shouldDeciv () {
        return Global.getSector().getPlayerPerson().getMemoryWithoutUpdate().contains(jdp_Stats.SATURATION_BOMBARDMENT_DECIV);
    }
}