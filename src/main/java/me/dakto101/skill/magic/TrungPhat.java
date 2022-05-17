package me.dakto101.skill.magic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;
import me.dakto101.util.DamageSourceEnum;
import me.dakto101.util.HCraftDamageSource;
import me.dakto101.util.Utils;

public class TrungPhat extends Skill {

	public TrungPhat() {
		super(SkillEnum.TRUNG_PHAT, Arrays.asList(
				"§7§nKích hoạt:§r§7 Triệu hồi một nguồn năng lượng giáng vào mục tiêu, ",
				"§7gây §f(20 + Cấp X 5)§7 sát thương chuẩn cho mục tiêu. Đồng thời ",
				"§7hồi máu cho bạn tương đương với §c20%§7 lượng sát thương gây ra.",
				"§7Không thể dùng cho người chơi. (Shift + Click phải)",
				"",
				"§7§nBị động:",
				"§7- Gây thêm §6(3 + Cấp X 0.5)§7 sát thương vật lý lên quái."
				), 10d, SkillType.MAGIC);
		setFoodRequire(10);
		setCooldown(50);
		setIcon(Material.BLAZE_ROD);
		
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<String>(this.getDescription());
    	description.replaceAll(s -> s.replace("(20 + Cấp X 5)", "" + (20 + level * 5)));
    	description.replaceAll(s -> s.replace("(3 + Cấp X 0.5)", "" + (3 + level * 0.5)));
    	return description;
    }
	
	//Active
	@Override
	public void applyInteractEntity(final Player user, final int level, final PlayerInteractEntityEvent e) {
		if (!user.isSneaking()) return;
		Entity target = e.getRightClicked();
		if (!(target instanceof LivingEntity)) return;
		if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.ACTIVE)) {
			Cooldown.sendMessage(user, this.getName(), CooldownType.ACTIVE);
			return;
		}
		if (target instanceof Player) {
			user.sendMessage("§cKhông thể kích hoạt §e" + this.getName() + "§c cho người chơi.");
			return;
		}
		if (!Utils.canAttack(user, (LivingEntity) target)) return;
		if (user.getFoodLevel() < getFoodRequire()) {
			user.sendMessage("§cKhông đủ điểm thức ăn!"); 
			return;
		} else {
			user.setFoodLevel(user.getFoodLevel() - getFoodRequire());
		}
		
		double damage = 20 + level * 5;
		double heal = 0.20 * damage;
		double maxHealth = user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		double health = user.getHealth();
		
		HCraftDamageSource.damage(user, (LivingEntity) target, DamageSourceEnum.GENERIC, (float) damage);
		target.getWorld().strikeLightningEffect(target.getLocation());
		user.setHealth((health + heal > maxHealth) ? maxHealth : health + heal);
		user.sendMessage("§6" + this.getName() + "§7 gây §f" + damage + "§7 sát thương"
				+ " chuẩn lên mục tiêu và hồi §c" + heal + "§7 máu cho bạn.");
		
		Cooldown.setCooldown(user.getUniqueId(), getCooldown(), CooldownType.ACTIVE);
	}
	
	//Passive
	public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (!(target instanceof Player)) {
			double bonusDamage = 3 + level * 0.5;
			e.setDamage(e.getDamage() + bonusDamage);
		}
		
	}
	
}
