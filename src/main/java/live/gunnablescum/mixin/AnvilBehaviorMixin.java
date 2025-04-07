package live.gunnablescum.mixin;

import live.gunnablescum.configuration.ConfigurationHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilMenu.class)
public class AnvilBehaviorMixin {

    @Inject(method = "mayPickup", at = @At(value = "HEAD", ordinal = 0), cancellable = true)
    public void canPickUp(Player player, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        AnvilMenu anvilMenu = (AnvilMenu) (Object) this;
        if(anvilMenu.inputSlots.getItem(0).getItem() == Items.VILLAGER_SPAWN_EGG && !ConfigurationHandler.getBoolean("allow_villager_rename_with_anvil")) {
            cir.setReturnValue(player.hasInfiniteMaterials() || player.hasPermissions(4));
        }
    }

}
