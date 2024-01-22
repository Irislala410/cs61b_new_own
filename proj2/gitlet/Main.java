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
        // TODO: what if args is empty?
        if (args.length == 0){
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                validateNumArgs(args, 1);
                try {
                    Repository.setupPersistence();
                } catch (IOException e) {
                    System.exit(0);
                }
                break;
            case "add":
                // TODO: handle the `add [filename]` command
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
                try {
                    Repository.newCommit(args[1]);
                } catch (IOException e) {
                    System.exit(0);
                }

                break;
            case "rm":
                validateNumArgs(args, 2);
                Repository.checkInitial();
                try {
                    Repository.removeFile(args[1]);
                } catch (IOException e) {
                    System.exit(0);
                }

                //
                break;
            case "log":
                validateNumArgs(args, 1);
                Repository.checkInitial();
                try {
                    Repository.log();
                } catch (IOException e) {
                    System.exit(0);
                }
                //
                break;
            case "global-log":
                validateNumArgs(args, 1);
                Repository.checkInitial();
                try {
                    Repository.globalLog();
                } catch (IOException e) {
                    System.exit(0);
                }
                //
                break;
            case "find":
                //
                validateNumArgs(args, 2);
                Repository.checkInitial();
                try {
                    Repository.find(args[1]);
                } catch (IOException e) {
                    System.exit(0);
                }

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
                if (args.length == 3){
                    try {
                        Repository.checkHEADCommit(args[2]);
                    } catch (IOException e) {
                        System.exit(0);
                    }
                }
                if (args.length == 4){
                    try {
                        Repository.checkSpecificCommit(args[1], args[3]);
                    } catch (IOException e) {
                        System.exit(0);
                    }

                }
                Repository.checkInitial();
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
    }

}
