package dev.xqedii.v1_20_1.mixins;

import com.mojang.authlib.GameProfile;
import dev.xqedii.core.OfflineCosmetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo; // Nowa paczka
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.lang.reflect.Field;

@Mixin(PlayerInfo.class)
public abstract class MixinNetworkPlayerInfo {

  @Inject(method = "<init>(Lcom/mojang/authlib/GameProfile;Z)V", at = @At("RETURN"))
  private void onInit(GameProfile profile, boolean listed, CallbackInfo ci) {
    if (profile == null) return;
    if (!OfflineCosmetics.get().configuration().enabled().get()) return;
    if (!OfflineCosmetics.get().configuration().showCosmetics().get()) return;

    Minecraft mc = Minecraft.getInstance();
    if (mc.getUser().getName().equals(profile.getName())) {
      try {
        Field idField = GameProfile.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(profile, mc.getUser().getProfileId());
      } catch (Exception ignored) {}
    }
  }
}