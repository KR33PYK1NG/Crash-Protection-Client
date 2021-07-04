package rmc.mixins.crash_protection_client.inject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.crash.ReportedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import rmc.mixins.crash_protection_client.DummyPermission;

import java.security.Permission;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * Developed by RMC Team, 2021
 * @author KR33PY
 */
@Mixin(value = Minecraft.class)
public abstract class MinecraftMixin {

    private static Boolean rmc$isRmcClient;
    private static final ArrayList<StackTraceElement> rmc$ALREADY_SENT = new ArrayList<>();

    @Redirect(method = "Lnet/minecraft/client/Minecraft;runTick(Z)V",
              at = @At(value = "INVOKE",
                       target = "Lnet/minecraft/client/renderer/GameRenderer;render(FJZ)V"))
    private void wrapGameRender(GameRenderer inst, float arg0, long arg1, boolean arg2) {
        if (rmc$isRmcClient == null) {
            try {
                Class.forName("rmc.mixins.title_and_icon.TitleAndIcon");
                rmc$isRmcClient = true;
            } catch (Exception ex) {
                rmc$isRmcClient = false;
            }
        }
        if (rmc$isRmcClient) {
            try {
                inst.render(arg0, arg1, arg2);
            } catch (Exception ex) {
                System.out.println("[RMC] CAUGHT AN EXCEPTION THAT COULD CAUSE A HARD CRASH!!!");
                ex.printStackTrace();
                Throwable tgt = ex instanceof ReportedException ? ex.getCause() : ex;
                StackTraceElement[] tgtStack = tgt.getStackTrace();
                if (tgtStack.length > 0 && !rmc$ALREADY_SENT.contains(tgtStack[0])) {
                    System.out.println("[RMC] Sending new report!");
                    StringBuilder sb = new StringBuilder();
                    sb.append(tgt.toString() + "\n");
                    for (StackTraceElement tgtLine : tgtStack) {
                        sb.append("  " + tgtLine.toString() + "\n");
                    }
                    System.getSecurityManager().checkPermission(new DummyPermission("sendBugReport", sb.toString()));
                    rmc$ALREADY_SENT.add(tgtStack[0]);
                }
            }
        }
    }

}
