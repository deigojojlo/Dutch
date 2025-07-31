package main.java.game.controller;

import java.awt.Dimension;
import javax.swing.JFrame;
import main.java.game.view.GameRuleView;
import main.java.game.view.MenuView;
import main.java.game.view.SettingsView;

public class MenuController {
    private final JFrame frame;
    private final MenuView view;

    /**
     * The constructor of the MenuController
     * 
     * @param jFrame the root JFrame
     */
    public MenuController(JFrame jFrame) {
        view = new MenuView(jFrame);
        frame = jFrame;

        setHandler();
    }

    /**
     * Sets the listeners on the menu buttons
     */
    private void setHandler() {
        view.getExitButton().addActionListener(_ -> exitGame());
        view.getSettingsButton().addActionListener(_ -> openSettings());
        view.getNewGameButton().addActionListener(_ -> launchSelection());
        view.getRuleButton().addActionListener(_ -> openRule());
    }

    /**
     * Defines the actions to exit the application properly
     */
    private void exitGame() {
        frame.setVisible(false);
        frame.dispose();

        System.exit(0);
    }

    /**
     * This method launches the creation of the Selection View & Model.
     */
    private void launchSelection() {
        SelectionController selectionController = new SelectionController(frame);

        frame.getContentPane().removeAll();
        frame.add(selectionController.getView());
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Cleans the JFrame and loads the Settings page
     */
    public void openSettings() {
        SettingsView settingsPanel = new SettingsView(frame, () -> this.backToMenu());
        frame.getContentPane().removeAll();
        frame.add(settingsPanel);
        frame.revalidate();
        frame.repaint();
    }

    /**
     * Cleans the JFrame and restores the Menu
     */
    private void backToMenu() {
        frame.getContentPane().removeAll();

        frame.add(new MenuController(frame).getView());

        frame.revalidate();
        frame.repaint();
    }

    /**
     * Cleans the JFrame and loadds the Rule page
     */
    private void openRule() {
        Dimension dimension = this.frame.getPreferredSize();
        this.frame.getContentPane().removeAll();
        GameRuleView gameRuleView = new GameRuleView();
        this.frame.getContentPane().add(gameRuleView);
        gameRuleView.setPreferredSize(dimension);
        gameRuleView.getBackButton().addActionListener(_ -> backToMenu());
        this.frame.revalidate();
        this.frame.repaint();
    }

    /**
     * 
     * @return the MenuView associated with this controller
     */
    public MenuView getView() {
        return view;
    }
}
