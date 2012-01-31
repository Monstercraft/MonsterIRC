package org.monstercraft.irc.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.monstercraft.irc.IRC;

/**
 * This class creates the ReadMe.txt.
 * 
 * @author fletch_to_99 <fletchto99@hotmail.com>
 * 
 */
public class CreateReadme extends IRC {

	/**
	 * Creates an instance of the CreateReadme class.
	 */
	public CreateReadme() {
		write();
	}

	/**
	 * Creates the ReadMe.txt file.
	 */
	public void write() {
		try {
			FileWriter outFile = new FileWriter(new File(
					Constants.SETTINGS_PATH + "README.txt"));
			PrintWriter out = new PrintWriter(outFile);
			out.println("How to set up MonsterIRC");
			out.println("Step 1 - Allow the plugin to run once which will create the default settings and channels.");
			out.println("Step 2 - Edit the config.yml file with your IRC information.");
			out.println("Step 3 - Create you desired channels");
			out.println("");
			out.println("How to create channels:");
			out.println("Each .channel file represents the IRC channel to join i.e. Sample.channel means MonsterIRC will join the channel #Sample");
			out.println("The default \"Sample.channel\" file represents the type that will pass all chat to the specified IRC channel (excluding admin chat).");
			out.println("The default \"HeroChatSample.channel\" file represents the type that will pass all chat from the specified ingame HeroChat channel to the specified IRC channel (excluding admin chat again).");
			out.println("");
			out.println("Options while creating channels");
			out.println("Each channel has the option to be enabled, setting that to false will make MonsterIRC pass that file and not join into the specified IRC channel");
			out.println("Auto join specifies weither you want MonsterIRC to automatically join the specified IRC channel when the plugin is started. If you wish for this to be false then you can manually connect later with /irc join (channel).");
			out.close();
		} catch (IOException e) {
			debug(e);
		}
	}
}
