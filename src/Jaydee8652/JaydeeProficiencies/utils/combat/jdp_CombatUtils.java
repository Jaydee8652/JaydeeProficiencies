package Jaydee8652.JaydeeProficiencies.utils.combat;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import java.util.ArrayList;
import java.util.List;

public class jdp_CombatUtils {
    public static List<DamagingProjectileAPI> getProjectilesFromSource(ShipAPI source) {
        List<DamagingProjectileAPI> projectiles = new ArrayList<>();
        for (DamagingProjectileAPI tmp : Global.getCombatEngine().getProjectiles()) {
            if (tmp instanceof MissileAPI) {
                continue;
            }

            if (tmp.getSource().equals(source)) {
                projectiles.add(tmp);
            }
        }
        return projectiles;
    }

    public static List<MissileAPI> getMissilesFromSource(ShipAPI source) {
        List<MissileAPI> projectiles = new ArrayList<>();
        for (DamagingProjectileAPI tmp : Global.getCombatEngine().getProjectiles()) {
            if (tmp instanceof MissileAPI) {
                if (tmp.getSource().equals(source)) {
                    projectiles.add((MissileAPI) tmp);
                }
            }
        }
        return projectiles;
    }
}








