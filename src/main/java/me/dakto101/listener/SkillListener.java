package me.dakto101.listener;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.dakto101.api.PlayerSkill;
import me.dakto101.api.Skill;
import me.dakto101.event.HCraftSkillXpAddEvent;

public class SkillListener implements Listener {
	//Note: Code type skill kiếm dùng cho kiếm, unarmed dùng cho tay không.....
	private static int count = 1;
	
    /**
     * Applies effects when the player left or right clicks (For other 
     * kinds of enchantments like spells)
     */
    @EventHandler(priority = EventPriority.HIGH)
    public static void onInteractBlock(final PlayerInteractEvent e) {
    	Player p = e.getPlayer();
    	
    	PlayerSkill ps = getPlayerSkill(p, true);
    	if (ps != null) {
    		Skill skill = ps.getPlayerChosenSkill();
    		if (skill != null) skill.applyInteractBlock(p, ps.getSkillLevel(skill), e);
    	}
    }
    
    /**
     * Applies the enchantment affect when attacking someone
     */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public static void onHit(final EntityDamageByEntityEvent e) {
		//me.dakto101.util: Check if entity can hit other entity or not.
		if (e.getCause().equals(DamageCause.CUSTOM)) return;
		if (!(getDamager(e) instanceof Player)) return;
		if (e.getEntity() == null) return;
		if (!(e.getEntity() instanceof LivingEntity)) return;
		Player p = (Player) getDamager(e);
		if (p != null) {
	    	PlayerSkill ps = getPlayerSkill(p, false);
	    	if (ps != null) {
	    		Skill skill = ps.getPlayerChosenSkill();
	    		if (skill != null) skill.applyOnHit(p, (LivingEntity) e.getEntity(), ps.getSkillLevel(skill), e);
	    	}
		}
	}
	
    /**
     * Applies the enchantment affect when user's vehicle taking damage
     */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public static void onVehicleHit(final EntityDamageEvent e) { 
		if (e.getCause().equals(DamageCause.CUSTOM)) return;
		if (!(e.getEntity() instanceof LivingEntity)) return;
		
		if (e instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
			LivingEntity vehicle = (LivingEntity) ee.getEntity();
			List<Entity> passengers = vehicle.getPassengers();
			if ((passengers.size() != 1)) return;
			if (!(passengers.get(0) instanceof Player)) return;
			Player p = (Player) passengers.get(0);
			
	    	PlayerSkill ps = getPlayerSkill(p, false);
	    	if (ps != null) {
	    		Skill skill = ps.getPlayerChosenSkill();
	    		if (skill != null) skill.applyOnVehicleHit(p, vehicle, getDamager(ee), ps.getSkillLevel(skill), ee);
	    	}
			return;
		} else {
			LivingEntity vehicle = (LivingEntity) e.getEntity();
			List<Entity> passengers = vehicle.getPassengers();
			if ((passengers.size() != 1)) return;
			if (!(passengers.get(0) instanceof Player)) return;
			Player p = (Player) passengers.get(0);
			
	    	PlayerSkill ps = getPlayerSkill(p, false);
	    	if (ps != null) {
	    		Skill skill = ps.getPlayerChosenSkill();
	    		if (skill != null) skill.applyOnVehicleHit(p, vehicle, null, ps.getSkillLevel(skill), e);
	    	}
		}

    }
	
    /**
     * Applies the enchantment defensively (when taking damage)
     */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public static void onDefense(final EntityDamageEvent e) { 
		if (e.getCause().equals(DamageCause.CUSTOM)) return;
		if (!(e.getEntity() instanceof Player)) return;
		
		if (e instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent ee = (EntityDamageByEntityEvent) e;
			Player p = (Player) ee.getEntity();
			
	    	PlayerSkill ps = getPlayerSkill(p, false);
	    	if (ps != null) {
	    		Skill skill = ps.getPlayerChosenSkill();
	    		if (skill != null) skill.applyDefense(p, getDamager(ee), ps.getSkillLevel(skill), ee);
	    	}
			return;
		} else {
			Player p = (Player) e.getEntity();
			
	    	PlayerSkill ps = getPlayerSkill(p, false);
	    	if (ps != null) {
	    		Skill skill = ps.getPlayerChosenSkill();
	    		if (skill != null) skill.applyDefense(p, null, ps.getSkillLevel(skill), e);
	    	}
		}

    }

    /**
     * Applies effects while breaking blocks (for tool effects)
     */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public static void onBreak(final BlockBreakEvent e) {
    	if (e.isCancelled()) return;
    	Player p = e.getPlayer();

    	PlayerSkill ps = getPlayerSkill(p, false);
    	if (ps != null) {
    		Skill skill = ps.getPlayerChosenSkill();
    		if (skill != null) skill.applyBreak(p, e.getBlock(), ps.getSkillLevel(skill), e);
    	}
    }
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public static void onBreak(final BlockDamageEvent e) {
    	if (e.isCancelled()) return;
    	Player p = e.getPlayer();
    	
    	PlayerSkill ps = getPlayerSkill(p, false);
    	if (ps != null) {
    		Skill skill = ps.getPlayerChosenSkill();
    		if (skill != null) skill.applyBreak(p, e.getBlock(), ps.getSkillLevel(skill), e);
    	}
    }

