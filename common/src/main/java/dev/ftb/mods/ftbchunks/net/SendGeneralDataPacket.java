package dev.ftb.mods.ftbchunks.net;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import dev.ftb.mods.ftbchunks.FTBChunks;
import dev.ftb.mods.ftbchunks.FTBChunksWorldConfig;
import dev.ftb.mods.ftbchunks.data.ClaimedChunk;
import dev.ftb.mods.ftbchunks.data.FTBChunksTeamData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

/**
 * @author LatvianModder
 */
public class SendGeneralDataPacket extends BaseS2CMessage {
	public static void send(FTBChunksTeamData playerData, ServerPlayer player) {
		SendGeneralDataPacket data = new SendGeneralDataPacket();

		data.maxClaimChunks = FTBChunksWorldConfig.getMaxClaimedChunks(playerData, player);
		data.maxForceLoadChunks = FTBChunksWorldConfig.getMaxForceLoadedChunks(playerData, player);

		for (ClaimedChunk chunk : playerData.getClaimedChunks()) {
			data.claimed++;

			if (chunk.isForceLoaded()) {
				data.loaded++;
			}
		}

		data.sendTo(player);
	}

	public int claimed;
	public int loaded;
	public int maxClaimChunks;
	public int maxForceLoadChunks;

	public SendGeneralDataPacket() {
	}

	@Override
	public MessageType getType() {
		return FTBChunksNet.SEND_GENERAL_DATA;
	}

	SendGeneralDataPacket(FriendlyByteBuf buf) {
		claimed = buf.readVarInt();
		loaded = buf.readVarInt();
		maxClaimChunks = buf.readVarInt();
		maxForceLoadChunks = buf.readVarInt();
	}

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeVarInt(claimed);
		buf.writeVarInt(loaded);
		buf.writeVarInt(maxClaimChunks);
		buf.writeVarInt(maxForceLoadChunks);
	}

	@Override
	public void handle(NetworkManager.PacketContext context) {
		FTBChunks.PROXY.updateGeneralData(this);
	}
}