package Jaydee8652.JaydeeProficiencies.utils.campaign;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.BaseCustomEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.util.FlickerUtilV2;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.BaseCampaignEntity;
import com.fs.starfarer.loading.specs.PlanetSpec;
import com.fs.util.C;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import org.lwjgl.util.vector.Vector3f;

import javax.swing.*;
import java.awt.*;

public class jdp_ColonyFlicker implements EveryFrameScript {

	protected float phase = 0.0F;
	protected FlickerUtilV2 flicker = new FlickerUtilV2();

	protected PlanetAPI planet;
	protected Float speed;

	protected Boolean glowColor;
	protected Boolean shieldColor;
	protected Boolean shieldColor2;

	public jdp_ColonyFlicker(PlanetAPI planet, Float speed, Boolean glowColor, Boolean shieldColor, Boolean shieldColor2) {
		this.planet = planet;

		//Default speed is 0.2F.
		// Higher numbers flicker faster, lower numbers slower.
		this.speed = speed;

		//Planet glow, colony lights
		this.glowColor = glowColor;

		//Actual shield, probably don't touch this one
		this.shieldColor = shieldColor;

		//Cosmetic shield, plasma dynamo like effects.
		this.shieldColor2 = shieldColor2;
	}

	@Override
	public boolean isDone() {
		return false;
	}

	@Override
	public boolean runWhilePaused() {
		return false;
	}

	@Override
	public void advance(float amount) {
		phase += amount * speed;
		while (phase > 1) phase --;

		flicker.advance(amount);

		render();
	}


	public float getFlickerBasedMult() {
		float f = 1f - 1.3f * flicker.getBrightness();
		return f;
	}

	public float getGlowAlpha() {
		float glowAlpha = 0f;
		if (phase < 0.5f) glowAlpha = phase * 2f;
		if (phase >= 0.5f) glowAlpha = (1f - (phase - 0.5f) * 2f);
		glowAlpha = 0.75f + glowAlpha * 0.25f;
		glowAlpha *= getFlickerBasedMult();
		if (glowAlpha < 0) glowAlpha = 0;
		if (glowAlpha > 1) glowAlpha = 1;
		return glowAlpha;
	}

	public void render() {

		PlanetSpecAPI spec = planet.getSpec();
		if (spec == null) return;
		float glowAlpha = getGlowAlpha();

		int alpha = (int) (glowAlpha * 0.5f * 255);

		if (glowColor) spec.setGlowColor(Misc.setAlpha(spec.getGlowColor(), alpha));
		if (shieldColor) spec.setShieldColor(Misc.setAlpha(spec.getGlowColor(), alpha));
		if (shieldColor2) spec.setShieldColor2(Misc.setAlpha(spec.getGlowColor(), alpha));

		planet.applySpecChanges();
	}
}









