package duke;

public class DukeWrongCommandException extends DukeException {
    private String command;

    public DukeWrongCommandException(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return String.format("     ☹ Sorry, '%s' is not a proper command for Duke.\n", command);
    }
}
