package Jaydee8652.JaydeeProficiencies.patch;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.hullmods.Automated;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import patchlib.api.context.AfterContext;
import patchlib.api.match.ClassMatch;
import patchlib.api.match.MethodMatch;
import patchlib.api.patch.After;
import patchlib.api.patch.Patch;

import java.util.HashMap;
import java.util.Map;

@Patch(target = @ClassMatch(subtype = Automated.class))
public class AutomatedHullmodPatch {
    public static Map<String, TooltipBuffer> JDP_AUTOMATED_NO_PENALTY_TAGS = new HashMap<String, TooltipBuffer>();
    public interface TooltipBuffer {
        void createTooltip(TooltipMakerAPI tooltip, ShipAPI ship);
    }

    public static boolean check(FleetMemberAPI member) {
        for (String tag: JDP_AUTOMATED_NO_PENALTY_TAGS.keySet()) {
            if (member.getHullSpec().hasTag(tag) || member.getVariant().hasTag(tag)) return true;
        }
        return false;
    }

    @After(target = @MethodMatch(methodName = "isAutomatedNoPenalty", parameterTypes = { MutableShipStatsAPI.class }))
    public static void isMutableShipStatsAPI(AfterContext context) {
        MutableShipStatsAPI stats = (MutableShipStatsAPI) context.getArg(0);
        if (stats == null) return;
        FleetMemberAPI member = stats.getFleetMember();
        if (member == null) return;
        if(check(member)) context.setReturnValue(true);
    }
    @After(target = @MethodMatch(methodName = "isAutomatedNoPenalty", parameterTypes = { ShipAPI.class }))
    public static void isShipAPI(AfterContext context) {
        ShipAPI ship = (ShipAPI) context.getArg(0);
        if (ship == null) return;
        FleetMemberAPI member = ship.getFleetMember();
        if (member == null) return;
        if(check(member)) context.setReturnValue(true);
    }
    @After(target = @MethodMatch(methodName = "isAutomatedNoPenalty", parameterTypes = { FleetMemberAPI.class }))
    public static void isFleetMemberAPI(AfterContext context) {
        FleetMemberAPI member = (FleetMemberAPI) context.getArg(0);
        if (member == null) return;
        if(check(member)) context.setReturnValue(true);
    }

    @After(target = @MethodMatch(methodName = "addPostDescriptionSection"))
    public static void addPostDescriptionSection(AfterContext context) {
        ShipAPI ship = (ShipAPI) context.getArg(2);
        if (ship == null) return;
        FleetMemberAPI member = ship.getFleetMember();
        if (member == null) return;

        TooltipMakerAPI tooltip = (TooltipMakerAPI) context.getArg(0);
        if ((member.getHullSpec().hasTag(Tags.TAG_AUTOMATED_NO_PENALTY)) || member.getVariant().hasTag(Tags.TAG_AUTOMATED_NO_PENALTY)) return;

        for (String tag: JDP_AUTOMATED_NO_PENALTY_TAGS.keySet()) {
            if (member.getHullSpec().hasTag(tag) || member.getVariant().hasTag(tag)) {
                JDP_AUTOMATED_NO_PENALTY_TAGS.get(tag).createTooltip(tooltip, ship);
            }
        }
    }
}