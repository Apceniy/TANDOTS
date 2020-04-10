package com.artemka091102.compasss;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

@Mod(Compasss.MODID)
public class CompassItem extends Item {
   static final Logger LOGGER = LogManager.getLogger();
   public CompassItem(Item.Properties builder) {
      super(builder);
      this.addPropertyOverride(new ResourceLocation("angle"), new IItemPropertyGetter() {
         @OnlyIn(Dist.CLIENT)
         private double rotation;
         @OnlyIn(Dist.CLIENT)
         private double rota;
         @OnlyIn(Dist.CLIENT)
         private long lastUpdateTick;

         @OnlyIn(Dist.CLIENT)
         public float call(ItemStack p_call_1_, @Nullable World p_call_2_, @Nullable LivingEntity p_call_3_) {
            if (p_call_3_ == null && !p_call_1_.isOnItemFrame()) {
               return 0.0F;
            } else {
               boolean flag = p_call_3_ != null;
               Entity entity = (Entity)(flag ? p_call_3_ : p_call_1_.getItemFrame());
               if (p_call_2_ == null) {
                  p_call_2_ = entity.world;
               }

               double d0;
               if (p_call_2_.dimension.isSurfaceWorld()) {
                  double d1 = flag ? (double)entity.rotationYaw : this.getFrameRotation((ItemFrameEntity)entity);
                  d1 = MathHelper.positiveModulo(d1 / 360.0D, 1.0D);
                  double d2 = this.getSouthToAngle(p_call_2_, entity) / (double)((float)Math.PI * 2F);
                  d0 = 0.5D - (d1 - 0.25D - d2);
               } else {
                  d0 = Math.random();
               }

               if (flag) {
                  d0 = this.wobble(p_call_2_, d0);
               }

               return MathHelper.positiveModulo((float)d0, 1.0F);
            }
         }

         @OnlyIn(Dist.CLIENT)
         private double wobble(World worldIn, double p_185093_2_) {
            if (worldIn.getGameTime() != this.lastUpdateTick) {
               this.lastUpdateTick = worldIn.getGameTime();
               double d0 = p_185093_2_ - this.rotation;
               d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
               this.rota += d0 * 0.1D;
               this.rota *= 0.8D;
               this.rotation = MathHelper.positiveModulo(this.rotation + this.rota, 1.0D);
            }

            return this.rotation;
         }

         @OnlyIn(Dist.CLIENT)
         private double getFrameRotation(ItemFrameEntity p_185094_1_) {
            return (double)MathHelper.wrapDegrees(180 + p_185094_1_.getHorizontalFacing().getHorizontalIndex() * 90);
         }

         @OnlyIn(Dist.CLIENT)
         private double getSouthToAngle(IWorld p_185092_1_, Entity p_185092_2_) {
        	LOGGER.debug("working");
            return Math.atan2((double)1, (double)0);
         }
      });
   }
}