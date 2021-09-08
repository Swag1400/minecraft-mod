package me.comu.client.module.impl.toggle.combat;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.EntityHelper;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.core.Gun;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.logging.Logger;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.active.render.TextGUI;
import me.comu.client.module.impl.toggle.exploits.FastUse;
import me.comu.client.module.impl.toggle.movement.NoFall;
import me.comu.client.module.impl.toggle.movement.Speed;
import me.comu.client.module.impl.toggle.movement.Step;
import me.comu.client.presets.Preset;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import me.comu.client.utils.ClientUtils;
import me.comu.client.utils.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public final class KillAura extends ToggleableModule {
    private final NumberProperty<Long> aps = new NumberProperty<>(10L, 1L, 20L, 1L, "APS", "d", "delay", "speed");
    ;
    private static final Property<Boolean> autoBlock = new Property<>(true, "Auto-Block", "b", "ab", "block", "autoblock");
    private static final Property<Boolean> pSilent = new Property<>(false, "pSilent", "Psilent");
    private static final Property<Boolean> angle = new Property<>(false, "Angle");
    private static final Property<Boolean> armor = new Property<>(false, "Armor", "ArmorCheck", "naked", "nakeds", "n");
    private static final Property<Boolean> dura = new Property<>(false, "ArmorBreaker", "dura");
    public static final Property<Boolean> team = new Property<>(false, "Team", "t", "teams");
    private static final Property<Boolean> direction = new Property<>(false, "Direction", "dir");
    private static final Property<Boolean> smart = new Property<>(false, "Smart");
    private static final Property<Boolean> rayTrace = new Property<>(true, "Ray-Trace", "raytrace", "rtrace", "trace", "ray");
    private static final Property<Boolean> players = new Property<>(true, "Players", "player", "p", "pl");
    private static final Property<Boolean> animals = new Property<>(true, "Animals", "animal", "a", "an", "ani", "anims", "anim");
    private static final Property<Boolean> monsters = new Property<>(true, "Monsters", "monster", "mon", "m", "mob");
    private static final Property<Boolean> invisibles = new Property<>(true, "Invisibles", "invisible", "invis", "inv");
    private static final Property<Boolean> silent = new Property<>(true, "Silent", "s", "lockview", "lock");
    private static final Property<Boolean> noswing = new Property<>(true, "No-Swing", "noswing", "swing", "no");
    private static final Property<Boolean> hvh = new Property<>(false, "HvH", "hl", "vortex", "hlswap", "swordswap");
    private static final Property<Boolean> print = new Property<>(false, "Print");
    private static final Property<Boolean> friends = new Property<>(true, "Friends", "friend", "fri", "frend", "f");
    private static final Property<Boolean> enemies = new Property<>(true, "Enemies", "enemy", "e", "enem", "enemys");
    private static final Property<Boolean> staff = new Property<>(true, "Staff", "staffs", "staffmember", "staff-member", "st");
    private static final Property<Boolean> sleeping = new Property<>(false, "Sleep", "mineplex", "sleeping", "slp", "sl");
    private static final Property<Boolean> msg = new Property<>(false, "Msg", "message");
    private static final Property<Boolean> fakeBlock = new Property<>(true, "Fake-Block", "fb", "fakeblock", "fblock");
    private static final Property<Boolean> aac = new Property<>(true, "AAC");
    private static final Property<Boolean> smooth = new Property<>(false, "Smooth", "smoothrotations", "smoothrots", "smoothrot", "smoothrotation");
    private static final Property<Boolean> randomizeAPS = new Property<>(false, "Randomize", "randomaps", "randomspeed", "randomizaps", "apsrandom", "random");
    private static final Property<Boolean> randomRotations = new Property<>(false, "Random-Rotations", "randomyaw", "randompitch", "randomrotation", "randomrotations", "randomr");
    private final NumberProperty<Float> reach = new NumberProperty<>(5f, 3.0f, 6.0f, 0.1F, "Reach", "range", "r");
    private final NumberProperty<Integer> fov = new NumberProperty<>(360, 30, 360, 30, "Fov", "f");
    private final NumberProperty<Integer> tickDelay = new NumberProperty<>(487, 0, 1000, 50, "Tick-Delay", "ticks-delay", "ticksdelay", "tickdelay", "tick", "td", "delaytick", "tdelay");
    private final NumberProperty<Integer> livingTicks = new NumberProperty<>(0, 0, 100, 5, "Living-Ticks", "livingticks", "ticks", "lt");
    private static final EnumProperty<Targeting> targeting = new EnumProperty<>(Targeting.SWITCH, "Targeting", "target", "t");
    private final EnumProperty<EntityHelper.Location> bone = new EnumProperty<>(EntityHelper.Location.HEAD, "Bone", "b");
    private static final EnumProperty<Type> type = new EnumProperty<>(Type.AFTER, "Type", "motionmode", "motiontype");
    public final List<Entity> validTargets;
    public Entity focusTarget;
    public Entity target;
    public EntityPlayer lastTarget;
    private final Stopwatch stopwatch;
    private final Stopwatch pS;
    public static boolean shouldCrit;
    public static boolean sendpacket;
    public boolean enemyPriority = true;
    private int switchTime;
    public static int timeCap;
    private float oldYaw;
    private float oldPitch;


    public KillAura() {
        super("KillAura", new String[]{"killaura", "aura", "ka"}, 0xFFBF4B4B, ModuleType.COMBAT);
        KillAura.sendpacket = false;
        KillAura.timeCap = 3;
        this.validTargets = new CopyOnWriteArrayList<>();
        this.target = null;
        this.lastTarget = null;
        this.stopwatch = new Stopwatch();
        this.pS = new Stopwatch();
        this.offerProperties(targeting, print, fov, enemies, friends, staff, autoBlock, direction, players, bone, smart, rayTrace, animals, monsters, invisibles, noswing, aps, silent, reach, livingTicks, team, dura, hvh, armor, angle, smooth, pSilent, tickDelay, type, randomizeAPS, randomRotations);
        this.offsetPresets(new Preset("Legit") {
                               @Override
                               public void onSet() {
                                   aps.setValue(9L);
                                   reach.setValue(3.8F);
                                   livingTicks.setValue(0);
                                   fov.setValue(90);
                                   targeting.setValue(Targeting.SINGLE);
                                   invisibles.setValue(false);
                                   rayTrace.setValue(false);
                                   animals.setValue(false);
                                   direction.setValue(true);
                                   monsters.setValue(false);
                                   autoBlock.setValue(false);
                                   //    minecraft.thePlayer.sendChatMessage("ok im rdy to hack with the legit preset!!!");
                               }
                           }, new Preset("Rage") {
                               @Override
                               public void onSet() {
                                   aps.setValue(14L);
                                   fov.setValue(360);
                                   reach.setValue(6.0F);
                                   livingTicks.setValue(0);
                                   targeting.setValue(Targeting.SWITCH);
                                   rayTrace.setValue(true);
                                   animals.setValue(false);
                                   monsters.setValue(false);
                                   direction.setValue(true);
                                   invisibles.setValue(true);
                                   autoBlock.setValue(true);
                                   //    minecraft.thePlayer.sendChatMessage("ok im rdy to hack with the rage preset!!!");
                               }
                           },
                new Preset("VortexHvH") {
                    @Override
                    public void onSet() {
                        aps.setValue(10L);
                        fov.setValue(360);
                        reach.setValue(3.7F);
                        livingTicks.setValue(0);
                        targeting.setValue(Targeting.SWITCH);
                        rayTrace.setValue(true);
                        direction.setValue(true);
                        animals.setValue(true);
                        monsters.setValue(true);
                        invisibles.setValue(true);
                        autoBlock.setValue(true);
                        Logger.getLogger().printToChat("Switch range to 6 if aura is raping its mom.");
                        //	minecraft.thePlayer.sendChatMessage("ok im rdy to hack with the vortexhvh preset!!!");
                    }
                },
                new Preset("Beaner") {
                    @Override
                    public void onSet() {
                        aps.setValue(8L);
                        fov.setValue(360);
                        reach.setValue(6.0F);
                        livingTicks.setValue(0);
                        targeting.setValue(Targeting.SWITCH);
                        rayTrace.setValue(true);
                        animals.setValue(false);
                        monsters.setValue(false);
                        invisibles.setValue(true);
                        direction.setValue(true);
                        autoBlock.setValue(true);
                        //	minecraft.thePlayer.sendChatMessage("ok im rdy to hack with the beener preset!!!");
                    }
                },
                new Preset("Archon") {
                    @Override
                    public void onSet() {
                        aps.setValue(10L);
                        fov.setValue(360);
                        reach.setValue(6.0f);
                        livingTicks.setValue(0);
                        targeting.setValue(Targeting.SWITCH);
                        rayTrace.setValue(true);
                        animals.setValue(false);
                        direction.setValue(true);
                        monsters.setValue(false);
                        invisibles.setValue(true);
                        autoBlock.setValue(true);
                        //		minecraft.thePlayer.sendChatMessage("ok im rdy to hack with the arkon preset!!!");
                    }
                },
                new Preset("Guardian") {
                    @Override
                    public void onSet() {
                        aps.setValue(10L);
                        animals.setValue(false);
                        monsters.setValue(false);
                        autoBlock.setValue(true);
                        direction.setValue(true);
                        invisibles.setValue(false);
                        rayTrace.setValue(true);
                        reach.setValue(4.0f);
                        livingTicks.setValue(5);
                        targeting.setValue(Targeting.SINGLE);
                        //Speed sp = (Speed) Gun.getInstance().getModuleManager().getModuleByAlias("speed");
                        //sp.getPropertyByAlias("mode");
                        //	sp.equals(Speed.Mode.GUARDIAN);
                    }
                },
                new Preset("AAC") {
                    @Override
                    public void onSet() {
                        aps.setValue(8L);
                        animals.setValue(false);
                        monsters.setValue(false);
                        direction.setValue(true);
                        invisibles.setValue(false);
                        reach.setValue(5.0f);
                        rayTrace.setValue(true); // just made these up i was too lazyy to aktaully make raeal 1s 4/4/18
                    }
                },
                new Preset("Hypixel") {
                    @Override
                    public void onSet() {
                        aps.setValue(9L);
                        animals.setValue(false);
                        monsters.setValue(false);
                        direction.setValue(true);
                        invisibles.setValue(false);
                        reach.setValue(4.3f);
                        rayTrace.setValue(true);
                        Logger.getLogger().printToChat("stupid step got me banned after 40 hours of mining in uhc im so triggered 12/20/17 6:51 am");
                        Step step = (Step) Gun.getInstance().getModuleManager().getModuleByAlias("step");
                        if (step.isRunning()) {
                            step.toggle();
                        }
                    }
                },
                new Preset("Mineplex") {
                    @Override
                    public void onSet() {
                        aps.setValue(14L);
                        fov.setValue(360);
                        reach.setValue(6.0f);
                        livingTicks.setValue(0);
                        targeting.setValue(Targeting.SINGLE);
                        rayTrace.setValue(true);
                        animals.setValue(false);
                        direction.setValue(true);
                        monsters.setValue(false);
                        invisibles.setValue(false);
                        sleeping.setValue(false);
                        autoBlock.setValue(true);
                        AntiBot antibot = (AntiBot) Gun.getInstance().getModuleManager().getModuleByAlias("antibot");

                        if (!antibot.isRunning()) {
                            antibot.toggle();
                            NoFall nofall = (NoFall) Gun.getInstance().getModuleManager().getModuleByAlias("antibot");

                            if (!nofall.isRunning()) {
                                nofall.toggle();
                                Criticals crit3 = (Criticals) Gun.getInstance().getModuleManager().getModuleByAlias("antibot");

                                if (crit3.isRunning()) {
                                    crit3.toggle();
                                    ;
                                    FastUse fast = (FastUse) Gun.getInstance().getModuleManager().getModuleByAlias("antibot");

                                    if (fast.isRunning()) {
                                        fast.toggle();
                                    }
                                }
                            }
                        }
                    }
                },
                new Preset("Cringo") {
                    @Override
                    public void onSet() {
                        aps.setValue(13L);
                        fov.setValue(360);
                        reach.setValue(5.0F);
                        livingTicks.setValue(0);
                        targeting.setValue(Targeting.SWITCH);
                        direction.setValue(true);
                        animals.setValue(true);
                        monsters.setValue(true);
                        invisibles.setValue(true);
                        autoBlock.setValue(true);
                        // 		minecraft.thePlayer.sendChatMessage("ok im rdy to hack with the cringo preset!!!");
                    }
                });
        this.listeners.add(new Listener<MotionUpdateEvent>("kill_aura_motion_update_listener") {
            @Override
            public void call(MotionUpdateEvent event) {
                TextGUI textGUI = (TextGUI) Gun.getInstance().getModuleManager().getModuleByAlias("textgui");
                Property<Boolean> sf = textGUI.getPropertyByAlias("Suffix");
                if (sf.getValue()) {
                    setTag("KillAura \2477" + targeting.getFixedValue());

                }
                switch (event.getTime()) {
                    case BEFORE:
                        if (validTargets.isEmpty()) {
                            for (Object object : minecraft.theWorld.loadedEntityList) {
                                Entity entity = (Entity) object;

                                if (entity instanceof EntityLivingBase) {
                                    EntityLivingBase entityLivingBase = (EntityLivingBase) entity;

                                    if (isValidTarget(entityLivingBase) && !validTargets.contains(entityLivingBase)) {
                                        validTargets.add(entityLivingBase);
                                    }
                                }
                            }
                        } else {

                            validTargets.forEach(target ->
                            {
                                if (!isValidTarget(target)) {
                                    validTargets.remove(target);
                                }
                            });
                        }

                        if (validTargets.isEmpty()) {
                            return;
                        }

                        if (target == null) {
                            if (isValidTarget(focusTarget)) {
                                target = focusTarget;
                            } else {
                                Optional<Entity> entity = validTargets.stream().filter(ent -> PlayerHelper.isAiming(EntityHelper.getRotations(ent)[0], EntityHelper.getRotations(ent)[1], fov.getValue())).filter(ent -> isValidTarget(ent)).min((entity1, entity2) ->
                                {
                                    float entityFOV = PlayerHelper.getFOV(EntityHelper.getRotations(entity1));
                                    float entity2FOV = PlayerHelper.getFOV(EntityHelper.getRotations(entity2));

                                    return Float.compare(entityFOV, entity2FOV);
                                });

                                entity.ifPresent(value -> target = value);
                            }
                        }

                        if (pSilent.getValue()) {
                            oldPitch = minecraft.thePlayer.rotationPitch;
                            oldYaw = minecraft.thePlayer.rotationYaw;

                        }

                        if (direction.getValue() && silent.getValue()) {
                            event.setRotationPitch(90);
                        }

                        if (dura.getValue() && targeting.getValue() == Targeting.TICK && KillAura.this.isRunning()) {
                            shouldCrit = true;
                        } else {
                            shouldCrit = false;
                        }

                        if (autoBlock.getValue() && minecraft.thePlayer.inventory.getCurrentItem() != null) {
                            if (minecraft.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
                                minecraft.thePlayer.setItemInUse(minecraft.thePlayer.inventory.getCurrentItem(), minecraft.thePlayer.inventory.getCurrentItem().getMaxItemUseDuration());
                            }
                        }
                        if (autoBlock.getValue() && minecraft.thePlayer.inventory.getCurrentItem() != null) {
                            if (minecraft.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
                                minecraft.thePlayer.inventory.getCurrentItem().getItemUseAction();

                            }
                        }

                        if (hvh.getValue()) {
                            minecraft.playerController.windowClick(minecraft.thePlayer.inventoryContainer.windowId, 9, minecraft.thePlayer.inventory.currentItem, 2, minecraft.thePlayer);
                            minecraft.playerController.windowClick(minecraft.thePlayer.inventoryContainer.windowId, 18, minecraft.thePlayer.inventory.currentItem, 2, minecraft.thePlayer);
                            minecraft.playerController.windowClick(minecraft.thePlayer.inventoryContainer.windowId, 27, minecraft.thePlayer.inventory.currentItem, 2, minecraft.thePlayer);
                            minecraft.playerController.windowClick(minecraft.thePlayer.inventoryContainer.windowId, 0, minecraft.thePlayer.inventory.currentItem, 2, minecraft.thePlayer);
                        }
                        if (noswing.getValue()) {
                            if (!noswing.getValue()) {
                                minecraft.thePlayer.swingItem();
                            }
                        }


                        if (isValidTarget(target)) {
                            AutoHeal autoHeal = (AutoHeal) Gun.getInstance().getModuleManager().getModuleByAlias("autoheal");
                            AutoHeal2 autoHeal2 = (AutoHeal2) Gun.getInstance().getModuleManager().getModuleByAlias("autopot2");
                            EnumProperty<AutoHeal.Mode> mode = (EnumProperty<AutoHeal.Mode>) autoHeal.getPropertyByAlias("Mode");

                            if (autoHeal != null && autoHeal.isRunning() && mode.getValue() == AutoHeal.Mode.POTION && autoHeal.isPotting()) {
                                return;
                            }

                            if (autoHeal2.isRunning() && autoHeal2.isPotting) {
                                return;
                            }


                            float[] rotations = EntityHelper.getRotationsAtLocation(bone.getValue(), target);
                            event.setLockview(!silent.getValue());
                            if (!silent.getValue()) {
                                if (smooth.getValue()) {
                                    if (event.getRotationYaw() < rotations[0])
                                        event.setRotationYaw(event.getRotationYaw() + 1);
                                    if (event.getRotationPitch() < rotations[1])
                                        event.setRotationPitch(event.getRotationPitch() + 1);
                                }
                            }
                            if (silent.getValue()) {
                                if (pSilent.getValue() && (randomizeAPS.getValue() ? stopwatch.hasCompleted(1000L / ClientUtils.randomNum(aps.getValue().intValue() - 3, aps.getValue().intValue() + 1)) : pS.hasCompleted(1000L / aps.getValue()))) {
                                    if (!sendpacket) {
                                        if (targeting.getValue() == Targeting.AAC) {
                                            // event.setRotationYaw(PlayerHelper.wrapAngleTo180(rotations[0]));
                                            // event.setRotationPitch(PlayerHelper.wrapAngleTo180(rotations[1]));
                                            smoothAim(event);
                                        }
                                        minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(PlayerHelper.wrapAngleTo180(rotations[0]), PlayerHelper.wrapAngleTo180(rotations[1]), true));
                                        attack(target);
                                        System.out.println("test-packet");
                                        validTargets.remove(target);
                                        if (target instanceof EntityPlayer) {
                                            lastTarget = (EntityPlayer) target;
                                        }
                                        target = null;
                                        sendpacket = true;
                                        pS.reset();
                                    }

                                    if (sendpacket) {
                                        if (targeting.getValue() == Targeting.AAC) {
                                            // event.setRotationYaw(PlayerHelper.wrapAngleTo180(oldYaw));
                                            // event.setRotationPitch(PlayerHelper.wrapAngleTo180(oldPitch));
                                            smoothAim(event);
                                        }
                                        minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(oldYaw, oldPitch, false));
                                        System.out.println(sendpacket);
                                        sendpacket = false;
                                        pS.reset();
                                    }
                                } else if (!pSilent.getValue()) {
                                    if (randomRotations.getValue()) {
                                        int randomYaw = new Random().nextInt(10);
                                        int randomPitch = new Random().nextInt(10);
                                        event.setRotationYaw(PlayerHelper.wrapAngleTo180(rotations[0] + randomYaw));
                                        event.setRotationPitch(PlayerHelper.wrapAngleTo180(rotations[1] + randomPitch));
                                    } else {
                                        event.setRotationYaw(PlayerHelper.wrapAngleTo180(rotations[0]));
                                        event.setRotationPitch(PlayerHelper.wrapAngleTo180(rotations[1]));
                                    }
                                }
                            } else {
                                if (randomRotations.getValue()) {
                                    int randomYaw = new Random().nextInt(10);
                                    int randomPitch = new Random().nextInt(10);
                                    minecraft.thePlayer.rotationYaw = PlayerHelper.wrapAngleTo180(rotations[0] + randomYaw);
                                    minecraft.thePlayer.rotationPitch = PlayerHelper.wrapAngleTo180(rotations[1] + randomPitch);
                                } else {
                                    minecraft.thePlayer.rotationYaw = PlayerHelper.wrapAngleTo180(rotations[0]);
                                    minecraft.thePlayer.rotationPitch = PlayerHelper.wrapAngleTo180(rotations[1]);
                                }
                            }
                        } else {
                            validTargets.remove(target);
                            if (target instanceof EntityPlayer) {
                                lastTarget = (EntityPlayer) target;
                            }
                            target = null;
                        }
                        if (type.getValue() == Type.BEFORE) {
                            switch (targeting.getValue()) {
                                case SINGLE:
                                    int randomAps = 1000 / ClientUtils.randomNum(aps.getValue().intValue() - 3, aps.getValue().intValue() + 1);
                                    if (isValidTarget(target) && (randomizeAPS.getValue() ? stopwatch.hasCompleted(randomAps) : stopwatch.hasCompleted(1000L / aps.getValue()))) {
                                            attack(target);

                                        if (angle.getValue()) {
                                            attack(target);
                                        }
                                        if (target instanceof EntityPlayer) {
                                            lastTarget = (EntityPlayer) target;
                                        }
                                        target = null;
                                        stopwatch.reset();
                                    }
                                    break;


                                case TICK:
                                    if (isValidTarget(target) && stopwatch.hasCompleted(tickDelay.getValue())) /*487*/ {
                                        if (dura.getValue()) {
                                            minecraft.playerController.windowClick(minecraft.thePlayer.inventoryContainer.windowId, 9, minecraft.thePlayer.inventory.currentItem, 2, minecraft.thePlayer);
                                            attack(target);
                                            minecraft.playerController.windowClick(minecraft.thePlayer.inventoryContainer.windowId, 9, minecraft.thePlayer.inventory.currentItem, 2, minecraft.thePlayer);
                                            attack(target);
                                            final ItemStack[] items = target.getInventory();
                                            if (print.getValue()) {
                                                if (items[3] != null) {
                                                    ItemStack helm;
                                                    helm = items[3];
                                                    Logger.getLogger().printToChat(target.getName() + "'s helmet durability is " + (helm.getMaxDamage() - helm.getItemDamage()));
                                                }
                                            }
                                        } else {
                                            attack(target);
                                        }
                                        validTargets.remove(target);
                                        if (target instanceof EntityPlayer) {
                                            lastTarget = (EntityPlayer) target;
                                        }
                                        target = null;
                                        stopwatch.reset();
                                    }
                                    break;
                                case SINGLETICK:
                                    if (isValidTarget(target) && stopwatch.hasCompleted(tickDelay.getValue())) /*487*/ {
                                        if (dura.getValue()) {
                                            minecraft.playerController.windowClick(minecraft.thePlayer.inventoryContainer.windowId, 9, minecraft.thePlayer.inventory.currentItem, 2, minecraft.thePlayer);
                                            attack(target);
                                            minecraft.playerController.windowClick(minecraft.thePlayer.inventoryContainer.windowId, 9, minecraft.thePlayer.inventory.currentItem, 2, minecraft.thePlayer);
                                            attack(target);
                                            final ItemStack[] items = target.getInventory();
                                            if (print.getValue()) {
                                                if (items[3] != null) {
                                                    ItemStack helm = null;
                                                    helm = items[3];
                                                    Logger.getLogger().printToChat(target.getName() + "'s helmet durability is " + String.valueOf(helm.getMaxDamage() - helm.getItemDamage()));
                                                }
                                            }
                                        } else {
                                            attack(target);
                                        }
                                        if (target instanceof EntityPlayer) {
                                            lastTarget = (EntityPlayer) target;
                                        }
                                        target = null;
                                        stopwatch.reset();
                                    }
                                    break;

                                case SWITCH:
                                    if (isValidTarget(target) && (randomizeAPS.getValue() ? stopwatch.hasCompleted(1000L / ClientUtils.randomNum(aps.getValue().intValue() - 3, aps.getValue().intValue() + 1)) : stopwatch.hasCompleted(1000L / aps.getValue()))) {

                                        attack(target);
                                        if (angle.getValue()) {
                                            attack(target);
                                        }


                                        validTargets.remove(target);
                                        if (target instanceof EntityPlayer) {
                                            lastTarget = (EntityPlayer) target;
                                        }
                                        target = null;
                                        stopwatch.reset();
                                    }

                                    break;
                                case AAC:
                                    aac.setValue(true);
                                    if (isValidTarget(target) && (randomizeAPS.getValue() ? stopwatch.hasCompleted(1000L / ClientUtils.randomNum(aps.getValue().intValue() - 3, aps.getValue().intValue() + 1)) : stopwatch.hasCompleted(1000L / aps.getValue()))) {
                                        attack(target);
                                        if (angle.getValue()) {
                                            attack(target);
                                        }
                                        if (target instanceof EntityPlayer) {
                                            lastTarget = (EntityPlayer) target;
                                        }
                                        target = null;
                                        stopwatch.reset();

                                    }
                                    aac.setValue(false);
                                    break;

                                case TEST:
                                    if (isValidTarget(target) && (randomizeAPS.getValue() ? stopwatch.hasCompleted(1000L / ClientUtils.randomNum(aps.getValue().intValue() - 3, aps.getValue().intValue() + 1)) : stopwatch.hasCompleted(1000L / aps.getValue()))) {
                                        attack(target);
                                        if (angle.getValue()) {
                                            attack(target);
                                        }
                                        // attack(target);
                                        minecraft.func_175102_a().addToSendQueue(new C02PacketUseEntity(null, C02PacketUseEntity.Action.ATTACK));
                                        if (target instanceof EntityPlayer) {
                                            lastTarget = (EntityPlayer) target;
                                        }
                                        target = null;
                                        stopwatch.reset();

                                    }

                                    break;
                                case NULL:

                                    break;
                                case VANILLA:
                                    if (isValidTarget(target) && (randomizeAPS.getValue() ? stopwatch.hasCompleted(1000L / ClientUtils.randomNum(aps.getValue().intValue() - 3, aps.getValue().intValue() + 1)) : stopwatch.hasCompleted(1000L / aps.getValue()))) {
                                        if (stopwatch.hasCompleted(ClientUtils.player().getLastAttackerTime())) {
                                            attack(target);
                                            //stopwatch.reset();
                                        }

                                        if (angle.getValue()) {
                                            attack(target);
                                        }

                                        validTargets.remove(target);
                                        if (target instanceof EntityPlayer) {
                                            lastTarget = (EntityPlayer) target;
                                        }
                                        target = null;
                                        stopwatch.reset();
                                    }
                            }

                        }

                        break;

                    case AFTER:

                        AutoHeal autoHeal = (AutoHeal) Gun.getInstance().getModuleManager().getModuleByAlias("autoheal");
                        EnumProperty<AutoHeal.Mode> mode = (EnumProperty<AutoHeal.Mode>) autoHeal.getPropertyByAlias("Mode");
                        if (autoHeal != null && autoHeal.isRunning() && (mode.getValue() == AutoHeal.Mode.POTION && autoHeal.isPotting() || mode.getValue() == AutoHeal.Mode.SOUP && autoHeal.isSouping())) {
                            return;
                        }

                        if (pSilent.getValue()) {
                            return;
                        }
                        if (type.getValue() == Type.AFTER) {
                            switch (targeting.getValue()) {
                                case SINGLE:
                                    int randomAps = 1000 / ClientUtils.randomNum(aps.getValue().intValue() - 3, aps.getValue().intValue() + 1);
                                    if (isValidTarget(target) && (randomizeAPS.getValue() ? stopwatch.hasCompleted(randomAps) : stopwatch.hasCompleted(1000L / aps.getValue()))) {
//                                    Logger.getLogger().printToChat("Attacking with speed: " + randomAps);
                                        attack(target);

                                        if (angle.getValue()) {
                                            attack(target);
                                        }
                                        if (target instanceof EntityPlayer) {
                                            lastTarget = (EntityPlayer) target;
                                        }
                                        target = null;
                                        stopwatch.reset();
                                    }
                                    break;


                                case TICK:
                                    if (isValidTarget(target) && stopwatch.hasCompleted(tickDelay.getValue())) /*487*/ {
                                        if (dura.getValue()) {
                                            minecraft.playerController.windowClick(minecraft.thePlayer.inventoryContainer.windowId, 9, minecraft.thePlayer.inventory.currentItem, 2, minecraft.thePlayer);
                                            attack(target);
                                            minecraft.playerController.windowClick(minecraft.thePlayer.inventoryContainer.windowId, 9, minecraft.thePlayer.inventory.currentItem, 2, minecraft.thePlayer);
                                            attack(target);
                                            final ItemStack[] items = target.getInventory();
                                            if (print.getValue()) {
                                                if (items[3] != null) {
                                                    ItemStack helm;
                                                    helm = items[3];
                                                    Logger.getLogger().printToChat(target.getName() + "'s helmet durability is " + (helm.getMaxDamage() - helm.getItemDamage()));
                                                }
                                            }
                                        } else {
                                            attack(target);
                                        }
                                        validTargets.remove(target);
                                        if (target instanceof EntityPlayer) {
                                            lastTarget = (EntityPlayer) target;
                                        }
                                        target = null;
                                        stopwatch.reset();
                                    }
                                    break;
                                case SINGLETICK:
                                    if (isValidTarget(target) && stopwatch.hasCompleted(tickDelay.getValue())) /*487*/ {
                                        if (dura.getValue()) {
                                            minecraft.playerController.windowClick(minecraft.thePlayer.inventoryContainer.windowId, 9, minecraft.thePlayer.inventory.currentItem, 2, minecraft.thePlayer);
                                            attack(target);
                                            minecraft.playerController.windowClick(minecraft.thePlayer.inventoryContainer.windowId, 9, minecraft.thePlayer.inventory.currentItem, 2, minecraft.thePlayer);
                                            attack(target);
                                            final ItemStack[] items = target.getInventory();
                                            if (print.getValue()) {
                                                if (items[3] != null) {
                                                    ItemStack helm = null;
                                                    helm = items[3];
                                                    Logger.getLogger().printToChat(target.getName() + "'s helmet durability is " + String.valueOf(helm.getMaxDamage() - helm.getItemDamage()));
                                                }
                                            }
                                        } else {
                                            attack(target);
                                        }
                                        if (target instanceof EntityPlayer) {
                                            lastTarget = (EntityPlayer) target;
                                        }
                                        target = null;
                                        stopwatch.reset();
                                    }
                                    break;

                                case SWITCH:
                                    if (isValidTarget(target) && (randomizeAPS.getValue() ? stopwatch.hasCompleted(1000L / ClientUtils.randomNum(aps.getValue().intValue() - 3, aps.getValue().intValue() + 1)) : stopwatch.hasCompleted(1000L / aps.getValue()))) {

                                        attack(target);
                                        if (angle.getValue()) {
                                            attack(target);
                                        }


                                        validTargets.remove(target);
                                        if (target instanceof EntityPlayer) {
                                            lastTarget = (EntityPlayer) target;
                                        }
                                        target = null;
                                        stopwatch.reset();
                                    }

                                    break;
                                case AAC:
                                    aac.setValue(true);
                                    if (isValidTarget(target) && (randomizeAPS.getValue() ? stopwatch.hasCompleted(1000L / ClientUtils.randomNum(aps.getValue().intValue() - 3, aps.getValue().intValue() + 1)) : stopwatch.hasCompleted(1000L / aps.getValue()))) {
                                        attack(target);
                                        if (angle.getValue()) {
                                            attack(target);
                                        }
                                        if (target instanceof EntityPlayer) {
                                            lastTarget = (EntityPlayer) target;
                                        }
                                        target = null;
                                        stopwatch.reset();

                                    }
                                    aac.setValue(false);
                                    break;

                                case TEST:
                                    if (isValidTarget(target) && (randomizeAPS.getValue() ? stopwatch.hasCompleted(1000L / ClientUtils.randomNum(aps.getValue().intValue() - 3, aps.getValue().intValue() + 1)) : stopwatch.hasCompleted(1000L / aps.getValue()))) {
                                        attack(target);
                                        if (angle.getValue()) {
                                            attack(target);
                                        }
                                        // attack(target);
                                        minecraft.func_175102_a().addToSendQueue(new C02PacketUseEntity(null, C02PacketUseEntity.Action.ATTACK));
                                        if (target instanceof EntityPlayer) {
                                            lastTarget = (EntityPlayer) target;
                                        }
                                        target = null;
                                        stopwatch.reset();

                                    }

                                    break;
                                case NULL:

                                    break;
                                case VANILLA:
                                    if (isValidTarget(target) && (randomizeAPS.getValue() ? stopwatch.hasCompleted(1000L / ClientUtils.randomNum(aps.getValue().intValue() - 3, aps.getValue().intValue() + 1)) : stopwatch.hasCompleted(1000L / aps.getValue()))) {
                                        if (stopwatch.hasCompleted(ClientUtils.player().getLastAttackerTime())) {
                                            attack(target);
                                            //stopwatch.reset();
                                        }

                                        if (angle.getValue()) {
                                            attack(target);
                                        }

                                        validTargets.remove(target);
                                        if (target instanceof EntityPlayer) {
                                            lastTarget = (EntityPlayer) target;
                                        }
                                        target = null;
                                        stopwatch.reset();
                                    }
                            }
                        }
                        validTargets.forEach(target ->
                        {
                            if (!isValidTarget(target)) {
                                validTargets.remove(target);
                            }
                        });
                        break;
                }
            }
        });
