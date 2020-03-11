package life;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;

public class GameOfLife extends JFrame {
    Grid grid;
    Universe universe;
    JLabel generationLabel;
    JLabel aliveLabel;

    public GameOfLife(){
        super("Game of Life");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 1500);
        setLocationRelativeTo(null);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
        setVisible(true);
    }

    private void createControlPanel(GameMaster.Callbacks callbacks){

        JPanel buttonsPanel = createButtonsPanel(callbacks);
        JPanel regulationsPanel = createRegulationsPanel(callbacks);
        JPanel infoPanel = new JPanel();

        generationLabel = new JLabel();
        generationLabel.setName("GenerationLabel");
        generationLabel.setText("Generation 1");
        generationLabel.setBounds(10, 5, 100, 20);
        infoPanel.add(generationLabel);

        aliveLabel = new JLabel();
        aliveLabel.setName("AliveLabel");
        aliveLabel.setText("Alive 10");
        aliveLabel.setBounds(10, 25, 100, 20);
        infoPanel.add(aliveLabel);

        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JPanel controlPanel = new JPanel();
        controlPanel.add(buttonsPanel);
        controlPanel.add(regulationsPanel);
        controlPanel.add(infoPanel);

        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        add(controlPanel);

        grid = new Grid(1000, 4);
        grid.setBounds(0, 50, 1000, 1000);
        add(grid);

        setVisible(true);
    }

    private JPanel createRegulationsPanel(GameMaster.Callbacks callbacks){
        JPanel regulationsPanel = new JPanel();

        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL);
        speedSlider.setToolTipText("Speed");
        speedSlider.setMinimum(0);
        speedSlider.setMaximum(100);
        speedSlider.setValue(50);
        speedSlider.addChangeListener((ChangeEvent e) -> {
            int speedValue = speedSlider.getValue();
            callbacks.setSpeedValue(speedValue);
        });

        regulationsPanel.add(speedSlider);
        regulationsPanel.setLayout(new BoxLayout(regulationsPanel, BoxLayout.Y_AXIS));

        return regulationsPanel;
    }

    private JPanel createButtonsPanel(GameMaster.Callbacks callbacks){
        JPanel buttonsPanel = new JPanel();

        JToggleButton playToggleButton = new JToggleButton();
        playToggleButton.setName("PlayToggleButton");
        playToggleButton.setText("Play");
        playToggleButton.addActionListener((e) -> {
            if(playToggleButton.getText().equals("Play")) {
                playToggleButton.setText("Pause");
                callbacks.play();
            }else{
                playToggleButton.setText("Play");
                callbacks.pause();
            }
        });

        JButton stopButton = new JButton();
        stopButton.setName("StopButton");
        stopButton.setText("Stop");
        stopButton.addActionListener((e) -> {
            callbacks.stop();
            playToggleButton.setText("Play");
        });

        JButton resetButton = new JButton();
        resetButton.setName("ResetButton");
        resetButton.setText("Reset");
        resetButton.addActionListener((e) -> {
            callbacks.reset();
            playToggleButton.setText("Play");
        });

        JButton saveToFile = new JButton();
        saveToFile.setText("Save");
        saveToFile.addActionListener(e -> {
            playToggleButton.setText("Play");
            callbacks.stop();
            JFileChooser fileSaver = new JFileChooser();
            int result = fileSaver.showSaveDialog(null);
            if(result == JFileChooser.APPROVE_OPTION){
                callbacks.saveToFile(fileSaver.getSelectedFile().getAbsolutePath());
            }
        });

        JButton loadSavedGame = new JButton();
        loadSavedGame.setText("Load");
        loadSavedGame.addActionListener(e -> {
            playToggleButton.setText("Play");
            callbacks.stop();
            JFileChooser fileSaver = new JFileChooser();
            int result = fileSaver.showOpenDialog(null);
            if(result == JFileChooser.APPROVE_OPTION){
                callbacks.loadSavedGame(fileSaver.getSelectedFile().getAbsolutePath());
            }
        });

        buttonsPanel.add(saveToFile);
        buttonsPanel.add(loadSavedGame);
        buttonsPanel.add(playToggleButton);
        buttonsPanel.add(stopButton);
        buttonsPanel.add(resetButton);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

        return buttonsPanel;
    }

    public void initialize(Universe universe, GameMaster.Callbacks callbacks){
        createControlPanel(callbacks);
        this.universe = universe;
        updateGridSize();
    }

    public void updateGridSize(){
        grid.setCells(universe.getSize());
        updateGrid();
    }

    public void updateGrid(){
        grid.setStatus(universe.getMap());
        grid.repaint();
        updateStatus();
    }

    private void updateStatus(){
        generationLabel.setText("Generation #" + universe.getGenerationNumber());
        aliveLabel.setText("Alive: " + universe.getAlive());
    }

}

class Grid extends JComponent{

    private int cells, size, side;
    private byte[][] status;

    Grid(int size, int cells){
        super();
        this.size = size;
        this.cells = cells;
        side = size/cells;
        status = new byte[cells][cells];
    }

    public void setCells(int cells) {
        this.cells = cells;
        side = size/cells;
        status = new byte[cells][cells];
    }

    public void setStatus(byte[][] status) {
        this.status = status;
    }

    @Override
    public void paintComponent(Graphics g) {
        for(int i = 0; i < cells; i ++) {
            for(int k = 0; k < cells; k ++) {
                g.setColor((status[i][k] == 1 ? Color.BLACK : Color.WHITE));
                g.fillRect(k * side, i * side, side, side);
            }
        }
    }

}