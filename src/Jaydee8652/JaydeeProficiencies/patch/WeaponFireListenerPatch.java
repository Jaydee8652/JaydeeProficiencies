package Jaydee8652.JaydeeProficiencies.patch;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import patchlib.api.context.BeforeContext;
import patchlib.api.context.MethodCallContext;
import patchlib.api.match.ClassMatch;
import patchlib.api.match.MethodMatch;
import patchlib.api.patch.Before;
import patchlib.api.patch.Patch;
import patchlib.api.patch.RedirectMethodCall;

import java.util.ArrayList;

@Patch(target = @ClassMatch(subtype = WeaponAPI.class, methodMatches = { @MethodMatch(methodName = "isToggle"), @MethodMatch(methodName = "fireProjectile") }))
public class WeaponFireListenerPatch {
    public interface WeaponFireListener {
        void reportProjectileSpawn(Object projectile, WeaponAPI spec, CombatEngineAPI engine);
        void reportOnFire(int burst, WeaponAPI spec, CombatEngineAPI engine);
    }

    @RedirectMethodCall(
            target = @MethodMatch(parameterTypes = { int.class, float.class }),
            call = @MethodMatch(parameterCount = 6, returnType = Object.class)
    )
    public static void projectileSpawn(MethodCallContext context) {
        var projectile = context.call();
        if (context.getSelf() instanceof WeaponAPI spec) {
            var ship = spec.getShip();
            for (WeaponFireListener listener : new ArrayList<WeaponFireListener>(Global.getCombatEngine().getListenerManager().getListeners(WeaponFireListener.class))) {
                listener.reportProjectileSpawn(projectile, spec, Global.getCombatEngine());
            }
            if (ship != null) {
                for (WeaponFireListener listener : new ArrayList<WeaponFireListener>(ship.getListeners(WeaponFireListener.class))) {
                    listener.reportProjectileSpawn(projectile, spec, Global.getCombatEngine());
                }
            }
        }
        context.setResult(projectile);
    }

    @Before(target = @MethodMatch(methodName = "fireProjectile"))
    public static void fireProjectile(BeforeContext context) {
        var burst = (int) context.getArg(0);
        if (context.getSelf() instanceof WeaponAPI spec) {
            var ship = spec.getShip();

            for (WeaponFireListener listener : new ArrayList<WeaponFireListener>(Global.getCombatEngine().getListenerManager().getListeners(WeaponFireListener.class))) {
                listener.reportOnFire(burst, spec, Global.getCombatEngine());
            }
            if (ship != null) {
                for (WeaponFireListener listener : new ArrayList<WeaponFireListener>(ship.getListeners(WeaponFireListener.class))) {
                    listener.reportOnFire(burst, spec, Global.getCombatEngine());
                }
            }
        }
    }
}