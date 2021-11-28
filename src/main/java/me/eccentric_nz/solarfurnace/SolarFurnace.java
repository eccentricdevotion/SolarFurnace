package me.eccentric_nz.solarfurnace;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.Set;

public class SolarFurnace extends JavaPlugin {

    private final SolarFurnaceDatabase service = SolarFurnaceDatabase.getInstance();
    private String pluginName;
    private Set<Location> furnaces;
    private Set<Location> detectors;

    @Override
    public void onEnable() {
        PluginDescriptionFile pdfFile = getDescription();
        pluginName = ChatColor.GOLD + "[" + pdfFile.getName() + "]" + ChatColor.RESET + " ";
        try {
            String path = getDataFolder() + File.separator + "SolarFurnace.db";
            service.setConnection(path);
            service.createTables();
        } catch (Exception e) {
            getServer().getConsoleSender().sendMessage(pluginName + "Connection and Tables Error: " + e);
        }
        loadFurnaces();
        getServer().getPluginManager().registerEvents(new SolarFurnaceListener(this), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new SolarFurnaceRunnable(this), 100L, 202L);
    }

    @Override
    public void onDisable() {
        try {
            if (service.connection != null) {
                service.connection.close();
            }
        } catch (SQLException e) {
            debug("Could not close database connection: " + e);
        }
    }

    public String getPluginName() {
        return pluginName;
    }

    private void loadFurnaces() {
        SolarFurnaceResultSet furnaceResultSet = new SolarFurnaceResultSet(this);
        furnaceResultSet.getLocations();
        detectors = furnaceResultSet.getDetectorSet();
        furnaces = furnaceResultSet.getFurnaceSet();
    }

    public Set<Location> getFurnaces() {
        return furnaces;
    }

    public Set<Location> getDetectors() {
        return detectors;
    }

    public void debug(Object o) {
        getServer().getConsoleSender().sendMessage(pluginName + o);
    }
}