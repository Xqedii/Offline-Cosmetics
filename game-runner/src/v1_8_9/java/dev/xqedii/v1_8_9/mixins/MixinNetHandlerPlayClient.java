package dev.xqedii.v1_8_9.mixins;

import com.mojang.authlib.GameProfile;
import dev.xqedii.core.OfflineCosmetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S01PacketJoinGame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.lang.reflect.Field;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient {

  @Inject(method = "handleJoinGame", at = @At("HEAD"))
  private void onHandleJoin(S01PacketJoinGame packetIn, CallbackInfo ci) {
    OfflineCosmetics addon = OfflineCosmetics.get();
    if (addon == null || !addon.configuration().enabled().get() || !addon.configuration().showCosmetics().get()) {
      return;
    }

    try {
      Minecraft mc = Minecraft.getMinecraft();
      GameProfile profile = mc.getSession().getProfile();

      if (profile != null) {
        Field idField = GameProfile.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(profile, mc.getSession().getProfile().getId());
      }
    } catch (Exception ignored) {}
  }
}