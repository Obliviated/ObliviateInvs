package mc.obliviate.inventory.configurable.util;

import com.google.common.base.Preconditions;
import mc.obliviate.inventory.configurable.ConfigurableGui;
import mc.obliviate.inventory.configurable.GuiConfigurationTable;
import mc.obliviate.util.placeholder.PlaceholderUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class GuiSerializer {

	public static void putDysfunctionalIcons(ConfigurableGui gui, GuiConfigurationTable table, ConfigurationSection iconsSection, PlaceholderUtil placeholderUtil, List<String> functionalSlots) {
		Preconditions.checkNotNull(gui, "dysfunctional icons could not put because gui was null!");
		Preconditions.checkNotNull(iconsSection, "null configuration section given!");

		for (final String sectionName : iconsSection.getKeys(false)) {
			final ConfigurationSection section = iconsSection.getConfigurationSection(sectionName);

			if (functionalSlots.contains(sectionName)) continue;
			if (!section.isSet(table.getSlotSectionName())) continue;
			if (!section.isSet(table.getMaterialSectionName())) continue;

			final int slotNo = section.getInt(table.getSlotSectionName(), -1);
			if (slotNo > 0) {
				gui.addItem(slotNo, gui.getGuiCache().getConfigItem(iconsSection.getConfigurationSection(sectionName), placeholderUtil, table));
				continue;
			}

			final List<Integer> slots = parseSlotString(section.getString(table.getSlotSectionName()));
			if (!slots.isEmpty()) {
				slots.forEach(slot -> {
					gui.addItem(slot, gui.getGuiCache().getConfigItem(iconsSection.getConfigurationSection(sectionName), placeholderUtil, table));
				});
			}
		}
	}

	public static List<Integer> parseSlotString(String str) {
		if (str == null) return new ArrayList<>();
		if (str.contains("-")) {
			return parseStringAsIntegerRange(str);
		} else if (str.contains(",")) {
			return parseStringAsIntegerList(str);
		}
		return new ArrayList<>();
	}

	private static List<Integer> parseStringAsIntegerRange(String str) {
		final String[] slots = str.split("-");
		if (slots.length != 2) new ArrayList<>();
		int from, to;

		try {
			from = Integer.parseInt(slots[0]);
			to = Integer.parseInt(slots[1]);
		} catch (NumberFormatException ignore) {
			return new ArrayList<>();
		}

		if (from > to) return new ArrayList<>();

		final List<Integer> result = new ArrayList<>();
		for (; from <= to; from++) {
			result.add(from);
		}
		return result;
	}

	private static List<Integer> parseStringAsIntegerList(String str) {
		final List<Integer> pageSlots = new ArrayList<>();
		final String[] slotStrings = str.split(",");

		for (final String slotText : slotStrings) {
			try {
				pageSlots.add(Integer.parseInt(slotText));
			} catch (NumberFormatException ignore) {
			}
		}
		return pageSlots;
	}

}
