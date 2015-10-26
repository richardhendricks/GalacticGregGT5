package bloodasp.galacticgreg.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import bloodasp.galacticgreg.GalacticGreg;
import bloodasp.galacticgreg.auxiliary.PlayerChatHelper;
import bloodasp.galacticgreg.schematics.SpaceSchematic;
import bloodasp.galacticgreg.schematics.SpaceSchematic.BaseStructureInfo;

public class AESpawnCommand implements ICommand {
	private List aliases;
	public AESpawnCommand()
	{
		this.aliases = new ArrayList();
		this.aliases.add("spawnstruct");
	}

	@Override
	public String getCommandName()
	{
		return "spawnstruct";
	}

	@Override
	public String getCommandUsage(ICommandSender pCommandSender)
	{
		return "spawnstruct <structure name>";
	}

	@Override
	public List getCommandAliases()
	{
		return this.aliases;
	}

	@Override
	public void processCommand(ICommandSender pCommandSender, String[] pArgs)
	{
		try
		{
			if (pCommandSender instanceof EntityPlayer)
			{
				if (pArgs.length < 4)
				{
					SendHelpToPlayer(pCommandSender);
					return;
				}

				String tName = pArgs[0].toString();
				int tX = Integer.parseInt(pArgs[1]);
				int tY = Integer.parseInt(pArgs[2]);
				int tZ = Integer.parseInt(pArgs[3]);

				EntityPlayer tEP = (EntityPlayer) pCommandSender;
				SpaceSchematic sSchem = GalacticGreg.SchematicHandler.LoadSpaceSchematic(tName);
				if (sSchem == null)
				{
					PlayerChatHelper.SendWarn(pCommandSender, String.format("Schematic %s can't be found or is invalid", tName));
					return;
				}

				// Structure SpawnCode. To be moved to some different location later on
				// Get the defined center-point of the structure
				Vec3 tCenter = sSchem.getStructureCenter();
				// Get the Center in our world
				Vec3 tWorldCenter = Vec3.createVectorHelper(tX, tY, tZ);
				World tWorld = tEP.worldObj;

				// Loop all Blocks we have
				for (BaseStructureInfo bsi : sSchem.coordInfo())
				{

					try
					{
						// Get the real coordinates
						// How this is meant to work:
						// The position in our StructureFile is based around 0/65/0.
						// The creator of that structure has to define the "center" point, which will then be used to "nullify" the coordinates
						// Then, once we have our 0/0/0 based coordinates (relative coords), we can add the coords
						// of the zero-point in our world.
						// Example: 
						// StructureBlock 1 has absolute coords x:-2 y:65 z:1
						// StructureBlock 2 has absolute coords x:-1 y:66 z:1
						// StructureBlock 3 has absolute coords x:0 y:67 z:1
						// StructureBlock 4 has absolute coords x:1 y:68 z:1
						// StructureBlock 5 has absolute coords x:2 y:69 z:1
						// The Structure Center is at x:0 y:67 z:1
						// Our world coordinates where the struct is supposed to spawn is: x:1000 y:20 z:-1000
						// So we subtract the block-position FROM the center, and ADD the world coordinates.
						// StructureBlock 1 will then spawn at: x:998 y:18 z:-999
						// StructureBlock 2 will then spawn at: x:999 y:19 z:-999
						// StructureBlock 3 will then spawn at: x:1000 y:20 z:-999
						// StructureBlock 4 will then spawn at: x:1001 y:21 z:-999
						// StructureBlock 5 will then spawn at: x:1002 y:22 z:-999
						Vec3 tRealSpawnPoint = tCenter.subtract(bsi.getVec3Pos()).addVector(tWorldCenter.xCoord, tWorldCenter.yCoord, tWorldCenter.zCoord);
						GalacticGreg.Logger.debug("Absolute : %f/%f/%f", bsi.getVec3Pos().xCoord, bsi.getVec3Pos().yCoord, bsi.getVec3Pos().zCoord);
						GalacticGreg.Logger.debug("Final    : %f/%f/%f", tRealSpawnPoint.xCoord, tRealSpawnPoint.yCoord, tRealSpawnPoint.zCoord);
						GalacticGreg.Logger.debug("Block    : %s:%d", bsi.getBlockName(), bsi.getBlockMeta());

						PlayerChatHelper.SendNeutral(pCommandSender, String.format("DBG: B[%s] P[%d/%d/%d]",
								bsi.getBlockName(),
								(int)tRealSpawnPoint.xCoord,
								(int)tRealSpawnPoint.yCoord,
								(int)tRealSpawnPoint.zCoord));

						tWorld.setBlock((int)tRealSpawnPoint.xCoord, 
								(int)tRealSpawnPoint.yCoord,
								(int)tRealSpawnPoint.zCoord,
								(Block) Block.blockRegistry.getObject(bsi.getBlockName()),
								bsi.getBlockMeta(),
								3);


						// If we have NBT data to be set...
						if (bsi.getTagCompound() != null)
						{
							// Get new TE
							TileEntity tTile = TileEntity.createAndLoadEntity(bsi.getTagCompound());

							// Assign coords
							tTile.xCoord = (int) tRealSpawnPoint.xCoord;
							tTile.yCoord = (int) tRealSpawnPoint.yCoord;
							tTile.zCoord = (int) tRealSpawnPoint.zCoord;

							// Assign it to our block
							tWorld.setTileEntity((int) tRealSpawnPoint.xCoord,
									(int) tRealSpawnPoint.yCoord,
									(int) tRealSpawnPoint.zCoord,
									tTile);
							PlayerChatHelper.SendNeutral(pCommandSender, "TE: Set");
						}

					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		catch (Exception e)
		{
			SendHelpToPlayer(pCommandSender);
		}

	}

	private void SendHelpToPlayer(ICommandSender pCommandSender)
	{
		PlayerChatHelper.SendNeutral(pCommandSender, "Usage:");
		PlayerChatHelper.SendNeutral(pCommandSender, "spawnstruct <structname> <x> <y> <z>");
		PlayerChatHelper.SendNeutral(pCommandSender, "<structname>: Without .xml. The name you used to export");
		PlayerChatHelper.SendNeutral(pCommandSender, "<x> <y> <z> : The center-point of the structure");
		PlayerChatHelper.SendNeutral(pCommandSender, "By default, all structures will use the coords you use");
		PlayerChatHelper.SendNeutral(pCommandSender, "as one of their edges. Change centerX/Y/Z if you want the");
		PlayerChatHelper.SendNeutral(pCommandSender, "Struct to have its center somewhere else");
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender pCommandSender)
	{
		// Command is only enabled for actual players and only if they are OP-ed
		if(pCommandSender instanceof EntityPlayerMP)
		{
			EntityPlayerMP tEP = (EntityPlayerMP)pCommandSender;
			return MinecraftServer.getServer().getConfigurationManager().func_152596_g(tEP.getGameProfile());
		}
		else
			return false;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender p_71516_1_,
			String[] p_71516_2_) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		return false;
	}

}
