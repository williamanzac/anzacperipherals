package anzac.peripherals.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ChatMessage implements IMessage {

	public String text;

	public ChatMessage() {
	}

	public ChatMessage(String text) {
		this.text = text;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		text = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, text);
	}

	public static class ChatMessageHandler implements IMessageHandler<ChatMessage, IMessage> {
		@Override
		public IMessage onMessage(ChatMessage message, MessageContext ctx) {
			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(message.text));
			return null;
		}
	}
}
