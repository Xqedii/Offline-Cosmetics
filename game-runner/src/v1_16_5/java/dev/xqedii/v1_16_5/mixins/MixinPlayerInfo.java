package dev.xqedii.v1_16_5.mixins;

import com.mojang.authlib.GameProfile;
import dev.xqedii.core.OfflineCosmetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(PlayerInfo.class)
public abstract class MixinPlayerInfo {

  @Shadow @Final private GameProfile profile;

  @Inject(method = "<init>", at = @At("RETURN"))
  private void onInit(CallbackInfo ci) {
    GameProfile currentProfile = this.profile;
    if (currentProfile == null) return;

    if (!OfflineCosmetics.get().configuration().enabled().get()) return;
    if (!OfflineCosmetics.get().configuration().showCosmetics().get()) return;

    try {
      Minecraft mc = Minecraft.getInstance();
      var user = mc.getUser();

      if (user != null && user.getName().equals(currentProfile.getName())) {
        Field idField = GameProfile.class.getDeclaredField("id");
        idField.setAccessible(true);

        idField.set(currentProfile, user.getGameProfile().getId());
      }
    } catch (Exception ignored) {}
  }
}