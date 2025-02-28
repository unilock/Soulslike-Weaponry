package net.soulsweaponry.registry;

import net.minecraft.item.Item;
import net.minecraft.util.Rarity;
import net.soulsweaponry.items.Blunderbuss;
import net.soulsweaponry.items.GatlingGun;
import net.soulsweaponry.items.GunItem;
import net.soulsweaponry.items.HunterCannon;
import net.soulsweaponry.items.HunterPistol;

public class GunRegistry {

    public static GunItem HUNTER_PISTOL = new HunterPistol(new Item.Settings().maxDamage(700).rarity(Rarity.RARE));
    public static GunItem BLUNDERBUSS = new Blunderbuss(new Item.Settings().maxDamage(900).rarity(Rarity.RARE));
    public static GunItem GATLING_GUN = new GatlingGun(new Item.Settings().maxDamage(1000).rarity(Rarity.RARE));
    public static GunItem HUNTER_CANNON = new HunterCannon(new Item.Settings().maxDamage(1250).rarity(Rarity.RARE));
    
    public static void init() {
        ItemRegistry.registerGunItem(HUNTER_PISTOL, "hunter_pistol");
        ItemRegistry.registerGunItem(BLUNDERBUSS, "blunderbuss");
        ItemRegistry.registerGunItem(GATLING_GUN, "gatling_gun");
        ItemRegistry.registerGunItem(HUNTER_CANNON, "hunter_cannon");
    }
}
