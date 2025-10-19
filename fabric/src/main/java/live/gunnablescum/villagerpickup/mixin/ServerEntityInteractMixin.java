package live.gunnablescum.villagerpickup.mixin;

import live.gunnablescum.villagerpickup.CommonClass;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerEntityInteractMixin {

    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleInteract", at = @At("HEAD"), cancellable = true)
    public void onPlayerInteract(ServerboundInteractPacket packet, CallbackInfo ci) {
        #if !PRE_PLAYER_LEVEL_METHOD
        Entity entity = packet.getTarget(player.level());
        #else
        Entity entity = packet.getTarget(player.serverLevel());
        #endif
        if(entity == null) return; // We don't care
        if(entity.getType() != EntityType.VILLAGER) return; // PASS
        ItemStack item = player.getMainHandItem();
        if (item.isEmpty()) {
            item = player.getOffhandItem();
        }

        if(item.getItem() == Items.VILLAGER_SPAWN_EGG && !item.getComponents().get(DataComponents.ENTITY_DATA).copyTagWithoutId().isEmpty()) // Fix for #28
        {
            ci.cancel(); // Fix for #26
            return;
        }

        ItemStack spawnEgg = CommonClass.convertVillagerToItemStack(player, entity);

        if(spawnEgg == null) return; // PASS

        if(player.getInventory().getFreeSlot() != -1) {
            player.getInventory().add(spawnEgg);
        } else {
            player.drop(spawnEgg, true);
        }

        entity.remove(Entity.RemovalReason.DISCARDED);
        // SUCCESS (no return necessary)
    }

}
