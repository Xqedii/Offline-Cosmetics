package dev.xqedii.v1_21_4.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import dev.xqedii.core.OfflineCosmetics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends Player {

  public MixinAbstractClientPlayer(Level level, BlockPos blockPos, float f, GameProfile gameProfile) {
    super(level, blockPos, f, gameProfile);
  }

  @Unique
  private PlayerSkin myCachedSkin;

  @Unique
  private GameProfile fetchedProfile;

  @Unique
  private boolean isProfileLoading = false;

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
    User sessionUser = mc.getUser();

    if (sessionUser != null && sessionUser.getName().equals(this.getName().getString())) {

      if (fetchedProfile != null) {
        PlayerSkin skin = mc.getSkinManager().getInsecureSkin(fetchedProfile);
        String path = skin.texture().getPath();

        if (!path.contains("entity/player/")) {
          this.myCachedSkin = skin;
          cir.setReturnValue(skin);
        }
        return;
      }

      if (!isProfileLoading) {
        isProfileLoading = true;

        CompletableFuture.runAsync(() -> {
          try {
            var result = mc.getMinecraftSessionService().fetchProfile(sessionUser.getProfileId(), false);

            if (result != null) {
              GameProfile filledProfile = result.profile();
              this.fetchedProfile = filledProfile;

              mc.execute(() -> mc.getSkinManager().getInsecureSkin(filledProfile));
            } else {
              isProfileLoading = false;
            }
          } catch (Exception e) {
            e.printStackTrace();
            isProfileLoading = false;
          }
        });
      }
    }
  }
}