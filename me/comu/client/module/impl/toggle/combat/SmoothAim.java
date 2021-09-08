package me.comu.client.module.impl.toggle.combat;


import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.EntityHelper;
import me.comu.client.core.Gun;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import me.comu.client.utils.ClientUtils;
import me.comu.client.utils.RotationUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;

public final class SmoothAim extends ToggleableModule {
    private final NumberProperty<Float> reach = new NumberProperty<>(6F, 3F, 10F, 1f, "Reach", "range", "re");
    private final NumberProperty<Integer> fov = new NumberProperty<>(60, 30, 360, 30, "Fov", "view");
    private final NumberProperty<Float> speed = new NumberProperty<>(5.0F, 1.0F, 30.0F, 5.0F, "Rate", "aps", "speed", "r");
    private final Property<Boolean> invis = new Property<>(true, "Invisibles", "invis", "invisible", "i");
    private final EnumProperty<EntityHelper.Location> bone = new EnumProperty<>(EntityHelper.Location.HEAD, "Bone", "b");
    private final Property<Boolean> armor = new Property<>(true, "Nakeds", "Armor", "a", "n", "naked");
    private EntityLivingBase target;
    public EntityPlayer focusedTarget;

    public SmoothAim() {
        super("SmoothAim", new String[]{"SmoothAim", "smooth", "sa", "aimassist", "aa", "aima", "smooth-aim", "aim-assist"}, 0xFFF57F99, ModuleType.COMBAT);
        this.offerProperties(speed, reach, fov, invis, bone, armor);
        this.target = null;
        this.listeners.add(new Listener<MotionUpdateEvent>("smooth_aim_motion_update_listener") {
            @Override
            public void call(MotionUpdateEvent event) {

                if (ClientUtils.player().isEntityAlive()) {
                    for (final Object o : ClientUtils.world().loadedEntityList) {
                        if (o instanceof EntityLivingBase) {
                            final EntityLivingBase entity = (EntityLivingBase) o;
                            if (isEntityValid(focusedTarget))
                            {
                                target =  focusedTarget;
                            } else {
                                if (isEntityValid(entity)) {
                                    target = entity;
                                    final EntityPlayerSP player = ClientUtils.player();
                                    player.rotationPitch += (getPitchChange(target) / speed.getValue());
                                    player.rotationYaw += (getYawChange(target) / speed.getValue());
                                }
                            }
                            if (minecraft.objectMouseOver != null) {
                                if (minecraft.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                                    if (minecraft.objectMouseOver.entityHit instanceof EntityPlayer) {
                                        EntityPlayer entityPlayer = (EntityPlayer) minecraft.objectMouseOver.entityHit;
                                        if (entityPlayer == target) {
                                            target = null;
                                        }
                                    }
                                }
                            }

                        }

                    }

                }
            }
        });
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

    @Override
    protected void onEnable() {
        super.onEnable();
        target = null;
    }


    private boolean isEntityValid(final Entity entity) {
        if (entity instanceof EntityLivingBase) {
            final EntityLivingBase entityLiving = (EntityLivingBase) entity;
            if (entity == minecraft.thePlayer || (entity.isInvisible() && !invis.getValue()) || !ClientUtils.player().isEntityAlive() || !entityLiving.isEntityAlive() || entityLiving.getDistanceToEntity(ClientUtils.player()) > (ClientUtils.player().canEntityBeSeen(entityLiving) ? reach.getValue() : 3.0)) {
                return false;
            }
            if (armor.getValue() && !hasArmor((EntityPlayer) entity)) {
                return false;
            }
            final double x = entity.posX - ClientUtils.player().posX;
            final double z = entity.posZ - ClientUtils.player().posZ;
            final double h = ClientUtils.player().posY + ClientUtils.player().getEyeHeight() - (entity.posY + entity.getEyeHeight());
            final double h2 = Math.sqrt(x * x + z * z);
            final float yaw = (float) (Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
            final float pitch = (float) (Math.atan2(h, h2) * 180.0 / 3.141592653589793);
            final double xDist = RotationUtils.getDistanceBetweenAngles(yaw, ClientUtils.player().rotationYaw % 360.0f);
            final double yDist = RotationUtils.getDistanceBetweenAngles(pitch, ClientUtils.player().rotationPitch % 360.0f);
            final double angleDistance = Math.sqrt(xDist * xDist + yDist * yDist);
            if (angleDistance > fov.getValue()) {
                return false;
            }
            if (entityLiving instanceof EntityPlayer) {
                final EntityPlayer entityPlayer = (EntityPlayer) entityLiving;
                return !(Gun.getInstance().getFriendManager().isFriend(entityPlayer.getName()));
            }
        }
        return false;
    }

    public float getPitchChange(final Entity entity) {
        final double deltaX = entity.posX - ClientUtils.mc().thePlayer.posX;
        final double deltaZ = entity.posZ - ClientUtils.mc().thePlayer.posZ;
        final double deltaY = entity.posY - 2.2 + entity.getEyeHeight() - ClientUtils.mc().thePlayer.posY;
        final double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        final double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        return -MathHelper.wrapAngleTo180_float(ClientUtils.mc().thePlayer.rotationPitch - (float) pitchToEntity) - 2.5f;
    }

    public float getYawChange(final Entity entity) {
        final double deltaX = entity.posX - ClientUtils.mc().thePlayer.posX;
        final double deltaZ = entity.posZ - ClientUtils.mc().thePlayer.posZ;
        double yawToEntity = 0.0;
        if (deltaZ < 0.0 && deltaX < 0.0) {
            yawToEntity = 90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else if (deltaZ < 0.0 && deltaX > 0.0) {
            yawToEntity = -90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else {
            yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }
        return MathHelper.wrapAngleTo180_float(-(ClientUtils.mc().thePlayer.rotationYaw - (float) yawToEntity));
    }


}


