package cn.maxpixel.mods.wuziqi.network.serverbound;

import cn.maxpixel.mods.wuziqi.block.entity.BoardBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static cn.maxpixel.mods.wuziqi.WuziqiMod.rl;

public record PrepareMatchPacket(Action action, BoardBlockEntity.Difficulty difficulty, BlockPos boardPos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PrepareMatchPacket> TYPE = new CustomPacketPayload.Type<>(rl("prepare_match"));
    public static final StreamCodec<FriendlyByteBuf, PrepareMatchPacket> STREAM_CODEC = StreamCodec.composite(
            NeoForgeStreamCodecs.enumCodec(Action.class),
            PrepareMatchPacket::action,
            NeoForgeStreamCodecs.enumCodec(BoardBlockEntity.Difficulty.class),
            PrepareMatchPacket::difficulty,
            net.minecraft.core.BlockPos.STREAM_CODEC,
            PrepareMatchPacket::boardPos,
            PrepareMatchPacket::new
    );

    public static void handle(PrepareMatchPacket message, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                var sender = (ServerPlayer) context.player();
                Action action = message.action;
                BoardBlockEntity.Difficulty difficulty = message.difficulty;
                BlockPos boardPos = message.boardPos;
                if (sender.level().getBlockEntity(boardPos) instanceof BoardBlockEntity blockEntity) {
                    blockEntity.setDifficulty(difficulty);
                    blockEntity.setChanged();
                    var level = sender.level();
                    switch (action) {
                        case JOIN -> blockEntity.addPlayer(sender);
                        case QUIT -> blockEntity.removePlayer(sender);
                        case START -> blockEntity.startMatch();
                        case SYNC -> blockEntity.syncJoinedPlayers();
                    }
                    var state = level.getBlockState(boardPos);
                    level.sendBlockUpdated(boardPos, state, state, Block.UPDATE_CLIENTS);
                }
            });
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum Action {
        JOIN, QUIT, SYNC, START
    }
}
