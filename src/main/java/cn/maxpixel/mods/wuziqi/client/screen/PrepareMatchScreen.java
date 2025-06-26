package cn.maxpixel.mods.wuziqi.client.screen;

import cn.maxpixel.mods.wuziqi.block.entity.BoardBlockEntity;
import cn.maxpixel.mods.wuziqi.network.serverbound.PrepareMatchPacket;
import cn.maxpixel.mods.wuziqi.util.I18nUtil;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.UUID;

public class PrepareMatchScreen extends Screen {
    public static final String SCREEN = "prepare_match";
    private static final int WIDTH = 300;
    public static final Component WUZIQI = Component.translatable(I18nUtil.makeScreenText(SCREEN, "wuziqi"));
    public static final Component PLAYERS = Component.translatable(I18nUtil.makeScreenText(SCREEN, "players"));
    public static final Component JOIN = Component.translatable(I18nUtil.makeScreenText(SCREEN, "join"));
    public static final Component QUIT = Component.translatable(I18nUtil.makeScreenText(SCREEN, "quit"));
    public static final Component START = Component.translatable(I18nUtil.makeScreenText(SCREEN, "start"));
    public static final Component START_SINGLEPLAYER = Component.translatable(I18nUtil.makeScreenText(SCREEN, "start_sp"));

    private final BoardBlockEntity blockEntity;

    private PlayerSelectionList queuedPlayers;
    private Button toggleJoin;
    private Button startMatch;
    private CycleButton difficultyButton;

    private boolean joined;

    public PrepareMatchScreen(BoardBlockEntity blockEntity) {
        super(GameNarrator.NO_TITLE);
        this.blockEntity = blockEntity;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, WUZIQI, this.width / 2, 20, 0xFFFFFF);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.fill(queuedPlayers.getRowLeft(), queuedPlayers.getBottom() - queuedPlayers.getHeight(), queuedPlayers.getRight(), queuedPlayers.getBottom(), 0xE0101010);// background for queuedPlayers

    }

    @Override
    public void tick() {
        super.tick();
        toggleJoin.setMessage(joined ? QUIT : JOIN);
        var playerCount = queuedPlayers.children().size();
        if (playerCount == 1) {
            startMatch.setMessage(START_SINGLEPLAYER);
        } else {
            startMatch.setMessage(START);
        }
        startMatch.active = playerCount >= 1;
    }

    @Override
    protected void init() {
        super.init();
        this.difficultyButton = CycleButton.<BoardBlockEntity.Difficulty>builder((serializedName) -> Component.translatable("screen.wuziqi.prepare_match." + serializedName.name().toLowerCase()))
                .withValues(ImmutableList.copyOf(BoardBlockEntity.Difficulty.values()))
                .displayOnlyValue()
                .withInitialValue(blockEntity.getDifficulty())
                .create(this.width / 2 - 143, 16, 100, 25, Component.literal("MODE"), (cycleButton, difficulty) -> updateMode(difficulty));
        PacketDistributor.sendToServer(new PrepareMatchPacket(PrepareMatchPacket.Action.SYNC, (BoardBlockEntity.Difficulty) difficultyButton.getValue(), blockEntity.getBlockPos()));
        this.queuedPlayers = new PlayerSelectionList(minecraft, WIDTH, 80, 40, 160, 20);
        queuedPlayers.setPosition(width / 2 - WIDTH / 2,40);
        queuedPlayers.setRenderHeader(true, 20);
        addRenderableWidget(queuedPlayers);

        this.addRenderableWidget(difficultyButton);

        this.toggleJoin = Button.builder(JOIN, button -> {
            if (joined) {
                updateJoin(false, PrepareMatchPacket.Action.QUIT);
            } else {
                updateJoin(true, PrepareMatchPacket.Action.JOIN);
            }
        }).pos(width / 2 - 150 - 1, 170).build();
        addRenderableWidget(toggleJoin);

        this.startMatch = Button.builder(START, button -> {
            PacketDistributor.sendToServer(new PrepareMatchPacket(PrepareMatchPacket.Action.START, (BoardBlockEntity.Difficulty) difficultyButton.getValue(), blockEntity.getBlockPos()));
            onClose();
        }).pos(width / 2 + 1, 170).build();
        addRenderableWidget(startMatch);
    }


    public void setQueuedPlayers(UUID[] players) {
        var list = queuedPlayers.children();
        list.clear();
        for (UUID uuid : players) {
            if (minecraft != null && minecraft.level != null && minecraft.level.getPlayerByUUID(uuid) instanceof AbstractClientPlayer player) {
                if (player == minecraft.player) joined = true;
                list.add(queuedPlayers.new Entry(player, minecraft));
            }
        }
    }

    private void updateJoin(boolean joined, PrepareMatchPacket.Action quit) {
        this.joined = joined;
        int size = this.queuedPlayers.children().size();
        this.difficultyButton.active = (size <= 1);
        PacketDistributor.sendToServer(new PrepareMatchPacket(quit, (BoardBlockEntity.Difficulty) difficultyButton.getValue(), blockEntity.getBlockPos()));
    }

    private void updateMode(BoardBlockEntity.Difficulty difficulty) {
        PacketDistributor.sendToServer(new PrepareMatchPacket(PrepareMatchPacket.Action.SYNC, difficulty, blockEntity.getBlockPos()));
    }
}