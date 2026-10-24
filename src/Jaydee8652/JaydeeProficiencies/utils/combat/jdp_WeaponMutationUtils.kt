package Jaydee8652.JaydeeProficiencies.utils.combat;

import Jaydee8652.JaydeeProficiencies.utils.refl.ReflectionUtilis
import Jaydee8652.JaydeeProficiencies.utils.refl.ReflectionUtils
import com.fs.starfarer.api.combat.WeaponAPI
import com.fs.starfarer.api.loading.ProjectileSpecAPI
import com.fs.starfarer.api.loading.WeaponSpecAPI
import Jaydee8652.JaydeeProficiencies.utils.refl.ui.ClassRefs;
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.MissileAPI
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.loading.MissileSpecAPI
import com.fs.starfarer.campaign.ui.fleet.FleetMemberView
import com.fs.starfarer.combat.entities.Missile
import org.lwjgl.util.vector.Vector2f

//Courtesy of Niko!
object jdp_WeaponMutationUtils {

	// How to modify certain aspects of weapons on a case-by-case basis:
	// Handleable through this object: Damage, velocity, virtually everything tied to the projectile
	// ROF: Create an EFS to call setRefireDelay() on the wpn every frame
	// Turn rate: setTurnRateOverride() on the wpn
	// Range: Use a WeaponBaseRangeModifier
	// Flux: UNKNOWN
	// Weapon health: UNKNOWN

	// Intended use:
	// val spec = wpn.getClonedSpec()
	// [CODE HERE]
	// wpn.setProjectileSpec(spec)

	@JvmStatic
	fun spawnMissile(ship: ShipAPI, weapon: WeaponAPI, spec: MissileSpecAPI, point: Vector2f, facing: Float, velocity: Vector2f ): Missile {
		var projectileSpawnerClass = ReflectionUtils.instantiate(ClassRefs.projectileSpawnerClass)
		return ReflectionUtilis.invokeMethodDirectly(ClassRefs.spawnMissileMethod, projectileSpawnerClass, ship, weapon, spec, point, facing, velocity) as Missile
	}

	@JvmStatic
	fun getClonedSpec(weapon: WeaponAPI): WeaponSpecAPI {
		weapon.ensureClonedSpec()
		return weapon.spec
	}

	@JvmStatic
	fun getClonedProjectileSpec(weapon: WeaponAPI): ProjectileSpecAPI? {
		val spec = getClonedSpec(weapon)
		val projectileSpec = (spec.projectileSpec as? ProjectileSpecAPI) ?: return null
		val clonedProjSpec = ReflectionUtilis.invokeMethod("clone", projectileSpec) as ProjectileSpecAPI
		return clonedProjSpec
	}

	@JvmStatic
	fun setProjectileSpec(weapon: WeaponAPI, newSpec: ProjectileSpecAPI){
		val spec = getClonedSpec(weapon)

		ReflectionUtilis.getMethodAndInvokeDirectly("setProjectileSpec", spec, 1, newSpec)
	}
}