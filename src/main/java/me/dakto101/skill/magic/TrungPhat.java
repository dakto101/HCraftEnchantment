package me.dakto101.skill.magic;

import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;
import me.dakto101.util.HCraftDamageSource;
import me.dakto101.util.Utils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrungPhat extends Skill {

	public TrungPhat() {
		super(SkillEnum.TRUNG_PHAT, Arrays.asList(
				"§7§nKích hoạt:§r§7 Triệu hồi một nguồn năng lượng giáng vào mục tiêu, §7gây",
				"§f(20 + Cấp X 2.5 + 10% máu tối đa mục tiêu)§7 sát thương chuẩn cho mục tiêu.",
				"§7Đồng thời hồi máu cho bạn tương đương với §c20%§7 lượng sát thương gây ra.",
				"§7Chỉ dùng được lên quái, mob. (Shift + Click phải)",
				"",
				"§7§nBị động:",
				"§7- Gây thêm §6(5 + Cấp X 0.45)§7 sát thương vật lý lên quái."
				), 10d, SkillType.MAGIC);
		setFoodRequire(10);
		setActiveCooldown(30);
		setPassiveCooldown(0.1);
		setIcon(Material.BLAZE_ROD);
		
	}
	
	@Override
    public List<String> getDescription(int level, final LivingEntity user) {
		List<String> description = new ArrayList<>(this.getDescription());
    	description.replaceAll(s -> s.replace("20 + Cấp X 2.5", "" + (float) (20 + level * 2.5)));
    	description.replaceAll(s -> s.replace("(5 + Cấp X 0.45)", "" + (float) (5 + level * 0.45)));
    	return description;
    }
	
	//Active
	@Override
	public void applyInteractEntity(final Player user, final int level, final PlayerInteractEntityEvent e) {
		if (!user.isSneaking()) return;
		Entity target = e.getRightClicked();
		if (!(target instanceof LivingEntity)) return;
		if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.ACTIVE_SKILL)) {
			Cooldown.sendMessage(user, this.getName(), CooldownType.ACTIVE_SKILL);
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
		user.swingMainHand();

		smite(user, (LivingEntity) target, level);

		Cooldown.setCooldown(user.getUniqueId(), getActiveCooldown(), CooldownType.ACTIVE_SKILL);
	}
	
	//Passive
	public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
		if (!(target instanceof Player)) {
			if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.PASSIVE_SKILL)) return;
			if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
				Cooldown.setCooldown(user.getUniqueId(), getPassiveCooldown(), CooldownType.PASSIVE_SKILL);
				// Passive
				float meleeDamage = (float) (5 + 0.45 * level);
				e.setDamage(e.getDamage() + meleeDamage);
			}
		}
		
	}

	private void smite(LivingEntity user, LivingEntity target, int level) {
		double userMaxHealth = user.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
		double targetMaxHealth = Math.min(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue(), 100);
		double bonusDMG = targetMaxHealth * 0.1;
		double health = user.getHealth();
		double damage = 20 + level * 2.5 + bonusDMG;
		double heal = 0.20 * damage;

		Cooldown.setCooldown(user.getUniqueId(), getPassiveCooldown(), CooldownType.PASSIVE_SKILL);
		user.swingMainHand();
		HCraftDamageSource.damageGeneric(user, target, (float) damage);
		target.getWorld().strikeLightningEffect(target.getLocation());
		user.setHealth(Math.min(health + heal, userMaxHealth));
		user.sendMessage("§6" + this.getName() + "§7 gây §f" + damage + "§7 sát thương"
				+ " chuẩn lên mục tiêu và hồi §c" + heal + "§7 máu cho bạn.");
	}
	
}
