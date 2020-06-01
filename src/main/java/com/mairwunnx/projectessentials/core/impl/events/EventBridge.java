package com.mairwunnx.projectessentials.core.impl.events;

import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI;
import com.mairwunnx.projectessentials.core.api.v1.events.forge.*;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class EventBridge {
    public static void initialize() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventBridge::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventBridge::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventBridge::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventBridge::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventBridge::complete);
    }

    public static void setup(final FMLCommonSetupEvent event) {
        ModuleEventAPI.INSTANCE.fire(
            ForgeEventType.SetupEvent, new FMLCommonSetupEventData(event)
        );
    }

    public static void doClientStuff(final FMLClientSetupEvent event) {
        ModuleEventAPI.INSTANCE.fire(
            ForgeEventType.DoClientStuffEvent, new FMLClientSetupEventData(event)
        );
    }

    public static void enqueueIMC(final InterModEnqueueEvent event) {
        ModuleEventAPI.INSTANCE.fire(
            ForgeEventType.EnqueueIMCEvent, new InterModEnqueueEventData(event)
        );
    }

    public static void processIMC(final InterModProcessEvent event) {
        ModuleEventAPI.INSTANCE.fire(
            ForgeEventType.ProcessIMCEvent, new InterModProcessEventData(event)
        );
    }

    public static void complete(final FMLLoadCompleteEvent event) {
        ModuleEventAPI.INSTANCE.fire(
            ForgeEventType.LoadComplete, new LoadCompleteEventData(event)
        );
    }
}
