/*
 * Copyright (c) 2019, jkybtw <https://github.com/jkybtw>
 * Copyright (c) 2019, openosrs <https://openosrs.com>
 * Copyright (c) 2019, kyle <https://github.com/Kyleeld>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.guardiansoftherift;

import com.google.inject.Provides;

import java.awt.*;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;

import java.lang.*;
import java.util.ArrayList;
import java.util.List;

import net.runelite.api.events.*;
import net.runelite.client.Notifier;
import net.runelite.client.game.ItemManager;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "GuardiansOfTheRift Helper",
	enabledByDefault = false,
	description = "Portal Timer & Notifications",
	tags = {"runecrafting"}
)
@Slf4j
public class GuardiansOfTheRiftPlugin extends Plugin {

	@Inject
	private Notifier notifier;
	@Inject
	private Client client;
	@Inject
	private GuardiansOfTheRiftConfig config;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	@Getter
	private final List<TileObject> objectsToMark = new ArrayList<>();

	@Provides
	GuardiansOfTheRiftConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(GuardiansOfTheRiftConfig.class);
	}


	private GuardiansOfTheRiftInfoBox guardiansOfTheRiftInfoBox;

	private int counter = -1;
	public String s = String.valueOf(counter);

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}

		String message = chatMessage.getMessage().toLowerCase();
		if (message.contains("the rift becomes active"))
		{
			counter = 260;
		}
		if (message.contains("the portal guardians close their rifts"))
		{
			counter = -1;
		}
	}
	//Move at 180
	//260 -  270 from star
	@Subscribe
	private void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned){
		// Guardians of the Rift Helper
		if (config.setting1()) {
			if (gameObjectSpawned.getGameObject().getId() == 43729){
				if (!(counter >= 2 && counter <= 60)){ // If you see the portal again, don't restart timer
					if (config.setting1()) {
						notifier.notify("The portal has opened!", TrayIcon.MessageType.INFO);
					}
					if (counter == -1) {
						counter = 230;
					}else{
						counter = 230;
					}
				}

			}
		}
	}

	@Subscribe
	private void onGameTick(final GameTick event) {

			if (counter > -1) {
				counter = counter - 1;
				s = String.valueOf(counter);
			}

			if (guardiansOfTheRiftInfoBox != null) {
				infoBoxManager.removeInfoBox(guardiansOfTheRiftInfoBox);
				guardiansOfTheRiftInfoBox = null;
			}
			if (counter > -1) {
				guardiansOfTheRiftInfoBox = new GuardiansOfTheRiftInfoBox(itemManager.getImage(ItemID.RIFT_GUARDIAN_20683), this);
				infoBoxManager.addInfoBox(guardiansOfTheRiftInfoBox);
			}
	}
}

