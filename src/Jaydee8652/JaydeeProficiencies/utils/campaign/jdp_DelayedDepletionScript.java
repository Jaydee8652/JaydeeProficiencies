package Jaydee8652.JaydeeProficiencies.utils.campaign;

import java.util.*;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.util.DelayedActionScript;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_TerraformingProcessUtils.TerraformingProcessData;

import static Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_IndustryUtils.updateMarket;
import static Jaydee8652.JaydeeProficiencies.utils.campaign.jdp_TerraformingProcessUtils.generateTerraformingIntel;

public class jdp_DelayedDepletionScript extends DelayedActionScript {
	protected float runaway;
	protected float initialTime;

	protected MarketAPI market;
	protected String id;
	protected Integer minimum;
	protected Integer variance;

	protected TerraformingProcessData primaryProcess = null;
	protected TerraformingProcessData secondaryProcess = null;

	protected List<TerraformingProcessData> terraformingProcessMap;

	protected float depletions = 0;
	protected Boolean locked = false;

	public Boolean confirmation() {
		return ((primaryProcess != null && primaryProcess.isAllowed(market)) && (locked || market.getAdmin().getStats().hasSkill(id.replace("_GO_0", ""))));
	}

	public void advance(float amount) {
		super.advance(amount);
		if(daysLeft / initialTime < runaway) locked = true;

		if(!confirmation()) {
			Global.getSector().removeScript(this);
			market.removeTag(id);
		}
	}

	//A truly devious script, hopefully uncheesable.
	//Checks on advance whether the skill is present, but at a certain % of completion becomes locked in.
	//Processes that are undesirable usually have a low %.
	//The Process is determined at the start of the process, so save-scumming should take months.
	public jdp_DelayedDepletionScript(float runaway, MarketAPI market, String id, Integer minimum, Integer variance, List<jdp_TerraformingProcessUtils.TerraformingProcessData> terraformingProcessMap) {
		super(Math.round(minimum + Math.floor(Math.random() * variance)));

		this.initialTime = this.daysLeft;
		this.runaway = runaway;
		this.market = market;
		this.id = id;
		this.minimum = minimum;
		this.variance = variance;
		this.terraformingProcessMap = terraformingProcessMap;

		pickProcess();
		market.addTag(id);
	}

	@Override
	public void doAction() {
		if(confirmation()) {
			actionScript();
		}
	}

	public void pickProcess() {
		if (market.getMemory().contains("$" + id + "_depletions")) depletions = (Float) market.getMemory().get("$" + id + "_depletions");

		List<TerraformingProcessData> primaryProcesses = new ArrayList<>(terraformingProcessMap.stream().filter(p -> p.getWeight() == null && p.isAllowed(market)).toList());
		List<TerraformingProcessData> secondaryProcesses = new ArrayList<>(terraformingProcessMap.stream().filter(p -> p.getWeight() != null && p.isAllowed(market)).toList());

		if (!primaryProcesses.isEmpty()) {
			Collections.shuffle(primaryProcesses);
			primaryProcess = primaryProcesses.get(0);

			WeightedRandomPicker<TerraformingProcessData> picker = new WeightedRandomPicker<TerraformingProcessData>(new Random());
			for(TerraformingProcessData processData : secondaryProcesses) picker.add(processData, processData.getWeight() + depletions);

			if(!picker.isEmpty()) secondaryProcess = picker.pick();
		}
	}

	public void actionScript() {
		if (primaryProcess != null) {
			MessageIntel intel = new MessageIntel();

			intel.setIcon(Global.getSettings().getSkillSpec(id.replace("_GO_0", "")).getSpriteName());
			intel.endAfterDelay(1);
			intel.setSound(intel.getCommMessageSound());
			intel.addLine(Global.getSettings().getSkillSpec(id.replace("_GO_0", "")).getName() + " - " + market.getName(), Misc.getTextColor(), new String[]{}, Misc.getHighlightColor());

			String product = primaryProcess.getProduct();
			if (product != null) {
				market.addCondition(product);
				market.getCondition(product).setSurveyed(true);
			}
			String reagent = primaryProcess.getReagent();
			if (reagent != null) {
				market.removeCondition(reagent);
			}

			if(secondaryProcess != null && secondaryProcess.isAllowed(market)) {
				String byproduct = secondaryProcess.getProduct();
				if (byproduct != null) {
					market.addCondition(byproduct);
					market.getCondition(byproduct).setSurveyed(true);
				}
				String byreagent = secondaryProcess.getReagent();
				if (byreagent != null) {
					market.removeCondition(byreagent);
				}
			}

			intel = generateTerraformingIntel(intel, market, primaryProcess, secondaryProcess);

			market.getMemory().set("$" + id + "_depletions", depletions + 1f);

			Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.COLONY_INFO, market);
		}

		updateMarket(market);
		market.removeTag(id);
	}
}



