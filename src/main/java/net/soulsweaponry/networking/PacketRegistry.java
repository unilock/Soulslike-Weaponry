package net.soulsweaponry.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.soulsweaponry.networking.C2S.CollectSummonsC2S;
import net.soulsweaponry.networking.C2S.DamagingBoxC2S;
import net.soulsweaponry.networking.C2S.KeybindAbilityC2S;
import net.soulsweaponry.networking.C2S.MoonlightC2S;
import net.soulsweaponry.networking.C2S.ParryC2S;
import net.soulsweaponry.networking.C2S.ReturnFreyrSwordC2S;
import net.soulsweaponry.networking.C2S.ReturnThrownWeaponC2S;
import net.soulsweaponry.networking.C2S.StationaryFreyrSwordC2S;
import net.soulsweaponry.networking.C2S.SwitchTrickWeaponC2S;
import net.soulsweaponry.networking.S2C.FlashParticleS2C;
import net.soulsweaponry.networking.S2C.FreyrSwordSummonDataSyncS2C;
import net.soulsweaponry.networking.S2C.ParrySyncS2C;
import net.soulsweaponry.networking.S2C.ParticleOutburstS2C;
import net.soulsweaponry.networking.S2C.ParticleSphereS2C;
import net.soulsweaponry.networking.S2C.PostureSyncS2C;
import net.soulsweaponry.networking.S2C.ReturningProjectileDataSyncS2C;
import net.soulsweaponry.networking.S2C.ShouldDamageRidingSyncS2C;
import net.soulsweaponry.networking.S2C.SingleParticleS2C;
import net.soulsweaponry.networking.S2C.SummonUUIDsSyncS2C;
import net.soulsweaponry.networking.S2C.UTDamageCooldownSyncS2C;

public class PacketRegistry {

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.MOONLIGHT, MoonlightC2S::receive);
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.RETURN_FREYR_SWORD, ReturnFreyrSwordC2S::receive);
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.STATIONARY_FREYR_SWORD, StationaryFreyrSwordC2S::receive);
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.COLLECT_SUMMONS, CollectSummonsC2S::receive);
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.SWITCH_TRICK_WEAPON, SwitchTrickWeaponC2S::receive);
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.KEYBIND_ABILITY, KeybindAbilityC2S::receive);
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.PARRY, ParryC2S::receive);
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.DAMAGING_BOX, DamagingBoxC2S::receive);
        ServerPlayNetworking.registerGlobalReceiver(PacketIds.RETURN_THROWN_WEAPON, ReturnThrownWeaponC2S::receive);
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(PacketIds.SPHERE_PARTICLES, ParticleSphereS2C::receive);
        ClientPlayNetworking.registerGlobalReceiver(PacketIds.OUTBURST_PARTICLES, ParticleOutburstS2C::receive);
        ClientPlayNetworking.registerGlobalReceiver(PacketIds.FLASH_PARTICLE, FlashParticleS2C::receive);
        ClientPlayNetworking.registerGlobalReceiver(PacketIds.SINGLE_PARTICLE, SingleParticleS2C::receive);
        ClientPlayNetworking.registerGlobalReceiver(PacketIds.PARRY_SYNC, ParrySyncS2C::receive);
        ClientPlayNetworking.registerGlobalReceiver(PacketIds.POSTURE_SYNC, PostureSyncS2C::receive);
        ClientPlayNetworking.registerGlobalReceiver(PacketIds.SUMMONS_UUIDS, SummonUUIDsSyncS2C::receive);
        ClientPlayNetworking.registerGlobalReceiver(PacketIds.SYNC_FREYR_SWORD_SUMMON_DATA, FreyrSwordSummonDataSyncS2C::receive);
        ClientPlayNetworking.registerGlobalReceiver(PacketIds.SYNC_DAMAGE_RIDING_DATA, ShouldDamageRidingSyncS2C::receive);
        ClientPlayNetworking.registerGlobalReceiver(PacketIds.SYNC_RETURNING_PROJECTILE_DATA, ReturningProjectileDataSyncS2C::receive);
        ClientPlayNetworking.registerGlobalReceiver(PacketIds.SYNC_UMBRAL_DAMAGE_COOLDOWN, UTDamageCooldownSyncS2C::receive);
    }
}
