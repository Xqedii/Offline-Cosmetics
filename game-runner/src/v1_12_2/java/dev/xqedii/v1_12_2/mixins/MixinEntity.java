package dev.xqedii.v1_12_2.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import dev.xqedii.core.OfflineCosmetics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.UUID;

@Mixin(Entity.class)
public abstract class MixinEntity {

  @Shadow public abstract String getName();

  @Inject(method = "getUniqueID", at = @At("HEAD"), cancellable = true)
  private void onGetUniqueID(CallbackInfoReturnable<UUID> cir) {
    OfflineCosmetics addon = OfflineCosmetics.get();

    if (addon == null || !addon.configuration().enabled().get() || !addon.configuration().showCosmetics().get()) {
      return;
    }

    try {
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.getSession() != null && this.getName().equalsIgnoreCase(mc.getSession().getUsername())) {
        cir.setReturnValue(mc.getSession().getProfile().getId());
      }
    } catch (Exception ignored) {}
  }
}