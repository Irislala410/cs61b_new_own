package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The .staging directory. */
    public static final File STAGING = join(GITLET_DIR, "staging");
    /** The .rmstaging directory. */
    public static final File RMSTAGING = join(GITLET_DIR, "rmstaging");
    /** The .blob directory. */
    public static final File BLOB = join(GITLET_DIR, "blob");
    /** The .commit directory. */
    public static final File COMMIT = join(GITLET_DIR, "commit");
    /** The .HEAD directory. */
    public static final File HEAD_DIR = join(GITLET_DIR, "HEAD");
    /** The .Master directory. */
    public static final File MASTER_DIR = join(GITLET_DIR, "Master");
    /** The .HEAD File. */
    public static final File HEAD = join(HEAD_DIR, "HEAD");
    /** The .Master file. */
    public static final File MASTER = join(MASTER_DIR, "Master");

    /** For the initialization of the working directory: create .gitlet and in it,
     * create .staging, .rmstaging, .blob, .commit for storing different files. Create
     * the initial commit.*/
    public static void setupPersistence() throws IOException {
        if (!GITLET_DIR.exists()){
            GITLET_DIR.mkdir();
            STAGING.mkdir();
            RMSTAGING.mkdir();
            BLOB.mkdir();
            COMMIT.mkdir();
            HEAD_DIR.mkdir();
            MASTER_DIR.mkdir();
            HEAD.createNewFile();
            MASTER.createNewFile();

            Commit initialCommit = new Commit("initial commit",null, "1-1-1970 00:00:00");
            //write the commit into a file
            initialCommit.saveCommit(COMMIT);
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
    }

    /* TODO: fill in the rest of this class. */
}
