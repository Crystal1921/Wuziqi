package cn.maxpixel.mods.wuziqi.datagen.lang;

import cn.maxpixel.mods.wuziqi.client.screen.PrepareMatchScreen;
import cn.maxpixel.mods.wuziqi.registry.BlockRegistry;
import cn.maxpixel.mods.wuziqi.registry.CreativeModeTabRegistry;
import cn.maxpixel.mods.wuziqi.util.I18nUtil;
import net.minecraft.data.PackOutput;

public class AmericanEnglishProvider extends AbstractLangProvider {
    public AmericanEnglishProvider(PackOutput output) {
        super(output, "en_us");
    }

    @Override
    protected void addTranslations() {
        addItemGroup(CreativeModeTabRegistry.GENERAL_NAME, "Wuziqi");

        addBlock(BlockRegistry.OAK_BOARD, "Oak board");

        addPrepareMatchScreen();
        add(I18nUtil.make("block_entity", "board.your_turn"), "It's your turn");
        add(I18nUtil.make("block_entity", "board.win.white"), "White win");
        add(I18nUtil.make("block_entity", "board.win.black"), "Black win");
    }

    private void addPrepareMatchScreen() {
        addScreen(PrepareMatchScreen.SCREEN, "wuziqi", "Wuziqi");
        addScreen(PrepareMatchScreen.SCREEN, "players", "Players");
        addScreen(PrepareMatchScreen.SCREEN, "join", "Join");
        addScreen(PrepareMatchScreen.SCREEN, "quit", "Quit");
        addScreen(PrepareMatchScreen.SCREEN, "start", "Start");
        addScreen(PrepareMatchScreen.SCREEN, "start_sp", "Start in singleplayer");
        addScreen(PrepareMatchScreen.SCREEN, "empty", "Empty");
        addScreen(PrepareMatchScreen.SCREEN, "easy", "Easy");
        addScreen(PrepareMatchScreen.SCREEN, "normal", "Normal");
        addScreen(PrepareMatchScreen.SCREEN, "hard", "Hard");
        addScreen(PrepareMatchScreen.SCREEN, "lunatic", "Lunatic");
    }
}
