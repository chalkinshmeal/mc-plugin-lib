package chalkinshmeal.mc_plugin_lib.entities;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

public class PlayerUtils {
    public static void resetPlayerState(Player player, boolean resetToDefaultMaxHealth) {
        // Health & survival
        if (resetToDefaultMaxHealth) LivingEntityUtils.setMaxHealth(player, 20f);
        LivingEntityUtils.healEntity(player);
        player.setFoodLevel(20);
        player.setSaturation(20f);
        player.setExhaustion(0f);
        player.setFireTicks(0);
        player.setFreezeTicks(0);
        player.setRemainingAir(player.getMaximumAir());
        player.setFallDistance(0f);
        player.setAbsorptionAmount(0);
        player.setFlySpeed(1);

        // Movement & physics
        player.setVelocity(new Vector(0, 0, 0));
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setGliding(false);
        player.setRiptiding(false);

        // Status flags
        player.setGlowing(false);
        player.setInvisible(false);
        player.setInvulnerable(false);
        player.lockFreezeTicks(false);

        // Potion effects
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        // Experience
        player.setExp(0f);
        player.setLevel(0);
        player.setTotalExperience(0);

        // Inventory
        player.getInventory().clear();
    }
}