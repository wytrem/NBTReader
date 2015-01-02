package net.wytrem.nbtmanager.gui;


import java.awt.Component;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import net.wytrem.nbtmanager.nbt.CompressedStreamTools;
import net.wytrem.nbtmanager.nbt.NBTBase;
import net.wytrem.nbtmanager.nbt.NBTTagCompound;


public class NBTTree
{
	/**
	 * Le coeur de l'arbre, un {@link JTree}.
	 */
	private JTree tree;

	/**
	 * Le composant que va récupérer la fenêtre pour l'ajouter à son pricipal
	 * conteneur. Permet également de gérer les débordements par des barres de
	 * défilement.
	 */
	private JScrollPane treeView;

	/**
	 * Le {@link NBTTagCompound} qui est affiché par cet objet NBTTree.
	 */
	private NBTTagCompound tag;

	/**
	 * Le fichier qui lui correspont.
	 */
	private File file;

	/**
	 * Crée un nouvel arbre de lecture du fichier NBT passé en argument.
	 */
	public NBTTree(File input)
	{
		file = input;

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
			MainGui.showErrorDialog("Erreur lors de la lecture du fichier : " + e.getMessage());
		}

		String name = tag.getName();

		if (name == null || name.isEmpty())
		{
			name = input.getName();
		}

		DefaultMutableTreeNode top = new DefaultMutableTreeNode(name);
		createNodes(top, tag);
		tree = new JTree(top);

		treeView = new JScrollPane(tree);
	}

	/**
	 * @return Le fichier correspondant à l'arbre affiché.
	 */
	public File getFile()
	{
		return file;
	}

	/**
	 * @return Le {@link NBTTagCompound} correspondant à l'arbre affiché.
	 */
	public NBTTagCompound getTag()
	{
		return tag;
	}
	

	/**
	 * @return Le composant ({@link NBTTree#treeView}) qui devra être ajouté à la fenêtre.
	 */
	public Component getComponent()
	{
		return treeView;
	}

	/**
	 * Ajoute chaque partie du tag passé en argument au neud parent.
	 */
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
}
