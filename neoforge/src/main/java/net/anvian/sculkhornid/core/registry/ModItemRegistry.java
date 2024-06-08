package net.anvian.sculkhornid.core.registry;

import net.anvian.sculkhornid.Constants;
import net.anvian.sculkhornid.core.item.ModItems;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);

    public static final DeferredItem<Item> SCULKHORN = ITEMS.register("sculkhorn", () -> ModItems.SCULKHORN);
    public static final DeferredItem<Item> SCULKHORN_SONICBOOM = ITEMS.register("sculkhorn_sonicboom", () -> ModItems.SCULKHORN_SONICBOOM);
}
