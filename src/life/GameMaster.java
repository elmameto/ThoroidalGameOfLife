package life;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class GameMaster {

    private final int minSpeed = 100;
    Universe universe;
    MotoreImmobile motoreImmobile;
    GameOfLife game;
    AtomicInteger delay = new AtomicInteger(minSpeed);
    private AtomicBoolean isPaused = new AtomicBoolean(false);
    private AtomicBoolean isStopped = new AtomicBoolean(true);


    GameMaster() {
        if (Const.SAVED_DIR_FILE.mkdir())
            System.out.println("Save directory created successfully");
        else
            System.out.println("Save directory already present or not created");

        universe = new Universe(new Size(100, 100));
        motoreImmobile = new MotoreImmobile(universe);

        game = new GameOfLife();
        game.initialize(this, universe.getMap(), universe.getGenerationNumber(), universe.getAlive());
    }

    public void play() {
        System.out.println("Play");

        if (isStopped.get()) {

            isStopped.set(false);
            isPaused.set(false);

            int generations = 10000;

            Thread player = new Thread(() -> {

                while (!isStopped.get()) {

                    while (isPaused.get() && !isStopped.get()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    while (!isStopped.get() && !isPaused.get()) {

                        long startTime = System.currentTimeMillis() + delay.get();
                        motoreImmobile.evolve();
                        game.updateGrid(universe.getMap(), universe.getGenerationNumber(), universe.getAlive());
                        long missingDelay = Math.max(startTime - System.currentTimeMillis(), 0);

                        try {
                            Thread.sleep(missingDelay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                isStopped.set(true);
            });

            player.start();

        } else if (isPaused.get())
            isPaused.set(false);
    }

    public void pause() {
        System.out.println("Pause");
        isPaused.set(true);
    }

    public void stop() {
        System.out.println("Stop");
        isStopped.set(true);
    }

    public void reset() {
        System.out.println("Reset");
        stop();
        Size size = new Size(300, 500);
        universe.initialize(size);
        motoreImmobile.initialize();
        game.updateGridSize(universe.getMap(), universe.getGenerationNumber(), universe.getAlive());
    }

    public void fillRandom() {
        motoreImmobile.fillRandom();
        game.updateGridSize(universe.getMap(), universe.getGenerationNumber(), universe.getAlive());
    }

    public void saveToFile(String path) {
        GameSaveLoad.saveToFile(universe.getMap(), path);
    }

    public void loadSavedGame(String path) {
        byte[][] data = GameSaveLoad.loadPattern(path);
        motoreImmobile.loadSavedMap(data);
        game.updateGridSize(universe.getMap(), universe.getGenerationNumber(), universe.getAlive());
    }

    public void addSavedPattern(String path) {
        byte[][] data = GameSaveLoad.loadPattern(path);
        motoreImmobile.insertPatternCentered(data);
        game.updateGridSize(universe.getMap(), universe.getGenerationNumber(), universe.getAlive());
    }

    public void setSpeedValue(int speedValue) {
        delay.set(minSpeed - speedValue);
    }
}