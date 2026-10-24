package Jaydee8652.JaydeeProficiencies.specialists;

import Jaydee8652.JaydeeProficiencies.specialists.data.jdp_SpecialistTypeData;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.coreui.S;

import java.util.HashMap;
import java.util.Map;

public class jdp_SpecialistTags {
	//Apprentice Types
	public static final String SPECIALIST_TYPE_ADMIN = "admin";
	public static final String SPECIALIST_TYPE_OFFICER = "officer";
	public static final String SPECIALIST_TYPE_PLAYER = "player";

	public static HashMap<String, jdp_SpecialistTypeData> SPECIALIST_TYPE_MAP = new HashMap<String, jdp_SpecialistTypeData>();
	static {
		SPECIALIST_TYPE_MAP.put(jdp_SpecialistTags.SPECIALIST_TYPE_ADMIN, new jdp_SpecialistTypeData("an ", "Administrator", "s", Global.getSettings().getSprite("graphics/icons/skills/jdp_iconadmin.png")));
		SPECIALIST_TYPE_MAP.put(jdp_SpecialistTags.SPECIALIST_TYPE_OFFICER, new jdp_SpecialistTypeData("an ", "Officer", "s", Global.getSettings().getSprite("graphics/icons/skills/jdp_iconofficer.png")));
		SPECIALIST_TYPE_MAP.put(jdp_SpecialistTags.SPECIALIST_TYPE_PLAYER, new jdp_SpecialistTypeData("", "You", "", Global.getSettings().getSprite("graphics/icons/skills/jdp_iconplayer.png")));
	}

	//Skill IDs
	public static final String  JDP_MENTORSHIP = "jdp_mentorship";
	public static final String  JDP_UNAPPLIED_MARKER = "jdp_unapplied_marker";

	//Spec Tags
	public static final String SPEC_TAG_AI_PERSON = "ai_person";//Don't add person to the fleet upon hiring, they're a core!
	public static final String SPEC_TAG_NO_XP_GAIN = "no_xp_gain";//Don't give XP to this proficiency
	public static final String SPEC_TAG_PLAYER_NO_XP_AS_ADMIN = "no_player_admin_xp_gain";//Don't give a player apprentice XP as if they were an Admin
	public static final String SPEC_TAG_PLAYER_NO_XP_AS_OFFICER = "no_player_officer_xp_gain";//Don't give a player apprentice XP as if they were an Officer
	public static final String SPEC_TAG_NO_COMMLINK = "no_commlink";//Don't allow people with this Proficiency to be contacted via the intel

	//Skill Tags
	public static final String SKILL_TAG_MARKER = "jdp_marker_skill";//Don't actually give this skill to specialists. Used for marker skills with no direct effects

	//Person Tags
	public static final String PERSON_TAG_NO_XP_GAIN = "jdp_noProficiencyXP";//Don't give proficiency XP to this person

	//Rules Utils
	public static final String SPECIALIST_COMMLINK_TRIGGER = "SpecialistOpenCommlink";//Trigger of specialist contact via intel
	public static final String SPECIALIST_COMMLINK_ID = "$specialist_commlink_id";//Memory value of the id of the specialist you contacted via intel. Readable as $player.specialist_commlink_id



	//Utils - Internal Use
	public static final String JDP_SPECIALIST_TAB_OPEN = "$jdp_specialistTabOpen";
	public static final String DONT_SHOW_EXPO_AGAIN = "jdp_hasSeenSpecialistTab";
	public static final String JDP_SPECIALIST_DATA = "$jdp_specialist_data";
	public static final String JDP_NEW_SPECIALIST = "$jdp_newSpecialist";
	public static final String JDP_DEFAULT_SPECIALIST = "jdp_default_specialist";
	public static final String JDP_DUMMY_PROFICIENCY = "jdp_dummy";
	public static final String JDP_ROOT_SECTION = "root";
}


