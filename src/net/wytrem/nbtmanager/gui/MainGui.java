package net.wytrem.nbtmanager.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import net.wytrem.nbtmanager.nbt.CompressedStreamTools;
import net.wytrem.nbtmanager.nbt.NBTTagCompound;


public class MainGui extends JFrame
{
	private static final long serialVersionUID = 1125045363621163546L;

	/**
	 * Barre de menu en haut de la fenêtre.
	 */
	private JMenuBar menus = new JMenuBar();

	/**
	 * Menu "Fichier".
	 */
	private JMenu fileMenu = new JMenu("Fichier");

	/**
	 * Item "Ouvrir" du menu "Fichier".
	 */
	private JMenuItem itemOpen = new JMenuItem("Ouvrir");

	/**
	 * Item "Enregistrer" du menu "Fichier".
	 */
	private JMenuItem itemSave = new JMenuItem("Enregistrer");

	/**
	 * Item "Enregistrer sous" du menu "Fichier".
	 */
	private JMenuItem itemSaveAs = new JMenuItem("Enregistrer sous");

	/**
	 * Item "Nouvelle fenêtre" du menu "Fichier".
	 */
	private JMenuItem itemNewWindow = new JMenuItem("Nouvelle fenêtre");

	/**
	 * Item "Quitter" du menu "Fichier".
	 */
	private JMenuItem itemExit = new JMenuItem("Quitter");

	/**
	 * L'arbre NBT actuellement affiché dans la fenêtre.
	 */
	private NBTTree currentTree = null;

	/**
	 * Crée une nouvelle fenêtre de NBTReader.
	 */
	public MainGui()
	{
		super("NBTReader");
		setLocationRelativeTo(null);
		setSize(400, 400);

		itemOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				JFileChooser chooser = new JFileChooser();

				chooser.setDialogTitle("Choisissez le fichier NBT à lire...");
				chooser.showOpenDialog(null);

				File choosen = chooser.getSelectedFile();

				if (choosen != null)
				{
					loadFile(choosen);
				}
			}
		});

		itemSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				saveTagTo(currentTree.getTag(), currentTree.getFile());
			}
		});

		itemSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				chooseFileAndSaveTag(currentTree.getTag());
			}
		});

		itemNewWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				MainGui frame = new MainGui();
				frame.setVisible(true);
			}
		});

		itemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				System.exit(0);
			}
		});

		fileMenu.add(itemOpen);
		fileMenu.add(itemSave);
		fileMenu.add(itemSaveAs);
		fileMenu.add(itemNewWindow);
		fileMenu.addSeparator();
		fileMenu.add(itemExit);

		menus.add(fileMenu);

		setJMenuBar(menus);
	}

	/**
	 * Ouvre un sélectionneur de fichiers puis enregistre le
	 * {@link NBTTagCompound} passé en argument dans le fichier sélectionné.
	 * 
	 * @see MainGui#saveTagTo(NBTTagCompound, File)
	 */
	protected void chooseFileAndSaveTag(NBTTagCompound tag)
	{
		JFileChooser chooser = new JFileChooser();

		chooser.setDialogTitle("Choisissez le fichier de destination...");
		chooser.showOpenDialog(null);

		File destination = chooser.getSelectedFile();

		saveTagTo(tag, destination);
	}

	/**
	 * Sauvergarde le {@link NBTTagCompound} passé en argument dans le fichier
	 * donné. Ouvre des boîtes de dialogue pour demander si le fichier doit être
	 * enregistré sous forme compréssée.
	 * 
	 * @see CompressedStreamTools#write(NBTTagCompound, java.io.DataOutput)
	 * @see CompressedStreamTools#writeCompressed(NBTTagCompound,
	 *      java.io.OutputStream)
	 */
	protected void saveTagTo(NBTTagCompound tag, File destination)
	{
		if (destination != null)
		{
			if (destination.exists())
			{
				destination.delete();
			}

			try
			{
				destination.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();

				showErrorDialog("Erreur lors de la création du fichier : " + e.getMessage());
				return;
			}

			if (JOptionPane.showConfirmDialog(null, "Voulez vous enregistrer le fichier sous forme compressée (GZIP) ?", "Paramètres", JOptionPane.YES_NO_OPTION) == 0)
			{
				try
				{
					CompressedStreamTools.writeCompressed(currentTree.getTag(), new DataOutputStream(new FileOutputStream(destination)));
				}
				catch (IOException e)
				{
					e.printStackTrace();

					showErrorDialog("Erreur lors de l'enregistrement du fichier : " + e.getMessage());
				}
			}
			else
			{
				try
				{
					CompressedStreamTools.write(currentTree.getTag(), new DataOutputStream(new FileOutputStream(destination)));
				}
				catch (IOException e)
				{
					e.printStackTrace();

					showErrorDialog("Erreur lors de l'enregistrement du fichier : " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Charge le fichier dans la fenêtre, et affiche le {@link NBTTagCompound}
	 * qu'il contient.
	 * 
	 * @param loc Le fichier à lire.
	 */
	public void loadFile(File loc)
	{
		getContentPane().removeAll();
		currentTree = new NBTTree(loc);
		getContentPane().add(currentTree.getComponent());

		validate();
		repaint();
	}

	/**
	 * Affiche une boîte modale signalant une erreur.
	 * 
	 * @param text Le texte de l'erreur.
	 */
	public static void showErrorDialog(String text)
	{
		JOptionPane.showMessageDialog(null, text, "Erreur !", JOptionPane.ERROR_MESSAGE);
	}
}
