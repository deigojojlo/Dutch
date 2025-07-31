package main.java.game.controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import main.java.game.view.SelectionView;
import main.java.onlinegame.WaitingRoomController;
import main.java.onlinegame.WebsocketClient;

public class SelectionController {
    private final JFrame frame;
    private final SelectionView view;
    private WaitingRoomController wrc;

    /**
     * The SelectionController constructor
     * 
     * @param jframe the root JFrame
     */
    public SelectionController(JFrame jframe) {
        view = new SelectionView(jframe);

        frame = jframe;

        setHandler();
    }

    /**
     * Sets the listeners on the SelectionView buttons
     */
    private void setHandler() {
        /* multiplayer Button */
        view.getMultiplayerButton().addActionListener(_ -> {
            if (!WebsocketClient.isAlreadyConnected()) {
                wrc = new WaitingRoomController(frame);
                if (!WebsocketClient.connection(wrc)) {
                    JOptionPane.showMessageDialog(frame,
                            "Connexion au serveur impossible, rééssayez plus tard ou jouez au mode solo",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                } else {
                    // add the waiting room controller
                    view.displayMultiplayerSelection();
                }
            } else {
                view.displayMultiplayerSelection();
            }
        });

        /* solo Button */
        view.getSoloButton().addActionListener(_ -> view.displaySolo());
        /* player Slider */
        view.getPlayerSlider().addChangeListener(_ -> {
            view.getPlayerLabel().setText(view.getPlayerSlider().getValue() + "");
        });
        /* start button */
        view.getStartButton().addActionListener(_ -> {
            // set The solo game panel
            GameController gc = new GameController(frame, 1, view.getPlayerSlider().getValue() - 1,
                    view.getDifficulty());
            // the
            // difficulty
            this.frame.getContentPane().removeAll();
            this.frame.getContentPane().add(gc.getGameView());
            this.frame.revalidate();
            this.frame.repaint();
            SwingUtilities.invokeLater(() -> new Thread(() -> {
                try {
                    gc.play();
                } catch (InterruptedException exception) {
                    System.err.println("A Thread interuption has occurred in play()");
                }
            }).start());
        });

        view.getBackButton().addActionListener(_ -> {
            view.displayModeSelection();
        });

        view.getBackToMenuButton().addActionListener(_ -> {
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new MenuController(frame).getView());
            frame.revalidate();
            frame.repaint();
        });

        view.getNewPrivateButton().addActionListener(_ -> {
            WebsocketClient.sendByte((byte) 66);
        });

        view.getJoinButton().addActionListener(_ -> {
            WebsocketClient.sendByte((byte) 65);
        });

        view.getJoinWithCodeButton().addActionListener(_ -> {
            codeConnection();
        });

        view.getCodeField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent k) {
                if (k.getKeyCode() == KeyEvent.VK_ENTER) {
                    codeConnection();
                }
            }
        });
    }

    /**
     * Join a waiting room with a code
     */
    private void codeConnection() {
        char[] c = view.getCodeField().getText().toCharArray();
        byte[] m = new byte[c.length + 1];
        for (int i = 0; i < c.length; i++) {
            m[i + 1] = (byte) c[i];
        }
        m[0] = 65;
        WebsocketClient.sendByte(m);
    }

    /**
     * 
     * @return the SelectionView associated with this controller
     */
    public SelectionView getView() {
        return view;
    }
}
