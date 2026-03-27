package com.kingpixel.ultrasts.models;

import com.cobblemon.mod.common.pokemon.Pokemon;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class UserSTSSession {
    private final UUID playerUuid;
    private final String stsId;
    private Set<Pokemon> selectedPokemon = new HashSet<>();
    private int currentBox = 0;
    private boolean showingPC = false;

    public UserSTSSession(UUID playerUuid, String stsId) {
        this.playerUuid = playerUuid;
        this.stsId = stsId;
    }

    public void clearSelection() {
        selectedPokemon.clear();
    }
}

