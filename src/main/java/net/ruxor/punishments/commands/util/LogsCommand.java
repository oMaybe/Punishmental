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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

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
            openMainMenu((Player) sender, IDFetcher, playerName);
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

    public void openMainMenu(Player sender, UUID uuid, String playerName){
        SGMenu mainMenu = Punishmental.getInstance().getSpiGUI().create("&0Punishment Logs", 1);

        mainMenu.setButton(0, new SGButton(new ItemBuilder(Material.BLACK_WOOL).name("&6Blacklists").get()).withListener((listener) -> {
            displayPunishments(sender, uuid, PunishmentType.BLACKLIST, playerName);
        }));

        mainMenu.setButton(2, new SGButton(new ItemBuilder(Material.RED_WOOL).name("&6Bans").get()).withListener((listener) -> {
            displayPunishments(sender, uuid, PunishmentType.BAN, playerName);
        }));

        mainMenu.setButton(4, new SGButton(new ItemBuilder(Material.ORANGE_WOOL).name("&6Mutes").get()).withListener((listener) -> {
            displayPunishments(sender, uuid, PunishmentType.MUTE, playerName);
        }));

        mainMenu.setButton(6, new SGButton(new ItemBuilder(Material.YELLOW_WOOL).name("&6Warns").get()).withListener((listener) -> {
            displayPunishments(sender, uuid, PunishmentType.WARN, playerName);
        }));

        mainMenu.setButton(8, new SGButton(new ItemBuilder(Material.GREEN_WOOL).name("&6Kicks").get()).withListener((listener) -> {
            displayPunishments(sender, uuid, PunishmentType.KICK, playerName);
        }));

        sender.openInventory(mainMenu.getInventory());
    }

    public void displayPunishments(Player sender, UUID targetID, PunishmentType type, String targetName){
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

        SGMenu menu = Punishmental.getInstance().getSpiGUI().create("&0" + type.getGuiName()+ " - {currentPage}/{maxPage}", rows + 1);

        menu.setAutomaticPaginationEnabled(true);
        menu.setCurrentPage(1);
        menu.setBlockDefaultInteractions(true);
        menu.setToolbarBuilder((slot, page, defaultType, basicMenu) -> {
            if (slot == 4){
                return new SGButton(new ItemBuilder(Material.PLAYER_HEAD).skullOwner(targetName).name("&6" + targetName).lore("&6" + targetID).get());
            } // maybe listener to copy ID?
            if (slot == 8){
                return new SGButton(new ItemBuilder(Material.BARRIER).name("&cClose").get()).withListener((listener) -> {
                    sender.closeInventory();
                });
            }

            return Punishmental.getInstance().getSpiGUI().getDefaultToolbarBuilder().buildToolbarButton(slot, page, defaultType, menu);
        });

        Material finalPrimary = primary;

        for (int i = 0; i < punishments.size(); i++){
            Punishment punishment = punishments.get(i);
            String id = String.valueOf(punishment.getPunishID());

            ItemStack item = createItem(finalPrimary, punishment);

            menu.setButton(i, new SGButton(item).withListener((listener) -> {
                // I dont think it do anything if click BUT maybe copy ID to clipboard?
                if (listener.isLeftClick()){
                    boolean removed = punishment.isRemoved();
                    if (removed) {
                        openConfirmMenu(sender, punishment);
                    }else{
                        sender.sendMessage(CC.Red + "This punishment is not in history.");
                    }
                }else {
                    TextComponent message = new TextComponent(CC.Gray + "Punishment ID: " + CC.Red + id);
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, id));
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click this to copy the punishment id.")));
                    sender.spigot().sendMessage(message);
                }
            }));
        }

        menu.setOnPageChange(inventory -> {
            int page = inventory.getCurrentPage();
            inventory.setName("&0" + type.getGuiName() + " - " + page + "/" + numberOfPages);
            menu.refreshInventory(sender);
        });

        sender.openInventory(menu.getInventory());
        menu.refreshInventory(sender);
    }

    public ItemStack createItem(Material material, Punishment punishment){
        String didOn = TimeUtils.getFormattedGuiTime(punishment.getTimePunished());
        String reason = punishment.getReason();
        String staff = punishment.getStaffName();
        String time = punishment.getExpiry() == -1L ? "permanent" : TimeUtils.formatTimeMillis(punishment.getExpiry());
        String removeReason = punishment.getRemoveReason();
        String removeStaff = punishment.getRemoveStaffName();
        String removeDate = TimeUtils.getFormattedGuiTime(punishment.getRemoveTime());
        String id = String.valueOf(punishment.getPunishID());
        String active = punishment.isActive() ? "&2True" : "&fExpired";

        ArrayList<String> lore = new ArrayList<>();
        lore.add(punishment.getSeperator());
        lore.add("&cID: &f" + id);
        lore.add("&cActive: &f" + active);
        lore.add("&cBy: &f" + staff);
        lore.add("&cReason: &f" + reason);
        lore.add("&cTime: &f" + time);
        // todo: lore.add("&cServer: &c" + punishment.getServer());

        if (punishment.isRemoved()){
            lore.add("&cRemoved By: &f" + removeStaff);
            lore.add("&cRemove Reason: &f" + removeReason);
            lore.add("&cRemove Date: &f" + removeDate);

            lore.add("");
            lore.add("&cLeft click to remove the punishment from history."); // rn make it only  remove from history if applicable otherwise future make it remove punishment if active
            lore.add("&cRight click to copy the punishment ID.");
            lore.add(punishment.getSeperator());
        }else{
            lore.add("");
            lore.add("&cLeft click to remove the punishment from history."); // rn make it only  remove from history if applicable otherwise future make it remove punishment if active
            lore.add("&cRight click to copy the punishment ID.");
            lore.add(punishment.getSeperator());
        }

        return new ItemBuilder(material).name(CC.Red + didOn).lore(lore).get();
    }

    private void openConfirmMenu(Player player, Punishment punishment){
        SGMenu confirmMenu = Punishmental.getInstance().getSpiGUI().create("&0Confirm Removal", 3);

        int[] placesToConfirm = {
                0, 1, 2,
                9, 10, 11,
                18, 19, 20
        };

        int[] placesToCancel = {
                6, 7, 8,
                15, 16, 17,
                24, 25, 26
        };

        IntStream.range(0, placesToConfirm.length).map(i -> placesToConfirm[i]).forEach(i -> confirmMenu.setButton(i, new SGButton(new ItemBuilder(Material.GREEN_WOOL).name("&7Confirm").get()).withListener(listener -> {
            Punishmental.getInstance().getDatabaseManager().getDatabase().removeFromHistory(punishment.getPunishID());
            player.sendMessage(CC.Green + "Punishment removed from history.");
            player.closeInventory();
        })));

        IntStream.range(0, placesToCancel.length).map(i -> placesToCancel[i]).forEach(i -> confirmMenu.setButton(i, new SGButton(new ItemBuilder(Material.RED_WOOL).name("&7Cancel").get()).withListener(listener -> {
            Punishmental.getInstance().getDatabaseManager().getDatabase().removeFromHistory(punishment.getPunishID());
            player.sendMessage(CC.Red + "Cancelled the removal operation.");
            player.closeInventory();
        })));

        player.openInventory(confirmMenu.getInventory());
    }

}
