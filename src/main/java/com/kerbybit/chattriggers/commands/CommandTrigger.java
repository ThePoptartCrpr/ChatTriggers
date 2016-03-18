package com.kerbybit.chattriggers.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.kerbybit.chattriggers.chat.ChatHandler;
import com.kerbybit.chattriggers.file.FileHandler;
import com.kerbybit.chattriggers.file.UpdateHandler;
import com.kerbybit.chattriggers.globalvars.global;
import com.kerbybit.chattriggers.triggers.EventsHandler;
import com.kerbybit.chattriggers.triggers.StringHandler;
import com.kerbybit.chattriggers.triggers.TagHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class CommandTrigger extends CommandBase {

	public String getCommandName() {return "trigger";}

	public void processCommand(ICommandSender sender, String[] args) throws CommandException {doCommand(args, false);}
	
	public int getRequiredPermissionLevel() {return 0;}

	public String getCommandUsage(ICommandSender sender) {return "/trigger [create/add/list] <...>";}
	
	public static void doCommand(String args[], Boolean silent) {
		if (args.length == 0) {
			ChatHandler.warn(ChatHandler.color("red", "/trigger [create/add/list] <...>"));
			ChatHandler.warn(ChatHandler.color("red", "/trigger [string/run] <...>"));
			ChatHandler.warn(ChatHandler.color("red", "/trigger [save/load]"));
			ChatHandler.warn(ChatHandler.color("red", "/trigger [import/export] <...>"));
		} else if (args[0].equalsIgnoreCase("IMPORT")) {
			commandImport(args, silent);
		} else if (args[0].equalsIgnoreCase("EXPORT")) {
			ChatHandler.warn(ChatHandler.color("red", "Command currently getting changed! Check back in a newer version."));
		} else if (args[0].equalsIgnoreCase("RUN")) {
			commandRun(args, silent);
		} else if (args[0].equalsIgnoreCase("CREATE")) {
			commandCreate(args, silent);
		} else if (args[0].equalsIgnoreCase("DELETE")) {
			commandDelete(args, silent);
		} else if (args[0].equalsIgnoreCase("ADD")) {
			commandAdd(args, silent);
		} else if (args[0].equalsIgnoreCase("REMOVE")) {
			commandRemove(args, silent);
		} else if (args[0].equalsIgnoreCase("STRING")) {
			commandString(args, silent);
		} else if (args[0].equalsIgnoreCase("LIST")) {
			commandList(args, silent);
		} else if (args[0].equalsIgnoreCase("SETTINGS")) {
			commandSettings(args, silent);
		} else if (args[0].equalsIgnoreCase("SAVE")) {
			commandSave(args, silent);
		} else if (args[0].equalsIgnoreCase("LOAD")) {
			commandLoad(args, silent);
		} else if (args[0].equalsIgnoreCase("TESTIMPORT")) {
			if (args.length < 2) {
				ChatHandler.warn(ChatHandler.color("red", "/trigger testImport [import name]"));
			} else {
				Boolean canTest = true;
				for (int i=1; i<args.length; i++) {
					String dir = "./mods/ChatTriggers/Imports/"+args[i]+".txt";
					File f = new File(dir);
					if (!f.exists() || !f.isFile()) {
						canTest=false;
						ChatHandler.warn(ChatHandler.color("red", args[i]+".txt is not an import! Could not start test mode"));
					}
				}
				
				if (canTest) {
					if (global.canSave==true) {
						global.trigger.clear(); global.USR_string.clear(); global.TMP_string.clear();
						global.waitEvents.clear(); global.asyncEvents.clear();
						for (int i=1; i<args.length; i++) {
							ChatHandler.warn(ChatHandler.color(global.settings.get(0), "You are now in testing mode for import '"+args[i]+"'"));
						}
						ChatHandler.warn(ChatHandler.color(global.settings.get(0), "To reload all of your triggers and strings, do </trigger load>"));
						try {
							for (int i=1; i<args.length; i++) {
								String dir = "./mods/ChatTriggers/Imports/"+args[i]+".txt";
								File f = new File(dir);
								global.trigger.addAll(FileHandler.loadTriggers(dir, true));
							}
						} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Unable to load import!")); e.printStackTrace();}
						global.canSave = false;
					} else {
						ChatHandler.warn(ChatHandler.color("red", "You must leave test mode before testing another trigger!"));
						ChatHandler.warn(ChatHandler.color("red", "</trigger load> to leave testing mode"));
					}
				} else {
					ChatHandler.warn(ChatHandler.color("red", "Not an import!"));
				}
			}
		} else if (args[0].equalsIgnoreCase("TEST")) {
			ChatHandler.warn(ChatHandler.color("&7", "This command does nothing :D"));
		} else {
			ChatHandler.warn(ChatHandler.color("red", "/trigger [create/add/list] <...>"));
			ChatHandler.warn(ChatHandler.color("red", "/trigger [string/run] <...>"));
			ChatHandler.warn(ChatHandler.color("red", "/trigger [save/load]"));
			ChatHandler.warn(ChatHandler.color("red", "/trigger [import/export] <...>"));
		}
	}
	
	public static void commandImport(String args[], Boolean silent) {
		if (args.length>=2) {
			for (int i=1; i<args.length; i++) {global.neededImports.add(args[i]);}
		} else {ChatHandler.warn(ChatHandler.color("red", "/trigger import <import name>"));}
	}
	
	public static void commandRun(String args[], Boolean silent) {
		if (args.length < 2) {
			ChatHandler.warn(ChatHandler.color("red", "/tr <trigger>"));
			ArrayList<List<String>> listCommands = new ArrayList<List<String>>();
			ArrayList<List<String>> showCommands = new ArrayList<List<String>>();
			for (int i=0; i<global.trigger.size(); i++) {
				if (global.trigger.get(i).get(1).contains("<imported>") && global.trigger.get(i).get(1).contains("<list=")) {
					if (global.trigger.get(i).get(0).trim().toUpperCase().startsWith("OTHER ")) {
						String toShow = global.trigger.get(i).get(0).trim();
						toShow = toShow.substring(toShow.toUpperCase().indexOf("OTHER ")+6);
						String inList = global.trigger.get(i).get(1);
						inList = inList.substring(inList.indexOf("<list=")+6, inList.indexOf(">", inList.indexOf("<list=")));
						ArrayList<String> temporary = new ArrayList<String>();
						temporary.add(inList);
						temporary.add(toShow);
						listCommands.add(temporary);
					}
				}
			}
			if (listCommands.size() != 0) {
				ChatHandler.warn(ChatHandler.color("white", "Available imported commands"));
				for (int i=0; i<listCommands.size(); i++) {
					Boolean isNewList = false;
					int isInList = -1;
					if (showCommands.size()>0) {
						for (int j=0; j<showCommands.size(); j++) {
							if (showCommands.get(j).get(0).equals(listCommands.get(i).get(0))) {isInList=j;}
						}
						if (isInList == -1) {isNewList = true;}
					} else {isNewList = true;}
					if (isNewList) {
						ArrayList<String> temporary = new ArrayList<String>();
						temporary.add(listCommands.get(i).get(0));
						temporary.add(listCommands.get(i).get(1));
						showCommands.add(temporary);
					} else {showCommands.get(isInList).add(listCommands.get(i).get(1));}
				}
				for (int i=0; i<showCommands.size(); i++) {
					ChatHandler.warn(ChatHandler.color("white",showCommands.get(i).get(0)));
					for (int j=1; j<showCommands.get(i).size(); j++) {
						ChatHandler.warn(ChatHandler.color(global.settings.get(0), "  /tr "+showCommands.get(i).get(j)));
					}
				}
			}
		} else {
			String TMP_e = "";
			for (int i=1; i<args.length; i++) {
				if (i==args.length-1) {TMP_e += args[i];} 
				else {TMP_e += args[i] + " ";}
			}
			ClientChatReceivedEvent e = null;
			try {
				int num = Integer.parseInt(args[1]);
				if (num >= 0 && num < global.trigger.size()) {
					List<String> TMP_events = new ArrayList<String>();
					for (int i=2; i<global.trigger.get(num).size(); i++) {
						TMP_events.add(global.trigger.get(num).get(i));
					}
					EventsHandler.doEvents(TMP_events, e);
				}
			} catch (NumberFormatException e1) {
				for (int k=0; k<global.trigger.size(); k++) {
					
					String TMP_trig = global.trigger.get(k).get(1);
					
					TMP_trig = TagHandler.removeTags(TMP_trig);
					
					if (TMP_trig.equals(TMP_e)) {
						List<String> TMP_events = new ArrayList<String>();
						for (int i=2; i<global.trigger.get(k).size(); i++) {
							TMP_events.add(global.trigger.get(k).get(i));
						}
						EventsHandler.doEvents(TMP_events, e);
					} else {
						String TMP_trigtype = global.trigger.get(k).get(0);
						if (TMP_trigtype.toUpperCase().startsWith("OTHER")) {
							if (TMP_trig.contains("(") && TMP_trig.endsWith(")")) {
								String TMP_trigtest = TMP_trig.substring(0,TMP_trig.indexOf("("));
								if (TMP_e.startsWith(TMP_trigtest) && TMP_e.endsWith(")")) {
									String TMP_argsIn = TMP_e.substring(TMP_e.indexOf("(")+1, TMP_e.length()-1);
									String TMP_argsOut = TMP_trig.substring(TMP_trig.indexOf("(")+1, TMP_trig.length()-1);
									String[] argsIn = TMP_argsIn.split(",");
									String[] argsOut = TMP_argsOut.split(",");
									if (argsIn.length == argsOut.length) {
										List<String> TMP_events = new ArrayList<String>();
										for (int j=2; j<global.trigger.get(k).size(); j++) {
											TMP_events.add(global.trigger.get(k).get(j));
										}
										EventsHandler.doEvents(TMP_events, e, argsOut, argsIn);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static void commandCreate(String args[], Boolean silent) {
		if (args.length < 3) {
			ChatHandler.warn(ChatHandler.color("red", "/trigger create <type> <trigger>"));
		} else {
			String TMP_type = args[1];
			String TMP_trig = "";
			for (int i=2; i<args.length; i++) {
				if (i==args.length-1) {TMP_trig += args[i];}
				else {TMP_trig += args[i] + " ";}
			}
			List<String> TMP_l = new ArrayList<String>();
			TMP_l.add(TMP_type);
			TMP_l.add(TMP_trig);
			if (TMP_type.equalsIgnoreCase("CHAT") 
			|| TMP_type.equalsIgnoreCase("OTHER")
			|| TMP_type.equalsIgnoreCase("ONWORLDLOAD")
			|| TMP_type.equalsIgnoreCase("ONWORLDFIRSTLOAD")
			|| TMP_type.equalsIgnoreCase("ONSERVERCHANGE")
			|| TMP_type.equalsIgnoreCase("ONNEWDAY")
			|| TMP_type.equalsIgnoreCase("ONCLIENTTICK")) {
				global.trigger.add(TMP_l);
				int TMP_num = global.trigger.size() - 1;
				if (silent==false) {
					List<String> TMP_out = new ArrayList<String>();
					TMP_trig = TMP_trig.replace("'", "\\'");
					TMP_type = TMP_type.replace("'", "\\'");
					TMP_out.add("text:'Created trigger ',color:gray");
					TMP_out.add("text:'" + TMP_trig + " ',color:"+global.settings.get(1));
					TMP_out.add("text:'with trigger type ',color:gray");
					TMP_out.add("text:'" + TMP_type + " ',color:"+global.settings.get(1));
					TMP_out.add("text:'(" + TMP_num + ")',color:gray,hoverEvent:{action:'show_text',value:'Add an event'},clickEvent:{action:'suggest_command',value:'/trigger add " + TMP_num + " '}");
					ChatHandler.sendJson(TMP_out);
				}
				try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
			} else {
				ChatHandler.warn(ChatHandler.color("red", "Not a valid trigger type"));
			}
		}
	}
	
	public static void commandDelete(String args[], Boolean silent) {
		if (args.length < 2) {
			ChatHandler.warn(ChatHandler.color("red", "/trigger delete <trigger number>"));
		} else {
			int num = -1;
			try {num = Integer.parseInt(args[1]);} 
			catch(NumberFormatException e) {num = -1;}
			if (num>-1 && num<global.trigger.size()) {
				String TMP_rem = global.trigger.get(num).get(1);
				if (!TMP_rem.contains("<imported>")) {
					if (silent==false) {
						ChatHandler.warnUnformatted(ChatHandler.color("gray", "Deleted trigger") + " " + ChatHandler.color(global.settings.get(0), TMP_rem) + " " + ChatHandler.color("gray", "and all of its events"));
					}
					global.trigger.remove(num).get(1);
					try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
				} else {
					ChatHandler.warnUnformatted(ChatHandler.color("red", "You cannot edit imported triggers!"));
					ChatHandler.warnUnformatted(ChatHandler.color("red", "You must edit them from the imported file!"));
				}
			} else {
				ChatHandler.warn(ChatHandler.color("red", "/trigger delete <trigger number>"));
			}
		}
	}
	
	public static void commandAdd(String args[], Boolean silent) {
		if (args.length < 3) {
			ChatHandler.warn(ChatHandler.color("red", "/trigger add <trigger number> <event> <event argument(s)>"));
		} else {
			int num = -1;
			try {num = Integer.parseInt(args[1]);} 
			catch(NumberFormatException e) {num = -1;}
			String TMP_e = "";
			for (int i=2; i<args.length; i++) {
				if (i==args.length-1) {TMP_e += args[i];}
				else {TMP_e += args[i] + " ";}
			}
			if (num>-1 && num<global.trigger.size()) {
				if (!global.trigger.get(num).get(1).contains("<imported>")) {
					String TMP_etype = TMP_e;
					if (TMP_e.contains(" ")) {TMP_etype = TMP_e.substring(0,TMP_e.indexOf(" "));}
					
					if (TMP_etype.equalsIgnoreCase("CHAT")
					|| TMP_etype.equalsIgnoreCase("CANCEL") 
					|| TMP_etype.equalsIgnoreCase("CHOOSE") 
					|| TMP_etype.equalsIgnoreCase("KILLFEED")
					|| TMP_etype.equalsIgnoreCase("NOTIFY")
					|| TMP_etype.equalsIgnoreCase("TRIGGER") 
					|| TMP_etype.equalsIgnoreCase("SOUND")
					|| TMP_etype.equalsIgnoreCase("COPY")
					|| TMP_etype.equalsIgnoreCase("URL")
					|| TMP_etype.equalsIgnoreCase("DO")
					|| TMP_etype.equalsIgnoreCase("IF")
					|| TMP_etype.equalsIgnoreCase("ELSE")
					|| TMP_etype.equalsIgnoreCase("ELSEIF")
					|| TMP_etype.equalsIgnoreCase("FOR")
					|| TMP_etype.equalsIgnoreCase("WAIT")
					|| TMP_etype.equalsIgnoreCase("END")
					|| TMP_etype.equalsIgnoreCase("ASYNC")) {
						global.trigger.get(num).add(TMP_e);
						if (silent==false) {
							ChatHandler.warnUnformatted(ChatHandler.color("gray", "Added event") + " " + ChatHandler.color(global.settings.get(0), TMP_e) + " " + ChatHandler.color("gray", "to trigger") + " " + ChatHandler.color(global.settings.get(0), global.trigger.get(num).get(1)));
						}
						try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
					} else {
						ChatHandler.warn(ChatHandler.color("red", "Not a valid event type!"));
					}
				} else {
					ChatHandler.warnUnformatted(ChatHandler.color("red", "You cannot edit imported triggers!"));
					ChatHandler.warnUnformatted(ChatHandler.color("red", "You must edit them from the imported file!"));
				}
			} else {
				ChatHandler.warn(ChatHandler.color("red", "/trigger add <trigger number> <event> <event argument(s)>"));
			}
		}
	}
	
	public static void commandRemove(String args[], Boolean silent) {
		if (args.length < 3) {
			ChatHandler.warn(ChatHandler.color("red", "/trigger remove [trigger number] [event number]"));
		} else {
			int num = -1;
			try {num = Integer.parseInt(args[1]);} 
			catch(NumberFormatException e) {num = -1;}
			if (num>-1 && num<global.trigger.size()) {
				if (!global.trigger.get(num).get(1).contains("<imported>")) {
					int num2 = 1;
					try {num2 = Integer.parseInt(args[2]);}
					catch(NumberFormatException e) {num2 = 1;}
					if (num2>1 && num2<global.trigger.get(num).size()) {
						String TMP_rem = global.trigger.get(num).remove(num2);
						if (silent==false) {
							ChatHandler.warnUnformatted(ChatHandler.color("gray", "Removed event") + " " + ChatHandler.color(global.settings.get(0), TMP_rem) + " " + ChatHandler.color("gray", "from trigger") + " " + ChatHandler.color(global.settings.get(0), global.trigger.get(num).get(1)));
						}
						try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
					} else {
						ChatHandler.warn(ChatHandler.color("red", "/trigger remove [trigger number] [event number]"));
					}
				} else {
					ChatHandler.warnUnformatted(ChatHandler.color("red", "You cannot edit imported triggers!"));
					ChatHandler.warnUnformatted(ChatHandler.color("red", "You must edit them from the imported file!"));
				}
			} else {
				ChatHandler.warn(ChatHandler.color("red", "/trigger remove [trigger number] [event number]"));
			}
		}
	}
	
	public static void commandString(String args[], Boolean silent) {
		if (args.length < 2) {
			ChatHandler.warn(ChatHandler.color("red", "/trigger string [create/set/list] <...>"));
			ChatHandler.warn(ChatHandler.color("red", "/trigger string <string name>"));
		} else {
			if (args[1].equalsIgnoreCase("CREATE")) {
				if (args.length != 3) {
					ChatHandler.warn(ChatHandler.color("red", "/trigger string create <string name>"));
				} else {
					if (global.canSave) {
						String TMP_sn = args[2];
						String TMP_list="";
						if (TMP_sn.contains("<list=") && TMP_sn.contains(">")) {
							TMP_list = TMP_sn.substring(TMP_sn.indexOf("<list=")+6, TMP_sn.indexOf(">",TMP_sn.indexOf("<list=")));
							TMP_sn = TMP_sn.replace("<list="+TMP_list+">", "");
						}
						
						Boolean isString = false;
						for (List<String> value : global.USR_string) {
							if (value.get(0).equals(TMP_sn)) {
								isString = true;
								if (TMP_list!="") {
									if (value.size()==2) {value.add(TMP_list);} 
									else {value.set(3, TMP_list);}
								}
							}
						}
						if (isString) {
							ChatHandler.warn(ChatHandler.color("red", TMP_sn + " already exists!"));
						} else {
							List<String> TMP_l = new ArrayList<String>();
							TMP_l.add(TMP_sn);
							TMP_l.add("");
							if (TMP_list!="") {TMP_l.add(TMP_list);}
							global.USR_string.add(TMP_l);
							if (silent==false) {
								TMP_sn = TMP_sn.replace("'", "\\'");
								List <String> TMP_out = new ArrayList<String>();
								TMP_out.add("text:'Created string ',color:gray");
								TMP_out.add("text:'"+TMP_sn+"',color:"+global.settings.get(1)+",clickEvent:{action:'suggest_command',value:'/trigger string set "+TMP_sn+" '},hoverEvent:{action:'show_text',value:'Set "+TMP_sn+"'}");
								ChatHandler.sendJson(TMP_out);
							}
							try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
						}
					} else {
						ChatHandler.warn(ChatHandler.color("red", "cannot create string while in test mode"));
						ChatHandler.warn(ChatHandler.color("red", "</trigger load> to leave testing mode"));
					}
				}
			} else if (args[1].equalsIgnoreCase("DELETE")) {
				if (args.length < 3) {
					ChatHandler.warn(ChatHandler.color("red", "/trigger string delete <string name>"));
				} else {
					if (global.canSave) {
						int num = -1;
						try {num = Integer.parseInt(args[2]);}
						catch(NumberFormatException e) {
							for (int i=0; i<global.USR_string.size(); i++) {
								if (args[2].equals(global.USR_string.get(i).get(0))) {
									num = i;
								}
							}
						}
						if (num>-1 && num<global.USR_string.size()) {
							String TMP_rem = global.USR_string.remove(num).get(0);
							if (silent==false) {
								ChatHandler.warnUnformatted(ChatHandler.color("gray", "Deleted string") + " " + ChatHandler.color(global.settings.get(0), TMP_rem));
							}
							try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
						}
					} else {
						ChatHandler.warn(ChatHandler.color("red", "cannot delete string while in test mode"));
						ChatHandler.warn(ChatHandler.color("red", "</trigger load> to leave testing mode"));
					}
				}
			} else if (args[1].equalsIgnoreCase("SET")) {
				if (args.length < 4) {
					ChatHandler.warn(ChatHandler.color("red", "/trigger string set <string name> <string>"));
				} else {
					int num = -1;
					try {num = Integer.parseInt(args[2]);} 
					catch(NumberFormatException e) {
						for (int i=0; i<global.USR_string.size(); i++) {
							if (args[2].equals(global.USR_string.get(i).get(0))) {num = i;}
						}
					}
					String TMP_s = "";
					for (int i=3; i<args.length; i++) {
						if (i==args.length-1) {TMP_s += args[i];}
						else {TMP_s += args[i] + " ";}
					}
					if (num>-1 && num<global.USR_string.size()) {
						if (silent==false) {ChatHandler.warnUnformatted(ChatHandler.color("gray", "Set value") + " " + ChatHandler.color(global.settings.get(0), TMP_s) + " " + ChatHandler.color("gray", "in string") + " " + ChatHandler.color(global.settings.get(0), global.USR_string.get(num).get(0)));}
						if (TMP_s.equals("{null}")) {TMP_s = "";}
						global.USR_string.get(num).set(1,TMP_s);
						try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
					} else {ChatHandler.warn(ChatHandler.color("red", "/trigger string set [string number] [string]"));}
				}
			} else if (args[1].equalsIgnoreCase("LIST")) {
				if (args.length==2) {
					String dashes = "";
					for (int j=0; j<Math.floor((((280*(Minecraft.getMinecraft().gameSettings.chatWidth))+40)/320)*51); j++) {dashes += "-";}
					ChatHandler.warn(ChatHandler.color(global.settings.get(0), "&m-"+dashes));
					if (global.USR_string.size() == 0) {
						ChatHandler.warnUnformatted(ChatHandler.color("red", "No strings created"));
						ChatHandler.warnUnformatted(ChatHandler.color("red", "Do </trigger string> to get started"));
					} else {
						List<String> STR_lists = new ArrayList<String>();
						for (int i=0; i<global.USR_string.size(); i++) {
							if (global.USR_string.get(i).size()!=3) {
								List<String> TMP_out = new ArrayList<String>();
								String TMP_sn = global.USR_string.get(i).get(0);
								TMP_sn = TMP_sn.replace("'", "\\'");
								TMP_out.add("text:'" + i + "> ',color:gray,hoverEvent:{action:'show_text',value:'Set string'},clickEvent:{action:'suggest_command',value:'/trigger string set " + i + " '}");
								TMP_out.add("text:'" + TMP_sn + " ',color:"+global.settings.get(1));
								if (global.canSave) {TMP_out.add("text:'-',color:red,hoverEvent:{action:'show_text',value:'Delete string'},clickEvent:{action:'suggest_command',value:'/trigger string delete " + i + " [enter to confirm]'}");}
								ChatHandler.sendJson(TMP_out);
								ChatHandler.warnUnformatted(ChatHandler.color("gray", "  " + global.USR_string.get(i).get(1)));
							} else {
								if (STR_lists.size()==0) {
									STR_lists.add(global.USR_string.get(i).get(2));
								} else {
									Boolean isList = false;
									for (int j=0; j<STR_lists.size(); j++) {
										if (STR_lists.get(j).equals(global.USR_string.get(i).get(2))) {isList = true;}
									}
									if (!isList) {STR_lists.add(global.USR_string.get(i).get(2));}
								}
							}
						}
						for (int i=0; i<STR_lists.size(); i++) {
							List<String> TMP_out = new ArrayList<String>();
							String TMP_list = STR_lists.get(i);
							TMP_list = TMP_list.replace("'", "\\'");
							TMP_out.add("text:'List> ',color:gray,hoverEvent:{action:'show_text',value:'Show "+TMP_list+" strings'},clickEvent:{action:'run_command',value:'/trigger string list "+TMP_list+"'}");
							TMP_out.add("text:'"+TMP_list+"',color:"+global.settings.get(1));
							ChatHandler.sendJson(TMP_out);
						}
					}
					ChatHandler.warn(ChatHandler.color(global.settings.get(0), "&m"+dashes+"&r" + global.settings.get(0) + "^"));
				} else {
					String dashes = "";
					for (int j=0; j<Math.floor((((280*(Minecraft.getMinecraft().gameSettings.chatWidth))+40)/320)*51); j++) {dashes += "-";}
					ChatHandler.warn(ChatHandler.color(global.settings.get(0), "&m-"+dashes));
					String showList = "";
					for (int i=2; i<args.length; i++) {showList += args[i]+" ";} 
					showList=showList.trim();
					for (int i=0; i<global.USR_string.size(); i++) {
						if (global.USR_string.get(i).size()==3) {
							if (global.USR_string.get(i).get(2).equals(showList)) {
								List<String> TMP_out = new ArrayList<String>();
								String TMP_sn = global.USR_string.get(i).get(0);
								TMP_sn = TMP_sn.replace("'", "\\'");
								TMP_out.add("text:'" + i + "> ',color:gray,hoverEvent:{action:'show_text',value:'Set string'},clickEvent:{action:'suggest_command',value:'/trigger string set " + i + " '}");
								TMP_out.add("text:'" + TMP_sn + " ',color:"+global.settings.get(1));
								if (global.canSave) {TMP_out.add("text:'-',color:red,hoverEvent:{action:'show_text',value:'Delete string'},clickEvent:{action:'suggest_command',value:'/trigger string delete " + i + " [enter to confirm]'}");}
								ChatHandler.sendJson(TMP_out);
								ChatHandler.warnUnformatted(ChatHandler.color("gray", "  " + global.USR_string.get(i).get(1)));
							}
						}
					}
					ChatHandler.warn(ChatHandler.color(global.settings.get(0), "&m"+dashes+"&r" + global.settings.get(0) + "^"));
				}
			} else {
				if (args.length==2) {
					if (StringHandler.getStringNum(args[1])==-1) {
						ChatHandler.warn(ChatHandler.color("red", "Not a string!"));
					} else {
						ChatHandler.warnUnformatted(ChatHandler.color("gray","value:") + " " + ChatHandler.color(global.settings.get(0), global.USR_string.get(StringHandler.getStringNum(args[1])).get(1)));
					}
				} else {
					ChatHandler.warn(ChatHandler.color("red", "/trigger string [create/set/list] <...>"));
					ChatHandler.warn(ChatHandler.color("red", "/trigger string <string name>"));
				}
			}
		}
	}
	
	public static void commandList(String args[], Boolean silent) {
		if (args.length==1) {
			String dashes = "";
			for (int j=0; j<Math.floor((((280*(Minecraft.getMinecraft().gameSettings.chatWidth))+40)/320)*51); j++) {dashes += "-";}
			ChatHandler.warn(ChatHandler.color(global.settings.get(0), "&m-"+dashes));
			if (global.trigger.size() == 0) {
				ChatHandler.warn(ChatHandler.color("red", "No triggers created"));
				ChatHandler.warn(ChatHandler.color("red", "Do </trigger> to get started"));
			} else {
				List<String> TMP_lists = new ArrayList<String>();
				for (int i=0; i<global.trigger.size(); i++) {
					String TMP_type = global.trigger.get(i).get(0);
					String TMP_trig = global.trigger.get(i).get(1);
					String TMP_list = "";
					String TMP_w    = "";
					String TMP_server = "";
					Boolean TMP_imported = false;
					Boolean TMP_formatted = false;
					
					if (TMP_trig.contains("<s>")) {TMP_w = "start"; TMP_trig = TMP_trig.replace("<s>", "");}
					if (TMP_trig.contains("<c>")) {TMP_w = "contain"; TMP_trig = TMP_trig.replace("<c>", "");}
					if (TMP_trig.contains("<e>")) {TMP_w = "end"; TMP_trig = TMP_trig.replace("<e>", "");}
					if (TMP_trig.contains("<start>")) {TMP_w = "start"; TMP_trig = TMP_trig.replace("<start>", "");}
					if (TMP_trig.contains("<contain>")) {TMP_w = "contain"; TMP_trig = TMP_trig.replace("<contain>", "");}
					if (TMP_trig.contains("<end>")) {TMP_w = "end"; TMP_trig = TMP_trig.replace("<end>", "");}
					
					if (TMP_trig.contains("<list=") && TMP_trig.contains(">")) {TMP_list = TMP_trig.substring(TMP_trig.indexOf("<list=")+6, TMP_trig.indexOf(">", TMP_trig.indexOf("<list="))); TMP_trig = TMP_trig.replace("<list="+TMP_list+">","");}
					TMP_lists.add(TMP_list);
					if (TMP_trig.contains("<server=") && TMP_trig.contains(">")) {TMP_server = TMP_trig.substring(TMP_trig.indexOf("<server=")+8, TMP_trig.indexOf(">", TMP_trig.indexOf("<server="))); TMP_trig = TMP_trig.replace("<server="+TMP_server+">","");}
					if (TMP_trig.contains("<imported>")) {TMP_imported = true; TMP_trig = TMP_trig.replace("<imported>", "");}
					if (TMP_trig.contains("<formatted>")) {TMP_formatted = true; TMP_trig = TMP_trig.replace("<formatted>", "");}
					
					String TMP_tags = "";
					if (TMP_imported) {TMP_tags+="Imported";}
					if (!TMP_w.equals("")) {if (!TMP_tags.equals("")) {TMP_tags += "\n";} TMP_tags += "Modifier: " + TMP_w;}
					if (!TMP_list.equals("")) {if (!TMP_tags.equals("")) {TMP_tags += "\n";} TMP_tags += "List: " + TMP_list;}
					if (!TMP_server.equals("")) {if (!TMP_tags.equals("")) {TMP_tags += "\n";} TMP_tags += "Server: " + TMP_server;}
					if (TMP_formatted) {if (!TMP_tags.equals("")) {TMP_tags += "\n";} TMP_tags += "Formatted";}
					
					if (TMP_list.equals("")) {
						List<String> TMP_out = new ArrayList<String>();
						TMP_type = TMP_type.replace("'", "\\'");
						TMP_trig = TMP_trig.replace("'", "\\'");
						TMP_list = TMP_list.replace("'", "\\'");
						if (TMP_imported) {TMP_out.add("text:'" + i + "> ',color:gray,hoverEvent:{action:'show_text',value:'You cannot edit\nimported triggers'}");} 
						else {TMP_out.add("text:'" + i + "> ',color:gray,hoverEvent:{action:'show_text',value:'Add an event'},clickEvent:{action:'suggest_command',value:'/trigger add " + i + " '}");}
						TMP_out.add("text:'" + TMP_type + " ',color:dark_gray");
						TMP_out.add("text:'" + TMP_trig + " ',color:"+global.settings.get(1));
						if (!TMP_tags.equals("")) {TMP_out.add("text:'tags ',color:dark_gray,hoverEvent:{action:'show_text',value:'" + TMP_tags + "'}");}
						if (!TMP_imported) {TMP_out.add("text:'-',color:red,hoverEvent:{action:'show_text',value:'Remove trigger'},clickEvent:{action:'suggest_command',value:'/trigger delete " + i + " [enter to confirm]'}");}
						ChatHandler.sendJson(TMP_out);
						
						int tabbed_logic = 0;
						
						for (int j=2; j<global.trigger.get(i).size(); j++) {
							
							String TMP_extraspaces = "";
							
							String TMP_e = global.trigger.get(i).get(j);
							String TMP_c = "";
							if (!TMP_e.contains(" ")) {TMP_c = TMP_e; TMP_e = "";}
							else {TMP_c = TMP_e.substring(0, TMP_e.indexOf(" ")); TMP_e = TMP_e.substring(TMP_e.indexOf(" ") + 1, TMP_e.length());}
							
							if (TMP_c.equalsIgnoreCase("END")
							|| TMP_c.toUpperCase().startsWith("ELSE")) { 
								tabbed_logic--;
							}
							
							
								for (int k=0; k<tabbed_logic; k++) {TMP_extraspaces+= "  ";}
								List<String> TMP_eventout = new ArrayList<String>();
								TMP_c = TMP_c.replace("'", "\\'");
								TMP_e = TMP_e.replace("'", "\\'");
								TMP_eventout.add("text:'" + TMP_extraspaces + "   '");
								if (TMP_imported) {TMP_eventout.add("text:' '");} 
								else {TMP_eventout.add("text:'-',color:red,hoverEvent:{action:'show_text',value:'Remove event'},clickEvent:{action:'suggest_command',value:'/trigger remove " + i + " " + j + " [enter to confirm]'}");}
								TMP_eventout.add("text:' " + TMP_c + " ',color:dark_gray");
								TMP_eventout.add("text:'" + TMP_e + "',color:gray");
								ChatHandler.sendJson(TMP_eventout);
							
							
							if (TMP_c.toUpperCase().startsWith("IF") 
							|| TMP_c.toUpperCase().startsWith("FOR")
							|| TMP_c.toUpperCase().startsWith("CHOOSE")
							|| TMP_c.toUpperCase().startsWith("WAIT")
							|| TMP_c.toUpperCase().startsWith("ELSE")
							|| TMP_c.toUpperCase().startsWith("ASYNC")) {
								tabbed_logic++;
							}
						}
					}
				}
				Set<String> uniqueTMP_lists = new HashSet<String>(TMP_lists);
				for (String value : uniqueTMP_lists) {
					if (!value.equals("")) {
						List<String> TMP_out = new ArrayList<String>();
						TMP_out.add("text:'List> ',color:gray,hoverEvent:{action:'show_text',value:'Show " + value + " triggers'},clickEvent:{action:'run_command',value:'/trigger list " + value + " '}");
						TMP_out.add("text:'" + value + " ',color:"+global.settings.get(1));
						ChatHandler.sendJson(TMP_out);
					}
				}
			}
			ChatHandler.warn(ChatHandler.color(global.settings.get(0), "&m"+dashes+"&r" + global.settings.get(0) + "^"));
		} else {
			String TMP_check = "";
			for (int i=1; i<args.length; i++) {
				if (i==args.length-1) {TMP_check += args[i];}
				else {TMP_check += args[i] + " ";}
			}
			
			String dashes = "";
			for (int j=0; j<Math.floor((((280*(Minecraft.getMinecraft().gameSettings.chatWidth))+40)/320)*51); j++) {dashes += "-";}
			ChatHandler.warn(ChatHandler.color(global.settings.get(0), "&m-"+dashes));
			if (global.trigger.size() == 0) {
				ChatHandler.warn(ChatHandler.color("red", "No triggers created"));
				ChatHandler.warn(ChatHandler.color("red", "Do </trigger> to get started"));
			} else {
				int TMP_test = 0;
				for (int i=0; i<global.trigger.size(); i++) {
					String TMP_type = global.trigger.get(i).get(0);
					String TMP_trig = global.trigger.get(i).get(1);
					String TMP_list = "";
					String TMP_w    = "";
					String TMP_server = "";
					Boolean TMP_imported = false;
					Boolean TMP_formatted = false;
					
					if (TMP_trig.contains("<s>")) {TMP_w = "start"; TMP_trig = TMP_trig.replace("<s>", "");}
					if (TMP_trig.contains("<c>")) {TMP_w = "contain"; TMP_trig = TMP_trig.replace("<c>", "");}
					if (TMP_trig.contains("<e>")) {TMP_w = "end"; TMP_trig = TMP_trig.replace("<e>", "");}
					if (TMP_trig.contains("<start>")) {TMP_w = "start"; TMP_trig = TMP_trig.replace("<start>", "");}
					if (TMP_trig.contains("<contain>")) {TMP_w = "contain"; TMP_trig = TMP_trig.replace("<contain>", "");}
					if (TMP_trig.contains("<end>")) {TMP_w = "end"; TMP_trig = TMP_trig.replace("<end>", "");}
					
					if (TMP_trig.contains("<list=") && TMP_trig.contains(">")) {TMP_list = TMP_trig.substring(TMP_trig.indexOf("<list=")+6, TMP_trig.indexOf(">", TMP_trig.indexOf("<list="))); TMP_trig = TMP_trig.replace("<list="+TMP_list+">","");}
					if (TMP_trig.contains("<server=") && TMP_trig.contains(">")) {TMP_server = TMP_trig.substring(TMP_trig.indexOf("<server=")+8, TMP_trig.indexOf(">", TMP_trig.indexOf("<server="))); TMP_trig = TMP_trig.replace("<server="+TMP_server+">","");}
					if (TMP_trig.contains("<imported>")) {TMP_imported = true; TMP_trig = TMP_trig.replace("<imported>", "");}
					if (TMP_trig.contains("<formatted>")) {TMP_formatted = true; TMP_trig = TMP_trig.replace("<formatted>", "");}
					
					String TMP_tags = "";
					if (TMP_imported==true) {TMP_tags+="Imported";}
					if (!TMP_w.equals("")) {if (!TMP_tags.equals("")) {TMP_tags += "\n";} TMP_tags += "Modifier: " + TMP_w;}
					if (!TMP_list.equals("")) {if (!TMP_tags.equals("")) {TMP_tags += "\n";} TMP_tags += "List: " + TMP_list;}
					if (!TMP_server.equals("")) {if (!TMP_tags.equals("")) {TMP_tags += "\n";} TMP_tags += "Server: " + TMP_server;}
					if (TMP_formatted) {if (!TMP_tags.equals("")) {TMP_tags += "\n";} TMP_tags += "Formatted";}
					
					
					if (TMP_check.equals(TMP_list)) {
						TMP_test++;
						
						List<String> TMP_out = new ArrayList<String>();
						TMP_type = TMP_type.replace("'", "\\'");
						TMP_trig = TMP_trig.replace("'", "\\'");
						TMP_list = TMP_list.replace("'", "\\'");
						if (TMP_imported) {TMP_out.add("text:'" + i + "> ',color:gray,hoverEvent:{action:'show_text',value:'You cannot edit\nimported triggers'}");} 
						else {TMP_out.add("text:'" + i + "> ',color:gray,hoverEvent:{action:'show_text',value:'Add an event'},clickEvent:{action:'suggest_command',value:'/trigger add " + i + " '}");}
						TMP_out.add("text:'" + TMP_type + " ',color:dark_gray");
						TMP_out.add("text:'" + TMP_trig + " ',color:"+global.settings.get(1));
						if (!TMP_tags.equals("")) {TMP_out.add("text:'tags ',color:dark_gray,hoverEvent:{action:'show_text',value:'" + TMP_tags + "'}");}
						if (!TMP_imported) {TMP_out.add("text:'-',color:red,hoverEvent:{action:'show_text',value:'Remove trigger'},clickEvent:{action:'suggest_command',value:'/trigger delete " + i + " [enter to confirm]'}");}
						ChatHandler.sendJson(TMP_out);
						
						int tabbed_logic=0;
						
						for (int j=2; j<global.trigger.get(i).size(); j++) {
							
							String TMP_extraspaces = "";
							
							String TMP_e = global.trigger.get(i).get(j);
							String TMP_c = "";
							if (!TMP_e.contains(" ")) {TMP_c = TMP_e; TMP_e = "";}
							else {TMP_c = TMP_e.substring(0, TMP_e.indexOf(" ")); TMP_e = TMP_e.substring(TMP_e.indexOf(" ") + 1, TMP_e.length());}
							
							if (TMP_c.equalsIgnoreCase("END")
							|| TMP_c.toUpperCase().startsWith("ELSE")) { 
								tabbed_logic--;
							}
							
								for (int k=0; k<tabbed_logic; k++) {TMP_extraspaces+= "  ";}
								List<String> TMP_eventout = new ArrayList<String>();
								TMP_c = TMP_c.replace("'", "\\'");
								TMP_e = TMP_e.replace("'", "\\'");
								TMP_eventout.add("text:'" + TMP_extraspaces + "   '");
								if (TMP_imported) {TMP_eventout.add("text:' '");} 
								else {TMP_eventout.add("text:'-',color:red,hoverEvent:{action:'show_text',value:'Remove event'},clickEvent:{action:'suggest_command',value:'/trigger remove " + i + " " + j + " [enter to confirm]'}");}
								TMP_eventout.add("text:' " + TMP_c + " ',color:dark_gray");
								TMP_eventout.add("text:'" + TMP_e + "',color:gray");
								ChatHandler.sendJson(TMP_eventout);
								
							if (TMP_c.toUpperCase().startsWith("IF") 
							|| TMP_c.toUpperCase().startsWith("FOR")
							|| TMP_c.toUpperCase().startsWith("CHOOSE")
							|| TMP_c.toUpperCase().startsWith("WAIT")
							|| TMP_c.toUpperCase().startsWith("ELSE")
							|| TMP_c.toUpperCase().startsWith("ASYNC")) {
								tabbed_logic++;
							}
							
						}
					}
				}
				if (TMP_test==0) {
					ChatHandler.warn(ChatHandler.color("red", "There is no list with name " + TMP_check));
					ChatHandler.warn(ChatHandler.color("red", "Create list " + TMP_check + " by using <list=" + TMP_check + "> when you create a trigger"));
				}
			}
			ChatHandler.warn(ChatHandler.color(global.settings.get(0), "&m"+dashes+"&r" + global.settings.get(0) + "^"));
		}
	}
	
	public static void commandSettings(String args[], Boolean silent) {
		if (args.length < 2) {
			ChatHandler.warn(ChatHandler.color("red", "/trigger settings [debug/test/color/killfeed/beta] <...>"));
		} else {
			if (args[1].equalsIgnoreCase("DEBUG")) {
				if (global.debug==false) {ChatHandler.warn(ChatHandler.color("gray", "Toggled debug mode") + " " + ChatHandler.color(global.settings.get(0), "on")); global.debug=true;}
				else {ChatHandler.warn(ChatHandler.color("gray", "Toggled debug mode") + " " + ChatHandler.color(global.settings.get(0), "off")); global.debug=false;}
			} else if (args[1].equalsIgnoreCase("COLOR") || args[1].equalsIgnoreCase("COLOUR")) {
				if (args.length < 3) {
					ChatHandler.warn(ChatHandler.color("red", "/trigger settings color [color]"));
				} else {
					args[2] = args[2].replace("_", "");
					args[2] = args[2].replace("&", "");
					if (args.length==4) {args[2] = args[2]+args[3];}
					if      (args[2].equalsIgnoreCase("0") || args[2].equalsIgnoreCase("BLACK"))       {global.settings.set(0, "&0"); global.settings.set(1, "black");        ChatHandler.warn(ChatHandler.color(global.settings.get(0), "Changed color"));}
					else if (args[2].equalsIgnoreCase("1") || args[2].equalsIgnoreCase("DARKBLUE"))    {global.settings.set(0, "&1"); global.settings.set(1, "dark_blue");    ChatHandler.warn(ChatHandler.color(global.settings.get(0), "Changed color"));}
					else if (args[2].equalsIgnoreCase("2") || args[2].equalsIgnoreCase("DARKGREEN"))   {global.settings.set(0, "&2"); global.settings.set(1, "dark_green");   ChatHandler.warn(ChatHandler.color(global.settings.get(0), "Changed color"));}
					else if (args[2].equalsIgnoreCase("3") || args[2].equalsIgnoreCase("DARKAQUA"))    {global.settings.set(0, "&3"); global.settings.set(1, "dark_aqua");    ChatHandler.warn(ChatHandler.color(global.settings.get(0), "Changed color"));}
					else if (args[2].equalsIgnoreCase("4") || args[2].equalsIgnoreCase("DARKRED"))     {global.settings.set(0, "&4"); global.settings.set(1, "dark_red");     ChatHandler.warn(ChatHandler.color(global.settings.get(0), "Changed color"));}
					else if (args[2].equalsIgnoreCase("5") || args[2].equalsIgnoreCase("DARKPURPLE"))  {global.settings.set(0, "&5"); global.settings.set(1, "dark_purple");  ChatHandler.warn(ChatHandler.color(global.settings.get(0), "Changed color"));}
					else if (args[2].equalsIgnoreCase("6") || args[2].equalsIgnoreCase("GOLD"))        {global.settings.set(0, "&6"); global.settings.set(1, "gold");         ChatHandler.warn(ChatHandler.color(global.settings.get(0), "Changed color"));}
					else if (args[2].equalsIgnoreCase("7") || args[2].equalsIgnoreCase("GRAY"))        {global.settings.set(0, "&7"); global.settings.set(1, "gray");         ChatHandler.warn(ChatHandler.color(global.settings.get(0), "Changed color"));}
					else if (args[2].equalsIgnoreCase("8") || args[2].equalsIgnoreCase("DARKGRAY"))    {global.settings.set(0, "&8"); global.settings.set(1, "dark_gray");    ChatHandler.warn(ChatHandler.color(global.settings.get(0), "Changed color"));}
					else if (args[2].equalsIgnoreCase("9") || args[2].equalsIgnoreCase("BLUE"))        {global.settings.set(0, "&9"); global.settings.set(1, "blue");         ChatHandler.warn(ChatHandler.color(global.settings.get(0), "Changed color"));}
					else if (args[2].equalsIgnoreCase("a") || args[2].equalsIgnoreCase("GREEN"))       {global.settings.set(0, "&a"); global.settings.set(1, "green");        ChatHandler.warn(ChatHandler.color(global.settings.get(0), "Changed color"));}
					else if (args[2].equalsIgnoreCase("b") || args[2].equalsIgnoreCase("AQUA"))        {global.settings.set(0, "&b"); global.settings.set(1, "aqua");         ChatHandler.warn(ChatHandler.color(global.settings.get(0), "Changed color"));}
					else if (args[2].equalsIgnoreCase("c") || args[2].equalsIgnoreCase("RED"))         {global.settings.set(0, "&c"); global.settings.set(1, "red");          ChatHandler.warn(ChatHandler.color(global.settings.get(0), "Changed color"));}
					else if (args[2].equalsIgnoreCase("d") || args[2].equalsIgnoreCase("LIGHTPURPLE")) {global.settings.set(0, "&d"); global.settings.set(1, "light_purple"); ChatHandler.warn(ChatHandler.color(global.settings.get(0), "Changed color"));}
					else if (args[2].equalsIgnoreCase("e") || args[2].equalsIgnoreCase("YELLOW"))      {global.settings.set(0, "&e"); global.settings.set(1, "yellow");       ChatHandler.warn(ChatHandler.color(global.settings.get(0), "Changed color"));}
					else if (args[2].equalsIgnoreCase("f") || args[2].equalsIgnoreCase("WHITE"))       {global.settings.set(0, "&f"); global.settings.set(1, "white");        ChatHandler.warn(ChatHandler.color(global.settings.get(0), "Changed color"));}
					else {ChatHandler.warn(ChatHandler.color("red", "Not a valid color"));}
					try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
				}
			} else if (args[1].equalsIgnoreCase("KILLFEED")) {
				if (args.length>2) {
					if (args[2].equalsIgnoreCase("POS") || args[2].equalsIgnoreCase("POSITION")) {
						if (args.length>3) {
							if (args[3].equalsIgnoreCase("TOP-LEFT") || args[3].equalsIgnoreCase("TL")) {
								global.settings.set(3, "top-left");
								ChatHandler.warn(ChatHandler.color("gray", "Changed killfeed position to") + " " + ChatHandler.color(global.settings.get(0), "top-left"));
								try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
							} else if (args[3].equalsIgnoreCase("TOP-RIGHT") || args[3].equalsIgnoreCase("TR")) {
								global.settings.set(3, "top-right");
								ChatHandler.warn(ChatHandler.color("gray", "Changed killfeed position to ") + " " + ChatHandler.color(global.settings.get(0), "top-right"));
								try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
							} else {
								ChatHandler.warn(ChatHandler.color("red", "/trigger settings killfeed position [top-left/top-right]"));
							}
						} else {
							ChatHandler.warn(ChatHandler.color("red", "/trigger settings killfeed position [top-left/top-right]"));
						}
					} else {
						ChatHandler.warn(ChatHandler.color("red", "/trigger settings killfeed [position] <...>"));
					}
				} else {
					ChatHandler.warn(ChatHandler.color("red", "/trigger settings killfeed [position] <...>"));
				}
			} else if (args[1].equalsIgnoreCase("BETA")) {
				if (args.length>2) {
					if (args[2].equalsIgnoreCase("TOGGLE")) {
						if (global.settings.get(4).equals("false")) {
							global.settings.set(4, "true");
							ChatHandler.warn(ChatHandler.color("red", "You have turned nightly notifications")+" "+ChatHandler.color("green","on!"));
							ChatHandler.warn(ChatHandler.color("red", "For more info, do </trigger settings beta>"));
							UpdateHandler.loadVersion("http://kerbybit.github.io/ChatTriggers/download/betaversion.txt");
						} else {
							global.settings.set(4, "false");
							ChatHandler.warn(ChatHandler.color("red", "You have turned nightly notifications off!"));
							ChatHandler.warn(ChatHandler.color("red", "For more info, do </trigger settings beta>"));
							UpdateHandler.loadVersion("http://kerbybit.github.io/ChatTriggers/download/version.txt");
						}
					} else {
						ChatHandler.warn(ChatHandler.color("red", "/trigger settings beta [toggle]"));
					}
				} else {
					String dashes = "";
 					for (int j=0; j<Math.floor((((280*(Minecraft.getMinecraft().gameSettings.chatWidth))+40)/320)*51); j++) {dashes += "-";}
 					ChatHandler.warn(ChatHandler.color(global.settings.get(0), "&m-"+dashes));
					if (global.settings.get(4).equals("false")) {
						ChatHandler.warn(ChatHandler.color("red", "You currently have the beta version disabled!"));
						ChatHandler.warn(ChatHandler.color("red", "Although this doesnt prevent you from downloading"));
						ChatHandler.warn(ChatHandler.color("red", "and using the beta version,"));
						ChatHandler.warn(ChatHandler.color("red", "You will NOT get notified of nightly builds"));
						ChatHandler.warn(ChatHandler.color("red", "To opt into the beta, do </trigger settings beta toggle>"));
						ChatHandler.warn(ChatHandler.color("red", ""));
						ChatHandler.warn(ChatHandler.color("red", "The beta versions may have unforseen bugs"));
						ChatHandler.warn(ChatHandler.color("red", "but gets updated regularly with new features"));
					} else {
						ChatHandler.warn(ChatHandler.color("red", "You currently have the beta version")+" "+ChatHandler.color("green","enabled!"));
						ChatHandler.warn(ChatHandler.color("red", "You will recieve notifications on nightly builds"));
						ChatHandler.warn(ChatHandler.color("red", "To change this, do </trigger settings beta toggle>"));
						ChatHandler.warn(ChatHandler.color("red", ""));
						ChatHandler.warn(ChatHandler.color("red", "The beta versions may have unforseen bugs"));
						ChatHandler.warn(ChatHandler.color("red", "but gets updated regularly with new features"));
					}
					ChatHandler.warn(ChatHandler.color(global.settings.get(0), "&m"+dashes+"&r" + global.settings.get(0) + "^"));
				}
			} else if (args[1].equalsIgnoreCase("TEST")){
				if (args.length<3) {
					ChatHandler.warn(ChatHandler.color("red", "/trigger settings test [onWorldLoad/onWorldFirstLoad]"));
					ChatHandler.warn(ChatHandler.color("red", "/trigger settings test [onServerChange/onNewDay]"));
				} else {
					if (args[2].equalsIgnoreCase("ONWORLDLOAD")) {
						global.worldLoaded = true;
					} else if (args[2].equalsIgnoreCase("ONWORLDFIRSTLOAD")) {
						global.worldLoaded = true;
						global.worldFirstLoad = true;
					} else if (args[2].equalsIgnoreCase("ONSERVERCHANGE")) {
						global.worldLoaded = true;
						global.connectedToServer = "";
					} else if (args[2].equalsIgnoreCase("ONNEWDAY")) {
						global.currentDate = "";
					} else {
						ChatHandler.warn(ChatHandler.color("red", "/trigger settings test [onWorldLoad/onWorldFirstLoad]"));
						ChatHandler.warn(ChatHandler.color("red", "/trigger settings test [onServerChange/onNewDay]"));
					}
				}
			} else {
				ChatHandler.warn(ChatHandler.color("red", "/trigger settings [debug/test/color/killfeed/beta] <...>"));
			}
		}
	}
	
	public static void commandSave(String args[], Boolean silent) {
		try {
			FileHandler.saveAll();
			global.trigger = FileHandler.loadTriggers("./mods/ChatTriggers/triggers.txt", false);
			global.USR_string = FileHandler.loadStrings("./mods/ChatTriggers/strings.txt");
			global.settings = FileHandler.loadSettings("./mods/ChatTriggers/settings.txt");
			ChatHandler.warn(ChatHandler.color(global.settings.get(0), "Organized and saved files"));
		} catch (IOException e) {
			ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));
		}
	}
	
	public static void commandLoad(String args[], Boolean silent) {
		global.canSave = true;
		try {
			global.trigger = FileHandler.loadTriggers("./mods/ChatTriggers/triggers.txt", false);
			global.USR_string = FileHandler.loadStrings("./mods/ChatTriggers/strings.txt");
			global.settings = FileHandler.loadSettings("./mods/ChatTriggers/settings.txt");
			FileHandler.loadImports("./mods/ChatTriggers/Imports/");
			ChatHandler.warn(global.settings.get(0) + "Files loaded");
		} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error loading triggers!"));}
	}
}
