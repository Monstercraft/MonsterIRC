package org.monstercraft.signature.listener;

import java.io.IOException;
import java.net.UnknownHostException;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.monstercraft.signature.Signature;
import org.monstercraft.signature.IO.Data;
import org.monstercraft.signature.util.Variables;

import com.iConomy.system.Account;

public class MCPlayerListener extends PlayerListener {

	private Signature Handle;

	public MCPlayerListener(Signature PluginHandle) {
		this.setHandle(PluginHandle);
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		String name = event.getPlayer().getName();
		Account account = new Account(name);
		ClanPlayer p = new ClanPlayer(name);
		int holdings = (int) account.getHoldings().balance();
		int power = com.gmail.nossr50.m.getPowerLevel(event.getPlayer());
		String clan = "N/A";
		if (p.getTag() != null && p.getTag() != "" && p.getTag() != " ") {
			clan = p.getTag();
		}
		try {
			Data.updateSignature(name, clan, holdings, power);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		p.sendMessage("§b View your signature at: " + Variables.view + "?User="
				+ p.getName());
	}

	public Signature getHandle() {
		return Handle;
	}

	private void setHandle(Signature handle) {
		Handle = handle;
	}

}