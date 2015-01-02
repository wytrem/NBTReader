package net.wytrem.nbtmanager.core;


import java.io.File;

import net.wytrem.nbtmanager.gui.MainGui;


public class NBTReader
{

	/**
	 * Lance le programme et ouvre le fenêtre principale.
	 * @param args Contient dans args[0] le nom du fichiers à ouvrir pour un accès direct.
	 */
	public static void main(String[] args)
	{
		MainGui frame = new MainGui();
		
		if (args.length > 0)
		{
			String path = args[0];
			
			if (path != null && !path.isEmpty())
			{
				File file = new File(path);
				
				if (file.exists())
				{
					frame.loadFile(file);
				}
				else
				{
					System.out.println("Le fichier '" + file.getAbsolutePath() + "' n'existe pas. Ignoré.");
				}
			}
		}
		
		frame.setVisible(true);
	}

}
