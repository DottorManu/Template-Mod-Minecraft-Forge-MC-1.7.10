
package net.minecraftforge.fluids;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * This is a cellular-automata based finite fluid block implementation.
 * 
 * It is highly recommended that you use/extend this class for finite fluid blocks.
 * 
 * @author OvermindDL1, KingLemming
 * 
 */
public class BlockFluidFinite extends BlockFluidBase
{
    public BlockFluidFinite(Fluid fluid, Material material)
    {
        super(fluid, material);
    }

    @Override
    public int getQuantaValue(IBlockAccess world, int x, int y, int z)
    {
        if (world.func_147439_a(x, y, z).isAir(world, x, y, z))
        {
            return 0;
        }

        if (world.func_147439_a(x, y, z) != this)
        {
            return -1;
        }

        int quantaRemaining = world.func_72805_g(x, y, z) + 1;
        return quantaRemaining;
    }

    @Override
    public boolean func_149678_a(int meta, boolean fullHit)
    {
        return fullHit && meta == quantaPerBlock - 1;
    }

    @Override
    public int getMaxRenderHeightMeta()
    {
        return quantaPerBlock - 1;
    }

    @Override
    public void func_149674_a(World world, int x, int y, int z, Random rand)
    {
        boolean changed = false;
        int quantaRemaining = world.func_72805_g(x, y, z) + 1;

        // Flow vertically if possible
        int prevRemaining = quantaRemaining;
        quantaRemaining = tryToFlowVerticallyInto(world, x, y, z, quantaRemaining);

        if (quantaRemaining < 1)
        {
            return;
        }
        else if (quantaRemaining != prevRemaining)
        {
            changed = true;
            if (quantaRemaining == 1)
            {
                world.func_72921_c(x, y, z, quantaRemaining - 1, 2);
                return;
            }
        }
        else if (quantaRemaining == 1)
        {
            return;
        }

        // Flow out if possible
        int lowerthan = quantaRemaining - 1;
        if (displaceIfPossible(world, x,     y, z - 1)) world.func_147449_b(x,     y, z - 1, Blocks.field_150350_a);
        if (displaceIfPossible(world, x,     y, z + 1)) world.func_147449_b(x,     y, z + 1, Blocks.field_150350_a);
        if (displaceIfPossible(world, x - 1, y, z    )) world.func_147449_b(x - 1, y, z,     Blocks.field_150350_a);
        if (displaceIfPossible(world, x + 1, y, z    )) world.func_147449_b(x + 1, y, z,     Blocks.field_150350_a);
        int north = getQuantaValueBelow(world, x,     y, z - 1, lowerthan);
        int south = getQuantaValueBelow(world, x,     y, z + 1, lowerthan);
        int west  = getQuantaValueBelow(world, x - 1, y, z,     lowerthan);
        int east  = getQuantaValueBelow(world, x + 1, y, z,     lowerthan);
        int total = quantaRemaining;
        int count = 1;

        if (north >= 0)
        {
            count++;
            total += north;
        }

        if (south >= 0)
        {
            count++;
            total += south;
        }

        if (west >= 0)
        {
            count++;
            total += west;
        }

        if (east >= 0)
        {
            ++count;
            total += east;
        }

        if (count == 1)
        {
            if (changed)
            {
                world.func_72921_c(x, y, z, quantaRemaining - 1, 2);
            }
            return;
        }

        int each = total / count;
        int rem = total % count;
        if (north >= 0)
        {
            int newnorth = each;
            if (rem == count || rem > 1 && rand.nextInt(count - rem) != 0)
            {
                ++newnorth;
                --rem;
            }

            if (newnorth != north)
            {
                if (newnorth == 0)
                {
                    world.func_147449_b(x, y, z - 1, Blocks.field_150350_a);
                }
                else
                {
                    world.func_147465_d(x, y, z - 1, this, newnorth - 1, 2);
                }
                world.func_147464_a(x, y, z - 1, this, tickRate);
            }
            --count;
        }

        if (south >= 0)
        {
            int newsouth = each;
            if (rem == count || rem > 1 && rand.nextInt(count - rem) != 0)
            {
                ++newsouth;
                --rem;
            }

            if (newsouth != south)
            {
                if (newsouth == 0)
                {
                    world.func_147449_b(x, y, z + 1, Blocks.field_150350_a);
                }
                else
                {
                    world.func_147465_d(x, y, z + 1, this, newsouth - 1, 2);
                }
                world.func_147464_a(x, y, z + 1, this, tickRate);
            }
            --count;
        }

        if (west >= 0)
        {
            int newwest = each;
            if (rem == count || rem > 1 && rand.nextInt(count - rem) != 0)
            {
                ++newwest;
                --rem;
            }
            if (newwest != west)
            {
                if (newwest == 0)
                {
                    world.func_147449_b(x - 1, y, z, Blocks.field_150350_a);
                }
                else
                {
                    world.func_147465_d(x - 1, y, z, this, newwest - 1, 2);
                }
                world.func_147464_a(x - 1, y, z, this, tickRate);
            }
            --count;
        }

        if (east >= 0)
        {
            int neweast = each;
            if (rem == count || rem > 1 && rand.nextInt(count - rem) != 0)
            {
                ++neweast;
                --rem;
            }

            if (neweast != east)
            {
                if (neweast == 0)
                {
                    world.func_147449_b(x + 1, y, z, Blocks.field_150350_a);
                }
                else
                {
                    world.func_147465_d(x + 1, y, z, this, neweast - 1, 2);
                }
                world.func_147464_a(x + 1, y, z, this, tickRate);
            }
            --count;
        }

        if (rem > 0)
        {
            ++each;
        }
        world.func_72921_c(x, y, z, each - 1, 2);
    }

