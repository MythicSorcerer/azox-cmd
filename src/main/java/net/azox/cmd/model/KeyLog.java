package net.azox.cmd.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class KeyLog {
    private UUID keyId;
    private String chestId;
    private UUID ownerUuid;
    private KeyType type;
    private String location;
    private KeyAction action;
    private long timestamp;

    public enum KeyType {
        PHYSICAL,
        VIRTUAL
    }

    public enum KeyAction {
        CREATED,
        DROPPED,
        PICKED_UP,
        EXPORTED_TO_PHYSICAL,
        DESTROYED,
        GIVEN_TO_PLAYER,
        REMOVED_FROM_PLAYER
    }
}