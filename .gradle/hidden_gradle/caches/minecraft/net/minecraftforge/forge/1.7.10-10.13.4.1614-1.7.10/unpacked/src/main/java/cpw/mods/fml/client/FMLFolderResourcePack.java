package cpw.mods.fml.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.Level;

import javax.imageio.ImageIO;

import net.minecraft.client.resources.FolderResourcePack;

import com.google.common.base.Charsets;

import cpw.mods.fml.common.FMLContainerHolder;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.ModContainer;

public class FMLFolderResourcePack extends FolderResourcePack implements FMLContainerHolder {

    private ModContainer container;

    public FMLFolderResourcePack(ModContainer container)
    {
        super(container.getSource());
        this.container = container;
    }

    @Override
    protected boolean func_110593_b(String p_110593_1_)
    {
        return super.func_110593_b(p_110593_1_);
    }
    @Override
    public String func_130077_b()
    {
        return "FMLFileResourcePack:"+container.getName();
    }
    @Override
    protected InputStream func_110591_a(String resourceName) throws IOException
    {
        try
        {
            return super.func_110591_a(resourceName);
        }
        catch (IOException ioe)
        {
            if ("pack.mcmeta".equals(resourceName))
            {
                FMLLog.log(container.getName(), Level.DEBUG, "Mod %s is missing a pack.mcmeta file, substituting a dummy one", container.getName());
                return new ByteArrayInputStream(("{\n" +
                        " \"pack\": {\n"+
                        "   \"description\": \"dummy FML pack for "+container.getName()+"\",\n"+
                        "   \"pack_format\": 1\n"+
                        "}\n" +
                        "}").getBytes(Charsets.UTF_8));
            }
            else throw ioe;
        }
    }

    @Override
    public BufferedImage func_110586_a() throws IOException
    {
        return ImageIO.read(func_110591_a(container.getMetadata().logoFile));
    }

    @Override
    public ModContainer getFMLContainer()
    {
        return container;
    }

}
