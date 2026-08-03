package com.npg418.xpdemagnetization.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.supermartijn642.simplemagnets.DemagnetizationCoilBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DemagnetizationCoilBlockEntity.class)
public abstract class DemagnetizationCoilBlockEntityMixin extends BlockEntity {
    public DemagnetizationCoilBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Inject(method = "update", at = @At("RETURN"))
    private void addXpOrbFlags(CallbackInfo ci, @Local(name = "area") AABB area) {
        if (this.level == null) return;

        List<ExperienceOrb> affectedOrbs = this.level.getEntitiesOfClass(ExperienceOrb.class, area);

        affectedOrbs.forEach(orb -> {
            orb.getPersistentData().putBoolean("PreventRemoteMovement", true);
            orb.getPersistentData().putBoolean("AllowMachineRemoteMovement", true);
        });
    }
}
