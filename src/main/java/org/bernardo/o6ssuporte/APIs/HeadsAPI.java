package org.bernardo.o6ssuporte.APIs;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public enum HeadsAPI {

	// mreren103 -> Cabeça Discord

	ARROW_LEFT("MHF_ArrowLeft"), ARROW_RIGHT("MHF_ArrowRight"), ARROW_UP("MHF_ArrowUp"), ARROW_DOWN("MHF_ArrowDown"),
	QUESTION("MHF_Question"), EXCLAMATION("MHF_Exclamation"), CAMERA("FHG_Cam"), ZOMBIE_PIGMAN("MHF_PigZombie"),
	PIG("MHF_Pig"), SHEEP("MHF_Sheep"), BLAZE("MHF_Blaze"), CHICKEN("MHF_Chicken"), COW("MHF_Cow"), SLIME("MHF_Slime"),
	SPIDER("MHF_Spider"), SQUID("MHF_Squid"), VILLAGER("MHF_Villager"), OCELOT("MHF_Ocelot"),
	HEROBRINE("MHF_Herobrine"), LAVA_SLIME("MHF_LavaSlime"), MOOSHROOM("MHF_MushroomCow"), GOLEM("MHF_Golem"),
	GHAST("MHF_Ghast"), ENDERMAN("MHF_Enderman"), CAVE_SPIDER("MHF_CaveSpider"), CACTUS("MHF_Cactus"),
	CHEST("MHF_Chest"), MELON("MHF_Melon"), LOG("MHF_OakLog"), PUMPKIN("MHF_Pumpkin"), TNT("MHF_TNT"), TNT2("MHF_TNT2");

	private String id;

	HeadsAPI(String string) {
		// TODO Auto-generated constructor stub
	}

	/*private void Skull(String id) {
		this.id = id;
	}*/

	public static ItemStack getPlayerSkull(String name) {
		ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
		meta.setOwner(name);
		itemStack.setItemMeta((ItemMeta) meta);
		return itemStack;
	}

	public String getId() {
		return this.id;
	}

	public ItemStack getSkull() {
		ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
		meta.setOwner(this.id);
		itemStack.setItemMeta((ItemMeta) meta);
		return itemStack;
	}
}