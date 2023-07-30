package me.dakto101.skill;

import me.dakto101.api.Skill;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;
import me.dakto101.util.HCraftDamageSource;
import org.bukkit.Material;
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

import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("deprecation")
public class TestSkill extends Skill {

	public TestSkill() {
		super(SkillEnum.TEST_SKILL, Arrays.asList("Skill dùng để test."), -1, SkillType.OTHER);
		setFoodRequire(0);
		setActiveCooldown(0);
		setMaterialList(Arrays.asList(Material.STICK));
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
	}
	
	@Override
    public void applyInteractEntity(final Player user, final int level, final PlayerInteractEntityEvent e) { 
		if (e.getRightClicked() instanceof LivingEntity) {
			user.sendMessage("applyInteractEntity...");
			HCraftDamageSource.damageExplosion(user, (LivingEntity) e.getRightClicked(), 10);
			user.sendMessage("§6damageExplosion test...");
		}

	}
	
	@Override
    public void applyProjectile(final LivingEntity user, final int level, final ProjectileLaunchEvent e) {
		user.sendMessage("applyProjectile...");
	}
	
}
