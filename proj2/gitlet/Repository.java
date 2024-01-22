package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
//import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  does at a high level.
 *
 *  @author Ruo Liang
 */
public class Repository {
    /**
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



    /**
     * Check if the working dictionary is an initialized Gitlet working dictionary
     * (one containing a .gitlet subdirectory)
     */
    public static void checkInitial() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
    public static void validateNumArgs(String[] args, int num) {
        if (args.length != num) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
    /** For the initialization of the working directory: create .gitlet and in it,
     * create .staging, .rmstaging, .blob, .commit for storing different files. Create
     * the initial commit.*/
    public static void setupPersistence() throws IOException {
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            STAGING.mkdir();
            RMSTAGING.mkdir();
            BLOB.mkdir();
            COMMIT.mkdir();
            HEAD_DIR.mkdir();
            MASTER_DIR.mkdir();
            HEAD.createNewFile();
            MASTER.createNewFile();

            Commit initialCommit = new Commit("initial commit", null, "Thu Jan 1 00:00:00 1970 +0000");
            //write the commit into a file
            initialCommit.saveCommit(COMMIT);
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
    }

    public static void add(String addFile) throws IOException {
        File fileToAdd = Utils.join(CWD, addFile);
        /*If the file does not exist, print the error message File does not exist.
         * and exit without changing anything.*/
        if (!fileToAdd.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        } else if (!fileInCurrentCommit(addFile)) {
            // if file exist and not in current commit, stage it and remove it if it is in removal area;
            Files.copy(fileToAdd.toPath(), Utils.join(STAGING, addFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
            removeFromRMSTAGING(addFile);
        } else if (!sameContentInCurrentCommit(addFile)) {
            // if it is in current commit but not the same, stage it and remove it if it is in removal area;
            Files.copy(fileToAdd.toPath(), Utils.join(STAGING, addFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
            removeFromRMSTAGING(addFile);
        } else if (Utils.join(STAGING, addFile).exists()) { //this line was else if (fileInStagingArea(addFile)){
            //if it is in current commit and the same, delete it from staging if it is there.
            Utils.restrictedDelete(Utils.join(STAGING, addFile));
        }


    }
    /** Return if the file is in current commit. */
    public static boolean fileInCurrentCommit(String fileName) {
        // get the sha1 of the current commit from HEAD file
        String currentCommitSha1 = Utils.readContentsAsString(HEAD);
        // read commit object from Commit directory
        Commit currentCommit = Utils.readObject(Utils.join(COMMIT, currentCommitSha1), Commit.class);
        return currentCommit.containFile(fileName);
    }
    /** Remove the file from RMSTAGING directory if it is there. */
    public static void removeFromRMSTAGING(String fileToRm) {
        List<String> filesInRm = plainFilenamesIn(RMSTAGING);
        if (filesInRm.contains(fileToRm)) {
            Utils.restrictedDelete(Utils.join(RMSTAGING, fileToRm));
        }
    }

    /** Return if the file is the same with its version in current commit. */
    public static boolean sameContentInCurrentCommit(String fileName) {
        String fileString = Utils.readContentsAsString(Utils.join(CWD, fileName));
        String fileSha1 = Utils.sha1(fileString);
        // get the sha1 of the current commit from HEAD file
        String currentCommitSha1 = Utils.readContentsAsString(HEAD);
        // read commit object from Commit directory
        Commit currentCommit = Utils.readObject(Utils.join(COMMIT, currentCommitSha1), Commit.class);
        String commitFileSha1 = currentCommit.getBlob(fileName);
        return fileSha1.equals(commitFileSha1);
    }

    /** Return if the file is in staging area. */
    public static boolean fileInStagingArea(String fileName) throws IOException {
        List<String> stagingFiles = Utils.plainFilenamesIn(STAGING);
        return stagingFiles.contains(fileName);
    }
    /** Create a new commit with the message the user provided. */
    public static void newCommit(String message) throws IOException {
        /* if staging area is empty, print message and exit. */
        if (Utils.plainFilenamesIn(STAGING).isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        } else if (message.length() == 0) {
            /* if message is empty, print message and exit. */
            System.out.println("Please enter a commit message.");
            System.exit(0);
        } else {
            String parent = Utils.readContentsAsString(HEAD);
            String timeStamp = getTimeStamp();
            Commit newCommit = new Commit(message, parent, timeStamp);
            /* Read parent commit and get its saved files as new commit's saved files. */
            Commit parentCommit = Utils.readObject(Utils.join(COMMIT, parent), Commit.class);
            newCommit.filenameBlob = parentCommit.filenameBlob;
            /* Update the saved files with staging area. */
            List<String> addedFiles = Utils.plainFilenamesIn(STAGING);
            for (String addedFile: addedFiles) {
                String blobSha1 = getBlobSha1(addedFile);
                newCommit.filenameBlob.put(addedFile, blobSha1);
            }
            /* Remove the saved file in remove staging area. */
            List<String> rmFiles = Utils.plainFilenamesIn(RMSTAGING);
            for (String rmFile: rmFiles) {
                newCommit.filenameBlob.remove(rmFile);
            }

            clearStaging();
            clearRMStaging();

            newCommit.saveCommit(COMMIT);
        }
    }

    /** Get system time as String. */
    public static String getTimeStamp() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy");
        String timeStamp = currentTime.format(formatter);
        return timeStamp;

    }

    /** Get the blob Sha1 of the file in staging area. */
    public static String getBlobSha1(String addedFile) throws IOException {
        /* Get the sha1 of the added file in staging area. */
        String addedFileString = Utils.readContentsAsString(Utils.join(STAGING, addedFile));
        String addedFileSha1 = Utils.sha1(addedFileString);
//        /** Create the blob file in Blob directory with the same Sha1. */
//        File newBlob = Utils.join(BLOB, addedFileSha1);
//        newBlob.createNewFile();
        /* Copy the added file in staging area to the blob file. */
        Files.copy(Utils.join(STAGING, addedFile).toPath(), Utils.join(BLOB, addedFileSha1).toPath());
        return addedFileSha1;
    }

    /** CLear staging area. */
    public static void clearStaging() throws IOException {
        List<String> addedFiles = Utils.plainFilenamesIn(STAGING);
        for (String addedFile: addedFiles) {
            Utils.join(STAGING, addedFile).delete();
        }
    }

    /** CLear remove staging area. */
    public static void clearRMStaging() throws IOException {
        List<String> rmFiles = Utils.plainFilenamesIn(RMSTAGING);
        for (String rmFile: rmFiles) {
            Utils.restrictedDelete(Utils.join(RMSTAGING, rmFile));
        }
    }

    /** Remove the file from staging area and current commit. Removing from the current
     *  commit is done in next commit. */
    public static void removeFile(String fileName) throws IOException {
        int flag = 0; // flag for failure case.
        /* If the file is in staging area, delete it. */
        if (Utils.join(STAGING, fileName).exists()) {
            Utils.join(STAGING, fileName).delete();
            flag = 1;
        }
        /* If the file is in the current commit, add it to RMSTAGING for removing in
         * the next commit and remove if from the working directory if it is there. */
        if (fileInCurrentCommit(fileName)) {
            /* Create a file with the same name for removing in the next commit.
             * The file content doesn't matter. */
            Utils.join(RMSTAGING, fileName).createNewFile();
            if (Utils.join(CWD, fileName).exists()) {
                Utils.restrictedDelete(Utils.join(CWD, fileName));
            }
            flag = 1;
        }
        /* if the above two cases are both failed then print failure message. */
        if (flag == 0) {
            System.out.println("No reason to remove the file. ");
        }
    }

    /** Print out the commit history from HEAD commit. */
    public static void log() throws IOException {
        printLog(Utils.readContentsAsString(HEAD));
    }

    /** Print out the commit information till initial commit. Without considering merge.*/
    public static void printLog(String commitSha1) {
        Commit printCommit = Utils.readObject(Utils.join(COMMIT, commitSha1), Commit.class);
        System.out.println("===");
        System.out.println("commit " + commitSha1);
        System.out.println("Date: " + printCommit.getDate());
        System.out.println(printCommit.getMessage());
        System.out.println();
        if (printCommit.getParent() != null) {
            printLog(printCommit.getParent());
        }
    }

    /** Print out all the commits' information. */
    public static void globalLog() throws IOException {
        List<String> allCommits = Utils.plainFilenamesIn(COMMIT);
        for (String commitSha1 : allCommits) {
            if (!commitSha1.equals("tempOutFile")) {
                Commit printCommit = Utils.readObject(Utils.join(COMMIT, commitSha1), Commit.class);
                System.out.println("===");
                System.out.println("commit " + commitSha1);
                System.out.println("Date: " + printCommit.getDate());
                System.out.println(printCommit.getMessage());
                System.out.println();
            }
        }
    }
    /** Prints out the ids of all commits that have the given commit message */
    public static void find(String findMessage) throws IOException {
        List<String> allCommits = Utils.plainFilenamesIn(COMMIT);
        for (String commitSha1 : allCommits) {
            if (!commitSha1.equals("tempOutFile")) {
                Commit printCommit = Utils.readObject(Utils.join(COMMIT, commitSha1), Commit.class);
                if (printCommit.getMessage().equals(findMessage)) {
                    System.out.println(commitSha1);
                }
            }
        }
    }
    /** Print out the status information.*/
    public static void status() {

    }

    /** Takes the version of the file as it exists in the head commit and puts it in
     * the working directory, overwriting the version of the file that’s already
     * there if there is one. The new version of the file is not staged.*/
    public static void checkHEADCommit(String fileName) throws IOException {
        /* Get the sha1 of head commit. */
        String HeadSha1 = Utils.readContentsAsString(HEAD);
        checkCommitFile(HeadSha1, fileName);
    }

    /** Takes the version of the file as it exists in the commit with the given id,
     * and puts it in the working directory, overwriting the version of the file
     * that’s already there if there is one. The new version of the file is not
     * staged.*/
    public static void checkSpecificCommit(String commitId, String fileName) throws IOException {
        List<String> files = Utils.plainFilenamesIn(COMMIT);
        /* Check if the commit id exists*/
        if (files.contains(commitId)) {
            String commitSha1 = Utils.readContentsAsString(Utils.join(COMMIT, commitId));
            checkCommitFile(commitSha1, fileName);
        } else {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
    }

    /** Copy a file from a commit to CWD. If the file doesn't exist in that commit,
     * print error message and exit. */
    public static void checkCommitFile(String commitSha1, String fileName) throws IOException {
        /* Get the checked commit by its sha1. */
        Commit checkedCommit = Utils.readObject(Utils.join(COMMIT, commitSha1), Commit.class);
        if (checkedCommit.containFile(fileName)) {
            /* Get the file's blob sha1 saved in the checked commit. */
            String blobSha1 = checkedCommit.getBlob(fileName);
            /* Copy the blob content to the file. */
            Files.copy(Utils.join(BLOB, blobSha1).toPath(), Utils.join(CWD, fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
    }
}
