package dev.xqedii.v1_20_6.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import dev.xqedii.core.OfflineCosmetics;
import dev.xqedii.core.SkinHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.concurrent.CompletableFuture;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends Player {
  public MixinAbstractClientPlayer(Level l, BlockPos p, float f, GameProfile g) { super(l, p, f, g); }

  @Unique private PlayerSkin myCachedSkin;

  @Inject(method = "getSkin", at = @At("HEAD"), cancellable = true)
  private void onGetSkin(CallbackInfoReturnable<PlayerSkin> cir) {
    if (!OfflineCosmetics.get().configuration().enabled().get()) return;
    if (!OfflineCosmetics.get().configuration().forceSkin().get()) {
      myCachedSkin = null;
      return;
    }

    if (myCachedSkin != null) {
      cir.setReturnValue(myCachedSkin);
      return;
    }

    Minecraft mc = Minecraft.getInstance();
    String name = this.getName().getString();

    if (mc.getUser() != null && mc.getUser().getName().equals(name)) {
      GameProfile fetched = (GameProfile) SkinHelper.getCachedProfile(name);

      if (fetched != null) {
        PlayerSkin skin = mc.getSkinManager().getInsecureSkin(fetched);
        if (!skin.texture().getPath().contains("entity/player/")) {
          this.myCachedSkin = skin;
          cir.setReturnValue(skin);
        }
        return;
      }

      if (!SkinHelper.isProfileLoading(name)) {
        SkinHelper.setProfileLoading(name, true);
        CompletableFuture.runAsync(() -> {
          try {
            var result = mc.getMinecraftSessionService().fetchProfile(mc.getUser().getProfileId(), false);
            if (result != null) {
              SkinHelper.cacheProfile(name, result.profile());
              mc.execute(() -> mc.getSkinManager().getInsecureSkin(result.profile()));
            }
          } catch (Exception e) {
            SkinHelper.setProfileLoading(name, false);
          }
        });
      }
    }
  }
}