package net.wytrem.nbtmanager.nbt;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class NBTTagList extends NBTBase
{
	/** The array list containing the tags encapsulated in this list. */
	private List<NBTBase> tagList = new ArrayList<NBTBase>();

	/**
	 * The type byte for the tags in the list - they must all be of the same
	 * type.
	 */
	private byte tagType;

	public NBTTagList()
	{
		super("");
	}

	public NBTTagList(String par1Str)
	{
		super(par1Str);
	}

	/**
	 * Write the actual data contents of the tag, implemented in NBT extension
	 * classes
	 */
	void write(DataOutput par1DataOutput) throws IOException
	{
		if (!this.tagList.isEmpty())
		{
			this.tagType = this.tagList.get(0).getId();
		}
		else
		{
			this.tagType = 1;
		}

		par1DataOutput.writeByte(this.tagType);
		par1DataOutput.writeInt(this.tagList.size());

		for (int var2 = 0; var2 < this.tagList.size(); ++var2)
		{
			this.tagList.get(var2).write(par1DataOutput);
		}
	}

	/**
	 * Read the actual data contents of the tag, implemented in NBT extension
	 * classes
	 */
	void load(DataInput par1DataInput, int par2) throws IOException
	{
		if (par2 > 512)
		{
			throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
		}
		else
		{
			this.tagType = par1DataInput.readByte();
			int var3 = par1DataInput.readInt();
			this.tagList = new ArrayList<NBTBase>();

			for (int var4 = 0; var4 < var3; ++var4)
			{
				NBTBase var5 = NBTBase.newTag(this.tagType, (String) null);
				var5.load(par1DataInput, par2 + 1);
				this.tagList.add(var5);
			}
		}
	}

	/**
	 * Gets the type byte for the tag.
	 */
	public byte getId()
	{
		return (byte) 9;
	}

	public String toString()
	{
		return "" + this.tagList.size() + " entries of type " + NBTBase.getTagName(this.tagType);
	}

	/**
	 * Adds the provided tag to the end of the list. There is no check to verify
	 * this tag is of the same type as any previous tag.
	 */
	public void appendTag(NBTBase par1NBTBase)
	{
		this.tagType = par1NBTBase.getId();
		this.tagList.add(par1NBTBase);
	}

	/**
	 * Removes a tag at the given index.
	 */
	public NBTBase removeTag(int par1)
	{
		return this.tagList.remove(par1);
	}

	/**
	 * Retrieves the tag at the specified index from the list.
	 */
	public NBTBase tagAt(int par1)
	{
		return this.tagList.get(par1);
	}

	/**
	 * Returns the number of tags in the list.
	 */
	public int tagCount()
	{
		return this.tagList.size();
	}

	/**
	 * Creates a clone of the tag.
	 */
	public NBTBase copy()
	{
		NBTTagList var1 = new NBTTagList(this.getName());
		var1.tagType = this.tagType;
		Iterator<NBTBase> var2 = this.tagList.iterator();

		while (var2.hasNext())
		{
			NBTBase var3 = var2.next();
			NBTBase var4 = var3.copy();
			var1.tagList.add(var4);
		}

		return var1;
	}

	public boolean equals(Object par1Obj)
	{
		if (super.equals(par1Obj))
		{
			NBTTagList var2 = (NBTTagList) par1Obj;

			if (this.tagType == var2.tagType)
			{
				return this.tagList.equals(var2.tagList);
			}
		}

		return false;
	}

	public int hashCode()
	{
		return super.hashCode() ^ this.tagList.hashCode();
	}
}
