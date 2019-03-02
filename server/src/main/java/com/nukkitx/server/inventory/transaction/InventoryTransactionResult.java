package com.nukkitx.server.inventory.transaction;

public enum InventoryTransactionResult {
    SUCCESS,
    FAILED_BALANCING,
    FAILED_VERIFYING,
    FAILED_EXECUTING,
    HOTBAR_MISMATCH,
    NOT_ALIVE,
    INVALID_POSITION,
}
