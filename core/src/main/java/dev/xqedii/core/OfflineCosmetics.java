package dev.xqedii.core;

import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class OfflineCosmetics extends LabyAddon<Configuration> {

  private static OfflineCosmetics instance;

  public OfflineCosmetics() {
    instance = this;
  }

  public static OfflineCosmetics get() {
    return instance;
  }

  @Override
  protected void enable() {
    this.registerSettingCategory();
    this.logger().info("");
    this.logger().info("   ___   __  __ _ _               ____                         _   _          ");
    this.logger().info("  / _ \\ / _|/ _| (_)_ __   ___   / ___|___  ___ _ __ ___   ___| |_(_) ___ ___ ");
    this.logger().info(" | | | | |_| |_| | | '_ \\ / _ \\ | |   / _ \\/ __| '_ ` _ \\ / _ \\ __| |/ __/ __|");
    this.logger().info(" | |_| |  _|  _| | | | | |  __/ | |__| (_) \\__ \\ | | | | |  __/ |_| | (__\\__ \\");
    this.logger().info("  \\___/|_| |_| |_|_|_| |_|\\___|  \\____\\___/|___/_| |_| |_|\\___|\\__|_|\\___|___/");
    this.logger().info("");
    this.logger().info("The addon has been successfully launched!");
    this.logger().info("");
    this.logger().info("Author: Xqedii");
    this.logger().info("Contact: https://xqedii.dev");
    this.logger().info("");
  }

  @Override
  protected Class<Configuration> configurationClass() {
    return Configuration.class;
  }
}