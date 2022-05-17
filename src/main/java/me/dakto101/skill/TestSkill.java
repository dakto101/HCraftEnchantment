package me.dakto101.skill;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.dakto101.api.Skill;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;

@SuppressWarnings("deprecation")
public class TestSkill extends Skill {

	public TestSkill() {
		super(SkillEnum.TEST_SKILL, Arrays.asList("Enchant dùng để test."), -1, SkillType.OTHER);
		setFoodRequire(0);
		setCooldown(0);
	}

	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		e.setDamage(1);
		user.sendMessage("applyOnHit. Damage = " + e.getDamage());
		user.sendMessage("original damage = " + e.getOriginalDamage(DamageModifier.BASE));
	}
	
	@Override
	public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent event) {
		user.sendMessage("applyDefense...");
		if (event.getCause().equals(DamageCause.FALL)) {
			user.sendMessage("Blocked damage fall. Level = " + level);
			event.setDamage(0);
		}
	}
	
	@Override
	public void applyBreak(final LivingEntity user, final Block block, final int level, final BlockEvent e) { 
		if (e instanceof BlockBreakEvent) {
			user.sendMessage("applyBreak... Create explosion, level = " + level);
			block.getWorld().createExplosion(block.getLocation(), 0f);
		}
	}
	
	@Override
	public void applyInteractBlock(final Player user, final int level, final PlayerInteractEvent e) { 
		user.sendMessage("applyInteractBlock...");
		if (e.getClickedBlock().getBlockData().getMaterial().equals(Material.DIAMOND_BLOCK)) {
			user.sendMessage("Ok. Level = " + level);
			user.playSound(user.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, (float) (Math.random()*2));
		}
	}
	
	@Override
    public void applyInteractEntity(final Player user, final int level, final PlayerInteractEntityEvent e) { 
		user.sendMessage("applyInteractEntity...");
		if (e.getRightClicked() instanceof Pig) {
			user.sendMessage("Add passenger... Level = " + level);
			if (!e.getRightClicked().isInsideVehicle()) {
				user.addPassenger(e.getRightClicked());
			}
			user.sendMessage("isInsideVehicle? " + e.getRightClicked().isInsideVehicle());
		}
	}
	
	@Override
    public void applyProjectile(final LivingEntity user, final int level, final ProjectileLaunchEvent e) {
		user.sendMessage("applyProjectile...");
	}
	
}