    /**
     * Applies effects when the player interacts with an entity
     */
    @EventHandler(priority = EventPriority.HIGH)
    public static void onInteractEntity(final PlayerInteractEntityEvent e) {
    	if (cast()) return;
    	Player p = e.getPlayer();
    	
    	PlayerSkill ps = getPlayerSkill(p, true);
    	if (ps != null) {
    		Skill skill = ps.getPlayerChosenSkill();
    		if (skill != null) skill.applyInteractEntity(p, ps.getSkillLevel(skill), e);
    	}
    }

    /**
     * Applies effects when firing a projectile
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public static void onShoot(final ProjectileLaunchEvent e) {
    	if (e.getEntity() == null) return;
    	if (e.getEntity().getShooter() == null) return;
		if (e.getEntity().getShooter() instanceof Player) {
			Player p = (Player) e.getEntity().getShooter();
			
			PlayerSkill ps = getPlayerSkill(p, false);
	    	if (ps != null) {
	    		Skill skill = ps.getPlayerChosenSkill();
	    		if (skill != null) skill.applyProjectile(p, ps.getSkillLevel(skill), e);
	    	}
		}
    	
    }
    
    /**
     * Applies effects when entity firing a projectile
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public static void onShoot(final EntityShootBowEvent e) {
    	
    	if ((e.getEntity() == null) || !(e.getEntity() instanceof Player)) return;
    	if (e.getProjectile() == null) return;
    	if (e.getBow() == null) return;
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			
			PlayerSkill ps = getPlayerSkill(p, false);
	    	if (ps != null) {
	    		Skill skill = ps.getPlayerChosenSkill();
	    		if (skill != null) skill.applyProjectile(p, ps.getSkillLevel(skill), e);
	    	}
		}
    	
    }
    
    /**
     * Applies effects when entity die
     */
    @EventHandler(priority = EventPriority.HIGH)
    public static void onRegainHealth(final EntityRegainHealthEvent e) {
    	
    	if ((e.getEntity() == null) || !(e.getEntity() instanceof Player)) return;
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			
	    	PlayerSkill ps = getPlayerSkill(p, false);
	    	if (ps != null) {
	    		Skill skill = ps.getPlayerChosenSkill();
	    		if (skill != null) skill.applyRegainHealth(p, ps.getSkillLevel(skill), e);
	    	}
		return;
		}
    	
    }
    
    /**
     * Applies effects when entity die
     */
    @EventHandler(priority = EventPriority.HIGH)
    public static void onDeath(final EntityDeathEvent e) {
    	
    	if ((e.getEntity() == null) || !(e.getEntity() instanceof Player)) return;
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			
	    	PlayerSkill ps = getPlayerSkill(p, false);
	    	if (ps != null) {
	    		Skill skill = ps.getPlayerChosenSkill();
	    		if (skill != null) skill.applyDeath(p, ps.getSkillLevel(skill), e);
	    	}
		return;
		}
    	
    }
    
    @EventHandler
    public static void onXPAdd(final HCraftSkillXpAddEvent e) {
    	e.getPlayer().sendMessage("§aBạn nhận được §e" + e.getXpAdd() + "§a điểm kinh nghiệm cho kỹ năng §c" + e.getSkill().getName() + "§a.");
    }
    
    /** To get the true damager.
     * 
     * @param event
     * @return
     */
    private static LivingEntity getDamager(final EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof LivingEntity) {
            return (LivingEntity) event.getDamager();
        }
        else if (event.getDamager() instanceof Projectile) {
            final Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof LivingEntity) {
                return (LivingEntity) projectile.getShooter();
            }
        }
        return null;
    }
    
    /** Return false if player dont have skill, skill level = 0 or material.
     * 
     * @param p player
     * @return player skill
     */
    private static PlayerSkill getPlayerSkill(final Player p, final boolean checkMaterial) {
    	PlayerSkill ps = new PlayerSkill(p);
    	ps.load(p);
    	Skill skill = ps.getPlayerChosenSkill();
    	if (!ps.getPlayerSkills().containsValue(skill)) return null;
    	if (skill == null) return null;
    	if (ps.getSkillLevel(skill) == 0) return null;
    	if (checkMaterial && !skill.getMaterialList().contains(p.getInventory().getItemInMainHand().getType())) return null;
    	return ps;
    }
    
    /** To check if castable.
     * 
     * @return true if castable
     */
    private static boolean cast() {
    	count++;
    	if (count > 10000) count = 0;
    	return count % 2 == 0;
    }
    
}
