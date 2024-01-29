package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
//import java.util.Date;
import java.util.HashMap;

import static gitlet.Utils.*;

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
    /** The second parent (the given branch) of this Commit. Only for merge commits. */
    private String secondParent;
    /** contains the file names and their blobs in the commit.*/
    HashMap<String, String> filenameBlob;

    /** The depth from initial commit. */
    private int depth;

    /** Constructor for Commit class.
    * @param message: the commit message provided by the user.
    * @param parent: the SHA - 1 hash of the parent commit.
    * @param timestamp: the timestamp of the commit.
    * */
    public Commit(String message, String parent, String secondParent, String timestamp) {
        this.message = message;
        this.parent = parent;
        this.secondParent = secondParent;
        this.timestamp = timestamp;
        this.filenameBlob = new HashMap<>();
        this.depth = calcuDepth(parent);
    }

    /** Calculate the depth by its parent commit depth. */
    private int calcuDepth(String parentCommitId) {
        // Initial commit, depth = 0.
        if (parentCommitId == null) {
            return 0;
        }
        Commit parentCommit = readObject(join(Repository.COMMIT, parentCommitId), Commit.class);
        int parentDepth = parentCommit.getDepth();
        return parentDepth + 1;
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
        Repository.updateActiveBranch(commitSha1);
        Repository.updateId(commitSha1);
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

    public int getDepth() {
        return this.depth;
    }
    public String getSecondParent() {
        return this.secondParent;
    }




}
