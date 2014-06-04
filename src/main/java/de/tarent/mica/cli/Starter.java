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
		Commands cmd = new Commands();
		Shell shell = ShellFactory.createConsoleShell("mica", "=== RayShip ===\n", 
				cmd);
		
		cmd.setShell(shell);
		shell.commandLoop();
	}
}
