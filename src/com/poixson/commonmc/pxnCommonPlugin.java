package com.poixson.commonmc;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import com.poixson.commonmc.charts.pxnPluginsChart;
import com.poixson.commonmc.events.PlayerMoveManager;
import com.poixson.commonmc.tools.TicksPerSecond;
import com.poixson.commonmc.tools.plugin.xJavaPlugin;
import com.poixson.commonmc.tools.updatechecker.UpdateCheckManager;
import com.poixson.tools.AppProps;
import com.poixson.tools.Keeper;


public class pxnCommonPlugin extends xJavaPlugin {
	@Override public int getSpigotPluginID() { return 107049; }
	@Override public int getBStatsID() {       return 17785;  }
	public static final String LOG_PREFIX = "[pxnCommon] ";
	public static final String CHAT_PREFIX = ChatColor.AQUA + LOG_PREFIX + ChatColor.WHITE;

	protected final Keeper keeper;
	protected final AppProps props;

	protected final CopyOnWriteArraySet<xJavaPlugin> plugins = new CopyOnWriteArraySet<xJavaPlugin>();
	protected final AtomicReference<pxnPluginsChart> pluginsListener = new AtomicReference<pxnPluginsChart>(null);
	protected final AtomicReference<TicksPerSecond>     tpsManager   = new AtomicReference<TicksPerSecond>(null);
	protected final AtomicReference<UpdateCheckManager> checkManager = new AtomicReference<UpdateCheckManager>(null);
	protected final AtomicReference<PlayerMoveManager>  moveManager  = new AtomicReference<PlayerMoveManager>(null);



	public pxnCommonPlugin() {
		super(pxnCommonPlugin.class);
		this.keeper = Keeper.get();
		try {
			this.props = AppProps.LoadFromClassRef(pxnCommonPlugin.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}



	@Override
	public void onEnable() {
		final ServicesManager services = Bukkit.getServicesManager();
		services.register(pxnCommonPlugin.class, this, this, ServicePriority.Normal);
		// plugins listener
		{
			final pxnPluginsChart listener = new pxnPluginsChart(this);
			final pxnPluginsChart previous = this.pluginsListener.getAndSet(listener);
			if (previous != null)
				previous.unregister();
			listener.register();
		}
		// ticks
		{
			final TicksPerSecond manager = new TicksPerSecond(this);
			final TicksPerSecond previous = this.tpsManager.getAndSet(manager);
			if (previous != null)
				previous.stop();
			manager.start();
			services.register(TicksPerSecond.class, manager, this, ServicePriority.Normal);
		}
		// update check manager
		{
			final UpdateCheckManager manager = new UpdateCheckManager(this);
			final UpdateCheckManager previous = this.checkManager.getAndSet(manager);
			if (previous != null)
				previous.stop();
			services.register(UpdateCheckManager.class, manager, this, ServicePriority.Normal);
			manager.addPlugin(this, this.getSpigotPluginID(), this.getPluginVersion());
			// wait for server to start
			manager.startLater();
		}
		super.onEnable();
		// player move listeners
		{
			final PlayerMoveManager manager = new PlayerMoveManager(this);
			final PlayerMoveManager previous = this.moveManager.getAndSet(manager);
			if (previous != null)
				previous.unregister();
			manager.register();
		}
		// custom stats
		{
			final Metrics metrics = this.metrics.get();
			if (metrics != null) {
				metrics.addCustomChart(pxnPluginsChart.GetChart(this));
			}
		}
	}

	@Override
	public void onDisable() {
		super.onDisable();
		// update check manager
		{
			final UpdateCheckManager manager = this.checkManager.getAndSet(null);
			if (manager != null) {
				manager.stop();
			}
		}
		// plugins listener
		{
			final pxnPluginsChart listener = this.pluginsListener.getAndSet(null);
			if (listener != null)
				listener.unregister();
		}
	}



	// -------------------------------------------------------------------------------



	public static pxnCommonPlugin GetCommonPlugin() {
		final pxnCommonPlugin plugin = Bukkit.getServicesManager().load(pxnCommonPlugin.class);
		if (plugin == null) throw new RuntimeException("pxnCommonPlugin not loaded");
		return plugin;
	}



	public static <T extends xJavaPlugin> boolean RegisterPluginPXN(final T plugin) {
		return GetCommonPlugin().registerPluginPXN(plugin);
	}
	public <T extends xJavaPlugin> boolean registerPluginPXN(final T plugin) {
		for (final xJavaPlugin p : this.plugins) {
			if (p.getClass().isInstance(plugin))
				throw new RuntimeException("Plugin already registered? " + plugin.getClass().getName());
		}
		return this.plugins.add(plugin);
	}

	public static <T extends xJavaPlugin> boolean UnregisterPluginPXN(final T plugin) {
		return GetCommonPlugin().unregisterPluginPXN(plugin);
	}
	public <T extends xJavaPlugin> boolean unregisterPluginPXN(final T plugin) {
		return this.plugins.remove(plugin);
	}

	public int getPluginsCount() {
		return this.plugins.size();
	}



}
