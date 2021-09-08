package me.comu.client.module.impl.toggle.render.tabgui.item;

import me.comu.client.properties.Property;

import java.util.List;

public class GuiPropertyItem {
	
	private final List<Property> properties;
	
	public GuiPropertyItem(List<Property> property)
	{
		this.properties = property;
	}
	
	public List<Property> getProperties()
	{
		return properties;
	}

}
