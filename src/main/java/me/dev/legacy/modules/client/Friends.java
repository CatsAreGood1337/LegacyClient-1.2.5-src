package me.dev.legacy.modules.client;

import me.dev.legacy.Legacy;
import me.dev.legacy.api.event.events.render.Render2DEvent;
import me.dev.legacy.modules.Module;
import me.dev.legacy.impl.setting.Setting;
import me.dev.legacy.api.util.Render.ColorUtil;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public class Friends extends Module
{
    private int color;
    public Setting<Integer> friendX = register(new Setting("PosX", Integer.valueOf(740), Integer.valueOf(0), Integer.valueOf(1000)));
    public Setting<Integer> friendY = register(new Setting("PosY", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(1000)));

    public Friends() {
        super("FriendList", "Lists ur friends in render", Category.CLIENT, true, false, false);
    }

    @Override
    public void onRender2D(final Render2DEvent event) {
        this.color = ColorUtil.toRGBA(Colors.getInstance().red.getValue(), Colors.getInstance().green.getValue(), Colors.getInstance().blue.getValue());
        this.renderFriends();
    }

    private void renderFriends() {
        final List<String> friends = new ArrayList<String>();
        for (final EntityPlayer player : Friends.mc.world.playerEntities) {
            if (Legacy.friendManager.isFriend(player.getName())) {
                friends.add(player.getName());
            }
        }
        if (Colors.getInstance().rainbow.getValue()) {
            if (Colors.getInstance().rainbowModeHud.getValue() == Colors.rainbowMode.Static) {
                int y = this.friendY.getValue();
                int x = this.friendX.getValue();
                if (friends.isEmpty()) {
                    this.renderer.drawString("No friends online", (float)x, (float)y, ColorUtil.rainbow(Colors.getInstance().rainbowHue.getValue()).getRGB(), true);
                }
                else {
                    this.renderer.drawString("Friend(s) near you:", (float)x, (float)y, ColorUtil.rainbow(Colors.getInstance().rainbowHue.getValue()).getRGB(), true);
                    y += 12;
                    for (final String friend : friends) {
                        this.renderer.drawString(friend, (float)x , (float)y, ColorUtil.rainbow(Colors.getInstance().rainbowHue.getValue()).getRGB(), true);
                        y += 12;
                    }
                }
            }
        }
        else {
            int y = this.friendY.getValue();
            int x = this.friendX.getValue();
            if (friends.isEmpty()) {
                this.renderer.drawString("No friends online", (float)x , (float)y, this.color, true);
            }
            else {
                this.renderer.drawString("Friend(s) near you:", (float)x, (float)y, this.color, true);
                y += 12;
                for (final String friend : friends) {
                    this.renderer.drawString(friend,(float)x, (float)y, this.color, true);
                    y += 12;
                }
            }
        }
    }
}
