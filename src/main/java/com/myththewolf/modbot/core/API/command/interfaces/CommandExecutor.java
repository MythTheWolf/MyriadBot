/*
 * Copyright (c) 2018 MythTheWolf
 *  Nicholas Agner, USA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.myththewolf.modbot.core.API.command.interfaces;


import com.myththewolf.modbot.core.lib.logging.Loggable;
import com.myththewolf.modbot.core.lib.plugin.manager.impl.BotPlugin;
import com.vdurmont.emoji.EmojiParser;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.util.logging.ExceptionLogger;

import java.awt.*;
import java.util.Optional;


/**
 * CommandExecutor class to be extended to plugins
 * This class is abstract so we can package helper methods, yet the onCommand can be handled by the plugin dev
 */
public abstract class CommandExecutor implements CommandAdapater, Loggable {
    /**
     * The TextChannel from the last command ran
     */
    private TextChannel lastTextChannel;
    /**
     * The Message from the last command ran
     */
    private Message lastMessage;
    /**
     * The plugin this command is registered
     */
    private BotPlugin plugin;

    /**
     * Updates the cache so when a command is ran, the helper methods work to the latest
     *
     * @param newTextChannel The new text channel from the command
     * @param source         The command message source
     */
    public void update(BotPlugin plugin, TextChannel newTextChannel, Message source) {
        this.lastTextChannel = newTextChannel;
        this.lastMessage = source;
        this.plugin = plugin;
    }

    /**
     * Sends the specified message to the channel in which the command was sent
     *
     * @param content The message to be sent
     */
    public void reply(String content) {
        if (!getLastAuthor().isPresent()) {
            getLogger().info(EmojiParser.parseToUnicode(content));
            return;
        }
        getLastTextChannel().sendMessage(content).exceptionally(ExceptionLogger.get());
    }

    /**
     * Sends the specified messasage embed to the channel in which the command was sent
     *
     * @param embedBuilder The message embed to be sent
     */
    public void reply(EmbedBuilder embedBuilder) {
        if (!getLastAuthor().isPresent()) {
            getLogger().info("[Unimplemented embed builder]");
            return;
        }
        getLastTextChannel().sendMessage(embedBuilder).exceptionally(ExceptionLogger.get());
    }

    /**
     * Sends a friendly message embed that gives a "success" look with the specified message
     *
     * @param content The message to send within the embed.
     */
    public void succeeded(String content) {
        succeeded(content, "The command completed successfully", "Success");
    }

    /**
     * Sends a friendly MessageEmbed that gives a "success" look with the specified content
     *
     * @param content The message to send within the embed.
     * @param footer  The footer to bind to the embed
     * @param title   The title to bind to the embed
     */
    public void succeeded(String content, String footer, String title) {
        if (getLastAuthor().isPresent()) {
            reply("\u001b[32mSuccess: " + EmojiParser.parseToUnicode(title));
            reply(EmojiParser.parseToUnicode(content));
            reply(EmojiParser.parseToUnicode(footer) + "\u001b[0m");
            return;
        }
        EmbedBuilder succ = new EmbedBuilder();
        succ.setColor(Color.GREEN);
        succ.setTitle(title);
        succ.setFooter(footer);
        succ.setDescription(content);
        reply(succ);
    }

    /**
     * Sends a pre-defined red "failed" message to the command source
     *
     * @param content
     */
    public void failed(String content) {
        failed(content, "Errors occurred while processing your command", "Error");
    }

    /**
     * Sends a red "failed" messaged to the command source
     *
     * @param content The message to send within the embed.
     * @param footer  The footer to bind to the embed
     * @param title   The title to bind to the embed
     */
    public void failed(String content, String footer, String title) {
        if (!getLastAuthor().isPresent()) {
            reply("\u001b[31mA error occured while executing the command: " + EmojiParser.parseToUnicode(title));
            reply(EmojiParser.parseToUnicode(content));
            reply(EmojiParser.parseToUnicode(footer) + "\u001b[0m");
            return;
        }
        EmbedBuilder fail = new EmbedBuilder().setAuthor(getLastAuthor().get().getName(), null, getLastAuthor().get().getAvatar().getUrl().toString());
        fail.setFooter(footer);
        fail.setTitle(title);
        fail.setDescription(content);
        reply(fail);
    }

    /**
     * Deletes the message that triggered this command.
     */
    public void deleteTriggerMessage() {
        getLastMessage().delete().exceptionally(ExceptionLogger.get());
    }

    /**
     * Gets the last known message of this command run
     *
     * @return The message
     */
    public Message getLastMessage() {
        return lastMessage;
    }

    /**
     * Gets the last known User who ran this command
     *
     * @return Optional, empty if ran by command
     */
    public Optional<MessageAuthor> getLastAuthor() {
        return Optional.ofNullable(getLastMessage() != null ? getLastMessage().getAuthor() : null);
    }

    /**
     * Gets the last known TextChannel in which this command was ran from
     *
     * @return The TextChannel
     */
    public TextChannel getLastTextChannel() {
        return lastTextChannel;
    }

    /**
     * Gets the plugin instance of this command
     *
     * @return The plugin
     */
    public BotPlugin getPlugin() {
        return plugin;
    }
}
