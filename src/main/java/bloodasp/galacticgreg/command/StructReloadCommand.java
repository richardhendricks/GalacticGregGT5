package bloodasp.galacticgreg.command;

import java.util.ArrayList;
import java.util.List;

import bloodasp.galacticgreg.GalacticGreg;
import bloodasp.galacticgreg.auxiliary.PlayerChatHelper;
import bloodasp.galacticgreg.schematics.SpaceSchematicHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class StructReloadCommand implements ICommand {
	private List aliases;
	public StructReloadCommand()
	{
		this.aliases = new ArrayList();
		this.aliases.add("reloadschematics");
	}

	@Override
	public String getCommandName()
	{
		return "reloadschematics";
	}

	@Override
	public String getCommandUsage(ICommandSender pCommandSender)
	{
		return "reloadschematics";
	}

	@Override
	public List getCommandAliases()
	{
		return this.aliases;
	}

	@Override
	public void processCommand(ICommandSender pCommandSender, String[] pArgs)
	{
		if (!GalacticGreg.SchematicHandler.reloadSchematics(false))
			PlayerChatHelper.SendWarn(pCommandSender, "Structures not reloaded. Check your log!");
		else
			PlayerChatHelper.SendInfo(pCommandSender, "Structures reloaded");
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
