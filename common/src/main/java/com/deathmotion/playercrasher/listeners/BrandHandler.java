/*
 * This file is part of PlayerCrasher - https://github.com/Bram1903/MinecraftPlayerCrasher
 * Copyright (C) 2024 Bram and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.deathmotion.playercrasher.listeners;

import com.deathmotion.playercrasher.PCPlatform;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;

public class BrandHandler<P> extends PacketListenerAbstract {

    private final UserTracker userManager;

    public BrandHandler(PCPlatform<P> plugin) {
        userManager = plugin.getUserTracker();
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.PLUGIN_MESSAGE && event.getPacketType() != PacketType.Configuration.Client.PLUGIN_MESSAGE)
            return;

        WrapperPlayClientPluginMessage packet = new WrapperPlayClientPluginMessage(event);

        String channelName = packet.getChannelName();
        if (!channelName.equalsIgnoreCase("minecraft:brand") && !channelName.equals("MC|Brand")) return;

        byte[] data = packet.getData();

        if (data.length > 64 || data.length == 0) return;

        byte[] minusLength = new byte[data.length - 1];
        System.arraycopy(data, 1, minusLength, 0, minusLength.length);
        String brand = new String(minusLength).replace(" (Velocity)", "");

        userManager.setClientBrand(event.getUser().getUUID(), prettyBrandName(brand));
    }

    private String prettyBrandName(String brand) {
        String lowerCaseBrand = brand.toLowerCase();

        if (lowerCaseBrand.contains("lunarclient")) {
            return "Lunar Client";
        }

        return capitalizeFirstLetter(brand);
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
