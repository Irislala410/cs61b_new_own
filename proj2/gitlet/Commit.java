package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
//import java.util.Date;
import java.util.HashMap;

import static gitlet.Utils.writeObject;

/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author Ruo Liang
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    /** The timestamp of this Commit. */
    private String timestamp;
    /** The parent of this Commit. */
    private String parent;
    /** contains the file names and their blobs in the commit.*/
    HashMap<String, String> filenameBlob;

    /** Constructor for Commit class.
    * @param message: the commit message provided by the user.
    * @param parent: the SHA - 1 hash of the parent commit.
    * @param timestamp: the timestamp of the commit.
    * */
    public Commit(String message, String parent, String timestamp) {
        this.message = message;
        this.parent = parent;
        this.timestamp = timestamp;
        this.filenameBlob = new HashMap<>();
    }

    /** Write commit into a file whose name is the sha1 of the commit. */
    public void saveCommit() throws IOException {
        /**temporarily used for convert commit object to string for sha1*/
        File tempOutFile = Utils.join(Repository.COMMIT, "tempOutFile");
        if (!tempOutFile.exists()) {
            tempOutFile.createNewFile();
        }
        writeObject(tempOutFile, this);
        String commitString = Utils.readContentsAsString(tempOutFile);
        String commitSha1 = Utils.sha1(commitString);
        File outFile = Utils.join(Repository.COMMIT, commitSha1);
        if (!outFile.exists()) {
            outFile.createNewFile();
            writeObject(outFile, this); // the saved commit object
        }

        Repository.updateHEAD(commitSha1);
//        updateMaster(commitSha1); //!!!this is a problem for branch. should be changed
        //to sth like updateActiveBranch.
        Repository.updateActiveBranch(commitSha1);
    }

    /** Update the master pointer. */
    public static void updateMaster(String commitSha1) {
        Utils.writeContents(Repository.MASTER, commitSha1);
    }







    /** Return if the commit contains file: fileName. */
    public boolean containFile(String fileName) {
        return this.filenameBlob.containsKey(fileName);
    }

    public String getBlob(String fileName) {
        return this.filenameBlob.get(fileName);
    }

    public String getMessage() {
        return this.message;
    }

    public String getParent() {
        return this.parent;
    }

    public String getDate() {
        return this.timestamp;
    }




}
