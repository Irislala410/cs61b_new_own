package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Ruo Liang
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
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
                Repository.setupPersistence();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                validateNumArgs(args, 2);
                Repository.checkInitial();
                Repository.add(args[1]);
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
                //
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
                break;
            case "checkout":
                //validateNumArgs has 3 situation.
                Repository.checkInitial();
                break;
            case "branch":
                //
                validateNumArgs(args, 2);
                Repository.checkInitial();
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
    public static void validateNumArgs(String[] args, int num){
        if (args.length != num){
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
