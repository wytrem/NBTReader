package net.wytrem.nbtmanager.core;


import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.wytrem.nbtmanager.gui.MainGui;


public class NBTReader
{

	/**
	 * @param args
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException
	{

		try
		{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		catch (UnsupportedLookAndFeelException e)
		{}
		catch (ClassNotFoundException e)
		{}
		catch (InstantiationException e)
		{}
		catch (IllegalAccessException e)
		{}

		MainGui frame = new MainGui();

		frame.setVisible(true);
	}

}
