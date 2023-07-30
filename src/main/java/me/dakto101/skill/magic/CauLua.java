package me.dakto101.skill.magic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.dakto101.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.dakto101.api.Cooldown;
import me.dakto101.api.Cooldown.CooldownType;
import me.dakto101.api.Skill;
import me.dakto101.api.SkillEnum;
import me.dakto101.api.SkillType;
import me.dakto101.util.HCraftDamageSource;

@SuppressWarnings("deprecation")
public class CauLua extends Skill {

    private static final String FIREBALL_NAME = "§6§lCầu lửa";

    public CauLua() {
        super(SkillEnum.CAU_LUA, Arrays.asList(
                        "§7§nKích hoạt:§r§7 Bắn ra quả cầu lửa gây §6(5 + 0.55 X Cấp)§7 sát thương vật lý và gây thiêu ",
                        "§7đốt kẻ địch bị trúng. Kẻ địch xung quanh sẽ chịu §9(4 + 0.55 X Cấp)§7 sát thương nổ.",
                        "§7Cần §f1§7 cầu lửa (fire charge) để bắn.  (Shift + Click phải)",
                        "",
                        "§7§nBị động:",
                        "§7- Giảm §f16%§7 sát thương nhận vào từ lửa đốt gây ra.",
                        "§7- Gây thêm §92.6 + 0.18 X Cấp§7 sát thương phép khi dùng sách làm vũ khí.",
                        "§7- Gây thêm §62.7 + 0.27 X Cấp§7 sát thương vật lý khi dùng sách làm vũ khí."
                ),
                10d, SkillType.MAGIC);
        setFoodRequire(10);
        setActiveCooldown(2);
        setPassiveCooldown(0.1);
        setIcon(Material.FIRE_CHARGE);
    }

    @Override
    public List<String> getDescription(int level, final LivingEntity user) {
        List<String> description = new ArrayList<String>(this.getDescription());
        description.replaceAll(s -> s.replace("(5 + 0.55 X Cấp)", "" + (float) (5 + 0.55 * level)));
        description.replaceAll(s -> s.replace("(4 + 0.55 X Cấp)", "" + (float) (4 + 0.55 * level)));
        description.replaceAll(s -> s.replace("2.6 + 0.18 X Cấp", "" + (float) (2.6 + 0.18 * level)));
        description.replaceAll(s -> s.replace("2.7 + 0.27 X Cấp", "" + (float) (2.7 + 0.27 * level)));
        return description;
    }

    //Active
    @Override
    public void applyInteractBlock(final Player user, final int level, final PlayerInteractEvent e) {
        if (!user.isSneaking()) return;
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) ||
                e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            cast(user, user.getEyeLocation().getDirection());
        }
    }

    //Active
    @Override
    public void applyOnHit(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageByEntityEvent e) {
        if (!(target instanceof LivingEntity)) return;
        // Normal attack
        if (this.getMaterialList().contains(user.getEquipment().getItemInMainHand().getType())) {
            if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.PASSIVE_SKILL)) return;
            if (e.getCause().equals(DamageCause.ENTITY_ATTACK)) {
                // Passive 2
                Cooldown.setCooldown(user.getUniqueId(), getPassiveCooldown(), CooldownType.PASSIVE_SKILL);

                float magicDamage = (float) (2.6 + 0.18 * level);

                HCraftDamageSource.damageIndirectMagic(user, target, magicDamage);
                target.getWorld().spawnParticle(Particle.SPELL_WITCH, target.getEyeLocation(), (int) (10 + magicDamage * 2));
                // Passive 3
                float meleeDamage = (float) (2.7 + 0.27 * level);
                // Damage normal chứ không dùng setDamage() để khỏi bị stack với damage phép.
                HCraftDamageSource.damageNormalAttack(user, target, meleeDamage);
            }
        }
        // Skill
        String name = e.getDamager().getCustomName();
        if (name != null && name.equals(CauLua.FIREBALL_NAME)) {
            // Param
            double phyDamage = 5 + 0.55 * level;
            float blastDamage = (float) (4 + 0.55 * level);
            double radius = 2;
            // Code
            Cooldown.setCooldown(user.getUniqueId(), getPassiveCooldown(), CooldownType.PASSIVE_SKILL);

            e.setDamage(phyDamage);
            target.setFireTicks(80);
            target.getWorld().getNearbyEntities(target.getLocation(), radius, radius, radius,
                            entity -> (entity instanceof LivingEntity) &&
                                    (Utils.canAttack(user, (LivingEntity) entity) &&
                                            (entity.getLocation().distance(target.getLocation()) < radius)))
                    .stream().limit(15).forEach(entity -> {
                        HCraftDamageSource.damageExplosion(user, (LivingEntity) entity, blastDamage);
                        entity.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, entity.getLocation(), 1);
                    });

        }

    }

    //Passive
    @Override
    public void applyDefense(final LivingEntity user, final LivingEntity target, final int level, final EntityDamageEvent e) {
        List<DamageCause> fire = Arrays.asList(
                DamageCause.FIRE,
                DamageCause.FIRE_TICK,
                DamageCause.HOT_FLOOR,
                DamageCause.LAVA);
        if (fire.contains(e.getCause())) {
            double reduction = e.getDamage(DamageModifier.BASE) * 0.16;
            e.setDamage(e.getDamage() - reduction);
        }
    }

    private void cast(final Player user, final Vector v) {
        //Condition
        if (Cooldown.onCooldown(user.getUniqueId(), CooldownType.ACTIVE_SKILL)) {
            return;
        }
        user.getWorld().playSound(user.getLocation(), Sound.UI_LOOM_SELECT_PATTERN, 1, 2);
        if (!user.getInventory().contains(Material.FIRE_CHARGE, 1)) {
            return;
        }
        if (user.getFoodLevel() < getFoodRequire()) {
            user.sendMessage("§cKhông đủ điểm thức ăn!");
            return;
        }
        user.getInventory().removeItem(new ItemStack(Material.FIRE_CHARGE, 1));
        user.setFoodLevel(user.getFoodLevel() - getFoodRequire());
        user.swingMainHand();
        //
        Location loc = user.getLocation();

        //Code

        loc.setPitch((float) (loc.getPitch() - 5 + Math.random() * 10));
        loc.setYaw((float) (loc.getYaw() - 5 + Math.random() * 10));

        user.teleport(loc);

        Fireball ball = (Fireball) user.getWorld().spawnEntity(user.getEyeLocation(), EntityType.FIREBALL);
        ball.setCustomNameVisible(true);
        ball.setCustomName(CauLua.FIREBALL_NAME);
        ball.setVelocity(v.multiply(2));
        ball.setShooter(user);
        ball.setFireTicks(0);

        user.setVelocity(v.multiply(-0.25));
        user.getWorld().playSound(user.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 3, 2);
        //Cooldown
        Cooldown.setCooldown(user.getUniqueId(), getActiveCooldown(), CooldownType.ACTIVE_SKILL);
    }

}
