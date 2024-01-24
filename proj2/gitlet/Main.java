package gitlet;

import java.io.IOException;

import static gitlet.Repository.validateNumArgs;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Ruo Liang
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0){
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        try{
            switch(firstArg) {
                case "init":
                    validateNumArgs(args, 1);
                    Repository.setupPersistence();
                    break;
                case "add":
                    validateNumArgs(args, 2);
                    Repository.checkInitial();
                    try {
                        Repository.add(args[1]);
                    } catch (IOException e) {
                        System.exit(0);
                    }
                    break;
                case "commit":
                    //
                    validateNumArgs(args, 2);
                    Repository.checkInitial();
                    Repository.newCommit(args[1]);
                    break;
                case "rm":
                    validateNumArgs(args, 2);
                    Repository.checkInitial();
                    Repository.removeFile(args[1]);
                    break;
                case "log":
                    validateNumArgs(args, 1);
                    Repository.checkInitial();
                    Repository.log();
                    //
                    break;
                case "global-log":
                    validateNumArgs(args, 1);
                    Repository.checkInitial();
                    Repository.globalLog();
                    //
                    break;
                case "find":
                    //
                    validateNumArgs(args, 2);
                    Repository.checkInitial();
                    Repository.find(args[1]);

                    break;
                case "status":
                    //
                    validateNumArgs(args, 1);
                    Repository.checkInitial();
                    Repository.status();
                    break;
                case "checkout":
                    //validateNumArgs has 3 situation.
                    if (args.length != 2 && args.length != 3 && args.length != 4) {
                        System.out.println("Incorrect operands.");
                        System.exit(0);
                    }
                    Repository.checkInitial();
                    if (args.length == 3) {
                        Repository.checkHEADCommit(args[2]);
                    }
                    if (args.length == 4) {
                        Repository.checkSpecificCommit(args[1], args[2], args[3]);

                    }
                    if (args.length == 2) {
                        Repository.checkBranch(args[1]);
                    }
                    break;
                case "branch":
                    //
                    validateNumArgs(args, 2);
                    Repository.checkInitial();
                    Repository.newBranch(args[1]);
                    break;
                case "rm-branch":
                    //
                    validateNumArgs(args, 2);
                    Repository.checkInitial();
                    break;
                case "reset":
                    //
                    validateNumArgs(args, 2);
                    Repository.checkInitial();
                    break;
                case "merge":
                    //
                    validateNumArgs(args, 2);
                    Repository.checkInitial();
                    break;
                default:
                    System.out.println("No command with that name exists.");
                    System.exit(0);
            }

        } catch (IOException e) {
            System.exit(0);
        }
    }

}
