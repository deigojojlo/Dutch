package main.java;

import java.awt.EventQueue;
import java.io.File;
import java.nio.file.Path;
import javax.swing.JFrame;
import main.java.game.controller.MenuController;
import main.java.game.view.MenuView;
import main.java.game.view.SettingsView;
import main.java.storage.Storage;
import main.java.util.SoundUtil;

public class Launcher {

    /**
     * This method is the main class and will launch the game.
     */
    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("LOCAL")) {
            Storage.SERVER_ADDRESS = "127.0.0.1";
        }

        Storage.loadImage();
        EventQueue.invokeLater(
                () -> {
                    JFrame mainFrame = new JFrame();
                    if (new File((Path.of(SettingsView.getConfigpath().toString(), "userConfig.json").toString()))
                            .exists()) {
                        SettingsView.loadSettings(mainFrame);
                    } else {
                        SettingsView.setBorderlessFullscreen(mainFrame);
                        SoundUtil.playSound("GAME_BOOT.wav");
                        SoundUtil.playAmbientLoop("GAME_MAIN.wav");
                    }

                    MenuView menuView = new MenuController(mainFrame).getView();
                    mainFrame.getContentPane().add(menuView);
                    mainFrame.revalidate();
                    mainFrame.repaint();
                });
    }
}