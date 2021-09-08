package me.comu.client.module.impl.toggle.world;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.api.minecraft.helper.WorldHelper;
import me.comu.client.core.Gun;
import me.comu.client.events.BlockClickedEvent;
import me.comu.client.events.MiningSpeedEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.toggle.combat.KillAura;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public final class Speedmine extends ToggleableModule
{
    private final NumberProperty<Float> speed = new NumberProperty<>(1.1F, 0.1F, 10F, 0.25F, "Speed", "s");
    private final NumberProperty<Integer> delay = new NumberProperty<>(1, 0, 50, 5, "Delay", "d");
    private final Property<Boolean> fastfall = new Property<>(true, "Fastfall", "ff"), autoTool = new Property<>(true, "AutoTool", "at", "tool");
    private final Property<Boolean> haste = new Property<>(false, "Haste","h");

    public Speedmine()
    {
        super("Speedmine", new String[] {"speedmine", "speedygonzales", "sg", "sm", "fastbreak","mine"}, 0xFFCC846E, ModuleType.WORLD);
        this.offerProperties(speed, fastfall, autoTool, delay, haste);
        this.listeners.add(new Listener<MiningSpeedEvent>("speedy_gonzales_mining_speed_listener")
        {
            @Override
            public void call(MiningSpeedEvent event)
            {
                KillAura killAura = (KillAura) Gun.getInstance().getModuleManager().getModuleByAlias("killaura");

                if (killAura != null && killAura.isRunning())
                {
                    return;
                }

                event.setCanceled(true);
                event.setSpeed(speed.getValue());


            }

        });
        this.listeners.add(new Listener<BlockClickedEvent>("speedy_gonzales_block_clicked_listener")
        {
            @Override
            public void call(BlockClickedEvent event)
            {
                KillAura killAura = (KillAura) Gun.getInstance().getModuleManager().getModuleByAlias("killaura");

                if (killAura != null && killAura.isRunning())
                {
                    return;
                }

                if (fastfall.getValue())
                {
                    if (PlayerHelper.getBlockBelowPlayer(1F).equals(WorldHelper.getBlock(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ())) && minecraft.thePlayer.onGround)
                    {
                        minecraft.thePlayer.motionY--;
                    }
                }

                if (autoTool.getValue())
                {
                    int slot = getBestTool(WorldHelper.getBlock(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ()));

                    if (slot == -1)
                    {
                        return;
                    }

                    if (slot < 9)
                    {
                        minecraft.thePlayer.inventory.currentItem = slot;
                        minecraft.playerController.syncCurrentPlayItem();
                    }
                    else
                    {
                        minecraft.playerController.windowClick(0, slot, minecraft.thePlayer.inventory.currentItem, 2, minecraft.thePlayer);
                    }
                }
            }
        });
    }

    private int getBestTool(Block block)
    {
        int maxStrSlot = -1;
        float hardness = 1F;

        for (int index = 44; index >= 9; index--)
        {
            ItemStack stack = minecraft.thePlayer.inventoryContainer.getSlot(index).getStack();

            if (stack != null)
            {
                float strength = stack.getStrVsBlock(block);

                if (strength > 1F)
                {
                    int efficiencyLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.EFFICIENCY.effectId, stack);
                    strength += (efficiencyLevel * efficiencyLevel + 1);
                }

                if (strength > hardness && strength > 1F)
                {
                    hardness = strength;
                    maxStrSlot = index;
                }
            }
        }

        return maxStrSlot;
    }

    @Override
    protected void onEnable()
    {
        super.onEnable();
        if (minecraft.theWorld != null) {
            minecraft.playerController.blockHitDelay = delay.getValue();
            if (haste.getValue())
                minecraft.thePlayer.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 0, 0));
        }
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();
        minecraft.playerController.blockHitDelay = 5;
        minecraft.thePlayer.removePotionEffect(3);
    }
}
