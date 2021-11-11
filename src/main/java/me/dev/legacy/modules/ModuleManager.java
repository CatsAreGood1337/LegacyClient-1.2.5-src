package me.dev.legacy.modules;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Legacy;
import me.dev.legacy.api.AbstractModule;
import me.dev.legacy.api.event.events.render.Render2DEvent;
import me.dev.legacy.api.event.events.render.Render3DEvent;
import me.dev.legacy.impl.gui.LegacyGui;
import me.dev.legacy.modules.exploit.DL;
import me.dev.legacy.modules.exploit.DLSpiral;
import me.dev.legacy.modules.render.Swing;
import me.dev.legacy.api.util.Util;
import me.dev.legacy.modules.client.*;
import me.dev.legacy.modules.combat.*;
import me.dev.legacy.modules.misc.*;
import me.dev.legacy.modules.movement.*;
import me.dev.legacy.modules.player.*;
import me.dev.legacy.modules.render.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.lwjgl.input.Keyboard;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ModuleManager
        extends AbstractModule {
    public ArrayList<Module> modules = new ArrayList();
    public List<Module> sortedModules = new ArrayList<Module>();
    public List<String> sortedModulesABC = new ArrayList<String>();
    public Animation animationThread;
    private static final LinkedHashMap<String, Module> modulesNameMap = new LinkedHashMap<>();
    private static final LinkedHashMap<Class<? extends Module>, Module> modulesClassMap = new LinkedHashMap<>();

    public void init() {
        //CLIENT
        this.modules.add(new ClickGui());
        this.modules.add(new HUD());
        this.modules.add(new Media());
        this.modules.add(new Watermark());
        this.modules.add(new ModulesList());
        this.modules.add(new Friends());
        this.modules.add(new Colors());
        this.modules.add(new TabFriends());
        this.modules.add(new Components());
        this.modules.add(new RPC());
        //RENDER
        this.modules.add(new BebraESP());
        this.modules.add(new BlockHighlight());
        this.modules.add(new Trajectories());
        this.modules.add(new NoRender());
        this.modules.add(new SkyColor());
        this.modules.add(new ESP());
        this.modules.add(new Fullbright());
        this.modules.add(new HoleESP());
        this.modules.add(new Tracers());
        this.modules.add(new HitMarkers());
        this.modules.add(new Swing());
        this.modules.add(new ItemViewModel());
        this.modules.add(new EntityHunger());
        this.modules.add(new Aspect());
        this.modules.add(new Chams());
        this.modules.add(new SmallShield());
        this.modules.add(new Nametags());
        //COMBAT
        this.modules.add(new BetterXP());
        this.modules.add(new Crits());
        this.modules.add(new Offhand());
        this.modules.add(new AutoWeb());
        this.modules.add(new HoleFill());
        this.modules.add(new AutoArmor());
        this.modules.add(new Burrow());
        this.modules.add(new AutoCrystal());
        this.modules.add(new AutoLog());
        this.modules.add(new MinDamage());
        this.modules.add(new Quiver());
        this.modules.add(new Aura());
        this.modules.add(new AutoTrap());
        this.modules.add(new AntiRegear());
        this.modules.add(new Surround());
        this.modules.add(new BowAim());
        this.modules.add(new AutoBed());
        this.modules.add(new AntiCity());
        this.modules.add(new SmartBurrow());
        this.modules.add(new AutoCity());
        //PLAYER
        this.modules.add(new EchestBackpack());
        this.modules.add(new AntiHunger());
        this.modules.add(new Freecam());
        this.modules.add(new FastPlace());
        this.modules.add(new Replenish());
        this.modules.add(new FakePlayer());
        this.modules.add(new MCP());
        this.modules.add(new LiquidInteract());
        this.modules.add(new TpsSync());
        this.modules.add(new MultiTask());
        this.modules.add(new Reach());
        this.modules.add(new FastBreak());
        this.modules.add(new Blink());
        this.modules.add(new Scaffold());
        //MISC
        this.modules.add(new Timestamps());
        this.modules.add(new BuildHeight());
        this.modules.add(new MCF());
        this.modules.add(new ShulkerViewer());
        this.modules.add(new Tracker());
        this.modules.add(new PopCounter());
        this.modules.add(new XCarry());
        this.modules.add(new Dupe());
        this.modules.add(new PearlNotify());
        this.modules.add(new BurrowAlert());
        this.modules.add(new NoHitBox());
        this.modules.add(new StashLogger());
        this.modules.add(new AutoBebra());
        this.modules.add(new AutoKit());
        this.modules.add(new PortalBuilder());
        //MOVEMENT
        this.modules.add(new AntiVoid());
        this.modules.add(new NoSlow());
        this.modules.add(new ReverseStep());
        this.modules.add(new Velocity());
        this.modules.add(new Sprint());
        this.modules.add(new Step());
        this.modules.add(new Jesus());
        this.modules.add(new Speed());
        this.modules.add(new IceSpeed());
        this.modules.add(new Rubberband());
        this.modules.add(new NoWeb());
        this.modules.add(new Anchor());
        this.modules.add(new HoleTP());
        this.modules.add(new AntiLevitate());
        this.modules.add(new PacketFly());
        this.modules.add(new Strafe());
        this.modules.add(new Phase());
        this.modules.add(new BoatFly());
        this.modules.add(new ElytraFlight());
        //EXPLOIT
        this.modules.add(new DLSpiral());
        this.modules.add(new DL());
    }

    public Module getModuleByName(String name) {
        for (Module module : this.modules) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }

    public <T extends Module> T getModuleByClass(Class<T> clazz) {
        for (Module module : this.modules) {
            if (!clazz.isInstance(module)) continue;
            return (T) module;
        }
        return null;
    }


    public void enableModule(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        if (module != null) {
            module.disable();
        }
    }

    public void enableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.disable();
        }
    }


    public Module getModuleByDisplayName(String displayName) {
        for (Module module : this.modules) {
            if (!module.getDisplayName().equalsIgnoreCase(displayName)) continue;
            return module;
        }
        return null;
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList<Module>();
        for (Module module : this.modules) {
            if (!module.isEnabled()) continue;
            enabledModules.add(module);
        }
        return enabledModules;
    }

    public ArrayList<String> getEnabledModulesName() {
        ArrayList<String> enabledModules = new ArrayList<String>();
        for (Module module : this.modules) {
            if (!module.isEnabled() || !module.isDrawn()) continue;
            enabledModules.add(module.getFullArrayString());
        }
        return enabledModules;
    }

    public ArrayList<Module> getModulesByCategory(Module.Category category) {
        ArrayList<Module> modulesCategory = new ArrayList<Module>();
        this.modules.forEach(module -> {
            if (module.getCategory() == category) {
                modulesCategory.add(module);
            }
        });
        return modulesCategory;
    }

    public List<Module.Category> getCategories() {
        return Arrays.asList(Module.Category.values());
    }

    public void onLoad() {
        this.modules.stream().filter(Module::listening).forEach(((EventBus) MinecraftForge.EVENT_BUS)::register);
        this.modules.forEach(Module::onLoad);
    }

    public void onUpdate() {
        this.modules.stream().filter(AbstractModule::isEnabled).forEach(Module::onUpdate);
    }

    public void onTick() {
        this.modules.stream().filter(AbstractModule::isEnabled).forEach(Module::onTick);
    }

    public void onRender2D(Render2DEvent event) {
        this.modules.stream().filter(AbstractModule::isEnabled).forEach(module -> module.onRender2D(event));
    }

    public void onRender3D(Render3DEvent event) {
        this.modules.stream().filter(AbstractModule::isEnabled).forEach(module -> module.onRender3D(event));
    }

    public <T extends Module> T getModuleT(Class<T> clazz) {
        return modules.stream().filter(module -> module.getClass() == clazz).map(module -> (T) module).findFirst().orElse(null);
    }

    public void sortModules(boolean reverse) {
        this.sortedModules = this.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(module -> this.renderer.getStringWidth(module.getFullArrayString()) * (reverse ? -1 : 1))).collect(Collectors.toList());
    }

    public void sortModulesABC() {
        this.sortedModulesABC = new ArrayList<String>(this.getEnabledModulesName());
        this.sortedModulesABC.sort(String.CASE_INSENSITIVE_ORDER);
    }

    public void onLogout() {
        this.modules.forEach(Module::onLogout);
    }

    public void onLogin() {
        this.modules.forEach(Module::onLogin);
    }

    public void onUnload() {
        this.modules.forEach(MinecraftForge.EVENT_BUS::unregister);
        this.modules.forEach(Module::onUnload);
    }

    public void onUnloadPost() {
        for (Module module : this.modules) {
            module.enabled.setValue(false);
        }
    }

    public void onKeyPressed(int eventKey) {
        if (eventKey == 0 || !Keyboard.getEventKeyState() || ModuleManager.mc.currentScreen instanceof LegacyGui) {
            return;
        }
        this.modules.forEach(module -> {
            if (module.getBind().getKey() == eventKey) {
                module.toggle();
            }
        });
    }

    private class Animation
            extends Thread {
        public Module module;
        public float offset;
        public float vOffset;
        ScheduledExecutorService service;

        public Animation() {
            super("Animation");
            this.service = Executors.newSingleThreadScheduledExecutor();
        }

        @Override
        public void run() {
            if (HUD.getInstance().renderingMode.getValue() == HUD.RenderingMode.Length) {
                for (Module module : ModuleManager.this.sortedModules) {
                    String text = module.getDisplayName() + ChatFormatting.GRAY + (module.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                    module.offset = (float) ModuleManager.this.renderer.getStringWidth(text) / HUD.getInstance().animationHorizontalTime.getValue().floatValue();
                    module.vOffset = (float) ModuleManager.this.renderer.getFontHeight() / HUD.getInstance().animationVerticalTime.getValue().floatValue();
                    if (module.isEnabled() && HUD.getInstance().animationHorizontalTime.getValue() != 1) {
                        if (!(module.arrayListOffset > module.offset) || Util.mc.world == null) continue;
                        module.arrayListOffset -= module.offset;
                        module.sliding = true;
                        continue;
                    }
                    if (!module.isDisabled() || HUD.getInstance().animationHorizontalTime.getValue() == 1) continue;
                    if (module.arrayListOffset < (float) ModuleManager.this.renderer.getStringWidth(text) && Util.mc.world != null) {
                        module.arrayListOffset += module.offset;
                        module.sliding = true;
                        continue;
                    }
                    module.sliding = false;
                }
            } else {
                for (String e : ModuleManager.this.sortedModulesABC) {
                    Module module = Legacy.moduleManager.getModuleByName(e);
                    String text = module.getDisplayName() + ChatFormatting.GRAY + (module.getDisplayInfo() != null ? " [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]" : "");
                    module.offset = (float) ModuleManager.this.renderer.getStringWidth(text) / HUD.getInstance().animationHorizontalTime.getValue().floatValue();
                    module.vOffset = (float) ModuleManager.this.renderer.getFontHeight() / HUD.getInstance().animationVerticalTime.getValue().floatValue();
                    if (module.isEnabled() && HUD.getInstance().animationHorizontalTime.getValue() != 1) {
                        if (!(module.arrayListOffset > module.offset) || Util.mc.world == null) continue;
                        module.arrayListOffset -= module.offset;
                        module.sliding = true;
                        continue;
                    }
                    if (!module.isDisabled() || HUD.getInstance().animationHorizontalTime.getValue() == 1) continue;
                    if (module.arrayListOffset < (float) ModuleManager.this.renderer.getStringWidth(text) && Util.mc.world != null) {
                        module.arrayListOffset += module.offset;
                        module.sliding = true;
                        continue;
                    }
                    module.sliding = false;
                }
            }
        }

        @Override
        public void start() {
            System.out.println("Starting animation thread.");
            this.service.scheduleAtFixedRate(this, 0L, 1L, TimeUnit.MILLISECONDS);
        }
    }

    public static ArrayList<Module> nigger;

    public static ArrayList<Module> getModules() {
        return nigger;
    }

    public static boolean isModuleEnablednigger(String name) {
        Module modulenigger = getModules().stream().filter(mm->mm.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        return modulenigger.isEnabled();
    }

    public static boolean isModuleEnablednigger(Module modulenigger) {
        return modulenigger.isEnabled();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Module> T getModule(Class<T> clazz) {
        return (T) modulesClassMap.get(clazz);
    }

    public static Module getModule(String name) {
        if (name == null) return null;
        return modulesNameMap.get(name.toLowerCase(Locale.ROOT));
    }

    public static boolean isModuleEnabled(Class<? extends Module> clazz) {
        Module module = getModule(clazz);
        return module != null && module.isEnabled();
    }

    public static boolean isModuleEnabled(String name) {
        Module module = getModule(name);
        return module != null && module.isEnabled();
    }
}

