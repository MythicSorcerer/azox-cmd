package com.azox.utils.storage;

import com.azox.utils.model.Kit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class KitStorage extends BaseStorage {

    public KitStorage() {
        super("kits.yml");
    }

    public void saveKit(final Kit kit) {
        final String path = kit.getName().toLowerCase();
        this.config.set(path + ".cooldown", kit.getCooldown());
        this.config.set(path + ".contents", kit.getContents());
        this.save();
    }

    public void deleteKit(final String name) {
        this.config.set(name.toLowerCase(), null);
        this.save();
    }

    public Map<String, Kit> getKits() {
        final Map<String, Kit> kits = new HashMap<>();
        for (final String key : this.config.getKeys(false)) {
            final ConfigurationSection section = this.config.getConfigurationSection(key);
            if (section == null) continue;

            final long cooldown = section.getLong("cooldown");
            // Suppress unchecked cast warning or handle safely. Bukkit config handles List<ItemStack>
            @SuppressWarnings("unchecked")
            final List<ItemStack> contentList = (List<ItemStack>) section.getList("contents");
            final ItemStack[] contents = contentList != null ? contentList.toArray(new ItemStack[0]) : new ItemStack[0];

            kits.put(key, new Kit(key, contents, cooldown));
        }
        return kits;
    }
}
