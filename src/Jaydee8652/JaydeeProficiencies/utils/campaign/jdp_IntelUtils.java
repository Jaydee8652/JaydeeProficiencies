package Jaydee8652.JaydeeProficiencies.utils.campaign;

import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_PanelData;
import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_Specialist;
import Jaydee8652.JaydeeProficiencies.specialists.elements.jdp_ExperienceBarElement;
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags;
import Jaydee8652.JaydeeProficiencies.specialists.tooltips.ExperienceBarTooltipCreator;
import Jaydee8652.JaydeeProficiencies.utils.jdp_SpecStore;
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.*;
import com.fs.starfarer.D.C;
import com.fs.starfarer.D.M;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.Script;
import com.fs.starfarer.api.campaign.BaseCustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.CampaignUIAPI;
import com.fs.starfarer.api.campaign.CoreUITabId;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.RuleBasedInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.abilities.TransponderAbility;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.*;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.rpg.Person;
import lunalib.lunaUI.elements.LunaColorPicker;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.*;
import java.util.List;

import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistPickerMenu.recreatePanel;
import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistScreen.initialise;
import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistScreen.recreateSections;
import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistTags.*;
import static Jaydee8652.JaydeeProficiencies.specialists.jdp_SpecialistUtils.*;
import static Jaydee8652.JaydeeProficiencies.utils.refl.ui.UIReflectionUtils.*;
import static com.fs.starfarer.coreui.returnnew.PANEL_HEIGHT;
import static com.fs.starfarer.coreui.returnnew.PANEL_WIDTH;

public class jdp_IntelUtils {
	public static String extractTypeData(List<String> types) {
		String personelle = "";
		for (int i = 0; i < types.size(); i++) {
			String type = types.get(i);
			String string = jdp_SpecialistTags.SPECIALIST_TYPE_MAP.get(type).name();

			String spacer;
			if (i == types.size() - 1) {
				spacer = "";
			} else if (i == types.size() - 2) {
				spacer = " & ";
			} else {
				spacer = ", ";
			}
			personelle += string + spacer;
		}
		return personelle;
	}

	public static String extractPlayerTypeData(List<String> tags) {
		String personelle = "";

		if(!tags.contains(jdp_SpecialistTags.SPEC_TAG_PLAYER_NO_XP_AS_OFFICER)) personelle += jdp_SpecialistTags.SPECIALIST_TYPE_MAP.get(jdp_SpecialistTags.SPECIALIST_TYPE_OFFICER).name();
		if(!tags.contains(jdp_SpecialistTags.SPEC_TAG_PLAYER_NO_XP_AS_ADMIN)) {
			if(!personelle.equals("")) personelle += " & ";
			personelle += jdp_SpecialistTags.SPECIALIST_TYPE_MAP.get(jdp_SpecialistTags.SPECIALIST_TYPE_ADMIN).name();
		}
		return "(Player) " + personelle;
	}

