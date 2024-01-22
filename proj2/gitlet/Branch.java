package gitlet;

import java.io.Serializable;
import java.util.HashMap;

public class Branch implements Serializable {
    /**
     * List all the branch names and their corresponding commits.
     * key HEAD's value is active branch.
     * */
    public HashMap<String, String> branch;

    public Branch() {
        this.branch = new HashMap<>();
        this.branch.put("head", "master");
        this.branch.put("master", null);
    }

//    public void initializeBranch() {
//        this.branch.put("head", "master");
//        this.branch.put("master", null);
//    }
    /**
     * Create a new branch.*/
    public void createNewBranch(String newBranch){
        String currentCommit = Utils.readContentsAsString(Repository.HEAD);
        this.branch.put(newBranch, currentCommit);
    }
    /**
     * Write the branch into Branch file. */
    public void save() {
        Utils.writeObject(Repository.BRANCH, this);
    }

    public void update(String branchToUpdate, String commitSHA1PointedAt){
        this.branch.put(branchToUpdate, commitSHA1PointedAt);

    }

}
