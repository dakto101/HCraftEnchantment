package me.dakto101.skill.archery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import me.dakto101.HCraftEnchantment;
import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.CustomEnchantmentAPI;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;
import me.dakto101.api.Toggle;
import me.dakto101.api.Toggle.ToggleType;

public class TanXaTien extends Skill {

	private static final int CONE_DEGREES = 45;

	public TanXaTien() {
		super(SkillEnum.TAN_XA_TIEN, Arrays.asList(
				"§7§nKích hoạt:§r§7 Tiêu thụ mũi tên để bắn ra §f(3 + Cấp)§7 mũi tên hình nón. (Shift + Click trái)",
				"",
				"§7§nBị động:",
				"§7- Gây thêm §6(1 + 0.2 X Cấp + Bonus)§7 sát thương vật lý nếu bắn trúng kẻ địch cách hơn 20 ô.",
				"§7Bonus = §66%§7 máu hiện tại của mục tiêu (bonus tối đa = 6)."
				), 10d, SkillType.ARCHERY);
		setFoodRequire(0);
		setCooldown(0);
		setIcon(Material.STRING);
		
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("(3 + Cấp)", "" + (3 + level)));
    	description.replaceAll(s -> s.replace("1 + 0.2 X Cấp", "" + (1 + 0.2 * level)));
    	return description;
    }
	
	//Active
	@Override
	public void applyInteractBlock(final Player user, final int level, final PlayerInteractEvent e) { 
		if (e.getAction().equals(Action.LEFT_CLICK_AIR) || 
				e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			if (!user.isSneaking()) return;
			boolean toggle = Toggle.getToggle(user.getUniqueId(), ToggleType.ACTIVE_SKILL);
			Toggle.setToggle(user.getUniqueId(), !toggle, ToggleType.ACTIVE_SKILL);
			Toggle.sendMessage(user, ToggleType.ACTIVE_SKILL);
			user.playSound(user.getLocation(), Sound.UI_BUTTON_CLICK, 1, toggle ? 0.5f : 0.7f);
		}
	}
	
	//Active
	@Override
    public void applyProjectile(final LivingEntity u, final int level, final EntityShootBowEvent e) {
		//Condition
		Player user = (Player) u;
		if (!(user instanceof Player)) return;
		if (!Toggle.getToggle(user.getUniqueId(), ToggleType.ACTIVE_SKILL)) return;
		if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.ACTIVE)) {
			return;
		}
		if (user.getFoodLevel() < getFoodRequire()) {
			user.sendMessage("§cKhông đủ điểm thức ăn!"); 
			return;
		}
		//Param
		//Code
		volley(user, level, e);
		//Cooldown and food
		Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.ACTIVE);
		user.setFoodLevel(user.getFoodLevel() - getFoodRequire());
	}
	
	//Active
    private void volley(final LivingEntity user, final int level, final EntityShootBowEvent e) {
        int amount = 3 + 1 * level; // Keep amount of arrows uneven, 2 extra arrows in a volley per level.
        
        
        if (!(e.getProjectile() instanceof Arrow)) return;

        Arrow oldArrow = (Arrow) e.getProjectile();
        int fireTicks = oldArrow.getFireTicks();
        int knockbackStrength = oldArrow.getKnockbackStrength();
        int pierce = oldArrow.getPierceLevel();
        boolean critical = oldArrow.isCritical();
        boolean gravity = oldArrow.hasGravity();

        double angleBetweenArrows = (CONE_DEGREES / (amount - 1)) * Math.PI / 180;
        double pitch = (user.getLocation().getPitch() + 90) * Math.PI / 180;
        double yaw = (user.getLocation().getYaw() + 90 - CONE_DEGREES / 2) * Math.PI / 180;

        // Starting direction values for the cone, each arrow increments it's direction on these values.
        double sZ = Math.cos(pitch);
        List<Arrow> arrowList = new ArrayList<Arrow>();

        for (int i = 0; i < amount; i++) { // spawn all arrows in a cone of 90 degrees (equally distributed).;

        	if (user instanceof Player) {
        		if (((Player) user).getInventory().contains(Material.ARROW, 1)) {
        			((Player) user).getInventory().removeItem(new ItemStack(Material.ARROW, 1));
        		} else break;
        	}
        	
        	double nX = Math.sin(pitch) * Math.cos(yaw + angleBetweenArrows * i);
            double nY = Math.sin(pitch) * Math.sin(yaw + angleBetweenArrows * i);
            Vector newDir = new Vector(nX, sZ, nY);

            Arrow arrow = user.launchProjectile(Arrow.class);
            arrow.setShooter(user);
            arrow.setVelocity(newDir.normalize().multiply(oldArrow.getVelocity().length())); // Need to make sure arrow has same speed as original arrow.
            arrow.setFireTicks(fireTicks); // Set the new arrows on fire if the original one was 
            arrow.setKnockbackStrength(knockbackStrength);
            arrow.setCritical(critical);
            arrow.setGravity(gravity);
    		arrow.setPierceLevel(pierce);
            
            
            user.getWorld().playSound(user.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
    		if ((user instanceof Player) && (CustomEnchantmentAPI.hasCustomEnchantment(e.getBow(), "Lông vũ"))) {
    			if (((Player) user).getInventory().contains(Material.FEATHER, 1)) {
    				((Player) user).getInventory().removeItem(new ItemStack(Material.FEATHER, 1));
    				arrow.setGravity(false);
    			}
    		}
            
            arrow.setPickupStatus(PickupStatus.DISALLOWED);
            arrowList.add(arrow);
            
            removeArrowTask(arrowList);
            
        }
        oldArrow.remove(); // Remove original arrow.
    }
    
    //Active
    private void removeArrowTask(List<Arrow> arrowList) {
		BukkitScheduler s = HCraftEnchantment.plugin.getServer().getScheduler();
		s.scheduleSyncDelayedTask(HCraftEnchantment.plugin, () -> {
			arrowList.forEach(arrow -> arrow.remove());
		}, 200L);
    }

    //Passive
	@Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (!(target instanceof LivingEntity)) return;
		if (e.getCause().equals(DamageCause.PROJECTILE) 
				&& user.getWorld().equals(target.getWorld()) && user.getLocation().distance(target.getLocation()) > 20) {
			//Param
			double bonus = target.getHealth() * 0.06 > 6 ? 6 : target.getHealth() * 0.06;
			double bonusDamage = 1 + 0.2 * level + bonus;
			//Code
			e.setDamage(e.getDamage() + bonusDamage);
			user.getWorld().playSound(user.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
			if (bonus > 5) target.getWorld().playSound(target.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1);
		}
		
	}
}
