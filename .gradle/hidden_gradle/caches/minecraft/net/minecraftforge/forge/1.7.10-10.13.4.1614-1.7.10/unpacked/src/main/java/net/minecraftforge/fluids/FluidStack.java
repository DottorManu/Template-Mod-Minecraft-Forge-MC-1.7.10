
package net.minecraftforge.fluids;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.RegistryDelegate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * ItemStack substitute for Fluids.
 *
 * NOTE: Equality is based on the Fluid, not the amount. Use
 * {@link #isFluidStackIdentical(FluidStack)} to determine if FluidID, Amount and NBT Tag are all
 * equal.
 *
 * @author King Lemming, SirSengir (LiquidStack)
 *
 */
public class FluidStack
{
    /**
     * This field will be removed in 1.8. It may be incorrect after a world is loaded. Code should always
     * use {@link #getFluid()} instead. That will always reflect the correct value.
     */
    @Deprecated
    public final Fluid fluid;
    public int amount;
    public NBTTagCompound tag;
    private RegistryDelegate<Fluid> fluidDelegate;

    public FluidStack(Fluid fluid, int amount)
    {
        if (fluid == null)
        {
            FMLLog.bigWarning("Null fluid supplied to fluidstack. Did you try and create a stack for an unregistered fluid?");
            throw new IllegalArgumentException("Cannot create a fluidstack from a null fluid");
        }
        else if (!FluidRegistry.isFluidRegistered(fluid))
        {
            FMLLog.bigWarning("Failed attempt to create a FluidStack for an unregistered Fluid %s (type %s)", fluid.getName(), fluid.getClass().getName());
            throw new IllegalArgumentException("Cannot create a fluidstack from an unregistered fluid");
        }
    	this.fluidDelegate = FluidRegistry.makeDelegate(fluid);
        this.amount = amount;
        this.fluid = fluid;
    }

    public FluidStack(Fluid fluid, int amount, NBTTagCompound nbt)
    {
        this(fluid, amount);

        if (nbt != null)
        {
            tag = (NBTTagCompound) nbt.func_74737_b();
        }
    }

    public FluidStack(FluidStack stack, int amount)
    {
        this(stack.getFluid(), amount, stack.tag);
    }

    // To be removed in 1.8
    @Deprecated
    public FluidStack(int fluidID, int amount)
    {
    	this(FluidRegistry.getFluid(fluidID), amount);
    }

    // To be removed in 1.8
    @Deprecated
    public FluidStack(int fluidID, int amount, NBTTagCompound nbt)
    {
    	this(FluidRegistry.getFluid(fluidID), amount, nbt);
    }

    /**
     * This provides a safe method for retrieving a FluidStack - if the Fluid is invalid, the stack
     * will return as null.
     */
    public static FluidStack loadFluidStackFromNBT(NBTTagCompound nbt)
    {
        if (nbt == null)
        {
            return null;
        }
        String fluidName = nbt.func_74779_i("FluidName");

        if (fluidName == null || FluidRegistry.getFluid(fluidName) == null)
        {
            return null;
        }
        FluidStack stack = new FluidStack(FluidRegistry.getFluid(fluidName), nbt.func_74762_e("Amount"));

        if (nbt.func_74764_b("Tag"))
        {
            stack.tag = nbt.func_74775_l("Tag");
        }
        return stack;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.func_74778_a("FluidName", FluidRegistry.getFluidName(getFluid()));
        nbt.func_74768_a("Amount", amount);

        if (tag != null)
        {
            nbt.func_74782_a("Tag", tag);
        }
        return nbt;
    }

    public final Fluid getFluid()
    {
        return fluidDelegate.get();
    }

    public final int getFluidID()
    {
    	return FluidRegistry.getFluidID(getFluid());
    }

    public String getLocalizedName()
    {
        return this.getFluid().getLocalizedName(this);
    }

    public String getUnlocalizedName()
    {
        return this.getFluid().getUnlocalizedName(this);
    }

    /**
     * @return A copy of this FluidStack
     */
    public FluidStack copy()
    {
        return new FluidStack(getFluid(), amount, tag);
    }

    /**
     * Determines if the FluidIDs and NBT Tags are equal. This does not check amounts.
     *
     * @param other
     *            The FluidStack for comparison
     * @return true if the Fluids (IDs and NBT Tags) are the same
     */
    public boolean isFluidEqual(FluidStack other)
    {
        return other != null && getFluid() == other.getFluid() && isFluidStackTagEqual(other);
    }

    private boolean isFluidStackTagEqual(FluidStack other)
    {
        return tag == null ? other.tag == null : other.tag == null ? false : tag.equals(other.tag);
    }

    /**
     * Determines if the NBT Tags are equal. Useful if the FluidIDs are known to be equal.
     */
    public static boolean areFluidStackTagsEqual(FluidStack stack1, FluidStack stack2)
    {
        return stack1 == null && stack2 == null ? true : stack1 == null || stack2 == null ? false : stack1.isFluidStackTagEqual(stack2);
    }

    /**
     * Determines if the Fluids are equal and this stack is larger.
     *
     * @param other
     * @return true if this FluidStack contains the other FluidStack (same fluid and >= amount)
     */
    public boolean containsFluid(FluidStack other)
    {
        return isFluidEqual(other) && amount >= other.amount;
    }

    /**
     * Determines if the FluidIDs, Amounts, and NBT Tags are all equal.
     *
     * @param other
     *            - the FluidStack for comparison
     * @return true if the two FluidStacks are exactly the same
     */
    public boolean isFluidStackIdentical(FluidStack other)
    {
        return isFluidEqual(other) && amount == other.amount;
    }

    /**
     * Determines if the FluidIDs and NBT Tags are equal compared to a registered container
     * ItemStack. This does not check amounts.
     *
     * @param other
     *            The ItemStack for comparison
     * @return true if the Fluids (IDs and NBT Tags) are the same
     */
    public boolean isFluidEqual(ItemStack other)
    {
        if (other == null)
        {
            return false;
        }

        if (other.func_77973_b() instanceof IFluidContainerItem)
        {
            return isFluidEqual(((IFluidContainerItem) other.func_77973_b()).getFluid(other));
        }

        return isFluidEqual(FluidContainerRegistry.getFluidForFilledItem(other));
    }

    @Override
    public final int hashCode()
    {
    	int code = 1;
    	code = 31*code + getFluid().hashCode();
    	code = 31*code + amount;
    	if (tag != null)
    		code = 31*code + tag.hashCode();
    	return code;
    }

    /**
     * Default equality comparison for a FluidStack. Same functionality as isFluidEqual().
     *
     * This is included for use in data structures.
     */
    @Override
    public final boolean equals(Object o)
    {
        if (!(o instanceof FluidStack))
        {
            return false;
        }

        return isFluidEqual((FluidStack) o);
    }
}
