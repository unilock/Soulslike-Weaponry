package net.soulsweaponry.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WitherRoseBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Supplier;

/**
 * Will turn back into a random flower instead of its previous form when ticked.
 */
public class WitheredFlower extends WitherRoseBlock implements Withered {

    private final RegistryEntry<StatusEffect> effect;
    public static final BooleanProperty CANNOT_TURN = BooleanProperty.of("can_turn");
    private static final Supplier<List<Block>> SMALL_FLOWERS = () -> Registries.BLOCK.stream().filter((block -> block.getDefaultState().isIn(BlockTags.SMALL_FLOWERS))).toList(); //NOTE: unsure if this works on servers (it should tho, right?)

    public WitheredFlower(RegistryEntry<StatusEffect> effect, int duration, Settings settings) {
        super(effect, duration, settings);
        this.effect = effect;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CANNOT_TURN);
    }

    public Block getBlock() {
        return this;
    }

    public Block getBlockToReturnAs() {
        return this.getRandomFlower();
    }

    private Block getRandomFlower() {
        return SMALL_FLOWERS.get().get(new java.util.Random().nextInt(SMALL_FLOWERS.get().size()));
    }

    @Override
    public boolean canTurn(BlockView world, BlockPos pos, int maxNeighbors) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        mutable.set(pos, Direction.DOWN);
        return !(world.getBlockState(mutable).getBlock() instanceof WitheredBlock) && !world.getBlockState(pos).get(CANNOT_TURN);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient || world.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        if (entity instanceof LivingEntity) {
            ((LivingEntity)entity).addStatusEffect(new StatusEffectInstance(this.effect, 40));
        }
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return super.canPlantOnTop(floor, world, pos) || floor.isOf(Blocks.NETHERRACK) || floor.isOf(Blocks.SOUL_SAND) || floor.isOf(Blocks.SOUL_SOIL) || floor.isOf(Blocks.END_STONE);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (this.canTurn(world, pos, 0)) {
            this.turnBack(world, pos);
        }
        DebugInfoSender.sendNeighborUpdate(world, pos);
    }
}