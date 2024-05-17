package com.deathmotion.playercrasher.listeners;

import com.deathmotion.playercrasher.PlayerCrasher;
import com.deathmotion.playercrasher.managers.CrashManager;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;

public class BrandHandler extends PacketListenerAbstract {

    private final CrashManager crashManager;

    public BrandHandler(PlayerCrasher plugin) {
        crashManager = plugin.getCrashManager();
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Configuration.Client.PLUGIN_MESSAGE) return;
        WrapperPlayClientPluginMessage wrapper = new WrapperPlayClientPluginMessage(event);

        String channelName = wrapper.getChannelName();
        byte[] data = wrapper.getData();

        if (!channelName.equalsIgnoreCase("minecraft:brand") && !channelName.equals("MC|Brand")) return;
        if (data.length > 64 || data.length == 0) return;

        byte[] minusLength = new byte[data.length - 1];
        System.arraycopy(data, 1, minusLength, 0, minusLength.length);
        String brand = new String(minusLength).replace(" (Velocity)", ""); // removes velocity's brand suffix

        crashManager.addClientBrand(event.getUser().getUUID(), prettyBrandName(brand));
    }

    private String prettyBrandName(String brand) {
        if (brand.toLowerCase().contains("lunarclient")) {
            return "Lunar Client";
        }

        return capitalizeFirstLetter(brand);
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        } else {
            return Character.toUpperCase(str.charAt(0)) + str.substring(1);
        }
    }
}
