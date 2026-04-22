package net.azox.cmd.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class FreezeManager {
    private final Set<UUID> frozenPlayers;

    public FreezeManager() {
        this.frozenPlayers = new HashSet<>();
    }

    public void freeze(final UUID uuid) {
        if (uuid == null) {
            return;
        }
        this.frozenPlayers.add(uuid);
    }

    public void unfreeze(final UUID uuid) {
        if (uuid == null) {
            return;
        }
        this.frozenPlayers.remove(uuid);
    }

    public boolean isFrozen(final UUID uuid) {
        return uuid != null && this.frozenPlayers.contains(uuid);
    }

    public void toggleFreeze(final UUID uuid) {
        if (this.isFrozen(uuid)) {
            this.unfreeze(uuid);
        } else {
            this.freeze(uuid);
        }
    }
}
