package net.ruxor.punishments.commands.util;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.ruxor.punishments.Punishmental;
import net.ruxor.punishments.commands.ConsoleCommand;
import net.ruxor.punishments.punishment.Punishment;
import net.ruxor.punishments.punishment.PunishmentType;
import net.ruxor.punishments.util.CC;
import net.ruxor.punishments.util.TimeUtils;
import net.ruxor.punishments.util.UUIDFetcher;
import net.ruxor.punishments.util.spigui.buttons.SGButton;
import net.ruxor.punishments.util.spigui.item.ItemBuilder;
import net.ruxor.punishments.util.spigui.menu.SGMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LogsCommand extends ConsoleCommand {

    public LogsCommand() {
        super("logs", "punish.logs");
        setUsage(CC.Red + "Usage: /logs <player> [page]");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(getUsage());
            return;
        }

        String playerName = args[0];
        UUID IDFetcher = UUIDFetcher.getUUID(playerName);
        if (IDFetcher == null){ // java one
            IDFetcher = Bukkit.getOfflinePlayer(playerName).getUniqueId();
            if (IDFetcher == null){
                sender.sendMessage(ChatColor.RED + "Could not find a player with that name!");
                return;
            }
        }

        if (sender instanceof Player) {
            openMainMenu((Player) sender, IDFetcher);
            return;
        }else{
            // chat
        }


        // get the player's profile
        // check if the player exists
        // get the player's punishments
        // check if the player has any punishments
        // send the player's punishments to the sender
    }

    // make it like the ac logs command from the Headed staff series

    public void openMainMenu(Player sender, UUID uuid){
        SGMenu mainMenu = Punishmental.getInstance().getSpiGUI().create("&0Punishment Logs", 1);

        mainMenu.setButton(0, new SGButton(new ItemBuilder(Material.BLACK_WOOL).name("&6Blacklists").get()).withListener((listener) -> {
            displayPunishments(sender, uuid, PunishmentType.BLACKLIST);
        }));

        mainMenu.setButton(2, new SGButton(new ItemBuilder(Material.RED_WOOL).name("&6Bans").get()).withListener((listener) -> {
            displayPunishments(sender, uuid, PunishmentType.BAN);
        }));

        mainMenu.setButton(4, new SGButton(new ItemBuilder(Material.ORANGE_WOOL).name("&6Mutes").get()).withListener((listener) -> {
            displayPunishments(sender, uuid, PunishmentType.MUTE);
        }));

        mainMenu.setButton(6, new SGButton(new ItemBuilder(Material.YELLOW_WOOL).name("&6Warns").get()).withListener((listener) -> {
            displayPunishments(sender, uuid, PunishmentType.WARN);
        }));

        mainMenu.setButton(8, new SGButton(new ItemBuilder(Material.GREEN_WOOL).name("&6Kicks").get()).withListener((listener) -> {
            displayPunishments(sender, uuid, PunishmentType.KICK);
        }));

        sender.openInventory(mainMenu.getInventory());
    }

    public void displayPunishments(Player sender, UUID targetID, PunishmentType type){
        Material primary = null;
        switch (type){
            case BLACKLIST:
                primary = Material.BLACK_WOOL;
                break;
            case BAN:
                primary = Material.RED_WOOL;
                break;
            case MUTE:
                primary = Material.ORANGE_WOOL;
                break;
            case WARN:
                primary = Material.YELLOW_WOOL;
                break;
            case KICK:
                primary = Material.GREEN_WOOL;
                break;
            default:
                primary = Material.PAPER;
                Bukkit.getLogger().warning("Invalid punishment type: " + type);
                break;
        }

        List<Punishment> currentPunishments = Punishmental.getInstance().getDatabaseManager().getDatabase().getPunishmentsByType(targetID, type);
        List<Punishment> historicPunishments = Punishmental.getInstance().getDatabaseManager().getDatabase().getPunishmentsByTypeFromHistory(targetID, type);
        List<Punishment> punishments = new ArrayList<>();

        punishments.addAll(currentPunishments);
        punishments.addAll(historicPunishments);

        int rows;
        if (punishments.isEmpty()){
            rows = 1;
        }else{
            // maybe fix this
            rows = (int) Math.ceil(punishments.size() / 9.0);
        }

        int numberOfPunishments = punishments.size();
        int punishmentsPerPage = 9;
        int rowsPerPage = 3;
        int numberOfPages = numberOfPunishments / (punishmentsPerPage * rowsPerPage) + 1;

        SGMenu menu = Punishmental.getInstance().getSpiGUI().create("&0" + type.getGuiName()+ " - 1/" + numberOfPages, rows + 1);

        menu.setAutomaticPaginationEnabled(true);
        menu.setRowsPerPage(rowsPerPage);
        Material finalPrimary = primary;

        for (int i = 0; i < punishments.size(); i++){
            Punishment punishment = punishments.get(i);
            String didOn = TimeUtils.getFormattedGuiTime(punishment.getTimePunished());
            String reason = punishment.getReason();
            String staff = punishment.getStaffName();
            String time = punishment.getExpiry() == -1L ? "permanent" : TimeUtils.formatTimeMillis(punishment.getExpiry());
            String removeReason = punishment.getRemoveReason();
            String removeStaff = punishment.getRemoveStaffName();
            String removeDate = TimeUtils.getFormattedGuiTime(punishment.getRemoveTime());
            String id = String.valueOf(punishment.getPunishID());
            String active = punishment.isActive() ? "&aActive" : "&cExpired";

            ArrayList<String> lore = new ArrayList<>();
            lore.add(punishment.getSeperator());
            lore.add("&eID: &c" + id);
            lore.add("&eActive: " + active);
            lore.add("&eBy: &c" + staff);
            lore.add("&eReason: &c" + reason);
            lore.add("&eTime: &c" + time);
            // todo: lore.add("&eServer: &c" + punishment.getServer());

            if (punishment.isRemoved()){
                lore.add("&eRemoved By: &c" + removeStaff);
                lore.add("&eRemove Reason: &c" + removeReason);
                lore.add("&eRemove Date: &c" + removeDate);
                lore.add(punishment.getSeperator());
            }else{
                lore.add(punishment.getSeperator());
            }

            menu.setButton(i, new SGButton(new ItemBuilder(finalPrimary).name("&c" + didOn).lore(lore).get()).withListener((listener) -> {
                // I dont think it do anything if click BUT maybe copy ID to clipboard?
                TextComponent message = new TextComponent(CC.Gray + "Punishment ID: " + CC.Red + id);
                message.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, id));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click this to copy the punishment id.")));
                sender.spigot().sendMessage(message);
            }));
        }
        /*doIt(menu, punishments, 1, finalPrimary, sender);

        menu.setOnPageChange(inventory -> {
            int page = inventory.getCurrentPage();
            inventory.setName("&0" + type.getGuiName() + " - " + page + "/" + numberOfPages);
            doIt(menu, punishments, page, finalPrimary, sender);
            menu.refreshInventory(sender);
        });*/

        sender.openInventory(menu.getInventory());
    }

    public void doIt(SGMenu menu, List<Punishment> punishments, int page, Material finalPrimary, Player sender){
        int rows;
        if (punishments.isEmpty()){
            rows = 1;
        }else{
            rows = (int) Math.ceil(punishments.size() / 9.0);
        }

        menu.setRowsPerPage(rows);

        int punishmentsPerPage = 9;
        int rowsPerPage = 3;

        ArrayList<Punishment> punishmentsThisPage = new ArrayList<>();
        for (int i = (page - 1) * punishmentsPerPage * rowsPerPage; i < page * punishmentsPerPage * rowsPerPage; i++){
            if (i >= punishments.size()) break;
            punishmentsThisPage.add(punishments.get(i));
        }

        // change max so that we can fit a toolbar at the top lol
        for (int i = 0; i < punishmentsThisPage.size(); i++){
            Punishment punishment = punishmentsThisPage.get(i);
            String didOn = TimeUtils.getFormattedGuiTime(punishment.getTimePunished());
            String reason = punishment.getReason();
            String staff = punishment.getStaffName();
            String time = punishment.getExpiry() == -1L ? "permanent" : TimeUtils.formatTimeMillis(punishment.getExpiry());
            String removeReason = punishment.getRemoveReason();
            String removeStaff = punishment.getRemoveStaffName();
            String removeDate = TimeUtils.getFormattedGuiTime(punishment.getRemoveTime());
            String id = String.valueOf(punishment.getPunishID());
            String active = punishment.isActive() ? "&aActive" : "&cExpired";

            ArrayList<String> lore = new ArrayList<>();
            lore.add(punishment.getSeperator());
            lore.add("&eID: &c" + id);
            lore.add("&eActive: " + active);
            lore.add("&eBy: &c" + staff);
            lore.add("&eReason: &c" + reason);
            lore.add("&eTime: &c" + time);
            // todo: lore.add("&eServer: &c" + punishment.getServer());

            if (punishment.isRemoved()){
                lore.add("&eRemoved By: &c" + removeStaff);
                lore.add("&eRemove Reason: &c" + removeReason);
                lore.add("&eRemove Date: &c" + removeDate);
                lore.add(punishment.getSeperator());
            }else{
                lore.add(punishment.getSeperator());
            }

            menu.setButton(i, new SGButton(new ItemBuilder(finalPrimary).name("&c" + didOn).lore(lore).get()).withListener((listener) -> {
                // I dont think it do anything if click BUT maybe copy ID to clipboard?
                TextComponent message = new TextComponent(CC.Gray + "Punishment ID: " + CC.Red + id);
                message.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, id));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click this to copy the punishment id.")));
                sender.spigot().sendMessage(message);
            }));
        }
    }

}
