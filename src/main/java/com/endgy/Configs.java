package com.endgy;

import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;

public class Configs {
    //instance of your config loaded from file and automatically registered to the SyncedConfigRegistry and ClientConfigRegistry using the getId() method
    //ConfigApiJava can come in handy to avoid pernicious compiler errors depending on your IDE and gradle setup.
    public static WFConfig myConfig = ConfigApiJava.registerAndLoadConfig(WFConfig::new);

    //adding the registerType, you can register a config as client-only. No syncing will occur. Useful for client-only mods.
    public static WFConfig myClientOnlyConfig = ConfigApiJava.registerAndLoadConfig(WFConfig::new, RegisterType.CLIENT);

    //adding the registerType, you can register a config as sync-only. Their won't be any client-side GUI functionality, so the config will only be editable from the file itself, but it will auto-sync with clients.
    public static WFConfig mySyncedOnlyConfig = ConfigApiJava.registerAndLoadConfig(WFConfig::new, RegisterType.SERVER);

    //Init function would be called in ModInitializer or some other entrypoint. Not strictly necessary if loading on-reference is ok.
    public static void init() {
    }
}