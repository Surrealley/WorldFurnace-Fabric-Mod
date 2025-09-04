package com.endgy;

import me.fzzyhmstrs.fzzy_config.api.FileType;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import net.minecraft.util.Identifier;

import java.util.Objects;

import static com.endgy.WorldFurnace.MOD_ID;

public class WFConfig extends me.fzzyhmstrs.fzzy_config.config.Config {
    public WFConfig() {
        super(Objects.requireNonNull(Identifier.of(MOD_ID, "my_config")));
    }

    public int damage = 2;
    public int increaseDamage = 1;
    public int delayBetweenDamageIncrease = 50;
    public int delayBetweenDamages = 100;
    public int startAfterMessage = 200;
    public int delayBetweenMessages = 100;
    public int fuelMax = 16000;
    public int fxDuration = 300;
    //Configs have a default permission level needed to edit them (disabled in single player). You can override that default here
    @Override
    public int defaultPermLevel() {
        return 4;
    }

    //Fzzy Config uses TOML files by default. You can override that behavior to any of the supported FileType
    @Override
    public FileType fileType() {
        return FileType.JSON5;
    }

    //You can define the save type for your config; which determines how clients act when receiving updates from a server.
    //SaveType.SEPARATE will not save updates to the local config files, keeping them separate for singleplayer play.
    @Override
    public SaveType saveType() {
        return SaveType.SEPARATE;
    }
}

