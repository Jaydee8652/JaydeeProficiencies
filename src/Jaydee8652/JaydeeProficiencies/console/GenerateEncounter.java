package Jaydee8652.JaydeeProficiencies.console;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.listeners.ListenerUtil;
import com.fs.starfarer.api.impl.campaign.enc.EPEncounterCreator;
import com.fs.starfarer.api.impl.campaign.enc.EncounterManager;
import com.fs.starfarer.api.impl.campaign.enc.EncounterPoint;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.lazywizard.console.BaseCommandWithSuggestion;
import org.lazywizard.console.CommonStrings;
import org.lazywizard.console.Console;
import org.lwjgl.util.vector.Vector2f;

import java.util.*;
import java.util.List;

import static com.fs.starfarer.api.impl.campaign.enc.EncounterManager.*;
import static com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator.random;
import static com.fs.starfarer.api.impl.campaign.terrain.HyperspaceAbyssPluginImpl.*;
import static com.fs.starfarer.api.util.Misc.getAbyssalDepth;
import static com.fs.starfarer.api.util.Misc.getAbyssalSystems;
import static org.lazywizard.console.CommandUtils.*;

public class GenerateEncounter implements BaseCommandWithSuggestion {
    public static Map<String, EPEncounterCreator> creatorMap = new LinkedHashMap<String, EPEncounterCreator>();
    static {
        for(EPEncounterCreator creator: CREATORS) {
            creatorMap.put(creator.getId(), creator);
        }
    }

    public List<EncounterPoint> generateEncounterPoints(LocationAPI where) {
        if (!where.isHyperspace()) return null;
        var sensorRange = Global.getSettings().getMaxSensorRange(where) / 2f;

        List<EncounterPoint> points = ListenerUtil.generateEncounterPoints(where);

        float startAngle = random.nextFloat() * 360f;
        for (float angle = startAngle; angle < startAngle + 360f; angle += 30f) {
            Vector2f loc = Misc.getUnitVectorAtDegreeAngle(angle);
            loc.scale(sensorRange);
            Vector2f.add(loc, Global.getSector().getPlayerFleet().getLocation(), loc);

            float depth = getAbyssalDepth(loc, true);
            if (depth < DEPTH_THRESHOLD_FOR_ENCOUNTER) continue;

            // Can match ids of other points, or have different ids for points that are
            // very close to each other if they're across 1000-boundary
            // this is fine. Just a way to *somewhat* limit repeated spawns in the same location

            String id = "abyssal_" + (int)(loc.x/1000f) + "_" + (int)(loc.y/1000f);
            EncounterPoint point = new EncounterPoint(id, where, loc, EP_TYPE_ABYSSAL);

            AbyssalEPData data = new AbyssalEPData();
            data.depth = depth;
            data.random = Misc.getRandom(random.nextLong(), 7);

            float minDist = Float.MAX_VALUE;
            StarSystemAPI nearest = null;
            for (StarSystemAPI system : getAbyssalSystems()) {
                float distToSystem = Misc.getDistance(system.getLocation(), loc);
                if (distToSystem < sensorRange && distToSystem < minDist) {
                    minDist = distToSystem;
                    nearest = system;
                }
            }
            if (nearest != null) {
                data.nearest = nearest;
                data.distToNearest = minDist;
            }

            point.custom = data;
            points.add(point);
        }

        return points;
    }


    @Override
    public CommandResult runCommand(String args, CommandContext context) {
        String[] tmp = args.split(" ");
        if (!context.isInCampaign()) {
            Console.showMessage(CommonStrings.ERROR_CAMPAIGN_ONLY);
            return CommandResult.WRONG_CONTEXT;
        }
        if (!Global.getSector().getPlayerFleet().isInHyperspace()) {
            Console.showMessage("Error: This command is hyperspace-only.");
            return CommandResult.WRONG_CONTEXT;
        }

        if (tmp.length != 1) {
            return CommandResult.BAD_SYNTAX;
        }

        LocationAPI where = Global.getSector().getCurrentLocation();
        if (where == null) {
            Console.showMessage("Current location is null!");
            return CommandResult.ERROR;
        }

        WeightedRandomPicker<EncounterPoint> picker = new WeightedRandomPicker<EncounterPoint>(random);
        for (EncounterPoint p : generateEncounterPoints(where)) {
            float dist = Misc.getDistance(Global.getSector().getPlayerFleet().getLocation(), p.loc);
            if (dist < Global.getSettings().getMaxSensorRange(where)) {
                picker.add(p, 1);
            }
        }

        var point = picker.pick();
        if (point != null) {
            final String creatorId = findBestStringMatch(tmp[0], creatorMap.keySet());
            if (creatorId == null && tmp[0] != "") {
                Console.showMessage("No encounter found with id [" + tmp[0] + "]");
                return CommandResult.ERROR;
            } else {
                var creator = creatorMap.get(creatorId);
                creator.createEncounter(new EncounterManager(), point);
                Console.showMessage("Generated encounter with id [" + tmp[0] + "] at [" + point.loc + "]");
                return CommandResult.SUCCESS;
            }
        } else {
            Console.showMessage("No valid location for encounter with id [" + tmp[0] + "]");
            return CommandResult.ERROR;
        }
    }

    @Override
    public List<String> getSuggestions(int parameter, List<String> previous, CommandContext context) {
        if (parameter != 0) return new ArrayList<>();
        return creatorMap.keySet().stream().toList();
    }
}
