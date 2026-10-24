package Jaydee8652.JaydeeProficiencies.patch;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.NavBuoyEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import patchlib.api.context.AfterContext;
import patchlib.api.context.BeforeContext;
import patchlib.api.match.ClassMatch;
import patchlib.api.match.MethodMatch;
import patchlib.api.patch.After;
import patchlib.api.patch.Before;
import patchlib.api.patch.Patch;

import static Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_CampaignEntityUtils.isInverted;
import static Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_CampaignEntityUtils.validateFleet;
import static com.fs.starfarer.api.impl.campaign.BaseCampaignObjectivePlugin.RESET;
import static com.fs.starfarer.api.impl.campaign.NavBuoyEntityPlugin.NAV_BONUS;
import static com.fs.starfarer.api.impl.campaign.NavBuoyEntityPlugin.NAV_BONUS_MAKESHIFT;

@Patch(target = @ClassMatch(subtype = NavBuoyEntityPlugin.class))
public class NavBuoyEntityPluginPatch {
    @Before(target = @MethodMatch(methodName = "printEffect"))
    public static void printEffect(BeforeContext context) throws NoSuchMethodException {
        var self = context.getSelf();
        if (self instanceof NavBuoyEntityPlugin plugin) {
            if(plugin.isHacked() && isInverted()) {
                var entity = (SectorEntityToken) context.getField("entity").get();//getPrivateVariable("entity", plugin);
                float bonus = NAV_BONUS;
                if (entity.hasTag(Tags.MAKESHIFT)) {
                    bonus = NAV_BONUS_MAKESHIFT;
                }
                var text = (TooltipMakerAPI) context.getArg(0);
                var pad = (float) context.getArg(1);

                text.addPara(BaseIntelPlugin.INDENT + "%s burn level for all hacker fleets in system",
                        pad, Misc.getHighlightColor(), "+" + Math.round(bonus));

                bonus = -bonus + 1;
                text.addPara(BaseIntelPlugin.INDENT + " %s burn level for all same-faction fleets in system",
                        3f, Misc.getHighlightColor(), Math.round(bonus) + "");

                if (entity.getMemoryWithoutUpdate().getBoolean(RESET)) {
                    text.addPara(BaseIntelPlugin.INDENT + "Auto-calibrating after factory reset; non-functional", 3f);
                }

                context.skipOriginal(null);
            }
        }
    }

    @Before(target = @MethodMatch(methodName = "addHackStatusToTooltip"))
    public static void addHackStatusToTooltip(BeforeContext context) throws NoSuchMethodException {
        var self = context.getSelf();
        if (self instanceof NavBuoyEntityPlugin plugin) {
            if(plugin.isHacked() && isInverted()) {
                var entity = (SectorEntityToken) context.getField("entity").get();//getPrivateVariable("entity", plugin);
                float bonus = NAV_BONUS;
                if (entity.hasTag(Tags.MAKESHIFT)) {
                    bonus = NAV_BONUS_MAKESHIFT;
                }
                var text = (TooltipMakerAPI) context.getArg(0);
                var pad = (float) context.getArg(1);

                text.addPara("%s burn level for in-system hacker fleets",
                        pad, Misc.getHighlightColor(), "+" + Math.round(bonus));
                bonus = -bonus + 1;
                text.addPara(" %s burn level for in-system faction fleets",
                        pad, Misc.getHighlightColor(), Math.round(bonus) + "");
                text.addPara("Hacked", Misc.getTextColor(), pad);

                context.skipOriginal(null);
            }
        }
    }


    @After(target = @MethodMatch(methodName = "advance"))
    public static void advance(AfterContext context) throws NoSuchMethodException {
        var self = context.getSelf();
        if (self instanceof NavBuoyEntityPlugin plugin) {
            var entity = (SectorEntityToken) context.getField("entity").get();
            if (entity.getContainingLocation() == null || entity.isInHyperspace()) return;
            if (plugin.isHacked() && isInverted()) {
                var id = "nav_buoy";

                for (CampaignFleetAPI fleet : entity.getContainingLocation().getFleets()) {
                    if (fleet.isInHyperspaceTransition()) continue;

                    if (validateFleet(fleet, entity)) {
                        String desc = "Nav buoy backlash";
                        float bonus = -NAV_BONUS;
                        if (entity.hasTag(Tags.MAKESHIFT)) {
                            desc = "Makeshift nav buoy backlash";
                            bonus = -NAV_BONUS_MAKESHIFT;
                        }

                        MutableStat.StatMod curr = fleet.getStats().getFleetwideMaxBurnMod().getFlatBonus(id);
                        bonus += 1;
                        if (curr == null || curr.value >= bonus) {
                            fleet.getStats().addTemporaryModFlat(0.1f, id,
                                    desc, bonus,
                                    fleet.getStats().getFleetwideMaxBurnMod());
                        }
                    }
                }
            }
        }
    }
}
