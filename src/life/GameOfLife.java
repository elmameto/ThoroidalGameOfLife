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
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1020, 1120);
        setLocationRelativeTo(null);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setVisible(true);
    }

    private void createControlPanel(GameMaster gameMaster){

        JPanel buttonsPanel = createButtonsPanel(gameMaster);
        JPanel regulationsPanel = createRegulationsPanel(gameMaster);
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
        controlPanel.setSize(150, 200);

        add(controlPanel);

        JPanel gridContainer = new JPanel();
        grid = new Grid(new Size(1000, 1000), new Size(100, 100));
        grid.setBounds(0, 50, 1000, 1000);
        gridContainer.add(grid);
        gridContainer.setLayout(new BoxLayout(gridContainer, BoxLayout.X_AXIS));

        add(gridContainer);

        setVisible(true);
    }

    private JPanel createRegulationsPanel(GameMaster gameMaster){
        JPanel regulationsPanel = new JPanel();

        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL);
        speedSlider.setToolTipText("Speed");
        speedSlider.setMinimum(0);
        speedSlider.setMaximum(100);
        speedSlider.setValue(speedSlider.getMinimum());
        gameMaster.setSpeedValue(speedSlider.getValue());
        speedSlider.addChangeListener((ChangeEvent e) -> gameMaster.setSpeedValue(speedSlider.getValue()));

        regulationsPanel.add(speedSlider);
        regulationsPanel.setLayout(new BoxLayout(regulationsPanel, BoxLayout.Y_AXIS));

        return regulationsPanel;
    }

    private JPanel createButtonsPanel(GameMaster gameMaster){
        JPanel buttonsPanel = new JPanel();

        JToggleButton playToggleButton = new JToggleButton();
        playToggleButton.setName("PlayToggleButton");
        playToggleButton.setText("Play");
        playToggleButton.addActionListener((e) -> {
            if(playToggleButton.getText().equals("Play")) {
                playToggleButton.setText("Pause");
                gameMaster.play();
            }else{
                playToggleButton.setText("Play");
                gameMaster.pause();
            }
        });

        JButton stopButton = new JButton();
        stopButton.setName("StopButton");
        stopButton.setText("Stop");
        stopButton.addActionListener((e) -> {
            gameMaster.stop();
            playToggleButton.setText("Play");
        });

        JButton resetButton = new JButton();
        resetButton.setName("ResetButton");
        resetButton.setText("Reset");
        resetButton.addActionListener((e) -> {
            gameMaster.reset();
            playToggleButton.setText("Play");
        });

        JButton fillRandom = new JButton();
        fillRandom.setName("FillRandomButton");
        fillRandom.setText("Fill random");
        fillRandom.addActionListener((e) -> {
            gameMaster.fillRandom();
            playToggleButton.setText("Play");
        });

        JButton saveToFile = new JButton();
        saveToFile.setText("Save");
        saveToFile.addActionListener(e -> {
            playToggleButton.setText("Play");
            gameMaster.stop();
            JFileChooser fileSaver = new JFileChooser();
            fileSaver.setCurrentDirectory(Const.SAVED_DIR_FILE);
            int result = fileSaver.showSaveDialog(null);
            if(result == JFileChooser.APPROVE_OPTION){
                gameMaster.saveToFile(fileSaver.getSelectedFile().getAbsolutePath());
            }
        });

        JButton loadSavedGame = new JButton();
        loadSavedGame.setText("Load");
        loadSavedGame.addActionListener(e -> {
            playToggleButton.setText("Play");
            gameMaster.stop();
            JFileChooser fileSaver = new JFileChooser();
            fileSaver.setCurrentDirectory(Const.SAVED_DIR_FILE);
            int result = fileSaver.showOpenDialog(null);
            if(result == JFileChooser.APPROVE_OPTION){
                gameMaster.loadSavedGame(fileSaver.getSelectedFile().getAbsolutePath());
            }
        });

        JButton addPattern = new JButton();
        addPattern.setText("Add Pattern");
        addPattern.addActionListener(e -> {
            playToggleButton.setText("Play");
            gameMaster.stop();
            JFileChooser fileSaver = new JFileChooser();
            fileSaver.setCurrentDirectory(Const.SAVED_DIR_FILE);
            int result = fileSaver.showOpenDialog(null);
            if(result == JFileChooser.APPROVE_OPTION){
                gameMaster.addSavedPattern(fileSaver.getSelectedFile().getAbsolutePath());
            }
        });

        buttonsPanel.add(saveToFile);
        buttonsPanel.add(addPattern);
        buttonsPanel.add(loadSavedGame);
        buttonsPanel.add(playToggleButton);
        buttonsPanel.add(stopButton);
        buttonsPanel.add(resetButton);
        buttonsPanel.add(fillRandom);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));

        return buttonsPanel;
    }

    public void initialize(GameMaster gameMaster, byte[][] map, int generationNumber, int alive){
        createControlPanel(gameMaster);
        updateGridSize(map, generationNumber, alive);
    }

    public void updateGridSize(byte[][] map, int generationNumber, int alive){
        grid.setCells(map);
        updateGrid(map, generationNumber, alive);
    }

    public void updateGrid(byte[][] map, int generationNumber, int alive){
        grid.setStatus(map);
        grid.repaint();
        updateStatus(generationNumber, alive);
    }

    private void updateStatus(int generationNumber, int alive){
        generationLabel.setText("Generation #" + generationNumber);
        aliveLabel.setText("Alive: " + alive);
    }

}

class Grid extends JComponent{

    private Size gridResolution;
    private Size gridSize;
    private int side;
    private byte[][] status;

    Grid(Size gridResolution, Size gridSize){
        super();
        this.gridResolution = gridResolution;
        this.gridSize = gridSize;
        updateSide();
        status = gridSize.makeByteMatrix();
    }

    public void setCells(byte[][] map) {
        this.gridSize = new Size(map);
        updateSide();
        status = gridSize.makeByteMatrix();
        setStatus(map);
    }

    private void updateSide(){
        side = Math.min(gridResolution.getX()/gridSize.getX(), gridResolution.getY()/gridSize.getY());
    }

    public void setStatus(byte[][] status) {
        this.status = status;
    }

    @Override
    public void paintComponent(Graphics g) {
        for(int i = 0; i < gridSize.getY(); i ++) {
            for(int k = 0; k < gridSize.getX(); k ++) {
                g.setColor((status[i][k] == 1 ? Color.BLACK : Color.WHITE));
                g.fillRect(k * side, i * side, side, side);
            }
        }
    }

}