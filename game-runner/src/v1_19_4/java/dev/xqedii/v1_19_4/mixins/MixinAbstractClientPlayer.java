package dev.xqedii.v1_19_4.mixins;

import com.mojang.authlib.GameProfile;
import dev.xqedii.core.OfflineCosmetics;
import dev.xqedii.core.SkinHelper;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer {

  @Inject(method = "getSkinTextureLocation", at = @At("HEAD"), cancellable = true)
  private void onGetSkinTextureLocation(CallbackInfoReturnable<ResourceLocation> cir) {
    if (!OfflineCosmetics.get().configuration().enabled().get()) return;
    if (!OfflineCosmetics.get().configuration().forceSkin().get()) return;

    Minecraft mc = Minecraft.getInstance();
    AbstractClientPlayer player = (AbstractClientPlayer) (Object) this;
    String name = player.getName().getString();

    if (mc.getUser() != null && mc.getUser().getName().equals(name)) {
      GameProfile profile = (GameProfile) SkinHelper.getCachedProfile(name);

      if (profile != null) {
        var map = mc.getSkinManager().getInsecureSkinInformation(profile);
        if (map.containsKey(com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.SKIN)) {
          cir.setReturnValue(mc.getSkinManager().registerTexture(
              map.get(com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.SKIN),
              com.mojang.authlib.minecraft.MinecraftProfileTexture.Type.SKIN));
        }
        return;
      }

      if (!SkinHelper.isProfileLoading(name)) {
        SkinHelper.setProfileLoading(name, true);
        CompletableFuture.runAsync(() -> {
          try {
            var sessionService = mc.getMinecraftSessionService();
            GameProfile gp = sessionService.fillProfileProperties(mc.getUser().getGameProfile(), false);
            SkinHelper.cacheProfile(name, gp);
          } catch (Exception e) {
            SkinHelper.setProfileLoading(name, false);
          }
        });
      }
    }
  }
}