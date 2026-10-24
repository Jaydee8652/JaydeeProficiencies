package Jaydee8652.JaydeeProficiencies.utils.campaign;

import java.util.*;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.util.DelayedActionScript;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;

public class jdp_DelayedActionScript extends DelayedActionScript {
	protected float runaway;
	protected float initialTime;

	protected MarketAPI market;
	protected String id;
	protected Integer minimum;
	protected Integer variance;

	protected Boolean locked = false;

	public Boolean confirmation() {
		return locked;
	}

	public void action() {

	}

	@Override
	public void advance(float amount) {
		super.advance(amount);
		if(daysLeft / initialTime < runaway) locked = true;

		if(!confirmation()) {
			Global.getSector().removeScript(this);
			market.removeTag(id);
		}
	}

	public jdp_DelayedActionScript(float runaway, MarketAPI market, String id, Integer minimum, Integer variance) {
		super(Math.round(minimum + Math.floor(Math.random() * variance)));

		this.initialTime = this.daysLeft;
		this.runaway = runaway;
		this.market = market;
		this.id = id;
		this.minimum = minimum;
		this.variance = variance;

		market.addTag(id);
	}

	@Override
	public void doAction() {
		if (confirmation()) {
			action();
			market.reapplyConditions();
			market.reapplyIndustries();

			market.removeTag(id);
		}
	}
}



