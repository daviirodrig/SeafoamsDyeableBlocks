package seafoamwolf.seafoamsdyeableblocks;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import seafoamwolf.seafoamsdyeableblocks.block.DyeableBlocks;
import seafoamwolf.seafoamsdyeableblocks.item.DyeableItems;
import seafoamwolf.seafoamsdyeableblocks.tab.SeafoamsDyeableBlocksItemTab;
import seafoamwolf.seafoamsdyeableblocks.block.DyeableBlockEntity;
import seafoamwolf.seafoamsdyeableblocks.item.DyeableBlockItem;
import seafoamwolf.seafoamsdyeableblocks.item.DyedItem;

import static net.minecraft.world.item.DyeableLeatherItem.TAG_COLOR;
import static net.minecraft.world.item.DyeableLeatherItem.TAG_DISPLAY;


@Mod(SeafoamsDyeableBlocks.MODID)
public class SeafoamsDyeableBlocks {
    public static final String MODID = "seafoamsdyeableblocks";

    public static final SeafoamsDyeableBlocksItemTab ITEM_TAB = new SeafoamsDyeableBlocksItemTab(CreativeModeTab.TABS.length, "dyeable_blocks_item_tab");

    public static List<Block> DYEABLE_BLOCKS = new ArrayList<>();
    public static List<DyeableBlockItem> DYEABLE_BLOCK_ITEMS = new ArrayList<>();
    public static List<DyedItem> DYEABLE_ITEMS = new ArrayList<>();

    public SeafoamsDyeableBlocks() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        DyeableBlocks.BLOCKS.register(modEventBus);
        DyeableBlocks.BLOCK_ENTITY_TYPES.register(modEventBus);
        DyeableItems.ITEMS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        DyeableItems.register();
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ColorRegisterHandler {
        @SubscribeEvent
        public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
            DYEABLE_BLOCKS.forEach(block -> event.register((state, blockAndTintGetter, pos, tintIndex) -> {
                if (blockAndTintGetter == null)
                    return 0;

                BlockEntity blockEntity = blockAndTintGetter.getBlockEntity(pos);

                if (blockEntity != null && blockEntity instanceof DyeableBlockEntity)
                    return ((DyeableBlockEntity) blockEntity).getColor();

                return 16777215;
            }, block));
        }

        @SubscribeEvent
        public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            DYEABLE_BLOCK_ITEMS.forEach(blockItem -> event.register((stack, tintIndex) -> {
                return ((DyeableBlockItem) stack.getItem()).getColor(stack);
            }, blockItem));

            DYEABLE_ITEMS.forEach(item -> event.register((stack, layer) -> {
                return (layer == 1 ? ((DyedItem) stack.getItem()).getColor(stack) : 16777215);
            }, item));
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    private static class ForgeBus {
        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            event.getDispatcher().register(Commands.literal("paintbrush")
                    .then(Commands.argument("hexColorCode", StringArgumentType.string())
                            .executes((ctx) -> {
                                String hex = StringArgumentType.getString(ctx, "hexColorCode");
                                if (hex.charAt(0) == '#') {
                                    hex = hex.substring(1);
                                }

                                int decimalColor = Integer.parseInt(hex, 16);

                                ItemStack item = new ItemStack(DyeableItems.NETHERITE_PAINTBRUSH.get());
                                CompoundTag nbtCompound = item.getOrCreateTagElement(TAG_DISPLAY);
                                nbtCompound.putInt(TAG_COLOR, decimalColor);

                                Objects.requireNonNull(ctx.getSource().getPlayer()).addItem(item);
                                return 1;
                            })));
        }
    }
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class BlockInit {

        @SubscribeEvent
        public static void onRegisterItems(final RegisterEvent event) {
            if (event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)) {
                DyeableBlocks.BLOCKS.getEntries().forEach((blockRegistryObject) -> {
                    Block block = blockRegistryObject.get();
                    Item.Properties properties = new Item.Properties().tab(ITEM_TAB);
                    DyeableBlockItem item = new DyeableBlockItem(block, properties);
                    event.register(ForgeRegistries.Keys.ITEMS, blockRegistryObject.getId(), () -> item);
                    DYEABLE_BLOCK_ITEMS.add(item);
                });
            }
        }
    }
}
