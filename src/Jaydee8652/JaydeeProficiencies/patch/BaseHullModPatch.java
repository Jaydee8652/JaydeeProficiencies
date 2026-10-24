package Jaydee8652.JaydeeProficiencies.patch;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import patchlib.api.context.AfterContext;
import patchlib.api.match.ClassMatch;
import patchlib.api.match.MethodMatch;
import patchlib.api.patch.After;
import patchlib.api.patch.Patch;

import static Jaydee8652.JaydeeProficiencies.utils.jdp_SpecStore.getHullmodIncompatibilities;

@Patch(target = @ClassMatch(subtype = BaseHullMod.class))
public class BaseHullModPatch {
    @After(target = @MethodMatch(methodName = "isApplicableToShip"))
    public static void afterIsApplicableToShip(AfterContext context) {
        if (context.getSelf() instanceof BaseHullMod baseHullMod) {
            context.getField("spec");
            var spec = (HullModSpecAPI) context.getField("spec").get();
            ShipAPI ship = (ShipAPI) context.getArg(0);
            if (spec == null || ship == null) return;

            if (getHullmodIncompatibilities(spec.getId()).stream().anyMatch(id -> ship.getVariant().getHullMods().contains(id))) {
                context.setReturnValue(false);
            }
        }
    }

    @After(target = @MethodMatch(methodName = "getUnapplicableReason"))
    public static void afterGetUnapplicableReason(AfterContext context) {
        if (context.getSelf() instanceof BaseHullMod baseHullMod) {
            var spec = (HullModSpecAPI) context.getField("spec").get();//ReflectionUtilis.getPrivateVariable("spec", baseHullMod);
            ShipAPI ship = (ShipAPI) context.getArg(0);
            if (spec == null || ship == null) return;

            var incompatible = getHullmodIncompatibilities(spec.getId()).stream().filter(id -> ship.getVariant().getHullMods().contains(id)).toList();
            if (!incompatible.isEmpty()) {
                if (incompatible.contains("jdp_modularShip")) {
                    context.setReturnValue("Incompatible, cannot be applied to modular ships.");
                } else {
                    var list = "";
                    for (int i = 0; i < incompatible.size(); i++) {
                        String id = incompatible.get(i);
                        String name = Global.getSettings().getHullModSpec(id).getDisplayName();

                        String spacer;
                        if (i == incompatible.size() - 1) {
                            spacer = "";
                        } else if (i == incompatible.size() - 2) {
                            spacer = " and ";
                        } else {
                            spacer = ", ";
                        }
                        list += name + spacer;
                    }
                    if (context.getReturnValue() == null) context.setReturnValue("Incompatible with " + list);
                }
            }
        }
    }
}