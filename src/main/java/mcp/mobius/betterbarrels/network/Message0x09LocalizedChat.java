package mcp.mobius.betterbarrels.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mcp.mobius.betterbarrels.common.LocalizedChat;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class Message0x09LocalizedChat extends SimpleChannelInboundHandler<Message0x09LocalizedChat> implements IBarrelMessage {
	public int messageID;
	public int count;
	public Integer[] extraNumbers;

	public Message0x09LocalizedChat() {}
	public Message0x09LocalizedChat(LocalizedChat message, Integer ... extraNumbers) {
		this.messageID = message.ordinal();
		this.count = extraNumbers.length;
		this.extraNumbers = new Integer[this.count];

		for (int i = 0; i < this.count; i++) {
			this.extraNumbers[i] = extraNumbers[i];
		}
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, IBarrelMessage msg, ByteBuf target) throws Exception {
		target.writeInt(this.messageID);
		target.writeInt(this.count);
		for (int i = 0; i < this.count; i++) {
			target.writeInt(this.extraNumbers[i]);
		}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IBarrelMessage rawmsg) {
		Message0x09LocalizedChat msg = (Message0x09LocalizedChat)rawmsg;
		msg.messageID  = dat.readInt();
		msg.count = dat.readInt();
		msg.extraNumbers = new Integer[msg.count];

		for (int i = 0; i < this.count; i++) {
			msg.extraNumbers[i] = dat.readInt();
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message0x09LocalizedChat msg) throws Exception {
		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentTranslation(LocalizedChat.values()[msg.messageID].localizationKey, (Object[])(msg.extraNumbers)));
	}
}
