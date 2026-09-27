package com.sophimoo.airplace;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.BoundedDiscrete;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

@Config(name = "airplace")
public class AirPlaceConfig implements ConfigData {
	public boolean enabled = true;
	@Tooltip
	@BoundedDiscrete(min = 0, max = 6)
	public int range = 3;
	@Tooltip
	public boolean blockOutline = true;
	@Tooltip
	public boolean ghostBlock = true;
	@Tooltip
	@BoundedDiscrete(min = 5, max = 100)
	public int ghostOpacity = 40;
	@Tooltip
	public boolean ghostLerp = false;
	@Tooltip
	@BoundedDiscrete(min = 1, max = 60)
	public int ghostLerpSpeed = 15;
}
