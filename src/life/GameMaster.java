package life;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class GameMaster {

    Universe universe;
    MotoreImmobile motoreImmobile;
    GameOfLife game;

    GameMaster() {
        universe = new Universe(10);
        motoreImmobile = new MotoreImmobile(universe);

        game = new GameOfLife();
        Callbacks callbacks = new Callbacks(this, universe, motoreImmobile, game);
        game.initialize(universe, callbacks);
    }

    public void resetUniverse(int size) {
        universe.initialize(size);
        motoreImmobile.initialize();
        game.updateGridSize();
    }


    static class Callbacks {

        private AtomicBoolean isPaused = new AtomicBoolean(false);
        private AtomicBoolean isStopped = new AtomicBoolean(true);

        private GameMaster gameMaster;
        Universe universe;
        MotoreImmobile motoreImmobile;
        GameOfLife game;

        private final int minSpeed = 100;
        AtomicInteger delay = new AtomicInteger(minSpeed);

        Callbacks(GameMaster gameMaster, Universe universe, MotoreImmobile motoreImmobile, GameOfLife game) {
            this.gameMaster = gameMaster;
            this.universe = universe;
            this.motoreImmobile = motoreImmobile;
            this.game = game;
        }

        public void play() {
            System.out.println("Play");

            if (isStopped.get()) {

                isStopped.set(false);
                isPaused.set(false);

                int generations = 10000;

                Thread player = new Thread(() -> {
                    while (!isStopped.get() && universe.getGenerationNumber() < generations) {

                        while (isPaused.get() && !isStopped.get()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        while (!isStopped.get() && !isPaused.get() && universe.getGenerationNumber() < generations) {
                            motoreImmobile.evolve();
                            game.updateGrid();
                            try {
                                Thread.sleep(delay.get());
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
            int size = 10;
            gameMaster.resetUniverse(size);
        }


        public void saveToFile(String path) {
            GameSaveLoad.saveToFile(universe, path);
        }

        public void loadSavedGame(String path) {
            GameSaveLoad.loadSavedGame(motoreImmobile, path);
            game.updateGridSize();
        }

        public void setSpeedValue(int speedValue) {
            delay.set(minSpeed - speedValue);
        }
    }
}