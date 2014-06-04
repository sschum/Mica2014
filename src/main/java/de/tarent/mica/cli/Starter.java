package de.tarent.mica.cli;

import java.io.IOException;

import asg.cliche.Shell;
import asg.cliche.ShellFactory;

/**
 * Startpunkt der Programmes
 *
 * @author rainu
 */
public class Starter {

	public static void main(String[] args) throws IOException {
		Shell shell = ShellFactory.createConsoleShell("mica", "=== RayShip ===\n", 
				new Commands());
		
		shell.commandLoop();
	}
}
