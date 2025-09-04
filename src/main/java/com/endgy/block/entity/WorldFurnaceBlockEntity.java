package com.endgy.block.entity;

import com.endgy.Configs;
import com.endgy.WorldFurnace;
import com.endgy.block.custom.WorldFurnaceBlock;
import com.endgy.screen.WorldFurnaceScreenHandler;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.SharedConstants;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class WorldFurnaceBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(12, ItemStack.EMPTY);

    protected final PropertyDelegate propertyDelegate;
    private int progress = 0;
    private int maxProgress = Configs.myConfig.fuelMax;
    private int zeroProgressTicks = 0; // Timer to count when progress is at 0 for taking damage

    private boolean sentNoFuelMessage = false;
    private int noFuelMessageTicks = 0;
    private int warningMessageTicks = 0;
    private int damageIncreaseTicks = 0;
    private float currentDamage = Configs.myConfig.damage;


    public WorldFurnaceBlockEntity(BlockPos pos, net.minecraft.block.BlockState state) {
        super(ModBlockEntities.WORLD_FURNACE_BLOCK_ENTITY, pos, state);
        this.propertyDelegate = new ArrayPropertyDelegate(2) {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progress = value;
                    case 1 -> maxProgress = value;
                }
            }
        };
    }
    private boolean hasFuelInInventory() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty() && canUseAsFuel(stack)) {
                return true;
            }
        }
        return false;
    }



    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, net.minecraft.network.PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("World furnace");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new WorldFurnaceScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);

        nbt.putInt("world_furnace.progress", progress);
        nbt.putInt("world_furnace.zeroTicks", zeroProgressTicks);
        nbt.putFloat("world_furnace.currentDamage", currentDamage);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);

        progress = nbt.getInt("world_furnace.progress");
        zeroProgressTicks = nbt.getInt("world_furnace.zeroTicks");
        currentDamage = nbt.contains("world_furnace.currentDamage")
                ? nbt.getFloat("world_furnace.currentDamage")
                : Configs.myConfig.damage;
    }


    // ================== FUEL HANDLING ==================

    private static boolean isNonFlammableWood(Item item) {
        return item.getRegistryEntry().isIn(ItemTags.NON_FLAMMABLE_WOOD);
    }

    private static void addFuel(Map<Item, Integer> fuelTimes, TagKey<Item> tag, int fuelTime) {
        for (RegistryEntry<Item> entry : Registries.ITEM.iterateEntries(tag)) {
            if (!isNonFlammableWood(entry.value())) {
                fuelTimes.put(entry.value(), fuelTime);
            }
        }
    }

    private static void addFuel(Map<Item, Integer> fuelTimes, ItemConvertible item, int fuelTime) {
        Item item2 = item.asItem();
        if (isNonFlammableWood(item2)) {
            if (SharedConstants.isDevelopment) {
                throw Util.throwOrPause(new IllegalStateException(
                        "Tried to make fire-resistant item " + item2.getName(null).getString() + " a furnace fuel."
                ));
            }
        } else {
            fuelTimes.put(item2, fuelTime);
        }
    }

    protected int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) return 0;
        return createFuelTimeMap().getOrDefault(fuel.getItem(), 0);
    }

    public static boolean canUseAsFuel(ItemStack stack) {
        return createFuelTimeMap().containsKey(stack.getItem());
    }

    public static Map<Item, Integer> createFuelTimeMap() {
        Map<Item, Integer> map = Maps.newLinkedHashMap();
        addFuel(map, Blocks.COAL_BLOCK, 16000);
        addFuel(map, Items.BLAZE_ROD, 2400);
        addFuel(map, Items.COAL, 1600);
        addFuel(map, Items.CHARCOAL, 1600);
        addFuel(map, ItemTags.LOGS, 300);
        addFuel(map, ItemTags.BAMBOO_BLOCKS, 300);
        addFuel(map, ItemTags.PLANKS, 300);
        addFuel(map, Blocks.BAMBOO_MOSAIC, 300);
        addFuel(map, ItemTags.WOODEN_STAIRS, 300);
        addFuel(map, Blocks.BAMBOO_MOSAIC_STAIRS, 300);
        addFuel(map, ItemTags.WOODEN_SLABS, 150);
        addFuel(map, Blocks.BAMBOO_MOSAIC_SLAB, 150);
        addFuel(map, ItemTags.WOODEN_TRAPDOORS, 300);
        addFuel(map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
        addFuel(map, ItemTags.WOODEN_FENCES, 300);
        addFuel(map, ItemTags.FENCE_GATES, 300);
        addFuel(map, Blocks.NOTE_BLOCK, 300);
        addFuel(map, Blocks.BOOKSHELF, 300);
        addFuel(map, Blocks.CHISELED_BOOKSHELF, 300);
        addFuel(map, Blocks.LECTERN, 300);
        addFuel(map, Blocks.JUKEBOX, 300);
        addFuel(map, Blocks.CHEST, 300);
        addFuel(map, Blocks.TRAPPED_CHEST, 300);
        addFuel(map, Blocks.CRAFTING_TABLE, 300);
        addFuel(map, Blocks.DAYLIGHT_DETECTOR, 300);
        addFuel(map, ItemTags.BANNERS, 300);
        addFuel(map, Items.BOW, 300);
        addFuel(map, Items.FISHING_ROD, 300);
        addFuel(map, Blocks.LADDER, 300);
        addFuel(map, ItemTags.SIGNS, 200);
        addFuel(map, ItemTags.HANGING_SIGNS, 800);
        addFuel(map, Items.WOODEN_SHOVEL, 200);
        addFuel(map, Items.WOODEN_SWORD, 200);
        addFuel(map, Items.WOODEN_HOE, 200);
        addFuel(map, Items.WOODEN_AXE, 200);
        addFuel(map, Items.WOODEN_PICKAXE, 200);
        addFuel(map, ItemTags.WOODEN_DOORS, 200);
        addFuel(map, ItemTags.BOATS, 1200);
        addFuel(map, ItemTags.WOOL, 100);
        addFuel(map, ItemTags.WOODEN_BUTTONS, 100);
        addFuel(map, Items.STICK, 100);
        addFuel(map, ItemTags.SAPLINGS, 100);
        addFuel(map, Items.BOWL, 100);
        addFuel(map, ItemTags.WOOL_CARPETS, 67);
        addFuel(map, Blocks.DRIED_KELP_BLOCK, 4001);
        addFuel(map, Items.CROSSBOW, 300);
        addFuel(map, Blocks.BAMBOO, 50);
        addFuel(map, Blocks.DEAD_BUSH, 100);
        addFuel(map, Blocks.SCAFFOLDING, 50);
        addFuel(map, Blocks.LOOM, 300);
        addFuel(map, Blocks.BARREL, 300);
        addFuel(map, Blocks.CARTOGRAPHY_TABLE, 300);
        addFuel(map, Blocks.FLETCHING_TABLE, 300);
        addFuel(map, Blocks.SMITHING_TABLE, 300);
        addFuel(map, Blocks.COMPOSTER, 300);
        addFuel(map, Blocks.AZALEA, 100);
        addFuel(map, Blocks.FLOWERING_AZALEA, 100);
        addFuel(map, Blocks.MANGROVE_ROOTS, 300);
        return map;
    }

    //ОСНОВНАЯ ЛОГИКА ПЕЧИ
    public void tick(World world, BlockPos pos, net.minecraft.block.BlockState state) {
        if (world.isClient()) return;

        if (isInventoryHasFuel()) {
            zeroProgressTicks = 0;
            noFuelMessageTicks = 0;
            sentNoFuelMessage = false;
        } else {
            decreaseProgress();
        }

        markDirty(world, pos, state);
        if(progress == maxProgress){
            List<PlayerEntity> players = (List<PlayerEntity>) world.getPlayers();
            for (PlayerEntity player : players) {
                player.sendMessage(
                        Text.translatable("worldfurnace.good").formatted(Formatting.GREEN),
                        false
                );
            }
        }
        if (progress <= 0) {
            if (!hasFuelInInventory()) {
                // Count ticks for warning messages
                warningMessageTicks++;
                if (warningMessageTicks >= Configs.myConfig.delayBetweenMessages) {
                    List<PlayerEntity> players = (List<PlayerEntity>) world.getPlayers();

                    // Вставка сообщений
                    String[] keys = {"worldfurnace.warning", "worldfurnace.warning1", "worldfurnace.warning2", "worldfurnace.warning3",};
                    String randomKey = keys[world.random.nextInt(keys.length)];

                    for (PlayerEntity player : players) {
                        player.sendMessage(Text.translatable(randomKey).formatted(Formatting.RED), false);
                    }

                    warningMessageTicks = 0; // reset message timer
                }

                // Damage logic
                noFuelMessageTicks++;
                if (noFuelMessageTicks > Configs.myConfig.startAfterMessage) {
                    zeroProgressTicks++;
                    if (zeroProgressTicks > Configs.myConfig.delayBetweenDamages) {
                        damageNearbyPlayers(world, pos);
                        zeroProgressTicks = 0;
                    }

                    // Track when to increase damage
                    damageIncreaseTicks++;
                    if (damageIncreaseTicks > Configs.myConfig.delayBetweenDamageIncrease) {
                        currentDamage += Configs.myConfig.increaseDamage;
                        damageIncreaseTicks = 0;
                    }
                }
            } else {
                // Reset if fuel is available
                warningMessageTicks = 0;
                noFuelMessageTicks = 0;
                zeroProgressTicks = 0;
                damageIncreaseTicks = 0;
                currentDamage = Configs.myConfig.damage;
            }

            world.setBlockState(pos, state.with(WorldFurnaceBlock.LIT, false), 3);
        } else {
            zeroProgressTicks = 0;
            noFuelMessageTicks = 0;
            warningMessageTicks = 0;
            damageIncreaseTicks = 0;
            currentDamage = Configs.myConfig.damage;

            world.setBlockState(pos, state.with(WorldFurnaceBlock.LIT, true), 3);

            if ((double) progress / maxProgress >= 0.8) {
                castEffects();
            }
        }
    }



    private void castEffects() {
        List<PlayerEntity> players = (List<PlayerEntity>) world.getPlayers();
        for (PlayerEntity player : players) {

            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, Configs.myConfig.fxDuration, 1, false, false, true));

            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, Configs.myConfig.fxDuration, 1, false, false, true));

        }
    }

    private boolean isInventoryHasFuel() {
        if (progress >= maxProgress) return false;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (stack.isEmpty() || !canUseAsFuel(stack)) continue;

            int fuelTime = getFuelTime(stack);
            if (fuelTime <= 0) continue;

            // Skip if fuel would overflow the progress
            if (progress + fuelTime > maxProgress && progress != 0) {
                continue;
            }

            consumeOneFuel(i, stack);
            increaseProgressClamped(fuelTime);
            return true;
        }
        return false;
    }


    private void consumeOneFuel(int slot, ItemStack stack) {
        Item remainderItem = stack.getItem().getRecipeRemainder();

        if (remainderItem != null) {
            if (stack.getCount() == 1) {
                inventory.set(slot, new ItemStack(remainderItem));
            } else {
                stack.decrement(1);
                tryInsertRemainder(new ItemStack(remainderItem));
            }
        } else {
            stack.decrement(1);
        }
    }

    private void tryInsertRemainder(ItemStack remainder) {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).isEmpty()) {
                inventory.set(i, remainder);
                return;
            }
        }
        if (this.world != null && !this.world.isClient) {
            ItemScatterer.spawn(this.world, this.pos, DefaultedList.ofSize(1, remainder));
        }
    }

    private void increaseProgressClamped(int fuel) {
        progress = Math.min(maxProgress, progress + fuel);
    }


    private void decreaseProgress() {
        if (progress > 0) progress--;
    }

    private void damageNearbyPlayers(World world, BlockPos pos) {
        List<PlayerEntity> players = (List<PlayerEntity>) world.getPlayers();
        for (PlayerEntity player : players) {
            player.damage(world.getDamageSources().magic(), currentDamage);
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}