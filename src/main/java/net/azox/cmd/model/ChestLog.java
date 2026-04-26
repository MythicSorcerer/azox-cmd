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
public final class ChestLog {
    private String chestId;
    private String worldName;
    private int x;
    private int y;
    private int z;
    private UUID ownerUuid;
    private ChestAction action;
    private long timestamp;

    public enum ChestAction {
        PLACE,
        BREAK,
        TAKE_ITEM,
        PUT_ITEM,
        HOPPER_IN,
        HOPPER_OUT,
        COPPER_GOLEM_TAKE,
        COMMAND_EXEC
    }
}