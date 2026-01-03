package chalkinshmeal.mc_plugin_lib.entities;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

public class LivingEntityUtils {
    //-------------------------------------------------------------------------
    // Health 
    //-------------------------------------------------------------------------
    public static void addHealth(LivingEntity livingEntity, double health) {
        if (livingEntity == null) return;
        livingEntity.setHealth(livingEntity.getHealth() + health);
    }

    public static void subtractHealth(LivingEntity livingEntity, double health, boolean hurtAnimation) {
        if (livingEntity == null) return;
        if (hurtAnimation) livingEntity.damage(health);
        else livingEntity.setHealth(Math.max(livingEntity.getHealth() - health, 0));
    }

    public static void healEntity(LivingEntity livingEntity) {
        livingEntity.setHealth(LivingEntityUtils.getAttributeValue(livingEntity, Attribute.MAX_HEALTH));
    }

    public static double getMaxHealth(LivingEntity livingEntity) {
        if (livingEntity == null) return 0.0f;
        return LivingEntityUtils.getAttributeValue(livingEntity, Attribute.MAX_HEALTH);
    }

    public static void setMaxHealth(LivingEntity livingEntity, double maxHealth) {
        if (livingEntity == null || maxHealth <= 0) return;
        LivingEntityUtils.setAttributeValue(livingEntity, Attribute.MAX_HEALTH, maxHealth);
    }

    //-------------------------------------------------------------------------
    // Attributes
    //-------------------------------------------------------------------------
    public static double getAttributeValue(LivingEntity livingEntity, Attribute attribute) {
        if (livingEntity == null || attribute == null) return -1;

        AttributeInstance attr = livingEntity.getAttribute(attribute);
        if (attr == null) return -1;

        return attr.getValue();
    }

    public static void setAttributeValue(LivingEntity livingEntity, Attribute attribute, double value) {
        if (livingEntity == null || attribute == null) return;

        AttributeInstance attr = livingEntity.getAttribute(attribute);
        if (attr == null) return;

        // Remove all existing modifiers
        attr.getModifiers().forEach(modifier -> attr.removeModifier(modifier));

        // Set the base value
        attr.setBaseValue(value);
    }
}