	public static void addHireSpecialistIntel(PersonAPI person, jdp_SpecStore.jdp_proficiencySpec spec) {
		MessageIntel intel = new MessageIntel();
		intel.setIcon(person.getPortraitSprite());
		intel.endAfterDelay(3);
		intel.setSound(intel.getCommMessageSound());
		intel.addLine("New Specialist: " + person.getName().getFullName(), Misc.getBasePlayerColor(), new String[]{});
		intel.addLine(BaseIntelPlugin.BULLET + "Proficiency: " + spec.getName(), Misc.getGrayColor(), new String[]{spec.getName()}, spec.getColor());

		String personelle = extractTypeData(spec.getType());
		intel.addLine(BaseIntelPlugin.BULLET + "Type: " + personelle, Misc.getGrayColor(), new String[]{personelle}, Misc.getHighlightColor());
		Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.CHARACTER_TAB);
	}

	public static void addSpecialistLevelIntel(jdp_Specialist specialist) {
		var person = specialist.getPerson();
		var spec = specialist.getProficiencySpec();

		MessageIntel intel = new MessageIntel();
		intel.setIcon(person.getPortraitSprite());
		intel.endAfterDelay(3);
		intel.setSound(intel.getCommMessageSound());
		intel.addLine("Specialist Level-up: " + person.getName().getFullName(), Misc.getBasePlayerColor(), new String[]{});

		if(specialist.getLevel() == specialist.getMaxLevel()) {
			intel.addLine(BaseIntelPlugin.BULLET + "Level " + specialist.getLevel() + " (Max Level Reached)", Misc.getTextColor(), new String[]{specialist.getLevel() + ""}, Misc.getHighlightColor());
		} else {
			intel.addLine(BaseIntelPlugin.BULLET + "Level " + specialist.getLevel(), Misc.getTextColor(), new String[]{specialist.getLevel() + ""}, Misc.getHighlightColor());
		}
		intel.addLine(BaseIntelPlugin.BULLET + "Proficiency: " + spec.getName(), Misc.getGrayColor(), new String[]{spec.getName()}, spec.getColor());
		Global.getSector().getCampaignUI().addMessage(intel, CommMessageAPI.MessageClickAction.CHARACTER_TAB);
	}

	public static void fireRuleFromIntel(String trigger) {
		RuleBasedInteractionDialogPluginImpl plugin = new RuleBasedInteractionDialogPluginImpl(trigger);
		Global.getSector().getCampaignUI().showInteractionDialogFromCargo(plugin,
				Global.getSector().getPlayerFleet(), new CampaignUIAPI.DismissDialogDelegate() {
					@Override
					public void dialogDismissed() {
					}
				});
	}


	public static final float SKILL_ELEMENT_SIZE = 40;//48f
	public static final float TYPE_ELEMENT_SIZE = 20f;
	public static final float PICKER_PANEL_WIDTH = 959f;
	public static final float PICKER_PANEL_HEIGHT = 707f;
	public static final float PICKER_PANEL_ENTRY_SIZE = 120f;
	public static final float PICKER_PANEL_ENTRY_GAP = 8f;

	public static final float HEIGHT_CAP = 80f;
	public static final float TITLE_GAP = 20f;
	public static final float PICKER_PANEL_WIDTH_OFFSET = 15f;
	public static final float PICKER_PANEL_OUTER_GAP = 10f;

	public static void createApprenticePickerPanel(jdp_PanelData panelData) {
		UIReflectionUtils.ConfirmDialogData master = UIReflectionUtils.showConfirmationDialog(
				EMPTY_STRING,
				CONFIRM_TEXT,
				CANCEL_TEXT,
				PICKER_PANEL_WIDTH,
				PICKER_PANEL_HEIGHT,
				new ListenerFactory.DialogDismissedListener() {
					@Override
					public void trigger(Object... args) {
						if (!isConfirmed(args)) return;
					}
				});
		if (master == null) return;
		master.panel.removeComponent(master.confirmButton.getInstance());

		var panel = Global.getSettings().createCustom(master.contentWidth, master.contentHeight, null);
		master.panel.addComponent(panel).inTL(0f, 0f);

		recreatePanel(panelData, panel);

		ButtonAPI cancelButton = master.cancelButton.getInstance();
		cancelButton.setShortcut(Keyboard.KEY_G, false);
	}

	public static void confirmApprenticeAssignment(PersonAPI apprentice, jdp_PanelData panelData, CustomPanelAPI pickerPanel) {
		if (willWipe(getSpecialistData().getSpecialist(apprentice.getId()), getOpenSpecialist().getId())) {
			UIReflectionUtils.ConfirmDialogData master = UIReflectionUtils.showConfirmationDialog(
					"Assigning this apprentice to a new Proficiency will clear all skills and experience. \n\nAre you sure?",
					CONFIRM_TEXT,
					CANCEL_TEXT,
					CONFIRM_DIALOG_WIDTH,
					CONFIRM_DIALOG_HEIGHT,
					new ListenerFactory.DialogDismissedListener() {
						@Override
						public void trigger(Object... args) {
							if (!isConfirmed(args)) return;
						}
					});
			if (master == null) return;

			var panel = Global.getSettings().createCustom(master.contentWidth, master.contentHeight, null);
			master.panel.addComponent(panel).inTL(master.borderThickness, 120f);

			var element = panel.createUIElement(master.contentWidth, master.contentHeight, false);
			panel.addUIElement(element);

			createUnassignedInfo(getSpecialistData().getSpecialist(apprentice.getId()), element, master.contentWidth, panelData);

			ButtonAPI cancelButton = master.cancelButton.getInstance();
			ButtonAPI confirmButton = master.confirmButton.getInstance();

			cancelButton.setShortcut(Keyboard.KEY_G, false);
			UIReflectionUtils.setButtonHook(confirmButton, () -> assignApprentice(apprentice, panelData, pickerPanel), () -> {});
		} else {
			assignApprentice(apprentice, panelData, pickerPanel);
		}
	}

	public static void createSiCPanel() {
		UIReflectionUtils.ConfirmDialogData master = UIReflectionUtils.showConfirmationDialog(
				EMPTY_STRING,
				CONFIRM_TEXT,
				CANCEL_TEXT,
				1106,
				756,
				255,
				new ListenerFactory.DialogDismissedListener() {
					@Override
					public void trigger(Object... args) {
						if (!isConfirmed(args)) return;
					}
				});
		if (master == null) return;

		master.panel.removeComponent(master.confirmButton.getInstance());

		var panel = Global.getSettings().createCustom(master.contentWidth, master.contentHeight, null);
		master.panel.addComponent(panel).inTL(0f, 0f);

		initialise(new jdp_PanelData(panel, master.contentWidth, master.contentHeight));

		ButtonAPI cancelButton = master.cancelButton.getInstance();
		cancelButton.setShortcut(Keyboard.KEY_G, false);
	}

	public static Boolean willWipe(jdp_Specialist apprenticeSpecialist, String newMentor) {
		if(isApprentice(apprenticeSpecialist)) return !apprenticeSpecialist.getMentor().equals(newMentor);
		if(isUnassigned(apprenticeSpecialist)) return !apprenticeSpecialist.getPreviousMentor().equals(newMentor);
		return false;
	}

	public static void createUnassignedInfo(jdp_Specialist specialist, TooltipMakerAPI tooltip, Float width, jdp_PanelData panelData) {
		var mentor = getSpecialistData().getSpecialist(specialist.getPreviousMentor());
		var inactive = "Inactive ";

		if(isApprentice(specialist)) {
			mentor = getSpecialistData().getSpecialist(specialist.getMentor());
			inactive = "Current ";
		}

		var spec = specialist.getProficiencySpec();
		var name = spec.getName() + " Proficiency";

		var xpBar = new jdp_ExperienceBarElement(spec, specialist, tooltip, 265f, 24f);
		tooltip.addTooltipTo(new ExperienceBarTooltipCreator(specialist), xpBar.getElementPanel(), TooltipMakerAPI.TooltipLocation.BELOW);
		xpBar.getElementPanel().getPosition().inTL((width - 265) / 2, 3f);

		if(panelData != null) {
			var newMentor = getSpecialistData().getSpecialist(getOpenSpecialist().getId());

			tooltip.addSpacer(10f);
			tooltip.addPara("This apprentice has acquired skills and experience in the " + name + " under " + mentor.person().getName().getFullName() + ".", 3f, Misc.getHighlightColor(), spec.getColor(), name).getPosition().setXAlignOffset( - (width - 265) / 2);
			tooltip.addSpacer(10f);
			var newName = newMentor.getProficiencySpec().getName() + " Proficiency";

			tooltip.addPara("Assigning " + specialist.person().getName().getFullName() + " to the " + newName + " will reset all levels and experience.", 3f, new Color[]{newMentor.getProficiencySpec().getColor(), Misc.getNegativeHighlightColor()}, newName, "reset");
		} else {
			if (isUnassigned(specialist)) {
				tooltip.addSpacer(10f);
				tooltip.addPara("This apprentice has been unassigned. While they retain the theoretical skills and experience gained within the " + name + " under " + mentor.person().getName().getFullName() + ", without their oversight any practical skills are currently rendered inactive.", 3f, Misc.getHighlightColor(), spec.getColor(), name).getPosition().setXAlignOffset(-(width - 265) / 2);
			} else if (isApprentice(specialist)) {
				mentor = getSpecialistData().getSpecialist(specialist.getMentor());

				tooltip.addSpacer(10f);
				tooltip.addPara("This apprentice has acquired skills and experience in the " + name + " under " + mentor.person().getName().getFullName() + ".", 3f, Misc.getHighlightColor(), spec.getColor(), name).getPosition().setXAlignOffset(-(width - 265) / 2);
			}
			tooltip.addSpacer(10f);
			tooltip.addPara("Assigning " + specialist.person().getName().getFullName() + " to the " + name + " will reactivate any skills and restore their experience. Reassigning them to a different Proficiency will instead reset all levels and experience.", 3f, new Color[]{spec.getColor(), Misc.getNegativeHighlightColor()}, name, "reset");
		}

		var skills = specialist.activeSkills();
		if (!skills.isEmpty()) {
			Person fake = (Person) Global.getFactory().createPerson();
			fake.setFaction(Factions.SLEEPER);

			var admin = 0;
			var officer = 0;
			for (HashMap.Entry<String, Float> entry : skills.entrySet()) {
				String inactiveSkill = entry.getKey();
				var skillSpec = Global.getSettings().getSkillSpec(inactiveSkill);

				if(skillSpec.isAdminSkill()) admin += 1;
				if(skillSpec.isCombatOfficerSkill()) officer += 1;

				fake.getStats().setSkillLevel(inactiveSkill, entry.getValue());
			}
			if(admin > 0) {
				var plural = "s";
				if(admin == 1) plural = "";

				tooltip.addSectionHeading(inactive + "Administrative Skill" + plural + " (" + admin +")", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, 10f);
				tooltip.addSkillPanel(fake, true, 3f);
				tooltip.addSpacer(3f);
			}
			if(officer > 0) {
				var plural = "s";
				if(officer == 1) plural = "";

				tooltip.addSectionHeading(inactive + "Combat-related Skill" + plural + " (" + officer +")", Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, 10f);
				tooltip.addSkillPanel(fake, false, 3f);
				tooltip.addSpacer(3f);
			}
		}
	}

	public static void assignApprentice(PersonAPI apprentice, jdp_PanelData panelData, CustomPanelAPI pickerPanel) {
		var data = getSpecialistData();
		var mentor = getOpenSpecialist();

		if (willWipe(data.getSpecialist(apprentice.getId()), mentor.getId())) data.getSpecialist(apprentice.getId()).resetExperience();

		jdp_Specialist mentorSpecialist = data.getSpecialist(mentor.getId());
		jdp_Specialist existingApprentice = data.getSpecialist(mentorSpecialist.getMentee());
		existingApprentice.setMentor(JDP_DEFAULT_SPECIALIST);
		mentorSpecialist.setMentee(apprentice.getId());

		jdp_Specialist apprenticeSpecialist = data.getSpecialist(apprentice.getId());
		jdp_Specialist existingMentor = data.getSpecialist(apprenticeSpecialist.getMentor());
		existingMentor.setMentee(JDP_DEFAULT_SPECIALIST);
		apprenticeSpecialist.setMentor(mentor.getId());

		if (!apprenticeSpecialist.equals(existingApprentice)) {
			apprenticeSpecialist.enableSkills();
			existingApprentice.disableSkills();
		}

		if (pickerPanel != null) recreatePanel(panelData, pickerPanel);
		recreateSections(panelData);
	}
}



