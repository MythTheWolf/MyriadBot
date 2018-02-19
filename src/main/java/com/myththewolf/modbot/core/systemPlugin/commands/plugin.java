package com.myththewolf.modbot.core.systemPlugin.commands;

import com.myththewolf.modbot.core.lib.Util;
import com.myththewolf.modbot.core.lib.invocation.impl.BotPlugin;
import com.myththewolf.modbot.core.lib.invocation.interfaces.PluginManager;
import com.myththewolf.modbot.core.systemPlugin.SystemCommand;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.message.MessageAuthor;
import de.btobastian.javacord.entities.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.Optional;

public class plugin implements SystemCommand {
    private PluginManager pluginManager;

    public plugin(PluginManager manager) {
        pluginManager = manager;
    }

    @Override
    public void onCommand(MessageAuthor author, Message message) {
        if (!(message.getContent().split(" ").length > 0)) {
            message.getChannel().sendMessage(":warning: Usage: `>plugin <plugin name>`").exceptionally(Javacord::exceptionLogger);
            return;
        }
        String plugin = Util.arrayToString(1, message.getContent().split(" "));
        Optional<BotPlugin> theBotPlugin = pluginManager.getPlugins().stream().filter(botPlugin -> botPlugin.getPluginName().equals(plugin)).findFirst();
        if (!theBotPlugin.isPresent()) {
            message.getChannel().sendMessage(":warning: No plugin found by that name").exceptionally(Javacord::exceptionLogger);
            return;
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.CYAN);
        embedBuilder.setTitle(theBotPlugin.get().getPluginName());
        embedBuilder.addField("Author", theBotPlugin.get().getPluginAuthor(), false);
        embedBuilder.addField("Version", theBotPlugin.get().getPluginVersionString(), false);
        embedBuilder.addField("Data folder location:", theBotPlugin.get().getDataFolder().get().getAbsolutePath(), false);
        embedBuilder.setDescription(theBotPlugin.get().getPluginDescription());
        message.getChannel().sendMessage(embedBuilder).exceptionally(Javacord::exceptionLogger);
    }
}
