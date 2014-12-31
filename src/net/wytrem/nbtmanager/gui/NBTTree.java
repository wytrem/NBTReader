package net.wytrem.nbtmanager.gui;


import java.awt.Component;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import net.wytrem.nbtmanager.nbt.CompressedStreamTools;
import net.wytrem.nbtmanager.nbt.NBTBase;
import net.wytrem.nbtmanager.nbt.NBTTagCompound;


public class NBTTree
{
	private JTree tree;
	private JScrollPane treeView;

	private NBTTagCompound tag;

	public NBTTree(File input)
	{
		try
		{
			DataInputStream stream = new DataInputStream(new FileInputStream(input));

			if (CompressedStreamTools.isGZipped(input))
			{
				tag = CompressedStreamTools.readCompressed(stream);
			}
			else
			{
				tag = CompressedStreamTools.read(stream);
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Erreur lors de la lecture du fichier : " + e.getMessage(), "Erreur !", JOptionPane.ERROR_MESSAGE);
		}

		String name = input.getName();

		DefaultMutableTreeNode top = new DefaultMutableTreeNode(name);
		createNodes(top, tag);
		tree = new JTree(top);

		treeView = new JScrollPane(tree);
	}
	
	public NBTTagCompound getTag()
	{
		return tag;
	}

	private void createNodes(DefaultMutableTreeNode parent, NBTTagCompound tag)
	{
		if (null == tag)
		{
			return;
		}

		Map<String, NBTBase> map = NBTTagCompound.getTagMap(tag);

		Iterator<Entry<String, NBTBase>> iterator = map.entrySet().iterator();

		while (iterator.hasNext())
		{
			Entry<String, NBTBase> entry = iterator.next();

			NBTBase base = entry.getValue();

			if (base instanceof NBTTagCompound)
			{
				DefaultMutableTreeNode category = new DefaultMutableTreeNode(entry.getKey());

				createNodes(category, (NBTTagCompound) base);

				parent.add(category);
			}
			else
			{
				parent.add(new DefaultMutableTreeNode(entry.getKey() + " : " + base.toString()));
			}
		}
	}

	public Component getComponent()
	{
		return treeView;
	}
}
