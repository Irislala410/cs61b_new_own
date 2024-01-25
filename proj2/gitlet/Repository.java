package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static gitlet.Utils.*;
//import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
//import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


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
    /** The .BRANCH directory. */
    public static final File BRANCH_DIR = join(GITLET_DIR, "Branch");
    /** The .Branch File. */
    public static final File BRANCH = join(BRANCH_DIR, "Branch");




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
            BRANCH_DIR.mkdir();
            BRANCH.createNewFile();

            Commit initialCommit = new Commit(
                    "initial commit",
                    null,
                    "Thu Jan 1 00:00:00 1970 +0000"
            );
            //Create a branch.
            Branch initialBranch = new Branch();
//            initialBranch.initializeBranch();
            initialBranch.save();

            //write the commit into a file. Branch Updating is made in .saveCommit.
            initialCommit.saveCommit();


        } else {
            System.out.println("A Gitlet version-control system already exists "
                    + "in the current directory.");
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
        }
        /*If the same file exits in RMStaging folder then this command means to cancel
        * the removal and doesn't stage the file. */
        File removedFile = join(RMSTAGING, addFile);
        if (removedFile.exists()) {
            String removedFileStr = Utils.readContentsAsString(removedFile);
            String fileToAddStr = Utils.readContentsAsString(fileToAdd);
            if (removedFileStr.equals(fileToAddStr)) {
//                removedFile.delete();
//                join(RMSTAGING, addFile).delete();
//                removeFromRMSTAGING(addFile);
                Files.delete(removedFile.toPath());
                System.exit(0);
            }
        }
        if (!fileInCurrentCommit(addFile)) {
            // if file exist and not in current commit, stage it and remove it if
            // it is in removal area;
            Files.copy(
                    fileToAdd.toPath(),
                    join(STAGING, addFile).toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
            removeFromRMSTAGING(addFile);
        } else if (!sameContentInCurrentCommit(addFile)) {
            // if it is in current commit but not the same, stage it and remove
            // it if it is in removal area;
            Files.copy(
                    fileToAdd.toPath(),
                    Utils.join(STAGING, addFile).toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
            removeFromRMSTAGING(addFile);
        } else if (Utils.join(STAGING, addFile).exists()) { //this line was else
            // if (fileInStagingArea(addFile)){
            //if it is in current commit and the same, delete it from staging if
            // it is there.
            Utils.restrictedDelete(Utils.join(STAGING, addFile));
        }


    }
    /** Return if the file is in current commit. */
    public static boolean fileInCurrentCommit(String fileName) {
        // get the sha1 of the current commit from HEAD file
        String currentCommitSha1 = Utils.readContentsAsString(HEAD);
        // read commit object from Commit directory
        Commit currentCommit = Utils.readObject(
                Utils.join(COMMIT, currentCommitSha1),
                Commit.class
                );
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
        Commit currentCommit = Utils.readObject(
                Utils.join(COMMIT, currentCommitSha1),
                Commit.class
                );
        String commitFileSha1 = currentCommit.getBlob(fileName);
        return fileSha1.equals(commitFileSha1);
    }

    /** Return if the file is in staging area. */
    public static boolean fileInStagingArea(String fileName) {
        List<String> stagingFiles = plainFilenamesIn(STAGING);
        return stagingFiles.contains(fileName);
    }
    /** Create a new commit with the message the user provided. */
    public static void newCommit(String message) throws IOException {
        /* if staging area and remove staging area are both empty, print message and exit. */
        if (plainFilenamesIn(STAGING).isEmpty()
                && plainFilenamesIn(RMSTAGING).isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (message.length() == 0) {
            /* if message is empty, print message and exit. */
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        String parent = readContentsAsString(HEAD);
        String timeStamp = getTimeStamp();
        Commit newCommit = new Commit(message, parent, timeStamp);
        /* Read parent commit and get its saved files as new commit's saved files. */
        Commit parentCommit = readObject(join(COMMIT, parent), Commit.class);
//        newCommit.filenameBlob = parentCommit.filenameBlob; // in this way the two
//        pointers point to a same hashmap.
        newCommit.filenameBlob = new HashMap<>(parentCommit.filenameBlob);//??? parentCommit.filenameBlob seems a problem.
        /* Update the saved files with staging area. */
        List<String> addedFiles = plainFilenamesIn(STAGING);
        for (String addedFile: addedFiles) {
            String blobSha1 = getBlobSha1(addedFile);
            newCommit.filenameBlob.put(addedFile, blobSha1);
        }
        /* Remove the saved file in remove staging area. */
        List<String> rmFiles = plainFilenamesIn(RMSTAGING);
        for (String rmFile: rmFiles) {
            newCommit.filenameBlob.remove(rmFile);
        }

        newCommit.saveCommit();

        clearStaging();
        clearRMStaging();

//        newCommit.saveCommit();
//        System.out.println("9");
    }

    /** Get system time as String. */
    public static String getTimeStamp() {
        ZonedDateTime currentTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy Z");
        String timeStamp = currentTime.format(formatter);
        return timeStamp;

    }

    /** Get the blob Sha1 of the file in staging area. */
    public static String getBlobSha1(String addedFile) throws IOException {
        /* Get the sha1 of the added file in staging area. */
        String addedFileString = readContentsAsString(join(STAGING, addedFile));
        String addedFileSha1 = sha1(addedFileString);
//        /** Create the blob file in Blob directory with the same Sha1. */
//        File newBlob = Utils.join(BLOB, addedFileSha1);
//        newBlob.createNewFile();
        /* Copy the added file in staging area to the blob file. */
        Files.copy(
                join(STAGING, addedFile).toPath(),
                join(BLOB, addedFileSha1).toPath(),
                StandardCopyOption.REPLACE_EXISTING
        );
        return addedFileSha1;
    }

    /** Clear staging area. */
    public static void clearStaging() {
        List<String> addedFiles = plainFilenamesIn(STAGING);
        for (String addedFile: addedFiles) {
            join(STAGING, addedFile).delete();
        }
    }

    /** Clear remove staging area. */
    public static void clearRMStaging() {
        List<String> rmFiles = Utils.plainFilenamesIn(RMSTAGING);
        for (String rmFile: rmFiles) {
            Utils.join(RMSTAGING, rmFile).delete();
//            Utils.restrictedDelete(rmFile);
        }
    }

    /** Remove the file from staging area and current commit. Removing from the current
     *  commit is done in next commit. */
    public static void removeFile(String fileName) throws IOException {
        int flag = 0; // flag for failure case.
        /* If the file is in staging area, delete it. */
        if (join(STAGING, fileName).exists()) {
            join(STAGING, fileName).delete();
            flag = 1;
        }
        /* If the file is in the current commit, add it to RMSTAGING for removing in
         * the next commit and remove if from the working directory if it is there. */
        if (fileInCurrentCommit(fileName)) {
            /* Create a file with the same name for removing in the next commit.
             * The file content doesn't matter. */
            join(RMSTAGING, fileName).createNewFile();
            if (join(CWD, fileName).exists()) {
                restrictedDelete(join(CWD, fileName));
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
        Commit printCommit = readObject(join(COMMIT, commitSha1), Commit.class);
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
        List<String> allCommits = plainFilenamesIn(COMMIT);
        for (String commitSha1 : allCommits) {
            if (!commitSha1.equals("tempOutFile")) {
                Commit printCommit = readObject(Utils.join(COMMIT, commitSha1), Commit.class);
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
        List<String> allCommits = plainFilenamesIn(COMMIT);
        int flag = 0;
        for (String commitSha1 : allCommits) {
            if (!commitSha1.equals("tempOutFile")) {
                Commit printCommit = readObject(Utils.join(COMMIT, commitSha1), Commit.class);
                if (printCommit.getMessage().equals(findMessage)) {
                    System.out.println(commitSha1);
                    flag = 1;
                }
            }
        }
        if (flag == 0) {
            System.out.println("Found no commit with that message.");
        }
    }
    /** Print out the status information.*/
    public static void status() {
        /* Print out branches. */
        System.out.println("===" + " Branches " + "===");
        Branch branch = readObject(BRANCH, Branch.class);
        // Print active branch.
        String activeBranch = branch.branch.get("head");
        System.out.println("*" + activeBranch);
        //Print other branches.
        Set<String> branches = branch.branch.keySet();
        for (String aBranch : branches) {
            if (!aBranch.equals(activeBranch) && !aBranch.equals("head")) {
                System.out.println(aBranch);
            }
        }
        System.out.println();

        /* Print out staged files*/
        System.out.println("===" + " Staged Files " + "===");
        List<String> stagedFiles = plainFilenamesIn(STAGING);
        for (String stagedFile : stagedFiles) {
            System.out.println(stagedFile);
        }
        System.out.println();

        /* Print out Removed files*/
        System.out.println("===" + " Removed Files " + "===");
        List<String> removedFiles = plainFilenamesIn(RMSTAGING);
        for (String removedFile : removedFiles) {
            System.out.println(removedFile);
        }
        System.out.println();

        System.out.println("===" + " Modifications Not Staged For Commit " + "===");
        System.out.println();

        System.out.println("===" + " Untracked Files " + "===");
        System.out.println();
    }

    /** Takes the version of the file as it exists in the head commit and puts it in
     * the working directory, overwriting the version of the file that’s already
     * there if there is one. The new version of the file is not staged.*/
    public static void checkHEADCommit(String fileName) throws IOException {
        /* Get the sha1 of head commit. */
        String headSha1 = readContentsAsString(HEAD);
        checkCommitFile(headSha1, fileName);
    }

    /** Takes the version of the file as it exists in the commit with the given id,
     * and puts it in the working directory, overwriting the version of the file
     * that’s already there if there is one. The new version of the file is not
     * staged.*/
    public static void checkSpecificCommit(String commitId, String operand, String fileName)
            throws IOException {
        if (!operand.equals("--")) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        List<String> files = plainFilenamesIn(COMMIT);
        /* Check if the commit id exists*/
        if (files.contains(commitId)) {
            checkCommitFile(commitId, fileName);
        } else {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
    }

    /** Copy a file from a commit to CWD. If the file doesn't exist in that commit,
     * print error message and exit. */
    public static void checkCommitFile(String commitSha1, String fileName) throws IOException {
        /* Get the checked commit by its sha1. */
        Commit checkedCommit = readObject(join(COMMIT, commitSha1), Commit.class);
        if (checkedCommit.containFile(fileName)) {
            /* Get the file's blob sha1 saved in the checked commit. */
            String blobSha1 = checkedCommit.getBlob(fileName);
            /* Copy the blob content to the file. */
            Files.copy(
                    join(BLOB, blobSha1).toPath(),
                    join(CWD, fileName).toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } else {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
    }

    public static void newBranch(String newBranchName) {
        Branch branch = readObject(BRANCH, Branch.class);
        if (branch.branch.containsKey(newBranchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        branch.createNewBranch(newBranchName);
        branch.save();

    }
    /** Checkout to a head of specific branch. The given branch will now be
     * considered the current branch (HEAD). */
    public static void checkBranch(String branchName) throws IOException {
        Branch branch = readObject(BRANCH, Branch.class);
        String activeBranch = branch.branch.get("head");
        // The given branch doesn't exist.
        if (!branch.branch.containsKey(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        // The given branch is already the current branch.
        if (activeBranch.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }

        // Check if there is untracked files.
        if (untrackedFileExists()) {
            System.out.println("There is an untracked file in the way; delete it, "
                    + "or add and commit it first.");
            System.exit(0);
        }

        //Clear CWD and copy files from checked branch to it.
        clearFolder(CWD);
        String checkedCommitSHA1 = branch.branch.get(branchName);
        copyCommitFile(checkedCommitSHA1, CWD);
        // Update HEAD to the checked branch.
        branch.branch.put("head", branchName);
        branch.save();
        updateHEAD(checkedCommitSHA1);


    }
    /** Deletes the branch with the given name. */
    public static void rmBranch(String branchToRm) {
        Branch branch = readObject(BRANCH, Branch.class);
        if (!branch.branch.containsKey(branchToRm)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (branch.branch.get("head").equals(branchToRm)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        branch.branch.remove(branchToRm);
        branch.save();
    }

    /** Checks out all the files tracked by the given commit. Moves HEAD to that commit.
     * Clears staging area. */
    public static void reset(String commitId) throws IOException {
        List<String> commits = plainFilenamesIn(COMMIT);
        if (!commits.contains(commitId)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        if (untrackedFileExists()) {
            System.out.println("There is an untracked file in the way; delete it, or add "
                    + "and commit it first.");
            System.exit(0);
        }

        clearFolder(CWD);
        copyCommitFile(commitId, CWD);

        updateHEAD(commitId);



    }

    /** Check if there is untracked file in CWD which is not saved in current commit.*/
    public static boolean untrackedFileExists() {
        Branch branch = readObject(BRANCH, Branch.class);
        String activeBranch = branch.branch.get("head");
        String currCommitSHA1 = branch.branch.get(activeBranch);
        Commit currCommit = readObject(join(COMMIT, currCommitSHA1), Commit.class);
        List<String> currFiles = plainFilenamesIn(CWD);
        for (String currFile : currFiles) {
            // A file in CWD but not in current commit, then it is an untracked file.
            if (!currCommit.containFile(currFile)) {
                return true;
            }
        }
        return false;
    }

    /** Delete all the files in the given folder. */
    public static void clearFolder(File folderToClear) {
        List<String> deleteFiles = plainFilenamesIn(folderToClear);
        for (String deleteFile : deleteFiles) {
            join(folderToClear, deleteFile).delete();
        }
    }

    /** Copy all the files from a commit to a folder. */
    public static void copyCommitFile(String commitId, File destFolder) throws IOException {
        if (commitId != null) {
            Commit copyCommit = readObject(join(COMMIT, commitId), Commit.class);
            // Get checked files from the commit.
            Set<String> copyFiles = copyCommit.filenameBlob.keySet();
            for (String copyFile : copyFiles) {
                String fileSHA1 = copyCommit.getBlob(copyFile);
                byte[] copyFileByte = readContents(join(BLOB, fileSHA1));
                //Create new file in CWD and paste file from commit to it.
                File pasteFile = join(destFolder, copyFile);
                pasteFile.createNewFile();
                writeContents(pasteFile, copyFileByte);
            }
        }
    }

    /** Update the HEAD pointer. */
    public static void updateHEAD(String commitSha1) {
        writeContents(HEAD, commitSha1);
    }

    /** Update the active branch pointer. */
    public static void updateActiveBranch(String commitSha1) {
        Branch branch = readObject(BRANCH, Branch.class);
        String activeBranch = branch.branch.get("head");
        branch.update(activeBranch, commitSha1);
        branch.save();
    }
}
