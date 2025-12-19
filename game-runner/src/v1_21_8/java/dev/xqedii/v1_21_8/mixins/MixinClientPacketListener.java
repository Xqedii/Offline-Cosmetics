package dev.xqedii.v1_21_8.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import dev.xqedii.core.OfflineCosmetics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.lang.reflect.Field;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener {
  @Shadow public abstract GameProfile getLocalGameProfile();

  @Inject(method = "handleLogin", at = @At("HEAD"))
  private void onHandleLogin(ClientboundLoginPacket packet, CallbackInfo ci) {
    if (!OfflineCosmetics.get().configuration().enabled().get()) return;
    if (!OfflineCosmetics.get().configuration().showCosmetics().get()) return;

    GameProfile profile = this.getLocalGameProfile();
    var user = Minecraft.getInstance().getUser();

    if (profile != null && user != null && user.getName().equals(profile.getName())) {
      try {
        Field idField = GameProfile.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(profile, user.getProfileId());
      } catch (Exception ignored) {}
    }
  }
}