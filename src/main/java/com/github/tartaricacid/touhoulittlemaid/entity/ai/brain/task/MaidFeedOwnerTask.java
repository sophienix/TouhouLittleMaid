package com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task;

import com.github.tartaricacid.touhoulittlemaid.api.task.IFeedTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class MaidFeedOwnerTask extends MaidCheckRateTask {
    private static final int MAX_DELAY_TIME = 20;
    private final IFeedTask task;
    private final float walkSpeed;
    private final int closeEnoughDist;

    public MaidFeedOwnerTask(IFeedTask task, int closeEnoughDist, float walkSpeed) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleStatus.VALUE_ABSENT));
        this.task = task;
        this.walkSpeed = walkSpeed;
        this.closeEnoughDist = closeEnoughDist;
        this.setMaxCheckRate(MAX_DELAY_TIME);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerWorld worldIn, EntityMaid maid) {
        if (super.checkExtraStartConditions(worldIn, maid)) {
            LivingEntity owner = maid.getOwner();
            if (owner instanceof PlayerEntity && owner.isAlive()) {
                if (owner.closerThan(maid, closeEnoughDist)) {
                    return true;
                }
                BrainUtil.setWalkAndLookTargetMemories(maid, owner, walkSpeed, 1);
            }
            return false;
        }
        return false;
    }

    @Override
    protected void start(ServerWorld worldIn, EntityMaid maid, long gameTimeIn) {
        LivingEntity owner = maid.getOwner();
        if (owner instanceof PlayerEntity && owner.isAlive()) {
            PlayerEntity player = (PlayerEntity) owner;
            boolean dying = player.getHealth() / player.getMaxHealth() < 0.5f;
            IntList lowestFoods = new IntArrayList();
            IntList lowFoods = new IntArrayList();
            IntList highFoods = new IntArrayList();

            CombinedInvWrapper inv = maid.getAvailableInv(true);
            for (int i = 0; i < inv.getSlots(); ++i) {
                ItemStack stack = inv.getStackInSlot(i);
                if (task.isFood(stack, player)) {
                    IFeedTask.Priority priority = task.getPriority(stack, player);
                    if (priority == IFeedTask.Priority.HIGH) {
                        highFoods.add(i);
                        break;
                    }
                    if (priority == IFeedTask.Priority.LOW) {
                        lowFoods.add(i);
                        break;
                    }
                    if (dying && priority == IFeedTask.Priority.LOWEST) {
                        lowestFoods.add(i);
                        break;
                    }
                }
            }

            if (highFoods.isEmpty() && lowFoods.isEmpty() && lowestFoods.isEmpty()) {
                return;
            }

            IntList map = !highFoods.isEmpty() ? highFoods : !lowFoods.isEmpty() ? lowFoods : lowestFoods;
            map.stream().skip(maid.getRandom().nextInt(map.size())).findFirst().ifPresent(slot -> {
                inv.setStackInSlot(slot, task.feed(inv.getStackInSlot(slot), player));
                maid.swing(Hand.MAIN_HAND);
                this.setNextCheckTickCount(5);
            });
        }
    }
}