//        this.listeners.add(new Listener<EntityKillEvent>("entity_kill_event") {
//            @Override
//            public void call(EntityKillEvent event) {
//                minecraft.thePlayer.sendChatMessage("Killed " + getEvent().getName());
//            }
//        });
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        validTargets.clear();
        target = null;
        lastTarget = null;
        shouldCrit = false;
    }

    private static void attack(Entity entity) {
        boolean wasSprinting = minecraft.thePlayer.isSprinting();
        boolean wasSneaking = minecraft.thePlayer.isSneaking();
        boolean wasBlocking = minecraft.thePlayer.isBlocking();

        if (wasSprinting) {
            minecraft.func_175102_a().addToSendQueue(new C0BPacketEntityAction(minecraft.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
        }

        if (wasSneaking) {
            minecraft.func_175102_a().addToSendQueue(new C0BPacketEntityAction(minecraft.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        }

        if (wasBlocking) {
            minecraft.func_175102_a().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }

        minecraft.thePlayer.swingItem();

        if (dura.getValue() && (targeting.getValue() == Targeting.TICK || targeting.getValue() == Targeting.SINGLETICK)) {
            minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer(true));
        }
        minecraft.func_175102_a().addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));

// this is so we aren't sending extra block and packet stuff.
        if (dura.getValue() && (targeting.getValue() == Targeting.TICK || targeting.getValue() == Targeting.SINGLETICK)) {
            minecraft.thePlayer.swingItem();
            Speed speed = (Speed) Gun.getInstance().getModuleManager().getModuleByAlias("speed");
            EnumProperty<Speed.Mode> speedMode = (EnumProperty<Speed.Mode>) speed.getPropertyByAlias("Mode");

            if (speed != null && speed.isRunning() && speedMode.getValue() == Speed.Mode.YPORT) {
                return;
            } else {
                Criticals.crit();
            }

            minecraft.func_175102_a().addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
        }


        if (wasBlocking) {
            minecraft.func_175102_a().addToSendQueue(new C08PacketPlayerBlockPlacement(minecraft.thePlayer.getCurrentEquippedItem()));
        }

        if (wasSprinting) {
            minecraft.func_175102_a().addToSendQueue(new C0BPacketEntityAction(minecraft.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
        }

        if (wasSneaking) {
            minecraft.func_175102_a().addToSendQueue(new C0BPacketEntityAction(minecraft.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
        }
    }

    private boolean isValidTarget(Entity entity) {
        EntityLivingBase entityLivingBase = (EntityLivingBase) entity;

        if ((entity == null) || (!rayTrace.getValue() && !minecraft.thePlayer.canEntityBeSeen(entity)) || entity.isDead || !entity.isEntityAlive() || (minecraft.thePlayer.getDistanceToEntity(entity) > reach.getValue()) || (entity.ticksExisted < livingTicks.getValue())) {
            return false;
        }

        if (sleeping.getValue() && minecraft.thePlayer.isPlayerSleeping()) {
            return false;
        }

        if (team.getValue() && ClientUtils.isTeam(minecraft.thePlayer, (EntityPlayer) entityLivingBase)) {
            return false;
        }

        if (players.getValue() && entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer) entity;

            if (armor.getValue() && !hasArmor(entityPlayer)) {
                return false;
            }

            if (friends.getValue() && Gun.getInstance().getFriendManager().isFriend(entityPlayer.getName())) {
                return false;
            }

            return !entityPlayer.equals(minecraft.thePlayer) && !(smart.getValue() && !entityPlayer.onGround && entityPlayer.fallDistance == 0D) && !((!invisibles.getValue() && entityPlayer.isInvisible()) || entityPlayer.capabilities.isCreativeMode);
        }
        if (enemyPriority && Gun.getInstance().getEnemyManager().isEnemy(entity.getName())) {
            return true;
        }

        return (animals.getValue() && (entity instanceof EntityAnimal) || (animals.getValue() && (entity instanceof EntityVillager)) || (animals.getValue() && (entity instanceof EntitySquid)) || (animals.getValue() && (entity instanceof EntityWolf)) || (animals.getValue() && (entity instanceof EntityBat)) || (monsters.getValue() && (entity instanceof EntityMob) || (monsters.getValue() && (entity instanceof EntityMagmaCube)) || (monsters.getValue() && (entity instanceof EntityDragon)) || (monsters.getValue() && (entity instanceof EntityGhast)) || (monsters.getValue() && (entity instanceof EntitySlime)) || monsters.getValue() && (entity instanceof EntityIronGolem)));
    }

    private long switchTime() {
        if (validTargets.size() == 1) {
            switchTime = 500;
        } else if (validTargets.size() > 1) {
            if (dura.getValue()) {
                switchTime = 500;
            } else {
                switchTime = 250;
            }
        }

        return switchTime;
    }


    private boolean hasArmor(EntityPlayer player) {
        ItemStack boots = player.inventory.armorInventory[0];
        ItemStack pants = player.inventory.armorInventory[1];
        ItemStack chest = player.inventory.armorInventory[2];
        ItemStack head = player.inventory.armorInventory[3];

        if ((boots != null) || (pants != null) || (chest != null) || (head != null)) {
            return true;
        }

        return false;
    }

    private void smoothAim(final MotionUpdateEvent em) {
        final double randomYaw = 0.05;
        final double randomPitch = 0.05;
        final float targetYaw = RotationUtils.getYawChange(minecraft.thePlayer.rotationYaw, this.target.posX + ClientUtils.randomNumber(1, -1) * randomYaw, this.target.posZ + ClientUtils.randomNumber(1, -1) * randomYaw);
        final float yawFactor = targetYaw / 1.7f;
        em.setRotationYaw(minecraft.thePlayer.rotationYaw + yawFactor);
        this.target.rotationYaw += yawFactor;
        final float targetPitch = RotationUtils.getPitchChange(minecraft.thePlayer.rotationPitch, this.target, this.target.posY + ClientUtils.randomNumber(1, -1) * randomPitch);
        final float pitchFactor = targetPitch / 1.7f;
        em.setRotationPitch(minecraft.thePlayer.rotationPitch + pitchFactor);
        minecraft.thePlayer.rotationPitch += pitchFactor;
    }

    private enum Targeting {
        SINGLE, SWITCH, TICK, VANILLA, AAC, NULL, TEST, SINGLETICK
    }

    private enum Type {
        BEFORE, AFTER
    }
}
