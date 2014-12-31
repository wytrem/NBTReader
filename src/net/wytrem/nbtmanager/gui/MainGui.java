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


public class MainGui extends JFrame
{
	private static final long serialVersionUID = 1125045363621163546L;

	private JMenuBar menus = new JMenuBar();
	private JMenu fileMenu = new JMenu("Fichier");
	private JMenuItem itemOpen = new JMenuItem("Ouvrir");
	private JMenuItem itemSave = new JMenuItem("Enregistrer");
	private JMenuItem itemExit = new JMenuItem("Quitter");

	private NBTTree currentTree = null;

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

					validate();
					repaint();
				}
			}
		});

		itemSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				JFileChooser chooser = new JFileChooser();

				chooser.setDialogTitle("Choisissez le fichier de destination...");
				chooser.showOpenDialog(null);

				
				File destination = chooser.getSelectedFile();
				
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
						
						JOptionPane.showMessageDialog(null, "Erreur lors de la création du fichier : " + e.getMessage(), "Erreur !", JOptionPane.ERROR_MESSAGE);
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
							
							JOptionPane.showMessageDialog(null, "Erreur lors de l'enregistrement du fichier : " + e.getMessage(), "Erreur !", JOptionPane.ERROR_MESSAGE);
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
							
							JOptionPane.showMessageDialog(null, "Erreur lors de l'enregistrement du fichier : " + e.getMessage(), "Erreur !", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
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
		fileMenu.addSeparator();
		fileMenu.add(itemExit);

		menus.add(fileMenu);

		setJMenuBar(menus);
	}

	private void loadFile(File loc)
	{
		getContentPane().removeAll();
		currentTree = new NBTTree(loc);
		getContentPane().add(currentTree.getComponent());
	}
}
