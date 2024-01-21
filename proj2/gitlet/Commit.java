package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.HashMap;

import static gitlet.Utils.writeObject;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;

    /* TODO: fill in the rest of this class. */
    /** The timestamp of this Commit. */
    private String timestamp;
    /** The parent of this Commit. */
    private String parent;
    /** contains the file names and their blobs in the commit.*/
    public HashMap<String, String> filenameBlob;

    /* TODO: fill in the rest of this class. */
    /** Constructor for Commit class.
     * @param message: the commit message provided by the user.
     * @param parent: the SHA - 1 hash of the parent commit.
     * @param timestamp: the timestamp of the commit.
     * */
    public Commit(String message, String parent, String timestamp){
        this.message = message;
        this.parent = parent;
        this.timestamp = timestamp;
        this.filenameBlob = new HashMap<>();
    }

    /** Write commit into a file whose name is the sha1 of the commit. */
    public void saveCommit(File saveCommitDIR) throws IOException {
        /**temporarily used for convert commit object to string for sha1*/
        File tempOutFile = Utils.join(saveCommitDIR, "tempOutFile");
        if (!tempOutFile.exists()) {
            tempOutFile.createNewFile();
        }
        writeObject(tempOutFile, this);
        String commitString = Utils.readContentsAsString(tempOutFile);
        String commitSha1 = Utils.sha1(commitString);
        File outFile = Utils.join(saveCommitDIR, commitSha1);
        if (!outFile.exists()){
            outFile.createNewFile();
            writeObject(outFile, this); // the saved commit object
        }

//        updateHEAD(commitSha1); //after updateHEAD and updateMaster added, this two lines need to be added.
//        updateMaster(commitSha1);
    }
}
