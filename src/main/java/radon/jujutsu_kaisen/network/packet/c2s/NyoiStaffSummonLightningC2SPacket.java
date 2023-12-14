package radon.jujutsu_kaisen.network.packet.c2s;

import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import radon.jujutsu_kaisen.JujutsuKaisen;
import radon.jujutsu_kaisen.capability.data.ISorcererData;
import radon.jujutsu_kaisen.capability.data.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Pact;
import radon.jujutsu_kaisen.entity.ConnectedLightningEntity;
import radon.jujutsu_kaisen.entity.NyoiStaffEntity;
import radon.jujutsu_kaisen.entity.effect.LightningEntity;

import java.util.UUID;
import java.util.function.Supplier;

public class NyoiStaffSummonLightningC2SPacket {
    private final UUID identifier;

    public NyoiStaffSummonLightningC2SPacket(UUID identifier) {
        this.identifier = identifier;
    }

    public NyoiStaffSummonLightningC2SPacket(FriendlyByteBuf buf) {
        this(buf.readUUID());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.identifier);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            assert sender != null;

            if (!(sender.serverLevel().getEntity(this.identifier) instanceof NyoiStaffEntity staff)) return;
            if (!staff.isCharged() || staff.getOwner() != sender) return;

            ISorcererData cap = sender.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            sender.level().addFreshEntity(new ConnectedLightningEntity(sender, cap.getAbilityPower(), sender.position().add(0.0D, sender.getBbHeight() / 2.0F, 0.0D),
                    staff.position()));

            staff.setCharged(false);
        });
        ctx.setPacketHandled(true);
    }
}