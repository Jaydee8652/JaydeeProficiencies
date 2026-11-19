package Jaydee8652.JaydeeProficiencies.patch;

import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_Stats;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import patchlib.api.context.AfterContext;
import patchlib.api.context.MethodCallContext;
import patchlib.api.match.ClassMatch;
import patchlib.api.match.MethodMatch;
import patchlib.api.patch.After;
import patchlib.api.patch.Patch;
import patchlib.api.patch.RedirectMethodCall;
import patchlib.api.query.FieldQuery;
import patchlib.api.spec.FieldQuerySpec;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Patch(target = @ClassMatch(subtype = MarketCMD.class))
public class MarketCommandPatch {
    private static final FieldQuerySpec factionQuery = FieldQuery.create().fieldName("faction").build();
    private static final FieldQuerySpec marketQuery = FieldQuery.create().fieldName("market").build();
    private static final FieldQuerySpec tempQuery = FieldQuery.create().fieldName("temp").build();

    @RedirectMethodCall(target = @MethodMatch(methodName = "bombardSaturation"), call = @MethodMatch(methodName = "getAllFactions"))
    public static void modifyFactions(MethodCallContext context) {
        var original = context.call();
        var faction = (FactionAPI) context.getField(factionQuery).get();

        if (isOverlooked(faction)) {
            List<FactionAPI> modified = Collections.singletonList(faction);
            context.setResult(modified);
        } else {
            context.setResult(original);
        }
    }

    @RedirectMethodCall(target = @MethodMatch(methodName = "bombardSaturation"), call = @MethodMatch(methodName = "addPara", parameterTypes = { String.class }))
    public static void modifyText(MethodCallContext context) {
        var market = (MarketAPI) context.getField(marketQuery).get();

        var string = context.getCallArg(0);

        if (isOverlooked(market.getFaction())) {
            if (string.equals("An atrocity of this scale can not be hidden, but any factions that would " +
                    "be dismayed by such actions are already hostile to you.")) {
                context.setCallArg(0, "An atrocity of this scale can not be hidden, " +
                        "but it can be overlooked.\n\n With the right ideas, planted in the right places" +
                        ", the Sector will find its opinion of the people of " + market.getName() + " quite malleable indeed. " +
                        "In time they will learn to forget, if not forgive.");
            } else if (string.equals("An atrocity of this scale can not be hidden, " +
                    "and will make the following factions hostile:")) {
                context.setCallArg(0, "An atrocity of this scale can not be hidden, " +
                        "but it can be overlooked.\n\n With the right ideas, planted in the right places" +
                        ", the Sector will find its opinion of the people of " + market.getName() + " quite malleable indeed. " +
                        "In time they will learn to forget, if not forgive." +
                        "\n\nOnly the market owner will become hostile:");
            }
        }
        if (shouldDeciv()) {
            if (string.equals("A saturation bombardment of a colony this size will destroy it utterly.")) {
                context.setCallArg(0, "A saturation bombardment of a colony this size, coupled with a "+
                        "decapitation strike of critical colonial leadership, is projected to cause decivilization within hours.");
            }
        }

        var original = context.call();
        context.setResult(original);
    }

    @RedirectMethodCall(target = @MethodMatch(methodName = "bombardSaturation"), call = @MethodMatch(methodName = "checkForSatBombLimitedHatred"))
    public static void modifyFactionsNEX(MethodCallContext context) {
        var test = (List<FactionAPI>) context.getCallArg(0);
        var faction = (FactionAPI) context.getField(factionQuery).get();

        test.add(faction);

        var original = context.call();
        context.setResult(original);
    }

