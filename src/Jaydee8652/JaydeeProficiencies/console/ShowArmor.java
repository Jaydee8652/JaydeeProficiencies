package Jaydee8652.JaydeeProficiencies.console;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;

import java.lang.ref.WeakReference;
import java.util.List;

public class ShowArmor implements BaseCommand {
    private static WeakReference<ShowArmorPlugin> plugin;

    @Override
    public CommandResult runCommand(String args, CommandContext context) {
        if (!context.isInCombat()) {
            Console.showMessage(CommonStrings.ERROR_COMBAT_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }

        ShowArmorPlugin tmp;
        if (plugin == null || plugin.get() == null || plugin.get().engine != Global.getCombatEngine()) {
            tmp = new ShowArmorPlugin();
            plugin = new WeakReference<>(tmp);
            Global.getCombatEngine().addPlugin(tmp);
            Console.showMessage("Armor display enabled.");
        } else {
            tmp = plugin.get();
            plugin.clear();
            tmp.active = false;
            Console.showMessage("Armor display disabled.");
        }

        return CommandResult.SUCCESS;
    }

    private static class ShowArmorPlugin extends BaseEveryFrameCombatPlugin {
        private boolean active = true;
        private CombatEngineAPI engine;

        @Override
        public void advance(float amount, List<InputEventAPI> events) {
            if (!active) {
                engine.removePlugin(this);
                return;
            }
            if (engine.isPaused()) return;

            for (ShipAPI ship : Global.getCombatEngine().getShips()) {
                var grid = ship.getArmorGrid();
                var armor = 0f;
                for (var x = 0; x < grid.getGrid().length; x++) {
                    for (var y = 0; y < grid.getGrid()[0].length; y++) {
                        var value = grid.getArmorValue(x, y);
                        armor += value;

                        if (value < grid.getMaxArmorInCell()) {
                            Global.getCombatEngine().addFloatingTextAlways(
                                    grid.getLocation(x, y),
                                    "" + grid.getArmorValue(x, y),
                                    10f,
                                    Misc.getHighlightColor(),
                                    ship,
                                    0f,
                                    0f,
                                    amount,
                                    0f,
                                    0f,
                                    1f);
                        }
                    }
                }

                Global.getCombatEngine().addFloatingTextAlways(
                        ship.getLocation(),
                        "" + Math.round(10080f - armor),
                        10f,
                        Misc.getBasePlayerColor(),
                        ship,
                        0f,
                        0f,
                        amount,
                        0f,
                        0f,
                        1f);
            }
        }

        @Override
        public void init(CombatEngineAPI engine) {
            this.engine = engine;
        }
    }
}
