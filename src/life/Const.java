package life;

import java.io.File;

public class Const {
    static final String SAVED_DIR_PATH = System.getProperty("user.dir") + File.separator + "saved" + File.separator;
    static final File SAVED_DIR_FILE = new File(SAVED_DIR_PATH);
}
