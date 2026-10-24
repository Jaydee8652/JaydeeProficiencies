package Jaydee8652.JaydeeProficiencies.utils.combat;

import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_FactionUtils;
import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.input.InputEventAPI;

import java.util.List;

public class jdp_CombatHandler implements EveryFrameCombatPlugin {
	public interface CombatHandlerImpl {
		void processInputPreCoreControls(float amount, List<InputEventAPI> events);
		void advance(float amount, List<InputEventAPI> events);
		void renderInWorldCoords(ViewportAPI viewport);
		void renderInUICoords(ViewportAPI viewport);
		void init(CombatEngineAPI engine);
	}

	@Override
	public void processInputPreCoreControls(float amount, List<InputEventAPI> events) {
		for (jdp_CombatHandler.CombatHandlerImpl CombatHandler : Global.getSector().getListenerManager().getListeners(jdp_CombatHandler.CombatHandlerImpl.class)) CombatHandler.processInputPreCoreControls(amount, events);
	}

	@Override
	public void advance(float amount, List<InputEventAPI> events) {
		for (jdp_CombatHandler.CombatHandlerImpl CombatHandler : Global.getSector().getListenerManager().getListeners(jdp_CombatHandler.CombatHandlerImpl.class)) CombatHandler.advance(amount, events);
	}

	@Override
	public void renderInWorldCoords(ViewportAPI viewport) {
		for (jdp_CombatHandler.CombatHandlerImpl CombatHandler : Global.getSector().getListenerManager().getListeners(jdp_CombatHandler.CombatHandlerImpl.class)) CombatHandler.renderInWorldCoords(viewport);
	}

	@Override
	public void renderInUICoords(ViewportAPI viewport) {
		for (jdp_CombatHandler.CombatHandlerImpl CombatHandler : Global.getSector().getListenerManager().getListeners(jdp_CombatHandler.CombatHandlerImpl.class)) CombatHandler.renderInUICoords(viewport);
	}

	@Override
	public void init(CombatEngineAPI engine) {
		for (jdp_CombatHandler.CombatHandlerImpl CombatHandler : Global.getSector().getListenerManager().getListeners(jdp_CombatHandler.CombatHandlerImpl.class)) CombatHandler.init(engine);
		if(Global.getCurrentState() == GameState.TITLE) jdp_FactionUtils.resetPlayerFaction();
	}
}








