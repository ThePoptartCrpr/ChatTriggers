package com.kerbybit.chattriggers.triggers;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.kerbybit.chattriggers.chat.ChatHandler;
import com.kerbybit.chattriggers.globalvars.global;

import com.kerbybit.chattriggers.references.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import static com.kerbybit.chattriggers.triggers.TriggerHandler.onChat;

public class EventsHandler {
	public static String doEvents(List<String> tmp_tmp_event, ClientChatReceivedEvent chatEvent) {
		List<String> tmp_event = new ArrayList<String>(tmp_tmp_event);
		return doEvents(tmp_event, chatEvent, null, null);
	}
	
	public static String doEvents(List<String> tmp_tmp_event, ClientChatReceivedEvent chatEvent, String[] toreplace, String[] replacement) {
		List<String> tmp_event = new ArrayList<String>(tmp_tmp_event);
		String stringCommaReplace = "stringCommaReplacementF6cyUQp9stringCommaReplacement";
        String ret = "null";
		
		if (toreplace != null) {
			for (int i=0; i<toreplace.length; i++) {
				List<String> temporary = new ArrayList<String>();
				temporary.add("TriggerArgument"+i+"-"+global.TMP_string.size());
				temporary.add(replacement[i]);
				for (int j=0; j<tmp_event.size(); j++) {
                    tmp_event.set(j, tmp_event.get(j).replace(toreplace[i],"{string[TriggerArgument"+i+"-"+global.TMP_string.size()+"]}"));
                }
				global.TMP_string.add(temporary);
			}
		}
		
		for (int i=0; i<tmp_event.size(); i++) {
        //SETUP
			String TMP_e = tmp_event.get(i);
            global.lastEvent = TMP_e;
			String TMP_c;
			if (!TMP_e.contains(" ")) {TMP_c = TMP_e; TMP_e="";}
			else {TMP_c = TMP_e.substring(0, TMP_e.indexOf(" ")); TMP_e = TMP_e.substring(TMP_e.indexOf(" ")+1, TMP_e.length());}
			int TMP_t = 50;
			int TMP_p = global.notifySize;
			int TMP_v = 100;
			int TMP_pi = 1;
			
		//setup backup for functions so strings don't get overwritten
			StringHandler.resetBackupStrings();
			
		//built in strings
			TMP_e = StringHandler.builtInStrings(TMP_e, chatEvent);
			
		//user strings and functions
			TMP_e = TMP_e.replace("{string<", "{string[").replace("{array<", "{array[").replace(">}", "]}");
			
			TMP_e = StringHandler.stringFunctions(TMP_e, chatEvent);
			TMP_e = ArrayHandler.arrayFunctions(TMP_e, chatEvent);
			TMP_e = StringHandler.stringFunctions(TMP_e, chatEvent);
			
		//tags
			try {
				if (TMP_e.contains("<time=") && TMP_e.contains(">")) {
					TMP_t = Integer.parseInt(TagHandler.eventTags(1, TMP_e));
					TMP_e = TMP_e.replace("<time="+TMP_t+">", "");
				}
			} catch (NumberFormatException e1) {ChatHandler.warn(ChatHandler.color("red", "<time=t> t must be an integer!"));}
			
			try {
				if (TMP_e.contains("<pos=") && TMP_e.contains(">")) {
					TMP_p = Integer.parseInt(TagHandler.eventTags(2, TMP_e));
					TMP_e = TMP_e.replace("<pos="+TMP_p+">", "");
				}
			} catch (NumberFormatException e1) {ChatHandler.warn(ChatHandler.color("red", "<pos=p> p must be an integer!"));}
			
			try {
				if (TMP_e.contains("<vol=") && TMP_e.contains(">")) {
					TMP_v = Integer.parseInt(TagHandler.eventTags(3, TMP_e));
					TMP_e = TMP_e.replace("<vol="+TMP_v+">", "");
				}
			} catch (NumberFormatException e1) {ChatHandler.warn(ChatHandler.color("red", "<vol=v> v must be an integer!"));}
			
			try {
				if (TMP_e.contains("<pitch=") && TMP_e.contains(">")) {
					TMP_pi = Integer.parseInt(TagHandler.eventTags(4, TMP_e));
					TMP_e = TMP_e.replace("<pitch="+TMP_pi+">", "");
				}
			} catch (NumberFormatException e1) {ChatHandler.warn(ChatHandler.color("red", "<pitch=p> p must be an integer!"));}
			
			
		//add formatting where needed
			if (TMP_c.equalsIgnoreCase("SAY") || TMP_c.equalsIgnoreCase("CHAT") || TMP_c.equalsIgnoreCase("KILLFEED") || TMP_c.equalsIgnoreCase("NOTIFY")) {
				if (TMP_c.equalsIgnoreCase("SAY")) {if (!Minecraft.getMinecraft().isSingleplayer()) {TMP_e = ChatHandler.addFormatting(TMP_e);}}
				else {TMP_e = ChatHandler.addFormatting(TMP_e);}
			}
			
		//non-logic events
			if (TMP_c.equalsIgnoreCase("TRIGGER")) {doTrigger(TMP_e, chatEvent);}
            if (TMP_c.equalsIgnoreCase("CHAT")) {
                ChatHandler.warn(TMP_e);
            }
			TMP_e = TMP_e.replace(stringCommaReplace, ",");
			if (TMP_c.equalsIgnoreCase("SAY")) {if (!global.hasWatermark) {global.chatQueue.add(TMP_e);}}

            if (TMP_c.equalsIgnoreCase("DEBUG") || TMP_c.equalsIgnoreCase("DO")) {if (global.debug) {ChatHandler.warn(TMP_e);}}
            if (TMP_c.equalsIgnoreCase("LOG")) {System.out.println(TMP_e
                    .replace("stringCommaReplacementF6cyUQp9stringCommaReplacement", ",")
                    .replace("stringOpenBracketF6cyUQp9stringOpenBracket", "(")
                    .replace("stringCloseBracketF6cyUQp9stringCloseBracket", ")"));}
            if (TMP_c.equalsIgnoreCase("SIMULATE")) {
                ChatHandler.warn(TMP_e);
                onChat(TMP_e, ChatHandler.deleteFormatting(TMP_e), null);
            }
			if (TMP_c.equalsIgnoreCase("SOUND")) {
                float real_v = ((float)TMP_v) / 100;
                ResourceLocation location = new ResourceLocation("minecraft",""+TMP_e
                        .replace("stringCommaReplacementF6cyUQp9stringCommaReplacement", ",")
                        .replace("stringOpenBracketF6cyUQp9stringOpenBracket", "(")
                        .replace("stringCloseBracketF6cyUQp9stringCloseBracket", ")"));
                Minecraft.getMinecraft().thePlayer.playSound(SoundEvent.REGISTRY.getObject(location), real_v, TMP_pi);}
			if (TMP_c.equalsIgnoreCase("CANCEL") && chatEvent!=null) {chatEvent.setCanceled(true);}
			if (TMP_c.equalsIgnoreCase("KILLFEED")) {
                if (global.settings.get(10).equalsIgnoreCase("FALSE")) {
                    global.killfeed.add(TMP_e
                            .replace("stringCommaReplacementF6cyUQp9stringCommaReplacement", ",")
                            .replace("stringOpenBracketF6cyUQp9stringOpenBracket", "(")
                            .replace("stringCloseBracketF6cyUQp9stringCloseBracket", ")")); global.killfeedDelay.add(TMP_t);
                } else {
                    TMP_c = "notify";
                }
            }

			if (TMP_c.equalsIgnoreCase("NOTIFY")) {
				global.notify.add(TMP_e
					.replace("stringCommaReplacementF6cyUQp9stringCommaReplacement", ",")
					.replace("stringOpenBracketF6cyUQp9stringOpenBracket", "(")
					.replace("stringCloseBracketF6cyUQp9stringCloseBracket", ")"));
				List<Float> temp_list = new ArrayList<Float>();
				temp_list.add((float) 0);temp_list.add((float) -1000);
				temp_list.add((float) TMP_p);temp_list.add((float) TMP_t);
				temp_list.add((float) 0);temp_list.add((float) -1000);
				global.notifyAnimation.add(temp_list);
				global.notifySize++;
			}
			if (TMP_c.equalsIgnoreCase("COMMAND")) {global.commandQueue.add(TMP_e);}
			if (TMP_c.equalsIgnoreCase("COPY")) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(new StringSelection(TMP_e
					.replace("stringCommaReplacementF6cyUQp9stringCommaReplacement", ",")
					.replace("stringOpenBracketF6cyUQp9stringOpenBracket", "(")
					.replace("stringCloseBracketF6cyUQp9stringCloseBracket", ")")), null);
			}
			if (TMP_c.equalsIgnoreCase("URL")) {
				try {Desktop.getDesktop().browse(URI.create(TMP_e
						.replace("stringCommaReplacementF6cyUQp9stringCommaReplacement", ",")
						.replace("stringOpenBracketF6cyUQp9stringOpenBracket", "(")
						.replace("stringCloseBracketF6cyUQp9stringCloseBracket", ")")));}
				catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Unable to open URL! IOExeption"));}
			}
            if (TMP_c.equalsIgnoreCase("RETURN")) {
                ret = TMP_e
                        .replace("stringCommaReplacementF6cyUQp9stringCommaReplacement", ",")
                        .replace("stringOpenBracketF6cyUQp9stringOpenBracket", "(")
                        .replace("stringCloseBracketF6cyUQp9stringCloseBracket", ")");
            }
			
			
		//logic events
			if (TMP_c.equalsIgnoreCase("ASYNC")) {
				int tabbed_logic = 0;
				List<String> eventsToAsync = new ArrayList<String>();
				
				if (i+1 < tmp_event.size()-1) {
					for (int j=i; j<tmp_event.size(); j++) {
						if (j != tmp_event.size()) {
							//increase tab
							if (tmp_event.get(j).toUpperCase().startsWith("IF")
							|| tmp_event.get(j).toUpperCase().startsWith("FOR")
							|| tmp_event.get(j).toUpperCase().startsWith("CHOOSE")
							|| tmp_event.get(j).toUpperCase().startsWith("WAIT")
							|| tmp_event.get(j).toUpperCase().startsWith("ASYNC")) {
								tabbed_logic++;
							}
							
							//add to list
							eventsToAsync.add(tmp_event.get(j));
							
							//decrease tab
							if (tmp_event.get(j).toUpperCase().startsWith("END")) {tabbed_logic--;}
						}

                        //check if exit
                        if (tabbed_logic==0) {j=tmp_event.size();}
					}
					
					eventsToAsync.remove(0);
					eventsToAsync.remove(eventsToAsync.size()-1);
					global.asyncEvents.clear();
					global.asyncEvents.addAll(eventsToAsync);
					Thread t1 = new Thread(new Runnable() {
					     public void run() {
					          EventsHandler.doEvents(global.asyncEvents, null);
					          global.asyncEvents.clear();
					     }
					});
					t1.start();
					
				}
				
				//move i
				i += eventsToAsync.size();
			}
			
			if (TMP_c.equalsIgnoreCase("WAIT")) {
				int tabbed_logic = 0;
				List<String> eventsToWait = new ArrayList<String>();
				
				if (i+1 < tmp_event.size()-1) { //check for events after if event
					for (int j=i; j<tmp_event.size(); j++) {
						if (j != tmp_event.size()) {
							if (chatEvent!=null) {tmp_event.set(j, tmp_event.get(j).replace("{msg}", chatEvent.getMessage().getFormattedText()));}
							
							//increase tab
							if (tmp_event.get(j).toUpperCase().startsWith("IF")
							|| tmp_event.get(j).toUpperCase().startsWith("FOR")
							|| tmp_event.get(j).toUpperCase().startsWith("CHOOSE")
							|| tmp_event.get(j).toUpperCase().startsWith("WAIT")
							|| tmp_event.get(j).toUpperCase().startsWith("ASYNC")) {
								tabbed_logic++;
							}
							
							//add to list
							eventsToWait.add(tmp_event.get(j));
							
							//decrease tab
							if (tmp_event.get(j).toUpperCase().startsWith("END")) {tabbed_logic--;}
						}
						
						//check if exit
						if (tabbed_logic==0) {j=tmp_event.size();}
					}
					
					eventsToWait.remove(0);
					eventsToWait.remove(eventsToWait.size()-1);
					try {
						int TMP_time = Integer.parseInt(TMP_e);
						if (TMP_time>0) {
							global.waitEvents.add(eventsToWait);
							global.waitTime.add(TMP_time);
						} else {
							ChatHandler.warn(ChatHandler.color("red", "Malformed WAIT event - skipping"));
						}
					} catch (NumberFormatException e) {
                        String get_time = TMP_e.toUpperCase();
						if (get_time.contains("H") || get_time.contains("M") || get_time.contains("S")) {
                            try {
                                if (get_time.startsWith("H") || get_time.startsWith("M") || get_time.startsWith("S")) {
                                    ChatHandler.warn(ChatHandler.color("red", "Malformed WAIT event - skipping"));
                                } else {
                                    String hours = "0";
                                    if (get_time.contains("H")) {
                                        hours = get_time.substring(0, get_time.indexOf("H"));
                                        if (hours.contains("M")) {
                                            hours = hours.substring(hours.indexOf("M")+1);
                                        }
                                        if (hours.contains("S")) {
                                            hours = hours.substring(hours.indexOf("S")+1);
                                        }
                                    }
                                    String minutes = "0";
                                    if (get_time.contains("M")) {
                                        minutes = get_time.substring(0, get_time.indexOf("M"));
                                        if (minutes.contains("H")) {
                                            minutes = minutes.substring(minutes.indexOf("H")+1);
                                        }
                                        if (minutes.contains("S")) {
                                            minutes = minutes.substring(minutes.indexOf("S")+1);
                                        }
                                    }
                                    String seconds = "0";
                                    if (get_time.contains("S")) {
                                        seconds = get_time.substring(0, get_time.indexOf("S"));
                                        if (seconds.contains("H")) {
                                            seconds = seconds.substring(seconds.indexOf("H")+1);
                                        }
                                        if (seconds.contains("M")) {
                                            seconds = seconds.substring(seconds.indexOf("M")+1);
                                        }
                                    }
                                    int TMP_time = Integer.parseInt(hours)*72000 + Integer.parseInt(minutes)*1200 + Integer.parseInt(seconds)*20;
                                    if (TMP_time>0) {
                                        global.waitEvents.add(eventsToWait);
                                        global.waitTime.add(TMP_time);
                                    } else {
                                        ChatHandler.warn(ChatHandler.color("red", "Malformed WAIT event - skipping"));
                                    }
                                }
                            } catch (NumberFormatException e2) {
                                ChatHandler.warn(ChatHandler.color("red", "Malformed WAIT event - skipping"));
                            }
                        } else {
                            ChatHandler.warn(ChatHandler.color("red", "Malformed WAIT event - skipping"));
                        }
					}
				}
				
				//move i
				i += eventsToWait.size();
			}
			
			if (TMP_c.equalsIgnoreCase("FOR")) {
				int tabbed_logic = 0;
				List<String> eventsToFor = new ArrayList<String>();

				
				if (i+1 < tmp_event.size()) {
					for (int j=i; j<tmp_event.size(); j++) {
						if (j != tmp_event.size()) {
							
							//increase tab
							if (tmp_event.get(j).toUpperCase().startsWith("IF")
							|| tmp_event.get(j).toUpperCase().startsWith("FOR")
							|| tmp_event.get(j).toUpperCase().startsWith("CHOOSE")
							|| tmp_event.get(j).toUpperCase().startsWith("WAIT")
							|| tmp_event.get(j).toUpperCase().startsWith("ASYNC")) {
								tabbed_logic++;
							}
							
							//add to list
							eventsToFor.add(tmp_event.get(j));
							
							//decrease tab
							if (tmp_event.get(j).toUpperCase().startsWith("END")) {tabbed_logic--;}
						}
						
						//check if exit
						if (tabbed_logic==0) {j=tmp_event.size();}
					}
				}
				
				eventsToFor.remove(0);
				eventsToFor.remove(eventsToFor.size()-1);

                if (TMP_e.contains(":") || (TMP_e.contains(" in "))) {
                    String[] tmp_valuefor;
                    if (TMP_e.contains(":")) {tmp_valuefor = TMP_e.split(":");}
                    else {tmp_valuefor = TMP_e.split(" in ");}

                    String valin = "";
                    String valfrom = "";
                    String valwait = "";
                    List<String> arrayto = new ArrayList<String>();
                    if (tmp_valuefor.length==2) {
                        valin = tmp_valuefor[0].trim();
                        valfrom = tmp_valuefor[1].trim();
						if (valfrom.contains(" wait ")) {
                            String[] temp_valuefrom = valfrom.split(" wait ");
                            if (temp_valuefrom.length==2) {
                                valfrom = temp_valuefrom[0].trim();
                                valwait = temp_valuefrom[1].trim();
                            }
                        }
                    } else {
                        if (global.debug) {ChatHandler.warn(ChatHandler.color("red", "ERR: for $value in $array -> missing value or array!"));}
                    }
                    for (int j=0; j<global.USR_array.size(); j++) {
                        if (global.USR_array.get(j).get(0).equals(valfrom)) {
                            arrayto.addAll(global.USR_array.get(j));
                        }
                    }

                    if (arrayto.size()>0 && eventsToFor.size() > 0) {
                        if (valwait.equals("")) {
                            for (int j=1; j<arrayto.size(); j++) {
                                String[] first = {valin};
                                String[] second = {arrayto.get(j)};
                                ret = doEvents(eventsToFor, chatEvent, first, second);
                            }
                        } else {
                            try {
                                int intwait = Integer.parseInt(valwait.replace(",",""));
                                for (int j=1; j<arrayto.size(); j++) {
                                    List<String> eventsToForFin = new ArrayList<String>(eventsToFor);
                                    List<String> temporary = new ArrayList<String>();
                                    temporary.add("TriggerArgument"+j+"-"+global.TMP_string.size());
                                    temporary.add(arrayto.get(j));
                                    for (int k=0; k<eventsToFor.size(); k++) {
                                        eventsToForFin.set(k, eventsToFor.get(k).replace(valin,"{string[TriggerArgument"+j+"-"+global.TMP_string.size()+"]}"));
                                    }
                                    global.TMP_string.add(temporary);

                                    global.waitEvents.add(eventsToForFin);
                                    global.waitTime.add(intwait*(j-1));
                                }
                            } catch (NumberFormatException e) {
                                if (global.debug) {ChatHandler.warn(ChatHandler.color("red", "ERR: for $value in $array wait $ticks -> expected number for ticks!"));}
                            }
                        }
                    }
                }

                if (TMP_e.contains(" from ") && TMP_e.contains(" to ")) {
                    String[] args = TMP_e.split(" from | to | wait");
                    if (args.length == 3 || args.length == 4) {
                        try {
                            int int_from = Integer.parseInt(args[1].trim());
                            int int_to = Integer.parseInt(args[2].trim());

                            if (args.length == 3) {
                                for (int j=int_from; j<int_to+1; j++) {
                                    String[] first = {args[0].trim()};
                                    String[] second = {j + ""};
                                    ret = doEvents(eventsToFor, chatEvent, first, second);
                                }
                            } else {
                                try {
                                    int intwait = Integer.parseInt(args[3].trim());
                                    int count = 0;
                                    for (int j=int_from; j<int_to+1; j++) {
                                        List<String> eventsToForFin = new ArrayList<String>(eventsToFor);
                                        List<String> temporary = new ArrayList<String>();
                                        temporary.add("TriggerArgument"+j+"-"+global.TMP_string.size());
                                        temporary.add(j+"");
                                        for (int k=0; k<eventsToFor.size(); k++) {
                                            eventsToForFin.set(k, eventsToFor.get(k).replace(args[0].trim(),"{string[TriggerArgument"+j+"-"+global.TMP_string.size()+"]}"));
                                        }
                                        global.TMP_string.add(temporary);

                                        global.waitEvents.add(eventsToForFin);
                                        global.waitTime.add(intwait*count);
                                        count++;
                                    }
                                } catch (NumberFormatException e) {
                                    if (global.debug) {ChatHandler.warn(ChatHandler.color("red", "ERR: for $i from $n1 to $n2 wait $ticks -> expected number for ticks!"));}
                                }
                            }
                        }catch (NumberFormatException e) {
                            if (global.debug) {ChatHandler.warn(ChatHandler.color("red", "'FOR $i FROM $n1 TO $n2' -> either $n1 or $n2 is not a number"));}
                        }
                    } else {
                        if (global.debug) {ChatHandler.warn(ChatHandler.color("red", "'FOR $i FROM $n1 TO $n2' -> expecting FROM and TO"));}
                    }
                }

				
				//move i
				i += eventsToFor.size();
			}
			
			if (TMP_c.equalsIgnoreCase("IF")) {
				int tabbed_logic = 0;
				List<String> eventsToIf = new ArrayList<String>();
				List<String> eventsToElse = new ArrayList<String>();
				Boolean gotoElse = false;
				
				if (i+1 < tmp_event.size()-1) { //check for events after if event
					for (int j=i; j<tmp_event.size(); j++) {
						if (j != tmp_event.size()) {
							
							//increase tab
							if (tmp_event.get(j).toUpperCase().startsWith("IF")
							|| tmp_event.get(j).toUpperCase().startsWith("FOR")
							|| tmp_event.get(j).toUpperCase().startsWith("CHOOSE")
							|| tmp_event.get(j).toUpperCase().startsWith("WAIT")
							|| tmp_event.get(j).toUpperCase().startsWith("ASYNC")) {
								tabbed_logic++;
							}
							
							//move to else
							if (tmp_event.get(j).toUpperCase().startsWith("ELSE") && tabbed_logic==1) {gotoElse=true;}
							
							//add to list
							if (!gotoElse) {eventsToIf.add(tmp_event.get(j));}
							else {eventsToElse.add(tmp_event.get(j));}
							
							//decrease tab
							if (tmp_event.get(j).toUpperCase().startsWith("END")) {tabbed_logic--;}
						}
						
						//check if exit
						if (tabbed_logic==0) {j=tmp_event.size();}
					}
					
					//move i to end of if
					i += eventsToIf.size()+eventsToElse.size()-2;
					
					//&& || ^
					String[] checkSplit = TMP_e.split(" ");
					for (int j=1; j<checkSplit.length; j++) {
						if (checkSplit[j].equals("&&")) {
							if (checkSplit[j-1].equalsIgnoreCase("TRUE") && checkSplit[j+1].equalsIgnoreCase("TRUE")) {
								checkSplit[j-1] = ""; checkSplit[j] = ""; checkSplit[j+1] = "TRUE";
							} else {checkSplit[j-1] = ""; checkSplit[j] = ""; checkSplit[j+1] = "FALSE";}
						}
						if (checkSplit[j].equals("||")) {
							if (checkSplit[j-1].equalsIgnoreCase("TRUE") || checkSplit[j+1].equalsIgnoreCase("TRUE")) {
								checkSplit[j-1] = ""; checkSplit[j] = ""; checkSplit[j+1] = "TRUE";
							} else {checkSplit[j-1] = ""; checkSplit[j] = ""; checkSplit[j+1] = "FALSE";}
						}
						if (checkSplit[j].equals("^")) {
							if (checkSplit[j-1].equalsIgnoreCase("TRUE") ^ checkSplit[j+1].equalsIgnoreCase("TRUE")) {
								checkSplit[j-1] = ""; checkSplit[j] = ""; checkSplit[j+1] = "TRUE";
							} else {checkSplit[j-1] = ""; checkSplit[j] = ""; checkSplit[j+1] = "FALSE";}
						}
					}
					TMP_e = "";
					for (String value : checkSplit) {TMP_e += value + " ";}
					TMP_e = TMP_e.trim();
					
					//check condition and do events
					if (TMP_e.equalsIgnoreCase("TRUE") || TMP_e.equalsIgnoreCase("NOT FALSE")) {
						if (eventsToIf.size()>0) {
							eventsToIf.remove(0);
							ret = doEvents(eventsToIf, chatEvent);
						}
					} else {
						if (eventsToElse.size()>0) {
							if (eventsToElse.get(0).toUpperCase().startsWith("ELSEIF")) {
								eventsToElse.set(0, eventsToElse.get(0).substring(4));
								ret = doEvents(eventsToElse, chatEvent);
							} else {
								eventsToElse.remove(0);
								ret = doEvents(eventsToElse, chatEvent);
							}
						}
					}
				}
			}
			
			
			if (TMP_c.equalsIgnoreCase("CHOOSE")) {
				int tabbed_logic = 0;
				List<List<String>> eventsToChoose = new ArrayList<List<String>>();
				List<String> eventsToChooseSub = new ArrayList<String>();
				
				if (i+1 < tmp_event.size()-1) {
					for (int j=i; j<tmp_event.size(); j++) {

						if (j != tmp_event.size()) {
							
							//increase tab
							if (tmp_event.get(j).toUpperCase().startsWith("IF")
							|| tmp_event.get(j).toUpperCase().startsWith("FOR")
							|| tmp_event.get(j).toUpperCase().startsWith("CHOOSE")
							|| tmp_event.get(j).toUpperCase().startsWith("WAIT")
							|| tmp_event.get(j).toUpperCase().startsWith("ASYNC")) {
								tabbed_logic++;
							}
							
							//check if first level event
							if (tabbed_logic==1) {
								if (eventsToChooseSub.size() > 0) { //add more than first level events
									List<String> tmp_list2nd = new ArrayList<String>(eventsToChooseSub);
									eventsToChoose.add(tmp_list2nd);
									eventsToChooseSub.clear(); //clears sub choice to add first level event
								}
								eventsToChooseSub.add(tmp_event.get(j));
								List<String> tmp_list1st = new ArrayList<String>(eventsToChooseSub);
								eventsToChoose.add(tmp_list1st); //add first level event
								eventsToChooseSub.clear(); //clear sub choose
							}
							
							//check if greater than first level event
							if (tabbed_logic>1) {eventsToChooseSub.add(tmp_event.get(j));}
							
							//check for last event to group and close any leftover sub choose
							if (j == tmp_event.size()-1 && eventsToChoose.size() > 0) {eventsToChoose.add(eventsToChooseSub);}
							
							//decrease tab
							if (tmp_event.get(j).toUpperCase().startsWith("END")) {tabbed_logic--;}
							
							//check again for first level event
							if (tabbed_logic==1) {
								if (eventsToChooseSub.size() > 0) {//add more than first level events
									List<String> tmp_list3rd = new ArrayList<String>(eventsToChooseSub);
									eventsToChoose.add(tmp_list3rd);
									eventsToChooseSub.clear(); //clear sub choose
								}
							}
						}
					
						//check if choose exit
						if (tabbed_logic==0) {j=tmp_event.size();}
					}
					
					//random number
					int rand = randInt(1,eventsToChoose.size()-2);
					
					//do events
					ret = doEvents(eventsToChoose.get(rand), chatEvent);
					
					//move i to closing end
					int moveEvents = 0;
					for (List<String> events : eventsToChoose) {moveEvents += events.size();}
					i += moveEvents-1;
				}
			}
		}
        return ret;
	}
	
	private static void doTrigger(String triggerName, ClientChatReceivedEvent chatEvent) {
		try {
			//run trigger by number
			int num = Integer.parseInt(triggerName);
			if (num >= 0 && num < global.trigger.size()) {
				//add all events to temp list
				List<String> TMP_events = new ArrayList<String>();
				for (int i=2; i<global.trigger.get(num).size(); i++) {TMP_events.add(global.trigger.get(num).get(i));}
				
				//do events
				doEvents(TMP_events, chatEvent);
			}
		} catch (NumberFormatException e1) { 
			//run trigger by name
			for (int k=0; k<global.trigger.size(); k++) {
				String TMP_trig = global.trigger.get(k).get(1);

                boolean getCase = true;
                if (TMP_trig.contains("<case=false>")) {
                    getCase = false;
                }

				TMP_trig = TagHandler.removeTags(TMP_trig);
				
				//check match
                if (getCase) {
                    if (TMP_trig.equals(triggerName)) {
                        //add all events to temp list
                        List<String> TMP_events = new ArrayList<String>();
                        for (int i = 2; i < global.trigger.get(k).size(); i++) {
                            TMP_events.add(global.trigger.get(k).get(i));
                        }

                        //do events
                        doEvents(TMP_events, chatEvent);
                    } else {
                        if (TMP_trig.contains("(") && TMP_trig.endsWith(")")) {
                            String TMP_trigtest = TMP_trig.substring(0, TMP_trig.indexOf("("));
                            if (triggerName.startsWith(TMP_trigtest) && triggerName.endsWith(")")) {
                                String TMP_argsIn = triggerName.substring(triggerName.indexOf("(") + 1, triggerName.length() - 1);
                                String TMP_argsOut = TMP_trig.substring(TMP_trig.indexOf("(") + 1, TMP_trig.length() - 1);
                                String[] argsIn = TMP_argsIn.split(",");
                                String[] argsOut = TMP_argsOut.split(",");
                                if (argsIn.length == argsOut.length) {
                                    List<String> TMP_events = new ArrayList<String>();
                                    for (int j = 2; j < global.trigger.get(k).size(); j++) {
                                        TMP_events.add(global.trigger.get(k).get(j));
                                    }
                                    doEvents(TMP_events, chatEvent, argsOut, argsIn);
                                }
                            }
                        }
                    }
                } else {
                    if (TMP_trig.equalsIgnoreCase(triggerName)) {
                        //add all events to temp list
                        List<String> TMP_events = new ArrayList<String>();
                        for (int i = 2; i < global.trigger.get(k).size(); i++) {
                            TMP_events.add(global.trigger.get(k).get(i));
                        }

                        //do events
                        doEvents(TMP_events, chatEvent);
                    } else {
                        if (TMP_trig.contains("(") && TMP_trig.endsWith(")")) {
                            String TMP_trigtest = TMP_trig.substring(0, TMP_trig.indexOf("("));
                            if (triggerName.toUpperCase().startsWith(TMP_trigtest.toUpperCase()) && triggerName.endsWith(")")) {
                                String TMP_argsIn = triggerName.substring(triggerName.indexOf("(") + 1, triggerName.length() - 1);
                                String TMP_argsOut = TMP_trig.substring(TMP_trig.indexOf("(") + 1, TMP_trig.length() - 1);
                                String[] argsIn = TMP_argsIn.split(",");
                                String[] argsOut = TMP_argsOut.split(",");
                                if (argsIn.length == argsOut.length) {
                                    List<String> TMP_events = new ArrayList<String>();
                                    for (int j = 2; j < global.trigger.get(k).size(); j++) {
                                        TMP_events.add(global.trigger.get(k).get(j));
                                    }
                                    doEvents(TMP_events, chatEvent, argsOut, argsIn);
                                }
                            }
                        }
                    }
                }
			}
		}
	}
	
	public static void eventTick() {
		try {
			global.playerHealth = (int) Minecraft.getMinecraft().thePlayer.getHealth();
		} catch (NullPointerException e1) {/*do nothing*/}
		
		try {Minecraft.getMinecraft().thePlayer.isServerWorld();} 
		catch (NullPointerException e1) {
			if (global.waitEvents.size()>0) {
				global.waitEvents.clear();
				global.waitTime.clear();
			}
			if (global.asyncEvents.size()>0) {
				global.asyncEvents.clear();
			}
		}
		
		if (global.waitEvents.size()==0 && global.asyncEvents.size()==0 && global.TMP_string.size()>0) {
			global.TMP_string.clear();
			global.jsonURL.clear();
		}
		
		if (global.waitEvents.size()>0) {
			if (global.waitEvents.size() == global.waitTime.size()) {
				for (int i=0; i<global.waitTime.size(); i++) {
					if (global.waitTime.get(i)>0) {
						global.waitTime.set(i, global.waitTime.get(i)-1);
					} else {
						doEvents(global.waitEvents.get(i), null);
						global.waitEvents.remove(i);
						global.waitTime.remove(i);
					}
				}
			} else {
				ChatHandler.warn(ChatHandler.color("red","ERR: wait events and wait time unsynced"));
				global.waitEvents.clear();
				global.waitTime.clear();
			}
		}
	}
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    return rand.nextInt((max - min) + 1) + min;
	}
}
