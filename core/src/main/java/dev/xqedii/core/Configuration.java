package dev.xqedii.core;

import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;

@ConfigName("settings")
public class Configuration extends AddonConfig {

  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @SettingSection("restorercategory")
  @SwitchSetting
  private final ConfigProperty<Boolean> forceSkin = new ConfigProperty<>(true);

  @SwitchSetting
  private final ConfigProperty<Boolean> showCosmetics = new ConfigProperty<>(true);

  @Override
  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }

  public ConfigProperty<Boolean> forceSkin() {
    return this.forceSkin;
  }

  public ConfigProperty<Boolean> showCosmetics() {
    return this.showCosmetics;
  }
}