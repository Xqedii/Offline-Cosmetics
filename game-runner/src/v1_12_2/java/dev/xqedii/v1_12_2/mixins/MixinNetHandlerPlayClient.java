package dev.xqedii.v1_12_2.mixins;

import com.mojang.authlib.GameProfile;
import dev.xqedii.core.OfflineCosmetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketJoinGame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.lang.reflect.Field;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient {

  @Inject(method = "handleJoinGame", at = @At("HEAD"))
  private void onHandleJoin(SPacketJoinGame packetIn, CallbackInfo ci) {
    OfflineCosmetics addon = OfflineCosmetics.get();
    if (addon == null || !addon.configuration().enabled().get() || !addon.configuration().showCosmetics().get()) return;

    Minecraft mc = Minecraft.getMinecraft();
    try {
      GameProfile profile = mc.getSession().getProfile();
      Field idField = GameProfile.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(profile, mc.getSession().getProfile().getId());
    } catch (Exception ignored) {}
  }
}