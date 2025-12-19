package dev.xqedii.v1_8_9.mixins;

import com.mojang.authlib.GameProfile;
import dev.xqedii.core.OfflineCosmetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.lang.reflect.Field;

@Mixin(NetworkPlayerInfo.class)
public abstract class MixinNetworkPlayerInfo {

  @Inject(method = "<init>(Lcom/mojang/authlib/GameProfile;)V", at = @At("RETURN"))
  private void onInit(GameProfile profile, CallbackInfo ci) {
    if (profile == null) return;

    OfflineCosmetics addon = OfflineCosmetics.get();
    if (addon == null || !addon.configuration().enabled().get() || !addon.configuration().showCosmetics().get()) {
      return;
    }

    Minecraft mc = Minecraft.getMinecraft();
    if (mc.getSession().getUsername().equalsIgnoreCase(profile.getName())) {
      try {
        Field idField = GameProfile.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(profile, mc.getSession().getProfile().getId());
      } catch (Exception ignored) {}
    }
  }
}