package dev.xqedii.v1_20_2.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import dev.xqedii.core.OfflineCosmetics;
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

  @Inject(method = "<init>(Lcom/mojang/authlib/GameProfile;Z)V", at = @At("RETURN"))
  private void onInit(GameProfile originalProfile, boolean isListed, CallbackInfo ci) {
    if (originalProfile == null) return;
    if (!OfflineCosmetics.get().configuration().enabled().get()) return;
    if (!OfflineCosmetics.get().configuration().showCosmetics().get()) return;

    var user = Minecraft.getInstance().getUser();
    if (user != null && user.getName().equals(originalProfile.getName())) {
      try {
        Field idField = GameProfile.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(originalProfile, user.getProfileId());
      } catch (Exception ignored) {}
    }
  }
}