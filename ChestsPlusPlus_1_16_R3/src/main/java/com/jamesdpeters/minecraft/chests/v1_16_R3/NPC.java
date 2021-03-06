package com.jamesdpeters.minecraft.chests.v1_16_R3;

import com.jamesdpeters.minecraft.chests.NPCProvider;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.MinecraftServer;
import net.minecraft.server.v1_16_R3.PlayerInteractManager;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.HumanEntity;

import java.util.UUID;

public class NPC implements NPCProvider {
    public HumanEntity createHumanEntity() {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "Chests++NPC");
        EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, gameProfile, new PlayerInteractManager(nmsWorld));
        return npc.getBukkitEntity();
    }
}
