package com.npg418.xpdemagnetization.mixin;

import com.supermartijn642.simplemagnets.MagnetItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(MagnetItem.class)
public class MagnetItemMixin {
    @Redirect(method = "inventoryUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntitiesOfClass(Ljava/lang/Class;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"))
    public <T extends Entity> List<T> filterOrbs(Level instance, Class<T> aClass, AABB aabb) {
        return instance.getEntitiesOfClass(aClass, aabb, orb -> !orb.getPersistentData().contains("PreventRemoteMovement"));
    }
}
