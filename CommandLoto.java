package tchestplate.commands;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Compile.CompileFlag;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import lavacraft.LavaCraft;
import lavacraft.container.ContainerBackpack;
import lavacraft.tile.TileWheelOfFortune;
import lavamobs.LavaModMobs;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.server.MinecraftServer;
import tchestplate.IWeapon;
import tchestplate.LavaChestPlate;
import tchestplate.Utils;
import tchestplate.entities.projectile.EntityParashutRadiation;
import tchestplate.items.ItemPets;
import tchestplate.packets.PacketMAUpdateInt;
import tchestplate.packets.PacketMAUpdateLvL;
import tchestplate.player.ExtendedPlayer;

public class CommandLoto implements ICommand 
{

	private List aliases = new ArrayList();
	private List<String> members = new ArrayList<String>();
	private String[] names = new String[2];

	public String getCommandName() 
	{
		return "loto";
	}

	public String getCommandUsage(ICommandSender icommandsender) 
	{
		return "loto";
	}

	public List getCommandAliases() 
	{
		return this.aliases;
	}

	public void processCommand(ICommandSender ics, String[] args) 
	{
		if(CompileFlag.SERVER) 
		{
			//========================================//
			if(ics instanceof EntityPlayer) 
			{
				if(args.length == 0)
				{
					ics.sendChatToPlayer("§c[Лото] §eИспользование: §c/loto help");
				}
				else if(args[0].equalsIgnoreCase("help"))
				{
					ics.sendChatToPlayer("§7§l[LOTO] §c§lИнформация о лото...");
					ics.sendChatToPlayer("§7§l[LOTO] §e/loto battle §c<Игрок> §e- Отправить приглашение");
					ics.sendChatToPlayer("§7§l[LOTO] §e/loto cancel §e- Отменить приглашение");
				}
				else if(args[0].equalsIgnoreCase("cancel"))
				{

					//========================================//
					String name_sender = ((EntityPlayer)ics).getEntityName();
					//========================================//

					if(members.contains(name_sender))
					{
						ics.sendChatToPlayer("§c[Лото] Отменили участие, удалены из списка...");
						members.remove(name_sender);
					}
					else 
					{
						ics.sendChatToPlayer("§c[Лото] Вас нет в списке участников...");
					}
				}
				else if(args[0].equalsIgnoreCase("battle") && args.length > 1)
				{

					//========================================//
					String name_sender = ((EntityPlayer)ics).getEntityName();
					String name_receiver = Utils.getPlayer(args[1]).getEntityName();

					ItemStack st_sender = ((EntityPlayer)ics).getCurrentEquippedItem();
					ItemStack st_receiver = Utils.getPlayer(args[1]).getCurrentEquippedItem();
					//========================================//

					if(name_sender != name_receiver) 
					{
						if(st_sender != null)
						{
							if(st_sender.getItem() instanceof IWeapon)
							{
								if(!members.contains(name_sender))
								{
									ics.sendChatToPlayer("§c[Лото] §eОтправили запрос §4" + name_receiver + " §eна предмет §4" + st_sender.getDisplayName() + " §eапгрейд оружия §4+" + st_sender.stackTagCompound.getByte("enhance") + "§e!");
									ics.sendChatToPlayer("§c[Лото] §eОжидаем подтверждения...");
									//
									names[0] = name_sender;
									names[1] = name_receiver;

									members.add(names[0]);
									members.add(names[1]);
									//
									Utils.getPlayer(args[1]).sendChatToPlayer("§c[Лото] §eВам отправил запрос §4" + name_sender + " §eна предмет §4" + st_sender.getDisplayName() + " §eапгрейд оружия §4+" + st_sender.stackTagCompound.getByte("enhance") + "§e!");
									Utils.getPlayer(args[1]).sendChatToPlayer("§c[Лото] §eДля того чтобы принять игру, держа такой же предмет в руке пропишите §c/loto accept " + name_sender);
								}
								else
								{
									ics.sendChatToPlayer("§c[Лото] §cВы уже отправили запрос! §eОжидаем подтверждения...");
									ics.sendChatToPlayer("§c[Лото] §cОтменить запрос §e/loto cancel§c...");
								}
							}
							else 
							{
								ics.sendChatToPlayer("§c[Лото] Можно играть только на Лава лук, РБ лук, Лава ультима лук, УС копьё, Демон копьё, Сюрикэн шот, новые оружия...");
							}
						} 
						else
						{
							ics.sendChatToPlayer("§c[Лото] Ошибка! Возьмите предмет в руку...");
						}
					}
					else 
					{
						ics.sendChatToPlayer("§c[Лото] Самому себе нельзя отправить приглашение...");
					}
				}
				else if(args[0].equalsIgnoreCase("accept") && args.length > 1) 
				{

					//========================================//
					EntityPlayer pl_sender = Utils.getPlayer(names[0]);
					EntityPlayer pl_receiver = Utils.getPlayer(names[1]);

					String name_sender = ((EntityPlayer)ics).getEntityName();
					String name_receiver = Utils.getPlayer(args[1]).getEntityName();
					//========================================//

					if(name_receiver == names[0] && name_sender == names[1])
					{
						if(members.size() != 0 && members.contains(names[0]) && members.contains(names[1])) 
						{
							ItemStack item_sender = pl_sender.getCurrentEquippedItem();
							ItemStack item_receiver = pl_receiver.getCurrentEquippedItem();

							if(item_sender == null || item_receiver == null)
							{
								pl_sender.sendChatToPlayer("§c[Лото] Игра отменена..");
								members.remove(names[0]);
								pl_receiver.sendChatToPlayer("§c[Лото] Игра отменена..");
								members.remove(names[1]);
							}

							if(item_sender.getItem() != null && item_receiver != null && item_sender.getItem() == item_receiver.getItem()
									&& item_sender.stackTagCompound != null && item_receiver.stackTagCompound != null
									&& item_sender.stackTagCompound.getByte("enhance") == item_receiver.stackTagCompound.getByte("enhance"))
							{

								if(Utils.randomize(50.0D))
								{
									if(pl_sender.inventory.getFirstEmptyStack() == -1)
									{
										pl_sender.sendChatToPlayer("§c[Лото] У вас заполненный инвентарь, выигранный итем дропнулся под вами!");
										pl_sender.entityDropItem(item_receiver, 0.0f);
										members.remove(names[0]);
									}
									else
									{
										pl_sender.inventory.addItemStackToInventory(item_receiver);
										pl_sender.inventoryContainer.detectAndSendChanges();
										pl_sender.sendChatToPlayer("§c[Лото] §eПоздравляем вы выиграли §c" + item_receiver.getDisplayName() + " §e+" + item_receiver.stackTagCompound.getByte("enhance") + "!");
										members.remove(names[0]);
									}

									pl_receiver.sendChatToPlayer("§c[Лото] §eВы проиграли §c" + item_receiver.getDisplayName() + " §e+" + item_receiver.stackTagCompound.getByte("enhance") + "!");
									pl_receiver.getCurrentEquippedItem().equals(null);
									pl_receiver.inventoryContainer.detectAndSendChanges();
									members.remove(names[1]);
								}
								else 
								{
									if(pl_receiver.inventory.getFirstEmptyStack() == -1) 
									{
										pl_receiver.sendChatToPlayer("§c[Лото] У вас заполненный инвентарь, выигранный итем дропнулся под вами!");
										pl_receiver.entityDropItem(item_sender, 0.0f);
										members.remove(names[1]);
									}
									else
									{
										pl_receiver.inventory.addItemStackToInventory(item_sender);
										pl_receiver.inventoryContainer.detectAndSendChanges();
										pl_receiver.sendChatToPlayer("§c[Лото] §eПоздравляем вы выиграли §c" + item_sender.getDisplayName() + " §e+" + item_sender.stackTagCompound.getByte("enhance") + "!");
										members.remove(names[1]);
									}

									pl_sender.sendChatToPlayer("§c[Лото] §eВы проиграли §c" + item_sender.getDisplayName() + " §e+" + item_sender.stackTagCompound.getByte("enhance") + "!");
									pl_sender.getCurrentEquippedItem().equals(null);
									pl_sender.inventoryContainer.detectAndSendChanges();
									members.remove(names[0]);
								}
							}

							else
							{
								pl_sender.sendChatToPlayer("§c[Лото] Итем или апгрейд не совпадает...");
								members.remove(names[0]);
								pl_receiver.sendChatToPlayer("§c[Лото] Итем или апгрейд не совпадает...");
								members.remove(names[1]);

							}
						}						
						else 
						{
							ics.sendChatToPlayer("§c[Лото] §cОшибка! Нету приглашений, не правильно указан ник или игрок отменил участие...");
							members.remove(name_sender);
						}
					}
					else
					{
						ics.sendChatToPlayer("§c[Лото] Ошибка! Не правильно введенный игрок...");
					}
				}	
				else
				{
					ics.sendChatToPlayer("§c[Лото] §cОшибка! Нету приглашений, или не правильно указан ник...");
				}
			}
			//========================================//
		}
	}

	public boolean canCommandSenderUseCommand(ICommandSender ics) { return true; }
	public List addTabCompletionOptions(ICommandSender ics, String[] astring) { return null; }
	public boolean isUsernameIndex(String[] astring, int i) {;return false; }
	public int compareTo(Object o) { return 0; }
}
