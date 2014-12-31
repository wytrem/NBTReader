package net.wytrem.nbtmanager.nbt;


import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class CompressedStreamTools
{
	public static boolean isGZipped(File f)
	{
		int magic = 0;
		
		try
		{
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			magic = raf.read() & 0xff | ((raf.read() << 8) & 0xff00);
			raf.close();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		
		return magic == GZIPInputStream.GZIP_MAGIC;
	}
	
	/**
	 * Load the gzipped compound from the inputstream.
	 */
	public static NBTTagCompound readCompressed(InputStream par0InputStream) throws IOException
	{
		DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(par0InputStream)));
		NBTTagCompound var2;

		try
		{
			var2 = read(var1);
		}
		finally
		{
			var1.close();
		}

		return var2;
	}

	/**
	 * Write the compound, gzipped, to the outputstream.
	 */
	public static void writeCompressed(NBTTagCompound par0NBTTagCompound, OutputStream par1OutputStream) throws IOException
	{
		DataOutputStream var2 = new DataOutputStream(new GZIPOutputStream(par1OutputStream));

		try
		{
			write(par0NBTTagCompound, var2);
		}
		finally
		{
			var2.close();
		}
	}

	/**
	 * Reads from a CompressedStream.
	 */
	public static NBTTagCompound read(DataInput par0DataInput) throws IOException
	{
		NBTBase var1 = NBTBase.readNamedTag(par0DataInput);

		if (var1 instanceof NBTTagCompound)
		{
			return (NBTTagCompound) var1;
		}
		else
		{
			throw new IOException("Root tag must be a named compound tag");
		}
	}

	public static void write(NBTTagCompound par0NBTTagCompound, DataOutput par1DataOutput) throws IOException
	{
		NBTBase.writeNamedTag(par0NBTTagCompound, par1DataOutput);
	}
}
