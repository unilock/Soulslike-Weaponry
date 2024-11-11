package net.soulsweaponry.networking;

import net.minecraft.util.Identifier;
import net.soulsweaponry.SoulsWeaponry;

public class PacketIds {

    // Server to Client
    public static final Identifier SPHERE_PARTICLES = Identifier.of(SoulsWeaponry.ModId, "sphere_particles");
    public static final Identifier OUTBURST_PARTICLES = Identifier.of(SoulsWeaponry.ModId, "outburst_particles");
    public static final Identifier FLASH_PARTICLE = Identifier.of(SoulsWeaponry.ModId, "flash_particle");
    public static final Identifier SINGLE_PARTICLE = Identifier.of(SoulsWeaponry.ModId, "single_particle");
    public static final Identifier PARRY_SYNC = Identifier.of(SoulsWeaponry.ModId, "parry_data_sync");
    public static final Identifier POSTURE_SYNC = Identifier.of(SoulsWeaponry.ModId, "posture_data_sync");
    public static final Identifier SYNC_FREYR_SWORD_SUMMON_DATA = Identifier.of(SoulsWeaponry.ModId, "freyr_sword_summon_uuid_sync");
    public static final Identifier SYNC_RETURNING_PROJECTILE_DATA = Identifier.of(SoulsWeaponry.ModId, "returning_projectile_uuid_sync");
    public static final Identifier SYNC_DAMAGE_RIDING_DATA = Identifier.of(SoulsWeaponry.ModId, "should_damage_riding_sync");
    public static final Identifier SYNC_UMBRAL_DAMAGE_COOLDOWN = Identifier.of(SoulsWeaponry.ModId, "umbral_trespass_damage_cooldown_sync");

    // Client to Server
    public static final Identifier MOONLIGHT = Identifier.of(SoulsWeaponry.ModId, "moonlight");
    public static final Identifier RETURN_FREYR_SWORD = Identifier.of(SoulsWeaponry.ModId, "check_can_freyr_return");
    public static final Identifier STATIONARY_FREYR_SWORD = Identifier.of(SoulsWeaponry.ModId, "switch_stationary_freyr");
    public static final Identifier COLLECT_SUMMONS = Identifier.of(SoulsWeaponry.ModId, "collect_summons_to_soul_reaper");
    public static final Identifier SWITCH_TRICK_WEAPON = Identifier.of(SoulsWeaponry.ModId, "switch_trick_weapon");
    public static final Identifier KEYBIND_ABILITY = Identifier.of(SoulsWeaponry.ModId, "keybind_ability");
    public static final Identifier DAMAGING_BOX = Identifier.of(SoulsWeaponry.ModId, "damaging_box");
    public static final Identifier RETURN_THROWN_WEAPON = Identifier.of(SoulsWeaponry.ModId, "try_return_thrown_weapons");
    public static final Identifier SUMMONS_UUIDS = Identifier.of(SoulsWeaponry.ModId, "summons_uuids");
    public static final Identifier PARRY = Identifier.of(SoulsWeaponry.ModId, "parry_keybind");
}