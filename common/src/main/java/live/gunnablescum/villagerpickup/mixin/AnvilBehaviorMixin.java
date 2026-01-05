package live.gunnablescum.villagerpickup.mixin;

import live.gunnablescum.villagerpickup.configuration.ConfigurationHandler;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilMenu.class)
public class AnvilBehaviorMixin {

    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    public void canTakeOutput(Player player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        AnvilMenu anvilScreenHandler = (AnvilMenu) (Object) this;
        if(anvilScreenHandler.inputSlots.getItem(0).getItem() != Items.VILLAGER_SPAWN_EGG) return;
        if(!ConfigurationHandler.getBoolean("allow_villager_rename_with_anvil") && !player.permissions().hasPermission(Permissions.COMMANDS_OWNER)) {
            cir.setReturnValue(false);
        }
    }

}
