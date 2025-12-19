package dev.xqedii.v1_8_9.mixins;

import com.mojang.authlib.GameProfile;
import dev.xqedii.core.OfflineCosmetics;
import dev.xqedii.core.SkinHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.concurrent.CompletableFuture;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer {

  @Inject(method = "getLocationSkin", at = @At("HEAD"), cancellable = true)
  private void onGetLocationSkin(CallbackInfoReturnable<ResourceLocation> cir) {
    if (!OfflineCosmetics.get().configuration().enabled().get()) return;
    if (!OfflineCosmetics.get().configuration().forceSkin().get()) return;

    Minecraft mc = Minecraft.getMinecraft();
    AbstractClientPlayer player = (AbstractClientPlayer) (Object) this;

    String name = player.getName();

    if (mc.getSession().getUsername().equals(name)) {
      GameProfile profile = (GameProfile) SkinHelper.getCachedProfile(name);

      if (profile != null) {
        var map = mc.getSkinManager().loadSkinFromCache(profile);
        if (map.containsKey(com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.SKIN)) {
          cir.setReturnValue(mc.getSkinManager().loadSkin(
                  map.get(com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.SKIN),
                  com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.SKIN));
        }
        return;
      }

      if (!SkinHelper.isProfileLoading(name)) {
        SkinHelper.setProfileLoading(name, true);
        CompletableFuture.runAsync(() -> {
          try {
            GameProfile gp = mc.getSessionService().fillProfileProperties(mc.getSession().getProfile(), false);
            SkinHelper.cacheProfile(name, gp);
          } catch (Exception e) {
            SkinHelper.setProfileLoading(name, false);
          }
        });
      }
    }
  }
}