package rbasamoyai.createbigcannons.munitions.big_cannon.shrapnel;

import java.util.List;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import rbasamoyai.createbigcannons.index.CBCEntityTypes;
import rbasamoyai.createbigcannons.munitions.big_cannon.AbstractBigCannonProjectile;
import rbasamoyai.createbigcannons.munitions.big_cannon.SimpleShellBlock;

public class ShrapnelShellBlock extends SimpleShellBlock {

	public ShrapnelShellBlock(Properties properties) {
		super(properties);
	}

	@Override
	public AbstractBigCannonProjectile getProjectile(Level level, List<StructureBlockInfo> projectileBlocks) {
		ShrapnelShellProjectile projectile = CBCEntityTypes.SHRAPNEL_SHELL.create(level);
		projectile.setFuze(getFuze(projectileBlocks));
		return projectile;
	}

	@Override public EntityType<?> getAssociatedEntityType() { return CBCEntityTypes.SHRAPNEL_SHELL.get(); }

}
