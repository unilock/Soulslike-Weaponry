package net.soulsweaponry.client.registry;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.item.CompassAnglePredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.soulsweaponry.items.BossCompass;
import net.soulsweaponry.items.IChargeNeeded;
import net.soulsweaponry.items.IShootModProjectile;
import net.soulsweaponry.items.Skofnung;
import net.soulsweaponry.items.Sting;
import net.soulsweaponry.registry.GunRegistry;
import net.soulsweaponry.registry.ItemRegistry;
import net.soulsweaponry.registry.WeaponRegistry;

public class PredicateRegistry {
    
    public static void initClient() {

        PredicateRegistry.registerPull(WeaponRegistry.GALEFORCE);
        PredicateRegistry.registerPulling(WeaponRegistry.GALEFORCE);
        PredicateRegistry.registerPull(WeaponRegistry.KRAKEN_SLAYER);
        PredicateRegistry.registerPulling(WeaponRegistry.KRAKEN_SLAYER);
        PredicateRegistry.registerPull(WeaponRegistry.KRAKEN_SLAYER_CROSSBOW);
        PredicateRegistry.registerPulling(WeaponRegistry.KRAKEN_SLAYER_CROSSBOW);
        PredicateRegistry.registerCrossbowCharged(WeaponRegistry.KRAKEN_SLAYER_CROSSBOW);
        PredicateRegistry.registerPull(WeaponRegistry.DARKMOON_LONGBOW);
        PredicateRegistry.registerPulling(WeaponRegistry.DARKMOON_LONGBOW);

        PredicateRegistry.registerThrowing(WeaponRegistry.COMET_SPEAR);
        PredicateRegistry.registerThrowing(WeaponRegistry.NIGHTFALL);
        PredicateRegistry.registerThrowing(WeaponRegistry.DARKIN_BLADE);
        PredicateRegistry.registerThrowing(WeaponRegistry.DRAGONSLAYER_SWORDSPEAR);
        PredicateRegistry.registerThrowing(WeaponRegistry.WHIRLIGIG_SAWBLADE);
        PredicateRegistry.registerThrowing(GunRegistry.GATLING_GUN);
        PredicateRegistry.registerThrowing(WeaponRegistry.LEVIATHAN_AXE);
        PredicateRegistry.registerThrowing(WeaponRegistry.MJOLNIR);
        PredicateRegistry.registerThrowing(WeaponRegistry.MOONLIGHT_GREATSWORD);
        PredicateRegistry.registerThrowing(WeaponRegistry.PURE_MOONLIGHT_GREATSWORD);
        PredicateRegistry.registerThrowing(WeaponRegistry.DRAUPNIR_SPEAR);

        PredicateRegistry.registerOtherModIsLoaded(WeaponRegistry.SOUL_REAPER, "bettercombat");
        PredicateRegistry.registerOtherModIsLoaded(WeaponRegistry.FORLORN_SCYTHE, "bettercombat");
        PredicateRegistry.registerOtherModIsLoaded(WeaponRegistry.DARKIN_SCYTHE_PRE, "bettercombat");
        PredicateRegistry.registerOtherModIsLoaded(WeaponRegistry.SHADOW_ASSASSIN_SCYTHE, "bettercombat");
        PredicateRegistry.registerOtherModIsLoaded(WeaponRegistry.DARKIN_SCYTHE_PRIME, "bettercombat");
        PredicateRegistry.registerOtherModIsLoaded(WeaponRegistry.KRAKEN_SLAYER, "epicfight");

        PredicateRegistry.registerCharged(WeaponRegistry.HOLY_MOONLIGHT_GREATSWORD);
        PredicateRegistry.registerCharged(WeaponRegistry.HOLY_MOONLIGHT_SWORD);
        PredicateRegistry.registerCharged(WeaponRegistry.BLUEMOON_GREATSWORD);

        ModelPredicateProviderRegistry.register(WeaponRegistry.DRAUGR, Identifier.of("night"), (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int number) -> {
            if (livingEntity != null && livingEntity.getWorld().getDimension().hasSkyLight() && livingEntity.getWorld().getTimeOfDay() % 24000 > 13000 && livingEntity.getWorld().getTimeOfDay() % 24000 < 23000) {
                if (itemStack.getEnchantments().size() > 0) {
                    return 0.5F;
                }
                return 1.0F;
            } else {
                return 0.0F;
            }
        });

        ModelPredicateProviderRegistry.register(ItemRegistry.BOSS_COMPASS, Identifier.of("angle"), new CompassAnglePredicateProvider((world, stack, entity) -> {
            if (stack.isOf(ItemRegistry.BOSS_COMPASS)) {
                BossCompass item = (BossCompass) stack.getItem();
                return item.getStructurePos(world, stack);
            }
            return null;
        }));

        ModelPredicateProviderRegistry.register(WeaponRegistry.SKOFNUNG, Identifier.of("prime"), (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int number) -> {
            if (itemStack.getItem() instanceof Skofnung) {
                boolean emp = Skofnung.isEmpowered(itemStack);
                if (emp) {
                    return 1.0F;
                }
            }
            return 0.0f;
        });


        ModelPredicateProviderRegistry.register(WeaponRegistry.STING, Identifier.of("prime"), (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int number) -> {
            if (itemStack.getItem() instanceof Sting) {
                boolean emp = ((Sting) itemStack.getItem()).isActive(itemStack);
                if (emp) {
                    return 1.0F;
                }
            }
            return 0.0f;
        });

        ModelPredicateProviderRegistry.register(WeaponRegistry.MASTER_SWORD, Identifier.of("prime"), (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int number) -> {
            if (itemStack.isOf(WeaponRegistry.MASTER_SWORD) && livingEntity != null && livingEntity.getHealth() >= livingEntity.getMaxHealth()) {
                return 1.0f;
            }
            return 0.0f;
        });
    }

    protected static void registerPulling(Item item) {
        ModelPredicateProviderRegistry.register(item, Identifier.of("pulling"), (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int number) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
        });
    }

    protected static void registerPull(Item item) {
        ModelPredicateProviderRegistry.register(item, Identifier.of("pull"), (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int number) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getActiveItem() != itemStack ? 0.0F : (itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / (float) ((IShootModProjectile)itemStack.getItem()).getPullTime();
        });
    }

    protected static void registerThrowing(Item item) {
        ModelPredicateProviderRegistry.register(item, Identifier.of("throwing"), (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int number) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
        });
    }

    protected static void registerOtherModIsLoaded(Item item, String id) {
        ModelPredicateProviderRegistry.register(item , Identifier.of(id), (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int number) -> FabricLoader.getInstance().isModLoaded(id) ? 1f : 0f);
    }

    protected static void registerCharged(Item item) {
        ModelPredicateProviderRegistry.register(item, Identifier.of("charged"), (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int number) -> {
            if (((IChargeNeeded)item).isCharged(itemStack)) {
                return 1.0f;
            }
            return 0.0f;
        });
    }

    protected static void registerCrossbowCharged(Item item) {
        ModelPredicateProviderRegistry.register(item, Identifier.of("charged"), (stack, world, entity, seed) -> {
            return CrossbowItem.isCharged(stack) ? 1.0f : 0.0f;
        });
    }
}
