package dev.xqedii.v1_21_8.mixins;

import com.mojang.authlib.GameProfile;
import dev.xqedii.core.OfflineCosmetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.lang.reflect.Field;

@Mixin(PlayerInfo.class)
public abstract class MixinPlayerInfo {

  @Shadow @Final @Mutable private GameProfile profile;

  @Inject(method = "<init>", at = @At("RETURN"))
  private void onInit(CallbackInfo ci) {
    if (this.profile == null) return;

    OfflineCosmetics addon = OfflineCosmetics.get();
    if (addon == null) return;

    try {
      var user = Minecraft.getInstance().getUser();
      if (user != null && user.getName().equalsIgnoreCase(this.profile.getName())) {

        Field idField = GameProfile.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(this.profile, user.getProfileId());
      }
    } catch (Exception ignored) {}
  }
}