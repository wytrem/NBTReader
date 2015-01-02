package net.wytrem.nbtmanager.gui;


import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

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
	
	public final MainGui parent;

	/**
	 * Crée un nouvel arbre de lecture du fichier NBT passé en argument.
	 */
	public NBTTree(File input, MainGui mainGui)
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
			DialogUtils.showErrorDialog("Erreur lors de la lecture du fichier : " + e.getMessage());
		}

		String name = tag.getName();

		if (name == null || name.isEmpty())
		{
			name = input.getName();
		}

		DefaultMutableTreeNode top = new DefaultMutableTreeNode(name);
		createNodes(top, tag);
		tree = new JTree(top);

		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e)
			{
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1)
				{
					if (SwingUtilities.isRightMouseButton(e))
					{
						doPopMenu(e, selRow, selPath);
					}
				}
			}
		};
		
		tree.addMouseListener(ml);

		treeView = new JScrollPane(tree);
		parent = mainGui;
	}
	
	/**
	 * Appelé lors du double clic sur un élément de l'arbre.
	 * @param selRow
	 * @param selPath
	 */
	protected void doPopMenu(MouseEvent e, int selRow, TreePath selPath)
	{
		NBTPopUpMenu menu = new NBTPopUpMenu(this, selPath);
        menu.show(e.getComponent(), e.getX(), e.getY());
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
	 * @return Le composant ({@link NBTTree#treeView}) qui devra être ajouté à
	 *         la fenêtre.
	 */
	public Component getComponent()
	{
		return treeView;
	}
	
	public JTree getTree()
	{
		return tree;
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

		Iterator<NBTBase> iterator = tag.getTags().iterator();

		while (iterator.hasNext())
		{
			NBTBase base = iterator.next();

			if (base instanceof NBTTagCompound)
			{
				DefaultMutableTreeNode category = new DefaultMutableTreeNode(new NBTNode(base));

				createNodes(category, (NBTTagCompound) base);

				parent.add(category);
			}
			else
			{
				parent.add(new DefaultMutableTreeNode(new NBTNode(base)));
			}
		}
	}
	
	public static String formatNodeName(NBTBase entry)
	{
		return entry.getName() + " : " + entry.toString();
	}
}