    public int tryToFlowVerticallyInto(World world, int x, int y, int z, int amtToInput)
    {
        int otherY = y + densityDir;
        if (otherY < 0 || otherY >= world.func_72800_K())
        {
            world.func_147449_b(x, y, z, Blocks.field_150350_a);
            return 0;
        }

        int amt = getQuantaValueBelow(world, x, otherY, z, quantaPerBlock);
        if (amt >= 0)
        {
            amt += amtToInput;
            if (amt > quantaPerBlock)
            {
                world.func_147465_d(x, otherY, z, this, quantaPerBlock - 1, 3);
                world.func_147464_a(x, otherY, z, this, tickRate);
                return amt - quantaPerBlock;
            }
            else if (amt > 0)
            {
                world.func_147465_d(x, otherY, z, this, amt - 1, 3);
                world.func_147464_a(x, otherY, z, this, tickRate);
                world.func_147449_b(x, y, z, Blocks.field_150350_a);
                return 0;
            }
            return amtToInput;
        }
        else
        {
            int density_other = getDensity(world, x, otherY, z);
            if (density_other == Integer.MAX_VALUE)
            {
                if (displaceIfPossible(world, x, otherY, z))
                {
                    world.func_147465_d(x, otherY, z, this, amtToInput - 1, 3);
                    world.func_147464_a(x, otherY, z, this, tickRate);
                    world.func_147449_b(x, y, z, Blocks.field_150350_a);
                    return 0;
                }
                else
                {
                    return amtToInput;
                }
            }

            if (densityDir < 0)
            {
                if (density_other < density) // then swap
                {
                    BlockFluidBase block = (BlockFluidBase)world.func_147439_a(x, otherY, z);
                    int otherData = world.func_72805_g(x, otherY, z);
                    world.func_147465_d(x, otherY, z, this,  amtToInput - 1, 3);
                    world.func_147465_d(x, y,      z, block, otherData, 3);
                    world.func_147464_a(x, otherY, z, this,  tickRate);
                    world.func_147464_a(x, y,      z, block, block.func_149738_a(world));
                    return 0;
                }
            }
            else
            {
                if (density_other > density)
                {
                    BlockFluidBase block = (BlockFluidBase)world.func_147439_a(x, otherY, z);
                    int otherData = world.func_72805_g(x, otherY, z);
                    world.func_147465_d(x, otherY, z, this,  amtToInput - 1, 3);
                    world.func_147465_d(x, y,      z, block, otherData, 3);
                    world.func_147464_a(x, otherY, z, this,  tickRate);
                    world.func_147464_a(x, y,      z, block, block.func_149738_a(world));
                    return 0;
                }
            }
            return amtToInput;
        }
    }

    /* IFluidBlock */
    @Override
    public FluidStack drain(World world, int x, int y, int z, boolean doDrain)
    {
        if (doDrain)
        {
            world.func_147449_b(x, y, z, Blocks.field_150350_a);
        }
        
        return new FluidStack(getFluid(),
                MathHelper.func_76141_d(getQuantaPercentage(world, x, y, z) * FluidContainerRegistry.BUCKET_VOLUME));
    }

    @Override
    public boolean canDrain(World world, int x, int y, int z)
    {
        return true;
    }
}