    @RedirectMethodCall(target = @MethodMatch(methodName = "checkForSatBombLimitedHatred"), call = @MethodMatch(methodName = "addPara", parameterTypes = { String.class }))
    public static void modifyTextNEX(MethodCallContext context) {
        var market = (MarketAPI) context.getField(marketQuery).get();
        var string = context.getCallArg(0);

        if (isOverlooked(market.getFaction())) {
            if ((Global.getSettings().getModManager().isModEnabled("nexerelin"))) {
                if (string.equals("An atrocity of this scale can not be hidden, but any factions that would " +
                        "be dismayed by such actions are already hostile to you.")) {
                    context.setCallArg(0, "An atrocity of this scale can not be hidden, " +
                            "but it can be overlooked.\n\n With the right ideas, planted in the right places" +
                            ", the Sector will find its opinion of the people of " + market.getName() + " quite malleable indeed. " +
                            "In time they will learn to forget, if not forgive.");
                } else {
                    context.setCallArg(0, "An atrocity of this scale can not be hidden, " +
                            "but it can be overlooked.\n\n With the right ideas, planted in the right places" +
                            ", the Sector will find its opinion of the people of " + market.getName() + " quite malleable indeed. " +
                            "In time they will learn to forget, if not forgive. \n\nOnly the market owner will become hostile:");
                }
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

    @After(target = @MethodMatch(methodName = "bombardConfirm"))
    public static void afterBombardConfirm(AfterContext context) {
        if (context.getSelf() instanceof MarketCMD marketCMD) {
            var temp = (MarketCMD.TempData) context.getField(tempQuery).get();
            var faction = (FactionAPI) context.getField(factionQuery).get();

            if (temp.bombardType == MarketCMD.BombardType.SATURATION) {
                if (isOverlooked(faction)) resetClaim(faction);
            }
        }
    }

    public static boolean isOverlooked(FactionAPI faction) {
        var claim = getClaim(faction);
        if (claim == null) return false;
        return claim.ready;
    }

    public static boolean shouldDeciv() {
        return Global.getSector().getPlayerPerson().getMemoryWithoutUpdate().contains(jdp_Stats.SATURATION_BOMBARDMENT_DECIV);
    }

    public static int BASE_CLAIM_GOAL = 100;
    public static float CLAIM_INCREMENT = 1.05f;

    public static claimData getClaim(FactionAPI faction) {
        HashMap<FactionAPI, claimData> data = (HashMap<FactionAPI, claimData>) Global.getSector().getMemoryWithoutUpdate().get(jdp_Stats.SATBOMB_CLAIMS);
        if(data == null) {
            data = new HashMap<FactionAPI, claimData>();
            Global.getSector().getMemoryWithoutUpdate().set(jdp_Stats.SATBOMB_CLAIMS, data);
        }

        if (!data.containsKey(faction)) {
            var claim = new claimData();
            data.put(faction, claim);
            Global.getSector().getMemoryWithoutUpdate().set(jdp_Stats.SATBOMB_CLAIMS, data);
        }
        return data.get(faction);
    }

    public static void resetClaim(FactionAPI faction) {
        HashMap<FactionAPI, claimData> data = (HashMap<FactionAPI, claimData>) Global.getSector().getMemoryWithoutUpdate().get(jdp_Stats.SATBOMB_CLAIMS);
        var claim = data.get(faction);

        claim.ready = false;
        claim.progress = 0;
        claim.previous += 1;
        claim.setGoal(Math.round(claim.getGoal() * CLAIM_INCREMENT));

        data.replace(faction, claim);
        Global.getSector().getMemoryWithoutUpdate().set(jdp_Stats.SATBOMB_CLAIMS, data);
    }

    public static class claimData {
        private boolean ready = false;
        private int previous = 0;
        private int progress = 0;
        private int goal = BASE_CLAIM_GOAL;

        public boolean getReady() {
            return ready;
        }
        public int getPrevious() {
            return previous;
        }
        public int getProgress() {
            return progress;
        }
        public int getGoal() {
            return goal;
        }

        public void setReady(boolean ready) {
            this.ready = ready;
        }
        public void setPrevious(int previous) {
            this.previous = previous;
        }
        public void setProgress(int progress) {
            this.progress = progress;
        }
        public void setGoal(int goal) {
            this.goal = goal;
        }
    }
}