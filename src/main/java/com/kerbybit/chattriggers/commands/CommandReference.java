package com.kerbybit.chattriggers.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.gson.JsonObject;
import com.kerbybit.chattriggers.chat.ChatHandler;
import com.kerbybit.chattriggers.file.FileHandler;
import com.kerbybit.chattriggers.globalvars.global;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CommandReference {
    public static void clearAll() {
        global.waitEvents.clear();
        global.waitTime.clear();
        global.asyncEvents.clear();
        global.backupTMP_strings.clear();
        global.backupUSR_strings.clear();
        global.killfeed.clear();
        global.killfeedDelay.clear();
        global.notify.clear();
        global.notifyAnimation.clear();
    }

	public static void resetAll() {
		ChatHandler.warn(ChatHandler.color("red", "Resetting everything in attempt to fix things"));
		clearAll();
		try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
		ChatHandler.warn(ChatHandler.color("green", "Reset completed"));
	}

    public static void silentResetAll() {
        clearAll();
        try {FileHandler.saveAll();} catch (IOException e) {ChatHandler.warn(ChatHandler.color("red", "Error saving triggers!"));}
    }
	
	
	static boolean isTriggerType(String TMP_type) {

        List<String> check = new ArrayList<String>(getTriggerTypes());

        for (String value : check) {
            if (!value.equals("")) {
                if (TMP_type.equalsIgnoreCase(value)) {
                    return true;
                }
            }
        }

        return false;
	}

    static List<String> getTriggerTypes() {
        List<String> r = new ArrayList<String>();

            r.add("chat");
            r.add("onChat");
            r.add("other");
            r.add("");
            r.add("onWorldLoad");
            r.add("onWorldFirstLoad");
            r.add("onServerChange");
            r.add("onNewDay");
            r.add("");
            r.add("onClientTick");
            r.add("onRightClickPlayer");
            r.add("");
            r.add("function");
            r.add("onUnknownError");

        return r;
    }
	
	public static void clearTriggerList() {
		global.chatTrigger.clear();
        global.onChatTrigger.clear();
		global.tickTrigger.clear();
		global.onWorldLoadTrigger.clear();
		global.onWorldFirstLoadTrigger.clear();
		global.onServerChangeTrigger.clear();
		global.onNewDayTrigger.clear();
		global.onRightClickPlayerTrigger.clear();
        global.function.clear();
        global.onUnknownError.clear();
	}
	
	public static void addToTriggerList(List<String> tmp_list) {
		if (tmp_list.get(0).equalsIgnoreCase("CHAT")) {
			global.chatTrigger.add(tmp_list);
		} else if (tmp_list.get(0).toUpperCase().startsWith("ONCLIENTTICK")) {
			global.tickTrigger.add(tmp_list);
			if (tmp_list.get(0).equalsIgnoreCase("ONCLIENTTICK")) {
				global.tickTriggerTime.add(1);
			} else {
				try {
					int num = Integer.parseInt(tmp_list.get(0).substring(12).trim());
					global.tickTriggerTime.add(num);
				} catch (NumberFormatException e) {
					global.tickTriggerTime.add(1);
				}
			}
		} else if (tmp_list.get(0).equalsIgnoreCase("ONCHAT")) {
            global.onChatTrigger.add(tmp_list);
        } else if (tmp_list.get(0).equalsIgnoreCase("ONWORLDLOAD")) {
			global.onWorldLoadTrigger.add(tmp_list);
		} else if (tmp_list.get(0).equalsIgnoreCase("ONWORLDFIRSTLOAD")) {
			global.onWorldFirstLoadTrigger.add(tmp_list);
		} else if (tmp_list.get(0).equalsIgnoreCase("ONSERVERCHANGE")) {
			global.onServerChangeTrigger.add(tmp_list);
		} else if (tmp_list.get(0).equalsIgnoreCase("ONNEWDAY")) {
			global.onNewDayTrigger.add(tmp_list);
		} else if (tmp_list.get(0).equalsIgnoreCase("ONRIGHTCLICKPLAYER")) {
			global.onRightClickPlayerTrigger.add(tmp_list);
		} else if (tmp_list.get(0).equalsIgnoreCase("FUNCTION")) {
            global.function.add(tmp_list);
        } else if (tmp_list.get(0).equalsIgnoreCase("ONUNKNOWNERROR")) {
            global.onUnknownError.add(tmp_list);
        }
	}
	
	static Boolean isEventType(String TMP_etype) {
        List<String> check = new ArrayList<String>(getEventTypes());
        for (String value : check) {
            if (!value.equals("")) {
                if (TMP_etype.equalsIgnoreCase(value)) {
                    return true;
                }
            }

        }
        return false;
	}

    public static List<String> getEventTypes() {
        List<String> r = new ArrayList<String>();

            r.add("chat");
            r.add("cancel");
            r.add("killfeed");
            r.add("notify");
            r.add("sound");
            r.add("");
            r.add("trigger");
            r.add("copy");
            r.add("url");
            r.add("do");
            r.add("");
            r.add("if");
            r.add("else");
            r.add("elseif");
            r.add("for");
            r.add("wait");
            r.add("choose");
            r.add("async");
            r.add("end");
            r.add("return");
            r.add("");
            r.add("debug");
            r.add("log");
            r.add("simulate");

        return r;
    }

    static List<String> getStrings() {
        List<String> r = new ArrayList<String>();

            r.add("{msg} {msg[$n]}");
            r.add("{br}");
            r.add("{trigsize}");
            r.add("{notifysize}");
            r.add("{setcol}");
            r.add("{debug}");
            r.add("");
            r.add("{me}");
            r.add("{hp}");
            r.add("{sneak}");
            r.add("{x} {y} {z}");
            r.add("{facing}");
            r.add("{fps}");
            r.add("");
            r.add("{server}");
            r.add("{serverIP}");
            r.add("{serverMOTD}");
            r.add("{serverversion}");
            r.add("{ping}");
            r.add("{scoreboardtitle}");

        return r;
    }

    static List<String> getStringFunctions() {
        List<String> r = new ArrayList<String>();

            r.add(".set($v) .set(~)");
            r.add(".save($v) .save(~)");
            r.add("");
            r.add(".add($n) .plus($n) .+($n)");
            r.add(".subtract($n) .minus($n) .-($n)");
            r.add(".multiply($n) .mult($n) .*($n)");
            r.add(".divide($n) .div($n) ./($n)");
            r.add(".divideGetPercentage($n) .divPercent($n) ./%($n)");
            r.add(".power($n) .pow($n) .^($n)");
            r.add(".modulus($n) .mod($n) .%($n)");
            r.add(".absolute() .abs()");
            r.add("");
            r.add(".greaterThan($n) .>($n)");
            r.add(".greaterThanOrEqualTo($n) .>=($n)");
            r.add(".lessThan($n) .<($n)");
            r.add(".lessThanOrEqualTo($n) .<=($n)");
            r.add("");
            r.add(".equals($v) .=($v) .equalsIgnoreCase($v)");
            r.add(".startsWith($v) .startsWithIgnoreCase($v)");
            r.add(".contains($v) .containsIgnoreCase($v)");
            r.add("endsWith($v) .endsWithIgnoreCase($v)");
            r.add("");
            r.add(".replace($v1,$v2) .replace($v)");
            r.add(".substring($v1,$v2)");
            r.add(".trim()");
            r.add(".prefix($v) .suffix($v)");
            r.add(".toUpper() .toUpperCase()");
            r.add(".toLower() .toLowerCase()");
            r.add(".removeFormatting() .remForm()");
            r.add(".capitalizeFirstWord() .capFirst()");
            r.add(".capitalizeAllWords() .capAll()");
            r.add(".ignoreEscape()");
            r.add("");
            r.add(".importJsonFile($file,$node)");
            r.add(".importJsonURL($URL,$node)");
            r.add("");
            r.add(".length()");
            r.add(".size()");

        return r;
    }

    static List<String> getArrayFunctions() {
        List<String> r = new ArrayList<String>();

            r.add(".clear()");
            r.add(".add($v)");
            r.add(".remove($v) .remove($n)");
            r.add(".get($v) .get($n)");
            r.add(".size()");
            r.add("");
            r.add(".setSplit($v,$split)");
            r.add(".importJsonFile($file,$node)");
            r.add(".improtJsonURL($URL,$node)");
            r.add(".exportJson($file,$node)");

        return r;
    }

    static List<String> getDisplayFunctions() {
        List<String> r = new ArrayList<String>();

            r.add(".add($v)");
            r.add(".clear()");
            r.add(".getX() .getY()");
            r.add(".setX($v) .setY($v)");
            r.add(".update()");

        return r;
    }

    public static String getPing() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) {
            return "" + Minecraft.getMinecraft().getCurrentServerData().pingToServer;
        }
        if(Minecraft.getMinecraft().getNetHandler().getPlayerInfo(UUID.fromString(Minecraft.getMinecraft().thePlayer.getGameProfile().getId().toString())) != null) {
            return "" + Minecraft.getMinecraft().getNetHandler().getPlayerInfo(UUID.fromString(Minecraft.getMinecraft().thePlayer.getGameProfile().getId().toString())).getResponseTime();
        }
        return "" + Minecraft.getMinecraft().getCurrentServerData().pingToServer;
    }

    public static String simplifyPotionName(String name) {
        if (name.equals("moveSpeed")) {
            return "speed";
        } else if (name.equals("fireResistance")) {
            return "fire resistance";
        } else if (name.equals("nightVision")) {
            return "night vision";
        } else if (name.equals("moveSlowdown")) {
            return "slowness";
        } else if (name.equals("damageBoost")) {
            return "strength";
        } else if (name.equals("jump")) {
            return "jump boost";
        } else if (name.equals("waterBreathing")) {
            return "water breathing";
        } else if (name.equals("confusion")) {
            return "nausea";
        } else if (name.equals("digSlowDown")) {
            return "mining fatigue";
        } else if (name.equals("healthBoost")) {
            return "health boost";
        }
        return name;
    }

    public static String getPotionColors(String name) {
        if (name.equals("speed")) {
            return "&b";
        } else if (name.equals("slowness")) {
            return "&9";
        } else if (name.equals("strength")) {
            return "&4";
        } else if (name.equals("weakness")) {
            return "&5";
        } else if (name.equals("jump boost")) {
            return "&a";
        } else if (name.equals("poison")) {
            return "&2";
        } else if (name.equals("fire resistance")
                || name.equals("health boost")) {
            return "&c";
        } else if (name.equals("water breathing")) {
            return "&3";
        } else if (name.equals("regeneration")) {
            return "&d";
        } else if (name.equals("night vision")) {
            return "&1";
        } else if (name.equals("invisibility")) {
            return "&7";
        } else if (name.equals("hunger")) {
            return "&2";
        } else if (name.equals("nausea")
                || name.equals("wither")
                || name.equals("mining fatigue")) {
            return "&8";
        } else if (name.equals("saturation")
                || name.equals("absorption")) {
            return "&6";
        }
        return "&r";
    }


    public static void trimJsons() {
        HashMap<String, JsonObject> jsons_copy = new HashMap<String, JsonObject>(global.jsons);

        for (String key : jsons_copy.keySet()) {
            if (key.startsWith("DefaultJson")) {
                global.jsons.remove(key);
            }
        }
    }

    public static void trimLists() {
        HashMap<String, List<String>> lists_copy = new HashMap<String, List<String>>(global.lists);

        for (String key : lists_copy.keySet()) {
            if (key.startsWith("JsonToList->") || key.startsWith("StringToList->")) {
                global.lists.remove(key);
            }
        }
    }

    private static Long sysTime = Minecraft.getSystemTime();
    //run on render
    public static void clickCalc() {
        while (Minecraft.getSystemTime() > sysTime + 1000L) {
            sysTime += 1000L;
            global.clicks_ave.add(global.clicks);
            if (global.clicks > global.clicks_max) {
                global.clicks_max = global.clicks;
            }
            global.clicks = 0.0;
            if (global.clicks_ave.size() > 10) {
                global.clicks_ave.remove(0);
            }
        }
    }

    private static void renderCustomTexture(int x, int y, int u, int v, int width, int height, ResourceLocation resourceLocation, float scale) {
        x /= scale;
        y /= scale;

        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);

        if(resourceLocation != null) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        }

        Minecraft.getMinecraft().ingameGUI.drawTexturedModalRect(x, y, u, v, width, height);

        GL11.glPopMatrix();
    }

    private static ResourceLocation resourceLocation = new ResourceLocation("textures/gui/container/inventory.png");
    private static void drawPotionIcon(int x, int y, Potion potion) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);

        if(potion.hasStatusIcon())
        {
            int iconIndex = potion.getStatusIconIndex();
            int u = iconIndex % 8 * 18;
            int v = 198 + iconIndex / 8 * 18;
            int width = 18;
            int height = 18;
            float scaler = 0.5f;

            GL11.glColor4f(1f, 1f, 1f, 1f);

            renderCustomTexture(x, y, u, v, width, height, null, scaler);
        }
    }

    private static void drawItemIcon(int x, int y, Item item) {
        RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
        itemRenderer.renderItemIntoGUI(new ItemStack(item), x, y);
    }

    private static Potion getPotionByName(String name) {
        if (name.equals("speed")) {
            return Potion.moveSpeed;
        } else if (name.equals("slowness")) {
            return Potion.moveSlowdown;
        } else if (name.equals("regeneration")) {
            return Potion.regeneration;
        } else if (name.equals("fire resistance")) {
            return Potion.fireResistance;
        } else if (name.equals("poison")) {
            return Potion.poison;
        } else if (name.equals("night vision")) {
            return Potion.nightVision;
        } else if (name.equals("weakness")) {
            return Potion.weakness;
        } else if (name.equals("strength")) {
            return Potion.damageBoost;
        } else if (name.equals("jump boost")) {
            return Potion.jump;
        } else if (name.equals("water breathing")) {
            return Potion.waterBreathing;
        } else if (name.equals("invisibility")) {
            return Potion.invisibility;
        } else if (name.equals("hunger")) {
            return Potion.hunger;
        } else if (name.equals("blindness")) {
            return Potion.blindness;
        } else if (name.equals("saturation")) {
            return Potion.saturation;
        } else if (name.equals("absorption")) {
            return Potion.absorption;
        } else if (name.equals("health boost")) {
            return Potion.healthBoost;
        } else if (name.equals("mining fatigue")) {
            return Potion.digSlowdown;
        } else if (name.equals("wither")) {
            return Potion.wither;
        }
        return null;
    }

    private static Boolean isPotion(String name) {
        return (name.equals("speed") || name.equals("slowness")
                ||name.equals("stength") || name.equals("weakness")
                || name.equals("regeneration") || name.equals("fire resistance")
                || name.equals("poison") || name.equals("night vision")
                || name.equals("jump boost") || name.equals("water breathing")
                || name.equals("invisibility") || name.equals("hunger")
                || name.equals("blindness") || name.equals("saturation")
                || name.equals("absorption") || name.equals("health boost")
                || name.equals("mining fatigue") || name.equals("wither"));
    }

    public static String drawIcons(String input, int x, int y) {
        if (input.contains("{icon[") && input.contains("]}")) {
            String get_name = input.substring(input.indexOf("{icon[")+6, input.indexOf("]}", input.indexOf("{icon[")));
            String before_value = input.substring(0, input.indexOf("{icon["+get_name+"]}"));
            if (isPotion(get_name.toLowerCase())) {
                Potion potion = getPotionByName(get_name.toLowerCase());
                if (potion != null) {
                    if (get_name.equalsIgnoreCase("health boost")) {
                        drawPotionIcon(x + Minecraft.getMinecraft().fontRendererObj.getStringWidth(before_value)+2, y-1, Potion.regeneration);
                        drawPotionIcon(x + Minecraft.getMinecraft().fontRendererObj.getStringWidth(before_value), y-1, Potion.regeneration);
                        drawPotionIcon(x + Minecraft.getMinecraft().fontRendererObj.getStringWidth(before_value)-2, y-1, Potion.regeneration);
                    } else if (get_name.equalsIgnoreCase("saturation")) {
                        drawItemIcon(x + Minecraft.getMinecraft().fontRendererObj.getStringWidth(before_value), y-1, Item.getItemById(396));
                    } else {
                        drawPotionIcon(x + Minecraft.getMinecraft().fontRendererObj.getStringWidth(before_value), y-1, potion);
                    }
                }
            } else {
                Item item = Item.getByNameOrId(get_name);
                if (item != null) {
                    drawItemIcon(x + Minecraft.getMinecraft().fontRendererObj.getStringWidth(before_value), y-4, item);
                    return input.replace("{icon["+get_name+"]}", "    ");
                }
            }
            return input.replace("{icon["+get_name+"]}", "  ");
        } else {
            return input;
        }
    }

    public static String removeIconString(String input) {
        if (input.contains("{icon[") && input.contains("]}")) {
            String get_name = input.substring(input.indexOf("{icon[")+6, input.indexOf("]}", input.indexOf("{icon[")));
            if (isPotion(get_name)) {
                return input.replace("{icon["+get_name+"]}", "  ");
            }
            return input.replace("{icon["+get_name+"]}", "    ");
        } else {
            return input;
        }
    }
}
