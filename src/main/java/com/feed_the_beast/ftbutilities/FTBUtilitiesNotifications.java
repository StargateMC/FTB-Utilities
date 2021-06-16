package com.feed_the_beast.ftbutilities;

import com.feed_the_beast.ftblib.lib.EnumTeamStatus;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import com.feed_the_beast.ftbutilities.data.ClaimedChunk;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import com.stargatemc.api.CoreAPI;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;

/**
 * @author LatvianModder
 */
public class FTBUtilitiesNotifications
{
	public static final ResourceLocation CHUNK_MODIFIED = new ResourceLocation(FTBUtilities.MOD_ID, "chunk_modified");
	public static final ResourceLocation CHUNK_CHANGED = new ResourceLocation(FTBUtilities.MOD_ID, "chunk_changed");
	public static final ResourceLocation CHUNK_CANT_CLAIM = new ResourceLocation(FTBUtilities.MOD_ID, "cant_claim_chunk");
	public static final ResourceLocation UNCLAIMED_ALL = new ResourceLocation(FTBUtilities.MOD_ID, "unclaimed_all");
	public static final ResourceLocation TELEPORT = new ResourceLocation(FTBUtilities.MOD_ID, "teleport");

	public static void sendCantModifyChunk(MinecraftServer server, EntityPlayerMP player)
	{
		Notification.of(new ResourceLocation(FTBUtilities.MOD_ID, "cant_modify_chunk"), FTBUtilities.lang(player, "ftbutilities.lang.chunks.cant_modify_chunk")).setError().send(server, player);
	}

	public static void notifyFaction(ForgePlayer intruder, ForgeTeam team, boolean entering) {
		
		// Builds a string.
		String s = (intruder.getName() + " has " + (entering ? "entered" : "exited") + " your clans territory near " + intruder.getPlayer().getPosition().getX() + "," + intruder.getPlayer().getPosition().getY() + "," + intruder.getPlayer().getPosition().getZ());
		DimensionProperties props = DimensionManager.getEffectiveDimId(player.getPlayer().getServerWorld().provider.getDimension(), intruder.getPlayer().getPosition());
		String worldName = "UnknownWorld";
		if (props != null) {
				worldName = props.getName();
		}
		s += (" on " + worldName);
		
		for (ForgePlayer player : team.getMembers()) {
			if (player.isOnline()) {
				CoreAPI.sendChat(player.getPlayer(), s);
			} else {
				CoreAPI.messagePlayerOffline(player.getId(), s);
			}
		}
		
	}
	
	public static void updateChunkMessage(EntityPlayerMP player, ChunkDimPos pos)
	{
		if (!ClaimedChunks.isActive())
		{
			return;
		}

		ClaimedChunk chunk = ClaimedChunks.instance.getChunk(pos);
		ForgeTeam team = chunk == null ? null : chunk.getTeam();
		short teamID = team == null ? 0 : team.getUID();

		if (player.getEntityData().getShort(FTBUtilitiesPlayerData.TAG_LAST_CHUNK) != teamID)
		{
			if (teamID == 0)
			{
				player.getEntityData().removeTag(FTBUtilitiesPlayerData.TAG_LAST_CHUNK);
			}
			else
			{
				player.getEntityData().setShort(FTBUtilitiesPlayerData.TAG_LAST_CHUNK, teamID);
			}

			if (team != null)
			{
				Notification notification = Notification.of(CHUNK_CHANGED, team.getTitle());

				if (!team.getDesc().isEmpty())
				{
					notification.addLine(StringUtils.italic(new TextComponentString(team.getDesc()), true));
				}
				notification.send(player.server, player);
			}
			else
			{
				Notification.of(CHUNK_CHANGED, StringUtils.color(FTBUtilities.lang(player, "ftbutilities.lang.chunks.wilderness"), TextFormatting.DARK_GREEN)).send(player.server, player);
			}
		}
	}
}