package com.kerbybit.chattriggers.commands;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.util.*;

import com.kerbybit.chattriggers.chat.ChatHandler;
import com.kerbybit.chattriggers.file.FileHandler;
import com.kerbybit.chattriggers.file.UpdateHandler;
import com.kerbybit.chattriggers.globalvars.global;
import com.kerbybit.chattriggers.references.BugTracker;
import com.kerbybit.chattriggers.triggers.EventsHandler;
import com.kerbybit.chattriggers.triggers.StringHandler;
import com.kerbybit.chattriggers.triggers.TagHandler;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import static com.kerbybit.chattriggers.triggers.TriggerHandler.onChat;

public class CommandTrigger extends CommandBase {

    public String getCommandName() {return "trigger";}

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        try {
            if (global.canUse) {doCommand(args, false);}
        } catch (Exception e) {
            BugTracker.show(e, "command");
        }
    }

    public int getRequiredPermissionLevel() {return 0;}

    public String getCommandUsage(ICommandSender sender) {return "/trigger [create/add/list] <...>";}

    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (global.canUse) {doCommand(args, false);}

    }

    public static void doCommand(String args[], Boolean silent) {
        String temp_command = "/trigger";
        for (String arg : args) {
            temp_command += " " + arg;
        }
        global.lastCommand = temp_command;
        if (args.length == 0) {
            ChatHandler.warn("&c/trigger &c[clickable(&ccreate,suggest_command,/trigger create ,&7Suggest &7/trigger &7create)&c/clickable(&cadd,suggest_command,/trigger add ,&7Suggest &7/trigger &7add)&c/clickable(&clist,run_command,/trigger list,&7Run &7/trigger &7list)&c] &c<...>");
            ChatHandler.warn("&c/trigger &c[clickable(&cstring,suggest_command,/trigger string ,&7Suggest &7/trigger &7string)&c/clickable(&carray,run_command,/trigger array,&7Run &7/trigger &7array)&c/clickable(&cdisplay,run_command,/trigger display,&7Run &7/trigger &7display)&c] &c<...>");
            ChatHandler.warn("&c/trigger &c[clickable(&csave,run_command,/trigger save,&7Run &7/trigger &7save)&c/clickable(&cload,run_command,/trigger load,&7Run &7/trigger 77load)&c]");
            ChatHandler.warn("&c/trigger &c[clickable(&crun,suggest_command,/trigger run ,&7Suggest &7/trigger &7run)&c/clickable(&cimport,suggest_command,/trigger import ,&7Suggest &7/trigger &7import)&c/clickable(&cimports,run_command,/trigger imports,&7Run &7/trigger &7imports)&c] &c<...>");
        } else if (args[0].equalsIgnoreCase("DISPLAYS") || args[0].equalsIgnoreCase("DISPLAY")) {
            ChatHandler.warnBreak(0);
            if (global.displays.size() > 0) {
                if (args.length==1) {
                    for (String display_name : global.displays.keySet()) {
                        ChatHandler.warn(display_name);
                        ChatHandler.warn(" " + global.displays_xy.get(display_name)[0] + " " + global.displays_xy.get(display_name)[1]);
                        for (String display_value : global.displays.get(display_name)) {
                            ChatHandler.warn(" " + ChatHandler.ignoreFormatting(display_value));
                        }
                    }
                }
            } else {
                ChatHandler.warn(ChatHandler.color("red","There are currently no displays"));
            }
            ChatHandler.warnBreak(1);
        } else if (args[0].equalsIgnoreCase("ARRAYS") || args[0].equalsIgnoreCase("ARRAY")) {
            ChatHandler.warnBreak(0);
            if (global.USR_array.size() > 0) {
                if (args.length == 1) {
                    for (List<String> array : global.USR_array) {
                        ChatHandler.warn("clickable("+array.get(0)+",run_command,/trigger array "+array.get(0)+",&7Get values stored in "+array.get(0)+")");
                    }
                } else {
                    String get_array = "";
                    Boolean is_array = false;
                    for (int i=1; i<args.length; i++) {
                        get_array += " " + args[i];
                    }
                    for (List<String> array : global.USR_array) {
                        if (array.get(0).equals(get_array.trim())) {
                            is_array = true;
                            ChatHandler.warn(array.get(0));
                            for (int i=1; i<array.size(); i++) {
                                ChatHandler.warn("  " + ChatHandler.ignoreFormatting(array.get(i)));
                            }
                        }
                    }
                    if (!is_array) {
                        ChatHandler.warn(ChatHandler.color("red", "That is not currently an array"));
                    }
                }
            } else {
                ChatHandler.warn(ChatHandler.color("red","There are currently no arrays"));
            }
            ChatHandler.warnBreak(1);
        } else if (args[0].equalsIgnoreCase("SUBMITBUGREPORT")) {
            BugTracker.send();
        } else if (args[0].equalsIgnoreCase("SIMULATE")) {
            String TMP_e = "";
            for (int i=1; i<args.length; i++) {
                TMP_e = TMP_e + args[i] + " ";
            }
            TMP_e = TMP_e.trim();
            //ChatHandler.warn(TMP_e);
            ClientChatReceivedEvent chatEvent = new ClientChatReceivedEvent((byte)0, IChatComponent.Serializer.jsonToComponent("{text:'"+TMP_e+"'}"));
            onChat(TMP_e, ChatHandler.deleteFormatting(TMP_e), chatEvent);
            if (!chatEvent.isCanceled()) {
                ChatHandler.warn(TMP_e);
            }
        } else if (args[0].equalsIgnoreCase("HELP") || (args[0].equalsIgnoreCase("?"))) {
            if (args.length==1) {
                ChatHandler.warnBreak(0);
                ChatHandler.warn(" &f>> clickable(" + global.settings.get(0) + "Get started,run_command,/trigger help getstarted,&7Getting started guides and tutorials)");
                ChatHandler.warn("");
                ChatHandler.warn(" &f>> clickable(" + global.settings.get(0) + "Available trigger types,run_command,/trigger help triggers,&7Trigger type list)");
                ChatHandler.warn(" &f>> clickable(" + global.settings.get(0) + "Available event types,run_command,/trigger help events,&7Event type list)");
                ChatHandler.warn("");
                ChatHandler.warn(" &f>> clickable(" + global.settings.get(0) + "Built in strings,run_command,/trigger help strings,&7Built in string list)");
                ChatHandler.warn(" &f>> clickable(" + global.settings.get(0) + "String functions,run_command,/trigger help stringfunctions,&7String function list)");
                ChatHandler.warn(" &f>> clickable(" + global.settings.get(0) + "Array functions,run_command,/trigger help arrayfunctions,&7Array function list)");
                ChatHandler.warn(" &f>> clickable(" + global.settings.get(0) + "Display functions,run_command,/trigger help displayfunctions,&7Display function list)");
                ChatHandler.warnBreak(1);
            } else {
                if (args[1].equalsIgnoreCase("GETSTARTED")) {
                    ChatHandler.warnBreak(0);
                    ChatHandler.warn("&fCheck out the help guide online");
                    ChatHandler.warn(" clickable("+global.settings.get(0)+"ct.kerbybit.com/howto,open_url,http://ct.kerbybit.com/howto/,&7Open the howto guide)");
                    ChatHandler.warn("");
                    ChatHandler.warn("&fCheck out the starting tutorial");
                    ChatHandler.warn(" clickable("+global.settings.get(0)+"ct.kerbybit.com/tutorials,open_url,http://ct.kerbybit.com/tutorials/,&7Open the starting tutorial)");
                    ChatHandler.warn("");
                    ChatHandler.warn("clickable(&f< Back,run_command,/trigger help,&7Go back to help page)");
                    ChatHandler.warnBreak(1);
                } else if (args[1].equalsIgnoreCase("TRIGGERS")) {
                    ChatHandler.warnBreak(0);
                    ChatHandler.warn("&fTrigger types");
                    for (String value : CommandReference.getTriggerTypes()) {
                        ChatHandler.warn(global.settings.get(0) + " " + value);
                    }
                    ChatHandler.warn("");
                    ChatHandler.warn("clickable(&f< Back,run_command,/trigger help,&7Go back to help page)");
                    ChatHandler.warnBreak(1);
                } else if (args[1].equalsIgnoreCase("EVENTS")) {
                    ChatHandler.warnBreak(0);
                    ChatHandler.warn("&fEvent types");
                    for (String value : CommandReference.getEventTypes()) {
                        ChatHandler.warn(global.settings.get(0) + " " + value);
                    }
                    ChatHandler.warn("");
                    ChatHandler.warn("clickable(&f< Back,run_command,/trigger help,&7Go back to help page)");
                    ChatHandler.warnBreak(1);
                } else if (args[1].equalsIgnoreCase("STRINGS")) {
                    ChatHandler.warnBreak(0);
                    ChatHandler.warn("&fBuilt in strings");
                    for (String value : CommandReference.getStrings()) {
                        ChatHandler.warn(global.settings.get(0) + " " + value);
                    }
                    ChatHandler.warn("");
                    ChatHandler.warn("clickable(&f< Back,run_command,/trigger help,&7Go back to help page)");
                    ChatHandler.warnBreak(1);
                } else if (args[1].equalsIgnoreCase("STRINGFUNCTIONS")) {
                    ChatHandler.warnBreak(0);
                    ChatHandler.warn("&fString functions");
                    for (String value : CommandReference.getStringFunctions()) {
                        ChatHandler.warn(global.settings.get(0) + " " + value);
                    }
                    ChatHandler.warn("");
                    ChatHandler.warn("clickable(&f< Back,run_command,/trigger help,&7Go back to help page)");
                    ChatHandler.warnBreak(1);
                } else if (args[1].equalsIgnoreCase("ARRAYFUNCTIONS")) {
                    ChatHandler.warnBreak(0);
                    ChatHandler.warn("&fArray functions");
                    for (String value : CommandReference.getArrayFunctions()) {
                        ChatHandler.warn(global.settings.get(0) + " " + value);
                    }
                    ChatHandler.warn("");
                    ChatHandler.warn("clickable(&f< Back,run_command,/trigger help,&7Go back to help page)");
                    ChatHandler.warnBreak(1);
                } else if (args[1].equalsIgnoreCase("DISPLAYFUNCTIONS")) {
                    ChatHandler.warnBreak(0);
                    ChatHandler.warn("&fDisplay functions");
                    for (String value : CommandReference.getDisplayFunctions()) {
                        ChatHandler.warn(global.settings.get(0) + " " + value);
                    }
                    ChatHandler.warn("");
                    ChatHandler.warn("clickable(&f< Back,run_command,/trigger help,&7Go back to help page)");
                    ChatHandler.warnBreak(1);
                }
            }
        } else if (args[0].equalsIgnoreCase("COPY")) {
            if (args.length != 1) {
                if (args.length == 3) {
                    if (args[1].equals("CopyFromDebugChat")) {
                        try {
                            int num = Integer.parseInt(args[2]);
                            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                            clipboard.setContents(new StringSelection(global.copyText.get(num)), null);
                            doCommand(new String[] {"notify", ChatHandler.color("green", "Copied debug chat to clipboard")}, silent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            ChatHandler.warn(ChatHandler.color("red", "Something went wrong when copying text!"));
                        }
                    } else {
                        String TMP_e = "";
                        for (int i=1; i<args.length; i++) {TMP_e += args[i] + " ";}
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(new StringSelection(TMP_e.trim()), null);
                        doCommand(new String[] {"notify", ChatHandler.color("green", "Copied chat to clipboard")}, silent);
                    }
                } else {
                    String TMP_e = "";
                    for (int i=1; i<args.length; i++) {TMP_e += args[i] + " ";}
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(new StringSelection(TMP_e.trim()), null);
                    doCommand(new String[] {"notify", ChatHandler.color("green", "Copied chat to clipboard")}, silent);
                }
            } else {
                ChatHandler.warn(ChatHandler.color("red", "/trigger copy <text>"));
            }
        } else if (args[0].equalsIgnoreCase("NOTIFY")) {
            if (args.length != 1) {
                String TMP_e = "";
                for (int i=1; i<args.length; i++) {TMP_e += args[i] + " ";}
                global.notify.add(TMP_e.trim());
                List<Float> temp_list = new ArrayList<Float>();
                temp_list.add((float) 0);temp_list.add((float) -1000);
                temp_list.add((float) global.notifySize);temp_list.add((float) 50);
                temp_list.add((float) 0);temp_list.add((float) -1000);
                global.notifyAnimation.add(temp_list);
                global.notifySize++;
            } else {
                ChatHandler.warn(ChatHandler.color("red", "/trigger notify <text>"));
            }
        } else if (args[0].equalsIgnoreCase("KILLFEED")) {
            if (args.length != 1) {
                String TMP_e = "";
                for (int i=1; i<args.length; i++) {TMP_e += args[i] + " ";}
                global.killfeed.add(TMP_e);
                global.killfeedDelay.add(100);
            } else {
                ChatHandler.warn(ChatHandler.color("red", "/trigger killfeed <text>"));
            }
        } else if (args[0].equalsIgnoreCase("IMPORTS")) {
            commandImports();
        } else if (args[0].equalsIgnoreCase("RESET")) {
            CommandReference.resetAll();
        } else if (args[0].equalsIgnoreCase("IMPORT")) {
            commandImport(args);
        } else if (args[0].equalsIgnoreCase("DISABLEIMPORT")) {
            commandDisableImport(args);
        } else if (args[0].equalsIgnoreCase("ENABLEIMPORT")) {
            commandEnableImport(args);
        } else if (args[0].equalsIgnoreCase("RUN")) {
            commandRun(args);
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
            commandList(args);
        } else if (args[0].equalsIgnoreCase("SETTINGS")) {
            commandSettings(args);
        } else if (args[0].equalsIgnoreCase("SAVE")) {
            commandSave();
        } else if (args[0].equalsIgnoreCase("LOAD")) {
            commandLoad();
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
                    if (global.canSave) {
                        global.trigger.clear(); global.USR_string.clear(); global.TMP_string.clear();
                        CommandReference.clearTriggerList();
                        global.waitEvents.clear(); global.asyncEvents.clear();
                        for (int i=1; i<args.length; i++) {
                            ChatHandler.warn(ChatHandler.color(global.settings.get(0), "You are now in testing mode for import '"+args[i]+"'"));
                        }
                        ChatHandler.warn(ChatHandler.color(global.settings.get(0), "To reload all of your triggers and strings, do </trigger load>"));
                        try {
                            for (int i=1; i<args.length; i++) {
                                String dir = "./mods/ChatTriggers/Imports/"+args[i]+".txt";
                                global.trigger.addAll(FileHandler.loadTriggers(dir, true, null));
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
        } else if (args[0].equalsIgnoreCase("RELOAD")) {
            ChatHandler.warn(global.settings.get(0)+"Reloading ChatTriggers");
            CommandReference.clearAll();
            global.tick = 0;
            FileHandler.firstFileLoad();
        } else if (args[0].equalsIgnoreCase("TEST")) {
            ChatHandler.warn(ChatHandler.color("&7", "This command does nothing :D"));
        } else {
            ChatHandler.warn("&c/trigger &c[clickable(&ccreate,suggest_command,/trigger create ,&7Suggest &7/trigger &7create)&c/clickable(&cadd,suggest_command,/trigger add ,&7Suggest &7/trigger &7add)&c/clickable(&clist,run_command,/trigger list,&7Run &7/trigger &7list)&c] &c<...>");
            ChatHandler.warn("&c/trigger &c[clickable(&cstring,suggest_command,/trigger string ,&7Suggest &7/trigger &7string)&c/clickable(&carray,run_command,/trigger array,&7Run &7/trigger &7array)&c] &c<...>");
            ChatHandler.warn("&c/trigger &c[clickable(&csave,run_command,/trigger save,&7Run &7/trigger &7save)&c/clickable(&cload,run_command,/trigger load,&7Run &7/trigger 77load)&c]");
            ChatHandler.warn("&c/trigger &c[clickable(&crun,suggest_command,/trigger run ,&7Suggest &7/trigger &7run)&c/clickable(&cimport,suggest_command,/trigger import ,&7Suggest &7/trigger &7import)&c/clickable(&cimports,run_command,/trigger imports,&7Run &7/trigger &7imports)&c] &c<...>");
        }
    }

    private static void commandImport(String args[]) {
        if (args.length>=2) {
            ArrayList<String> temp = new ArrayList<String>(Arrays.asList(args));
            temp.remove(0);
            global.neededImports.addAll(temp);
        } else {ChatHandler.warn(ChatHandler.color("red", "/trigger import <import name>"));}
    }

    private static void commandImports() {
        ChatHandler.warnBreak(0);


        File dir = new File("./mods/ChatTriggers/Imports/");
        if (!dir.exists()) {
            if (!dir.mkdir()) {ChatHandler.warn(ChatHandler.color("red", "Unable to create file!"));}
        }
        File[] activeFiles = dir.listFiles();
        if (activeFiles != null) {
            ChatHandler.warn("Active Imports");
            for (File file : activeFiles) {
                if (file.isFile()) {
                    if (file.getName().endsWith(".txt")) {
                        ChatHandler.warn("clickable( &l\u02C5,run_command,/t disableimport -showlist " + file.getName().replace(".txt","") + ",&7Disable import " + file.getName().replace(".txt","") + ") " + ChatHandler.color(global.settings.get(0), file.getName().replace(".txt","")));
                    }
                }
            }
        }

        dir = new File("./mods/ChatTriggers/Imports/DisabledImports/");
        if (!dir.exists()) {
            if (!dir.mkdir()) {ChatHandler.warn(ChatHandler.color("red", "Unable to create file!"));}
        }
        File[] disabledFiles = dir.listFiles();
        if (disabledFiles !=null) {
            ChatHandler.warn("Disabled Imports");
            for (File file : disabledFiles) {
                if (file.getName().endsWith(".txt")) {
                    ChatHandler.warn("clickable( &l\u02C4,run_command,/t enableimport -showlist " + file.getName().replace(".txt","") + ",&7Enable import " + file.getName().replace(".txt","") + ") " + ChatHandler.color(global.settings.get(0), file.getName().replace(".txt","")));
                }
            }
        }

        ChatHandler.warnBreak(1);
    }

    private static void commandDisableImport(String args[]) {
        if (args.length>=2) {
            Boolean showlist = false;
            for (int i=1; i<args.length; i++) {
                try {
                    if (args[i].equalsIgnoreCase("-showlist")) {
                        showlist = true;
                    } else {
                        File dir = new File("./mods/ChatTriggers/Imports/DisabledImports/");
                        if (!dir.exists()) {if (!dir.mkdir()) {ChatHandler.warn(ChatHandler.color("red","Something went wrong while creating the file!"));}}
                        File file = new File("./mods/ChatTriggers/Imports/" + args[i]+".txt");
                        if (!file.exists()) {throw new IOException();}
                        if (!file.renameTo(new File("./mods/ChatTriggers/Imports/DisabledImports/" + args[i] + ".txt"))) {ChatHandler.warn(ChatHandler.color("red", "Something went wrong while moving the file!"));}
                        ChatHandler.warn(ChatHandler.color(global.settings.get(0), "Disabled " + args[i] + ".txt"));
                        try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
                    }
                } catch (IOException e) {
                    ChatHandler.warn(ChatHandler.color("red", args[i] + " is not an active import!"));
                }
            }
            if (showlist) {
                commandImports();
            }
        } else {ChatHandler.warn(ChatHandler.color("red", "/trigger disableImport <import name>"));}
    }

    private static void commandEnableImport(String args[]) {
        if (args.length>=2) {
            Boolean showlist = false;
            for (int i=1; i<args.length; i++) {
                try {
                    if (args[i].equalsIgnoreCase("-showlist")) {
                        showlist = true;
                    } else {
                        File dir = new File("./mods/ChatTriggers/Imports/DisabledImports/");
                        if (!dir.exists()) {if (!dir.mkdir()) {ChatHandler.warn(ChatHandler.color("red","Something went wrong while creating the file!"));}}
                        File file = new File("./mods/ChatTriggers/Imports/DisabledImports/" + args[i]+".txt");
                        if (!file.exists()) {throw new IOException();}
                        if (!file.renameTo(new File("./mods/ChatTriggers/Imports/" + args[i] + ".txt"))) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
                        ChatHandler.warn(ChatHandler.color(global.settings.get(0), "Enabled " + args[i] + ".txt"));
                        try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
                    }

                } catch (IOException e) {
                    ChatHandler.warn(ChatHandler.color("red", args[i] + " is not an inactive import!"));
                }
            }
            if (showlist) {
                commandImports();
            }
        } else {ChatHandler.warn(ChatHandler.color("red", "/trigger disableImport <import name>"));}
    }

    private static void commandRun(String args[]) {
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
            try {
                int num = Integer.parseInt(args[1]);
                if (num >= 0 && num < global.trigger.size()) {
                    List<String> TMP_events = new ArrayList<String>();
                    for (int i=2; i<global.trigger.get(num).size(); i++) {
                        TMP_events.add(global.trigger.get(num).get(i));
                    }
                    EventsHandler.doEvents(TMP_events, null);
                }
            } catch (NumberFormatException e1) {
                for (int k=0; k<global.trigger.size(); k++) {

                    String TMP_trig = global.trigger.get(k).get(1);

                    boolean getCase = true;
                    if (TMP_trig.contains("<case=false>")) {
                        getCase = false;
                    }

                    TMP_trig = TagHandler.removeTags(TMP_trig);

                    if (getCase) {
                        if (TMP_trig.equals(TMP_e)) {
                            List<String> TMP_events = new ArrayList<String>();
                            for (int i=2; i<global.trigger.get(k).size(); i++) {
                                TMP_events.add(global.trigger.get(k).get(i));
                            }
                            EventsHandler.doEvents(TMP_events, null);
                        } else {
                            String TMP_trigtype = global.trigger.get(k).get(0);
                            if (TMP_trigtype.toUpperCase().startsWith("OTHER")) {
                                if (TMP_trig.contains("(") && TMP_trig.endsWith(")")) {
                                    String TMP_trigtest = TMP_trig.substring(0, TMP_trig.indexOf("("));
                                    if (TMP_e.startsWith(TMP_trigtest) && TMP_e.endsWith(")")) {
                                        String TMP_argsIn = TMP_e.substring(TMP_e.indexOf("(") + 1, TMP_e.length() - 1);
                                        String TMP_argsOut = TMP_trig.substring(TMP_trig.indexOf("(") + 1, TMP_trig.length() - 1);
                                        String[] argsIn = TMP_argsIn.split(",");
                                        String[] argsOut = TMP_argsOut.split(",");
                                        if (argsIn.length == argsOut.length) {
                                            List<String> TMP_events = new ArrayList<String>();
                                            for (int j = 2; j < global.trigger.get(k).size(); j++) {
                                                TMP_events.add(global.trigger.get(k).get(j));
                                            }
                                            EventsHandler.doEvents(TMP_events, null, argsOut, argsIn);
                                        }
                                    } else if (TMP_e.startsWith(TMP_trigtest)) {
                                        String[] argsIn = TMP_e.replace(TMP_trigtest,"").trim().split(" ");

                                        String TMP_argsOut = TMP_trig.substring(TMP_trig.indexOf("(") + 1, TMP_trig.length() - 1);
                                        String[] argsOut = TMP_argsOut.split(",");

                                        if (argsIn.length == argsOut.length) {
                                            List<String> TMP_events = new ArrayList<String>();
                                            for (int j = 2; j < global.trigger.get(k).size(); j++) {
                                                TMP_events.add(global.trigger.get(k).get(j));
                                            }
                                            EventsHandler.doEvents(TMP_events, null, argsOut, argsIn);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (TMP_trig.equalsIgnoreCase(TMP_e)) {
                            List<String> TMP_events = new ArrayList<String>();
                            for (int i=2; i<global.trigger.get(k).size(); i++) {
                                TMP_events.add(global.trigger.get(k).get(i));
                            }
                            EventsHandler.doEvents(TMP_events, null);
                        } else {
                            String TMP_trigtype = global.trigger.get(k).get(0);
                            if (TMP_trigtype.toUpperCase().startsWith("OTHER")) {
                                if (TMP_trig.contains("(") && TMP_trig.endsWith(")")) {
                                    String TMP_trigtest = TMP_trig.substring(0, TMP_trig.indexOf("("));
                                    if (TMP_e.toUpperCase().startsWith(TMP_trigtest.toUpperCase()) && TMP_e.endsWith(")")) {
                                        String TMP_argsIn = TMP_e.substring(TMP_e.indexOf("(") + 1, TMP_e.length() - 1);
                                        String TMP_argsOut = TMP_trig.substring(TMP_trig.indexOf("(") + 1, TMP_trig.length() - 1);
                                        String[] argsIn = TMP_argsIn.split(",");
                                        String[] argsOut = TMP_argsOut.split(",");
                                        if (argsIn.length == argsOut.length) {
                                            List<String> TMP_events = new ArrayList<String>();
                                            for (int j = 2; j < global.trigger.get(k).size(); j++) {
                                                TMP_events.add(global.trigger.get(k).get(j));
                                            }
                                            EventsHandler.doEvents(TMP_events, null, argsOut, argsIn);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void commandCreate(String args[], Boolean silent) {
        if (args.length < 3) {
            String hover_value = "";
            for (String value : CommandReference.getTriggerTypes()) {
                hover_value += " " + value;
            }
            hover_value = hover_value.trim().replace(" ", "\n&7");
            ChatHandler.warn("&c/trigger &ccreate &c<hover(&ctype,&7"+hover_value+")&c> &c<trigger>");
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
            if (CommandReference.isTriggerType(TMP_type)) {
                global.trigger.add(TMP_l);
                try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
                int TMP_num = -1;
                for (int i=0; i<global.trigger.size(); i++) {
                    if (global.trigger.get(i).equals(TMP_l)) {
                        TMP_num = i;
                    }
                }
                if (!silent) {
                    TMP_trig = TMP_trig.replace("(","LeftParF6cyUQp9LeftPar").replace(")","RightParF6cyUQp9RightPar").replace("&","AmpF6cyUQp9Amp");
                    if (TMP_num == -1) {
                        ChatHandler.warn("&7Created trigger "+global.settings.get(0)+TMP_trig.replace(" "," "+global.settings.get(0))+" &7with trigger type "+global.settings.get(0)+TMP_type);
                    } else {
                        ChatHandler.warn("&7Created trigger "+global.settings.get(0)+TMP_trig.replace(" "," "+global.settings.get(0))+" &7with trigger type "+global.settings.get(0)+TMP_type+" clickable(&7("+TMP_num+"),suggest_command,/trigger add "+TMP_num+" ,Add an event)");
                    }
                }

            } else {
                ChatHandler.warn(ChatHandler.color("red", "Not a valid trigger type"));
            }
        }
    }

    private static void commandDelete(String args[], Boolean silent) {
        if (args.length < 2) {
            ChatHandler.warn(ChatHandler.color("red", "/trigger delete <trigger number>"));
        } else {
            int num;
            try {num = Integer.parseInt(args[1]);}
            catch(NumberFormatException e) {num = -1;}
            if (num>-1 && num<global.trigger.size()) {
                String TMP_rem = global.trigger.get(num).get(1);
                if (!TMP_rem.contains("<imported>")) {
                    if (!silent) {
                        ChatHandler.warn(ChatHandler.color("gray", "Deleted trigger") + " " + ChatHandler.color(global.settings.get(0), ChatHandler.ignoreFormatting(TMP_rem)) + " " + ChatHandler.color("gray", "and all of its events"));
                    }
                    global.trigger.remove(num).get(1);
                    try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
                } else {
                    ChatHandler.warn(ChatHandler.color("red", "You cannot edit imported triggers!"));
                    ChatHandler.warn(ChatHandler.color("red", "You must edit them from the imported file!"));
                }
            } else {
                ChatHandler.warn(ChatHandler.color("red", "/trigger delete <trigger number>"));
            }
        }
    }

    private static void commandAdd(String args[], Boolean silent) {
        if (args.length < 3) {
            String hover_value = "";
            for (String value : CommandReference.getEventTypes()) {
                hover_value += " " + value;
            }
            hover_value = hover_value.trim().replace(" ", "\n&7");
            ChatHandler.warn("&c/trigger &cadd &c<trigger number> &c<hover(&cevent,&7"+hover_value+")&c> &c<event &cargument(s)>");
        } else {
            int num;
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

                    if (CommandReference.isEventType(TMP_etype)) {
                        global.trigger.get(num).add(TMP_e);
                        if (!silent) {
                            TMP_e = TMP_e.replace("(", "LeftParF6cyUQp9LeftPar")
                                    .replace(")", "RightParF6cyUQp9RightPar")
                                    .replace(",", "CommaReplacementF6cyUQp9CommaReplacement");
                            ChatHandler.warn(ChatHandler.color("gray", "Added event") + " " + ChatHandler.color(global.settings.get(0), ChatHandler.ignoreFormatting(TMP_e)) + " " + ChatHandler.color("gray", "to trigger") + " " + ChatHandler.color(global.settings.get(0), ChatHandler.ignoreFormatting(global.trigger.get(num).get(1))));
                        }
                        try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
                    } else {
                        ChatHandler.warn(ChatHandler.color("red", "Not a valid event type!"));
                    }
                } else {
                    ChatHandler.warn(ChatHandler.color("red", "You cannot edit imported triggers!"));
                    ChatHandler.warn(ChatHandler.color("red", "You must edit them from the imported file!"));
                }
            } else {
                ChatHandler.warn(ChatHandler.color("red", "/trigger add <trigger number> <event> <event argument(s)>"));
            }
        }
    }

    private static void commandRemove(String args[], Boolean silent) {
        if (args.length < 3) {
            ChatHandler.warn(ChatHandler.color("red", "/trigger remove [trigger number] [event number]"));
        } else {
            int num;
            try {num = Integer.parseInt(args[1]);}
            catch(NumberFormatException e) {num = -1;}
            if (num>-1 && num<global.trigger.size()) {
                if (!global.trigger.get(num).get(1).contains("<imported>")) {
                    int num2;
                    try {num2 = Integer.parseInt(args[2]);}
                    catch(NumberFormatException e) {num2 = 1;}
                    if (num2>1 && num2<global.trigger.get(num).size()) {
                        String TMP_rem = global.trigger.get(num).remove(num2);
                        if (!silent) {
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

    private static void commandString(String args[], Boolean silent) {
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
                                if (!TMP_list.equals("")) {
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
                            if (!TMP_list.equals("")) {TMP_l.add(TMP_list);}
                            global.USR_string.add(TMP_l);
                            if (!silent) {
                                TMP_sn = TMP_sn.replace("(","LeftParF6cyUQp9LeftPar").replace(")","RightParF6cyUQp9RightPar").replace("&","AmpF6cyUQp9Amp").replace("\\", "BackslashF6cyUQp9Backslash");
                                ChatHandler.warn("&7Created string clickable("+global.settings.get(0)+TMP_sn+",suggest_command,/trigger string set "+TMP_sn+" ,Set "+TMP_sn+")");
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
                            if (!silent) {
                                TMP_rem = TMP_rem.replace("(","LeftParF6cyUQp9LeftPar").replace(")","RightParF6cyUQp9RightPar").replace("&","AmpF6cyUQp9Amp");
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
                        if (!silent) {ChatHandler.warnUnformatted(ChatHandler.color("gray", "Set value") + " " + ChatHandler.color(global.settings.get(0), TMP_s) + " " + ChatHandler.color("gray", "in string") + " " + ChatHandler.color(global.settings.get(0), global.USR_string.get(num).get(0)));}
                        if (TMP_s.equals("{null}")) {TMP_s = "";}
                        global.USR_string.get(num).set(1,TMP_s);
                        try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
                    } else {ChatHandler.warn(ChatHandler.color("red", "/trigger string set [string number] [string]"));}
                }
            } else if (args[1].equalsIgnoreCase("LIST")) {
                if (args.length==2) {
                    ChatHandler.warnBreak(0);
                    if (global.USR_string.size() == 0) {
                        ChatHandler.warnUnformatted(ChatHandler.color("red", "No strings created"));
                        ChatHandler.warnUnformatted(ChatHandler.color("red", "Do </trigger string> to get started"));
                    } else {
                        List<String> STR_lists = new ArrayList<String>();
                        for (int i=0; i<global.USR_string.size(); i++) {
                            if (global.USR_string.get(i).size()!=3) {
                                String TMP_sn = global.USR_string.get(i).get(0);
                                TMP_sn = TMP_sn.replace("(","LeftParF6cyUQp9LeftPar").replace(")","RightParF6cyUQp9RightPar").replace("&","AmpF6cyUQp9Amp");
                                ChatHandler.warn("clickable(&7>,suggest_command,/trigger string set "+i+" ,Set string) "+global.settings.get(0)+TMP_sn+" clickable(&c-,suggest_command,/trigger string delete "+i+" [enter to confirm],Delete string)");
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
                            String TMP_list = STR_lists.get(i);
                            TMP_list = TMP_list.replace("(","LeftParF6cyUQp9LeftPar").replace(")","RightParF6cyUQp9RightPar").replace("&","AmpF6cyUQp9Amp");
                            ChatHandler.warn("clickable(&7List>,run_command,/trigger string list "+TMP_list+",Show "+TMP_list+") "+global.settings.get(1)+TMP_list);
                        }
                    }
                    ChatHandler.warnBreak(1);
                } else {
                    ChatHandler.warnBreak(0);
                    String showList = "";
                    for (int i=2; i<args.length; i++) {showList += args[i]+" ";}
                    showList=showList.trim();
                    for (int i=0; i<global.USR_string.size(); i++) {
                        if (global.USR_string.get(i).size()==3) {
                            if (global.USR_string.get(i).get(2).equals(showList)) {
                                List<String> TMP_out = new ArrayList<String>();
                                String TMP_sn = global.USR_string.get(i).get(0);
                                TMP_sn = TMP_sn.replace("(","LeftParF6cyUQp9LeftPar").replace(")","RightParF6cyUQp9RightPar").replace("&","AmpF6cyUQp9Amp");
                                ChatHandler.warn("clickable(&7>,suggest_command,/trigger set "+i+" ,Set string) "+global.settings.get(0)+TMP_sn+" clickable(&c-,suggest_command,/trigger string delete "+i+" [enter to confirm],Delete string)");
                                ChatHandler.warnUnformatted(ChatHandler.color("gray", "  " + global.USR_string.get(i).get(1)));
                            }
                        }
                    }
                    ChatHandler.warnBreak(1);
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

    private static void commandList(String args[]) {
        if (args.length==1) {
            ChatHandler.warnBreak(0);
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
                    String TMP_case = "";
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
                    if (TMP_trig.contains("<case=") && TMP_trig.contains(">")) {TMP_case = TMP_trig.substring(TMP_trig.indexOf("<case=")+6, TMP_trig.indexOf(">", TMP_trig.indexOf("<case="))); TMP_trig = TMP_trig.replace("<case="+TMP_case+">","");}
                    if (!TMP_case.equals("false")) {TMP_case = "";}
                    if (TMP_trig.contains("<imported>")) {TMP_imported = true; TMP_trig = TMP_trig.replace("<imported>", "");}
                    if (TMP_trig.contains("<formatted>")) {TMP_formatted = true; TMP_trig = TMP_trig.replace("<formatted>", "");}

                    String TMP_tags = "";
                    if (TMP_imported) {TMP_tags+="Imported";}
                    if (!TMP_w.equals("")) {if (!TMP_tags.equals("")) {TMP_tags += "\n";} TMP_tags += "Modifier: " + TMP_w;}
                    if (!TMP_list.equals("")) {if (!TMP_tags.equals("")) {TMP_tags += "\n";} TMP_tags += "List: " + TMP_list;}
                    if (!TMP_server.equals("")) {if (!TMP_tags.equals("")) {TMP_tags += "\n";} TMP_tags += "Server: " + TMP_server;}
                    if (!TMP_case.equals("")) {if (!TMP_tags.equals("")) {TMP_tags += "\n";} TMP_tags += "Case Insensitive";}
                    if (TMP_formatted) {if (!TMP_tags.equals("")) {TMP_tags += "\n";} TMP_tags += "Formatted";}

                    if (TMP_list.equals("")) {
                        TMP_type = TMP_type.replace("(","LeftParF6cyUQp9LeftPar").replace(")","RightParF6cyUQp9RightPar").replace("&","AmpF6cyUQp9Amp");
                        TMP_trig = TMP_trig.replace("(","LeftParF6cyUQp9LeftPar").replace(")","RightParF6cyUQp9RightPar").replace("&","AmpF6cyUQp9Amp");
                        String pre_out;
                        if (TMP_imported) {pre_out = "hover(&7>,You cannot edit\nimported triggers)";}
                        else {pre_out = "clickable(&7>,suggest_command,/trigger add "+i+" ,Add an event)";}
                        String post_out = "";
                        if (!TMP_tags.equals("")) {post_out = "hover(&8tags ,"+TMP_tags+")";}
                        if (!TMP_imported) {post_out += "clickable(&c-,suggest_command,/trigger delete "+i+" [enter to confirm],Remove trigger)";}
                        ChatHandler.warn(pre_out+" &8"+TMP_type+" "+global.settings.get(0)+TMP_trig.replace(" "," "+global.settings.get(0))+" "+post_out);

                        int tabbed_logic = 0;

                        for (int j=2; j<global.trigger.get(i).size(); j++) {

                            String TMP_extraspaces = "  ";

                            String TMP_e = global.trigger.get(i).get(j);
                            String TMP_c;
                            if (!TMP_e.contains(" ")) {TMP_c = TMP_e; TMP_e = "";}
                            else {TMP_c = TMP_e.substring(0, TMP_e.indexOf(" ")); TMP_e = TMP_e.substring(TMP_e.indexOf(" ") + 1, TMP_e.length());}

                            if (TMP_c.equalsIgnoreCase("END")
                                    || TMP_c.toUpperCase().startsWith("ELSE")) {
                                tabbed_logic--;
                            }

                            for (int k=0; k<tabbed_logic; k++) {TMP_extraspaces+= "  ";}
                            TMP_c = TMP_c.replace("(","LeftParF6cyUQp9LeftPar").replace(")","RightParF6cyUQp9RightPar").replace("&","AmpF6cyUQp9Amp");
                            TMP_e = TMP_e.replace("(","LeftParF6cyUQp9LeftPar").replace(")","RightParF6cyUQp9RightPar").replace("&","AmpF6cyUQp9Amp");
                            String pre_out_event;
                            if (TMP_imported) {pre_out_event = " ";}
                            else {pre_out_event = "clickable(&c-,suggest_command,/trigger remove "+i+" "+j+" [enter to confirm],Remove event)";}
                            ChatHandler.warn(TMP_extraspaces+pre_out_event+" &8"+TMP_c+" &7"+TMP_e.replace(" "," &7").replace("\\","BackslashF6cyUQp9Backslash"));


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
                        ChatHandler.warn("clickable(&7List>,run_command,/trigger list "+value+") "+global.settings.get(0)+value);
                    }
                }
            }
            ChatHandler.warnBreak(1);
        } else {
            String TMP_check = "";
            for (int i=1; i<args.length; i++) {
                if (i==args.length-1) {TMP_check += args[i];}
                else {TMP_check += args[i] + " ";}
            }

            ChatHandler.warnBreak(0);
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
                    String TMP_case = "";
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
                    if (TMP_trig.contains("<case=") && TMP_trig.contains(">")) {TMP_case = TMP_trig.substring(TMP_trig.indexOf("<case=")+6, TMP_trig.indexOf(">", TMP_trig.indexOf("<case="))); TMP_trig = TMP_trig.replace("<case="+TMP_case+">","");}
                    if (!TMP_case.equals("false")) {TMP_case = "";}
                    if (TMP_trig.contains("<imported>")) {TMP_imported = true; TMP_trig = TMP_trig.replace("<imported>", "");}
                    if (TMP_trig.contains("<formatted>")) {TMP_formatted = true; TMP_trig = TMP_trig.replace("<formatted>", "");}

                    String TMP_tags = "";
                    if (TMP_imported) {TMP_tags+="Imported";}
                    if (!TMP_w.equals("")) {if (!TMP_tags.equals("")) {TMP_tags += "\n";} TMP_tags += "Modifier: " + TMP_w;}
                    if (!TMP_list.equals("")) {if (!TMP_tags.equals("")) {TMP_tags += "\n";} TMP_tags += "List: " + TMP_list;}
                    if (!TMP_server.equals("")) {if (!TMP_tags.equals("")) {TMP_tags += "\n";} TMP_tags += "Server: " + TMP_server;}
                    if (!TMP_case.equals("")) {if (!TMP_tags.equals("")) {TMP_tags += "\n";} TMP_tags += "Case Insensitive";}
                    if (TMP_formatted) {if (!TMP_tags.equals("")) {TMP_tags += "\n";} TMP_tags += "Formatted";}


                    if (TMP_check.equals(TMP_list)) {
                        TMP_test++;
                        TMP_type = TMP_type.replace("(","LeftParF6cyUQp9LeftPar").replace(")","RightParF6cyUQp9RightPar").replace("&","AmpF6cyUQp9Amp");
                        TMP_trig = TMP_trig.replace("(","LeftParF6cyUQp9LeftPar").replace(")","RightParF6cyUQp9RightPar").replace("&","AmpF6cyUQp9Amp");
                        String pre_out;
                        if (TMP_imported) {pre_out = "hover(&7>,You cannot edit\nimported triggers)";}
                        else {pre_out = "clickable(&7>,suggest_command,/trigger add "+i+" ,Add an event)";}
                        String post_out = "";
                        if (!TMP_tags.equals("")) {post_out = "hover(&8tags ,"+TMP_tags+")";}
                        if (!TMP_imported) {post_out += "clickable(&c-,suggest_command,/trigger delete "+i+" [enter to confirm],Remove trigger)";}
                        ChatHandler.warn(pre_out+" &8"+TMP_type+" "+global.settings.get(0)+TMP_trig.replace(" "," "+global.settings.get(0))+" "+post_out);

                        int tabbed_logic=0;

                        for (int j=2; j<global.trigger.get(i).size(); j++) {

                            String TMP_extraspaces = "  ";

                            String TMP_e = global.trigger.get(i).get(j);
                            String TMP_c;
                            if (!TMP_e.contains(" ")) {TMP_c = TMP_e; TMP_e = "";}
                            else {TMP_c = TMP_e.substring(0, TMP_e.indexOf(" ")); TMP_e = TMP_e.substring(TMP_e.indexOf(" ") + 1, TMP_e.length());}

                            if (TMP_c.equalsIgnoreCase("END")
                                    || TMP_c.toUpperCase().startsWith("ELSE")) {
                                tabbed_logic--;
                            }
                            for (int k=0; k<tabbed_logic; k++) {TMP_extraspaces+= "  ";}
                            TMP_c = TMP_c.replace("(","LeftParF6cyUQp9LeftPar").replace(")","RightParF6cyUQp9RightPar").replace("&","AmpF6cyUQp9Amp");
                            TMP_e = TMP_e.replace("(","LeftParF6cyUQp9LeftPar").replace(")","RightParF6cyUQp9RightPar").replace("&","AmpF6cyUQp9Amp");
                            String pre_out_event;
                            if (TMP_imported) {pre_out_event = " ";}
                            else {pre_out_event = "clickable(&c-,suggest_command,/trigger remove "+i+" "+j+" [enter to confirm],Remove event)";}
                            ChatHandler.warn(TMP_extraspaces+pre_out_event+" &8"+TMP_c+" &7"+TMP_e.replace(" "," &7").replace("\\","BackslashF6cyUQp9Backslash"));

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
            ChatHandler.warnBreak(1);
        }
    }

    private static void commandSettings(String args[]) {
        if (args.length < 2) {
            ChatHandler.warn(ChatHandler.color("red", "/trigger settings [debug/test/color/killfeed/beta] <...>"));
        } else {
            if (args[1].equalsIgnoreCase("DEBUG")) {
                if (args.length == 2) {
                    if (!global.debug) {ChatHandler.warn(ChatHandler.color("gray", "Toggled debug mode") + " " + ChatHandler.color(global.settings.get(0), "on")); global.debug=true;}
                    else {ChatHandler.warn(ChatHandler.color("gray", "Toggled debug mode") + " " + ChatHandler.color(global.settings.get(0), "off")); global.debug=false;}
                } else {
                    if (args[2].equalsIgnoreCase("CHAT")) {
                        if (!global.debugChat) {ChatHandler.warn(ChatHandler.color("gray", "Toggled debug chat mode") + " " + ChatHandler.color(global.settings.get(0), "on")); global.debugChat=true;}
                        else {ChatHandler.warn(ChatHandler.color("gray", "Toggled debug chat mode") + " " + ChatHandler.color(global.settings.get(0), "off")); global.debugChat=false;}
                    }
                }

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
                                global.showDisplayGui = true;
                            }
                        } else {
                            global.showDisplayGui = true;
                        }
                    } else if (args[2].equalsIgnoreCase("FADE")) {
                        if (global.settings.get(9).equalsIgnoreCase("TRUE")) {
                            global.settings.set(9, "false");
                            CommandReference.silentResetAll();
                            ChatHandler.warn(ChatHandler.color("gray", "Toggled killfeed fade") + " " + ChatHandler.color("red", "off"));
                            try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
                        } else {
                            global.settings.set(9, "true");
                            CommandReference.silentResetAll();
                            ChatHandler.warn(ChatHandler.color("gray", "Toggled killfeed fade") + " " + ChatHandler.color("green", "on"));
                            try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
                        }
                    } else if (args[2].equalsIgnoreCase("SHOWINNOTIFICATION") || args[2].equalsIgnoreCase("SHOWINNOTIFICATIONS") || args[2].equalsIgnoreCase("SHOWINNOTIFY")) {
                        if (global.settings.get(10).equalsIgnoreCase("FALSE")) {
                            global.settings.set(10, "true");
                            CommandReference.silentResetAll();
                            ChatHandler.warn(ChatHandler.color("gray", "Toggled showing killfeed as notifications") + " " + ChatHandler.color("green", "on"));
                            try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
                        } else {
                            global.settings.set(10, "false");
                            CommandReference.silentResetAll();
                            ChatHandler.warn(ChatHandler.color("gray", "Toggled showing killfeed as notifications") + " " + ChatHandler.color("red", "off"));
                            try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
                        }
                    } else {
                        ChatHandler.warn(ChatHandler.color("red", "/trigger settings killfeed [position/fade/showInNotify] <...>"));
                    }
                } else {
                    ChatHandler.warn(ChatHandler.color("red", "/trigger settings killfeed [position/fade/showInNotify] <...>"));
                }
            } else if (args[1].equalsIgnoreCase("NOTIFY")) {
                if (args.length>2) {
                    if (args[2].equalsIgnoreCase("SPEED")) {
                        if (args.length>3) {
                            try {
                                int get = Integer.parseInt(args[3]);
                                if (get<=1) {
                                    throw new NumberFormatException();
                                } else {
                                    global.settings.set(8, get+"");
                                    global.settingsNotificationSpeed = get;
                                    ChatHandler.warn(ChatHandler.color("gray", "Notification speed set to") + " " + ChatHandler.color(global.settings.get(0), get+""));
                                    try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
                                }
                            } catch(NumberFormatException e) {
                                e.printStackTrace();
                                ChatHandler.warn(ChatHandler.color("red", "/trigger settings notify [speed] <integer (greater is slower)>"));
                                ChatHandler.warn(ChatHandler.color("red", "<integer> MUST be a number greater than 1!"));
                            }
                        } else {
                            ChatHandler.warn(ChatHandler.color("red", "/trigger settings notify [speed] <integer (greater is slower)>"));
                        }
                    } else {
                        ChatHandler.warn(ChatHandler.color("red", "/trigger settings notify [speed] <...>"));
                    }
                } else {
                    ChatHandler.warn(ChatHandler.color("red", "/trigger settings notify [speed] <...>"));
                }
            } else if (args[1].equalsIgnoreCase("BETA")) {
                if (args.length>2) {
                    if (args[2].equalsIgnoreCase("TOGGLE")) {
                        if (global.settings.get(4).equals("false")) {
                            global.settings.set(4, "true");
                            ChatHandler.warn(ChatHandler.color("red", "You have turned nightly notifications")+" "+ChatHandler.color("green","on!"));
                            ChatHandler.warn(ChatHandler.color("red", "For more info, do </trigger settings beta>"));
                            UpdateHandler.loadVersion("http://kerbybit.github.io/ChatTriggers/download/betaversion.txt");
                            try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
                        } else {
                            global.settings.set(4, "false");
                            ChatHandler.warn(ChatHandler.color("red", "You have turned nightly notifications off!"));
                            ChatHandler.warn(ChatHandler.color("red", "For more info, do </trigger settings beta>"));
                            UpdateHandler.loadVersion("http://kerbybit.github.io/ChatTriggers/download/version.txt");
                            try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
                        }
                    } else {
                        ChatHandler.warn(ChatHandler.color("red", "/trigger settings beta [toggle]"));
                    }
                } else {
                    ChatHandler.warnBreak(0);
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
                    ChatHandler.warnBreak(1);
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
            }else if (args[1].equalsIgnoreCase("DUMP")) {
                if (args.length == 2) {
                    for (String fmsg : global.chatHistory) {
                        String tmp_out = ChatHandler.removeFormatting(fmsg);
                        global.copyText.add(tmp_out);
                        /*String tmp_outfin = tmp_out.replace(",", "stringCommaReplacementF6cyUQp9stringCommaReplacement")
                                .replace("(", "stringOpenBracketF6cyUQp9stringOpenBracket")
                                .replace(")", "stringCloseBracketF6cyUQp9stringCloseBracket");
                        ChatHandler.warn("clickable("+tmp_outfin+",run_command,/t copy CopyFromDebugChat "+(global.copyText.size()-1)+",Click to copy\n"+tmp_out+")");*/
                        tmp_out = tmp_out.replace("'", "\\'");
                        List<String> TMP_eventout = new ArrayList<String>();
                        TMP_eventout.add("text:'" + tmp_out + "',clickEvent:{action:'run_command',value:'/t copy CopyFromDebugChat " + (global.copyText.size()-1) + "'},hoverEvent:{action:'show_text',value:'Click to copy\n" + tmp_out + "'}");
                        ChatHandler.sendJson(TMP_eventout);
                    }
                } else {
                    try {
                        int get = Integer.parseInt(args[2]);
                        if (get > global.chatHistory.size()) {
                            ChatHandler.warn("&c/trigger settings dump [number]");
                            ChatHandler.warn("&c[number] must not be bigger than "+global.chatHistory.size()+"!");
                        } else {
                            for (int i=global.chatHistory.size() - get; i < global.chatHistory.size(); i++) {
                                String tmp_out = ChatHandler.removeFormatting(global.chatHistory.get(i));
                                global.copyText.add(tmp_out);
                                /*String tmp_outfin = tmp_out.replace(",", "stringCommaReplacementF6cyUQp9stringCommaReplacement")
                                        .replace("(", "stringOpenBracketF6cyUQp9stringOpenBracket")
                                        .replace(")", "stringCloseBracketF6cyUQp9stringCloseBracket");
                                ChatHandler.warn("clickable("+tmp_outfin+",run_command,/t copy CopyFromDebugChat "+(global.copyText.size()-1)+",Click to copy\n"+tmp_out+")");*/
                                tmp_out = tmp_out.replace("'", "\\'");
                                List<String> TMP_eventout = new ArrayList<String>();
                                TMP_eventout.add("text:'" + tmp_out + "',clickEvent:{action:'run_command',value:'/t copy CopyFromDebugChat " + (global.copyText.size() - 1) + "'},hoverEvent:{action:'show_text',value:'Click to copy\n" + tmp_out + "'}");
                                ChatHandler.sendJson(TMP_eventout);
                            }
                        }
                    } catch (Exception e) {
                        ChatHandler.warn(ChatHandler.color("red", "/trigger settings dump [number]"));
                        ChatHandler.warn(ChatHandler.color("red", args[2] + " is not a number!"));
                    }
                }
            } else if (args[1].equalsIgnoreCase("FPS")) {
                if (args.length == 2) {
                    ChatHandler.warn(ChatHandler.color("red", "/trigger settings fps [low/medium/high] <...>"));
                } else {
                    if (args[2].equalsIgnoreCase("LOW") || args[2].equalsIgnoreCase("L")) {
                        if (args.length == 3) {
                            ChatHandler.warn(ChatHandler.color("red","/trigger settings fps low [color] [number]"));
                        } else {
                            if (args.length == 4) {
                                global.fpslowcol = args[3];
                                ChatHandler.warn(global.settings.get(0) +"Changed fps low to " + global.fpslowcol + global.fpslow);
                                try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
                            } else {
                                try {
                                    global.fpslowcol = args[3];
                                    global.fpslow = Integer.parseInt(args[4]);
                                    ChatHandler.warn(global.settings.get(0) + "Changed fps low to " + global.fpslowcol + global.fpslow);
                                    try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
                                } catch (NumberFormatException e) {
                                    ChatHandler.warn(ChatHandler.color("red", "/trigger settings fps low [color] [number]"));
                                    ChatHandler.warn(ChatHandler.color("red", args[4] + " is not a number!"));
                                }
                            }
                        }
                    } else if (args[2].equalsIgnoreCase("MED") || args[2].equalsIgnoreCase("MEDIUM") || args[2].equalsIgnoreCase("M")) {
                        if (args.length == 3) {
                            ChatHandler.warn(ChatHandler.color("red","/trigger settings fps medium [color]"));
                        } else {
                            global.fpsmedcol = args[3];
                            ChatHandler.warn(global.settings.get(0) + "Changed fps medium to " + global.fpsmedcol + global.fpslow + "-" + global.fpshigh);
                            try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
                        }
                    } else if (args[2].equalsIgnoreCase("HIGH") || args[2].equalsIgnoreCase("H")) {
                        if (args.length == 3) {
                            ChatHandler.warn(ChatHandler.color("red","/trigger settings fps high [color] [number]"));
                        } else {
                            if (args.length == 4) {
                                global.fpshighcol = args[3];
                                ChatHandler.warn(global.settings.get(0) + "Changed fps high to " + global.fpshighcol + global.fpshigh);
                                try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
                            } else {
                                try {
                                    global.fpshighcol = args[3];
                                    global.fpshigh = Integer.parseInt(args[4]);
                                    ChatHandler.warn(global.settings.get(0) + "Changed fps high to " + global.fpshighcol + global.fpshigh);
                                    try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
                                } catch (NumberFormatException e) {
                                    ChatHandler.warn(ChatHandler.color("red", "/trigger settings fps high [color] [number]"));
                                    ChatHandler.warn(ChatHandler.color("red", args[4] + " is not a number!"));
                                }
                            }
                        }
                    }
                }
            } else {
                ChatHandler.warn(ChatHandler.color("red", "/trigger settings [debug/dump/test/color/killfeed/beta] <...>"));
            }
        }
    }

    private static void commandSave() {
        CommandReference.silentResetAll();
        ChatHandler.warn(ChatHandler.color(global.settings.get(0), "Organized and saved files"));
    }

    private static void commandLoad() {
        global.canSave = true;
        try {
            global.trigger = FileHandler.loadTriggers("./mods/ChatTriggers/triggers.txt", false, null);
            global.USR_string = FileHandler.loadStrings("./mods/ChatTriggers/strings.txt");
            global.settings = FileHandler.loadSettings("./mods/ChatTriggers/settings.txt");
            FileHandler.loadImports("./mods/ChatTriggers/Imports/");
            CommandReference.silentResetAll();
            ChatHandler.warn(global.settings.get(0) + "Files loaded");
        } catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error loading triggers!"));}
    }
}
