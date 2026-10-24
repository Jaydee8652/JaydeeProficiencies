package Jaydee8652.JaydeeProficiencies;

import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistExperienceManager;
import Jaydee8652.JaydeeProficiencies.ui.jdp_FactionDialogInjector;
import Jaydee8652.JaydeeProficiencies.ui.jdp_SpecialistTabInjector;
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_FactionUtils;
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_PlayerFactionIntel;
import Jaydee8652.JaydeeProficiencies.utils.jdp_SpecStore;
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.ClassRefs;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.loading.FighterWingSpecAPI;
import org.apache.log4j.Logger;

import java.util.ArrayList;


public class JaydeeProficienciesPlugin extends BaseModPlugin {
    private static final Logger log = Global.getLogger(JaydeeProficienciesPlugin.class);

    public void onApplicationLoad() {
        ClassRefs.findAllClasses();

        jdp_SpecStore.loadNormalAptitudeSpecsFromCSV();
        jdp_SpecStore.loadProficiencySpecsFromCSV();
        jdp_SpecStore.loadHullmodIncompatibilitiesFromCSV();
    }

    public void onGameLoad(boolean newGame) {
        jdp_FactionUtils.updatePlayerFaction();
        Global.getSector().getPlayerPerson().getStats().setSkillLevel("jdp_refit_applicator", 1);
        Global.getSector().addTransientScript(new jdp_SpecialistTabInjector());
        Global.getSector().addTransientScript(new jdp_FactionDialogInjector());
        new jdp_SpecialistExperienceManager().init();

        orbitValidator();
        starAgeValidator();
        systemCentreValidator();
        fighterSpecValidator();
    }

    /*
    public void beforeGameSave() {
        resetPlayerFaction();
    }*/

    public void onNewGame() {
        new jdp_PlayerFactionIntel().init();
    }

    public void orbitValidator() {
        String name = "Orbit Validator";
        log.info("JDP_DEBUG: " + name + " Running");
        int count = 0;

        ArrayList<SectorEntityToken> tokens = new ArrayList<SectorEntityToken>();

        for (StarSystemAPI system : Global.getSector().getStarSystems()) {
            tokens.addAll(system.getAllEntities());
        }
        tokens.addAll(Global.getSector().getHyperspace().getAllEntities());

        for (SectorEntityToken token : tokens) {
            float x = token.getLocation().x;
            float y = token.getLocation().y;
            if (Float.isNaN(x) || Float.isInfinite(x) || Float.isNaN(y) || Float.isInfinite(y)) {
                count++;
                log.warn("JDP_DEBUG: Entity [" + token.getFullName() + "] [" + token.getId() + "] in [" + token.getContainingLocation().getNameWithLowercaseType() + "] has an invalid orbit");
            }
        }
        if (count == 0) {
            log.info("JDP_DEBUG: "+ name + " found no issues");
        } else {
            log.info("JDP_DEBUG: " + name + " found " + count + " entities with invalid orbits");
        }
    }

    public void starAgeValidator() {
        String name = "Star Age Validator";
        log.info("JDP_DEBUG: " + name + " Running");
        int count = 0;

        for (StarSystemAPI system : Global.getSector().getStarSystems()) {
            if (system.getAge() == null) {
                count++;
                log.warn("JDP_DEBUG: System [" + system.getBaseName() + "] [" + system.getId() + "] at [" + system.getLocation() + "] has null age");
                log.info("JDP_DEBUG: Has tags [" + system.getTags() + "]");
            }
        }
        if (count == 0) {
            log.info("JDP_DEBUG: "+ name + " found no issues");
        } else {
            log.info("JDP_DEBUG: " + name + " found " + count + " systems with null age");
        }
    }

    public void systemCentreValidator() {
        String name = "System Centre Validator";
        log.info("JDP_DEBUG: " + name + " Running");
        int count = 0;

        for (StarSystemAPI system : Global.getSector().getStarSystems()) {
            if (system.getCenter() == null) {
                count++;
                log.warn("JDP_DEBUG: System [" + system.getBaseName() + "] [" + system.getId() + "] at [" + system.getLocation() + "] has an uninitialized centre");
                log.info("JDP_DEBUG: Has tags [" + system.getTags() + "]");
            }
        }
        if (count == 0) {
            log.info("JDP_DEBUG: "+ name + " found no issues");
        } else {
            log.info("JDP_DEBUG: " + name + " found " + count + " systems with an uninitialized centre");
        }
    }

    public void fighterSpecValidator() {
        String name = "Fighter Spec Validator";
        log.info("JDP_DEBUG: " + name + " Running");
        int count = 0;

        for (FactionAPI faction : Global.getSector().getAllFactions()) {
            for (String id : faction.getKnownFighters()) {
                FighterWingSpecAPI spec = Global.getSettings().getFighterWingSpec(id);
                if (spec == null) {
                    count++;
                    log.warn("JDP_DEBUG: Fighter [" + id + "] has null spec");
                }
            }
        }
        if (count == 0) {
            log.info("JDP_DEBUG: " + name + " found no issues");
        } else {
            log.info("JDP_DEBUG: " + name + " found " + count + " fighters with an null specs");
        }
    }
}
