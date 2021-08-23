package core;

import java.io.Serializable;

public class Request implements Serializable {
    public static final long serialVersionUID = 761427;
    String action;
    String folder;
    String file;

    public Request(String action, String path, String file) {
        this.action = action;
        this.folder = path;
        this.file = file;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
