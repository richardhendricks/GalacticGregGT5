package bloodasp.galacticgreg.schematics;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import net.minecraft.block.Block;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import bloodasp.galacticgreg.api.StructureInformation;

/**
 * Class for XML Structure files. You only should edit/use this file/class if you want to add/fix stuff with 
 * GalacticGreg itself, and never if you're a mod developer and want to add support for GGreg to your mod.
 * However, feel free to copy this code to your own mod to implement structures. If you have questions, find me on github
 * and ask 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SpaceSchematic")
public class SpaceSchematic {
	@XmlAttribute(name = "enabled")
	protected boolean _mStructureEnabled;
	@XmlAttribute(name="centerX")
	protected int _mCenterX;
	@XmlAttribute(name="centerY")
	protected int _mCenterY;
	@XmlAttribute(name="centerZ")
	protected int _mCenterZ;

	@XmlElement(name = "StructureName")
	protected String _mStructureName;

	@XmlElement(name = "Rarity")
	protected int _mRarity;

	@XmlElementWrapper(name = "Coords")
	@XmlElement(name="block")
	protected ArrayList<BaseStructureInfo> mStructureInfoList;

	public boolean isEnabled()
	{
		return _mStructureEnabled;
	}

	public SpaceSchematic()
	{
		// Set the default center-point to 0,65,0
		_mCenterX = 0;
		_mCenterY = 65;
		_mCenterZ = 0;
	}

	public Vec3 getStructureCenter()
	{
		return Vec3.createVectorHelper(_mCenterX, _mCenterY, _mCenterZ);
	}

	public int getRarity()
	{
		return _mRarity;
	}

	public String getName()
	{
		return _mStructureName;
	}

	public ArrayList<BaseStructureInfo> coordInfo()
	{
		if (mStructureInfoList == null)
			mStructureInfoList = new ArrayList<BaseStructureInfo>();

		return mStructureInfoList;
	}

	public void addStructureInfo(StructureInformation pStrucInfo)
	{
		if (mStructureInfoList == null)
			mStructureInfoList = new ArrayList<BaseStructureInfo>();
		mStructureInfoList.add(new BaseStructureInfo(pStrucInfo));
	}

	public static class BaseStructureInfo
	{
		@XmlAttribute(name = "X")
		protected int posX;
		@XmlAttribute(name = "Y")
		protected int posY;
		@XmlAttribute(name = "Z")
		protected int posZ;
		@XmlAttribute(name = "Block")
		protected String blockName;		
		@XmlAttribute(name = "Meta")
		protected int blockMeta;

		private NBTTagCompound blockNBTCompound;

		@XmlAttribute(name = "NBT")
		protected String blockNBT;

		/**
		 * Get the NBTTagCompound bound to this block. Returns null if none is present or if
		 * the tag is invalid
		 * @return
		 */
		public NBTTagCompound getTagCompound()
		{
			try
			{
				if (blockNBTCompound == null && blockNBT.length() > 1)
					blockNBTCompound = (NBTTagCompound) JsonToNBT.func_150315_a(blockNBT);

				if (blockNBTCompound != null)
					return blockNBTCompound;
				else 
					return null;
			}
			catch (Exception e)
			{
				return null;
			}
		}

		public BaseStructureInfo()
		{

		}

		public BaseStructureInfo(StructureInformation pSI)
		{
			posX = pSI.getX();
			posY = pSI.getY();
			posZ = pSI.getZ();
			blockName = Block.blockRegistry.getNameForObject(pSI.getBlock().getBlock());
			blockMeta = pSI.getBlock().getMeta();
			blockNBTCompound = pSI.getNBTCompound();
			if (blockNBTCompound != null)
				blockNBT = blockNBTCompound.toString();
		}

		public String getBlockName()
		{
			return blockName;
		}

		public int getBlockMeta()
		{
			return blockMeta;
		}

		public Vec3 getVec3Pos()
		{
			return Vec3.createVectorHelper(posX, posY, posZ);
		}
	}
}
