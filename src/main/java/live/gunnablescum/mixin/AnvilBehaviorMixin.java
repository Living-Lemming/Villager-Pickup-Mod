package live.gunnablescum.mixin;

import live.gunnablescum.configuration.ConfigurationHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilScreenHandler.class)
public class AnvilBehaviorMixin {

    @Inject(method = "canTakeOutput", at = @At("HEAD"), cancellable = true)
    public void canTakeOutput(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        AnvilScreenHandler anvilScreenHandler = (AnvilScreenHandler) (Object) this;
        if(anvilScreenHandler.input.getStack(0).getItem() != Items.VILLAGER_SPAWN_EGG) return;
        if(!ConfigurationHandler.getBoolean("allow_villager_rename_with_anvil") && player.getPermissionLevel() != 4) {
            cir.setReturnValue(false);
        }
    }

}
