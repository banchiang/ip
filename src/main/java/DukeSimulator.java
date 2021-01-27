import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;


public class DukeSimulator {
    private final String line = "    ____________________________________________________________\n";
    private final String help = line + "     These are the proper format of Duke's commands:\n"
            + "      -help\n"
            + "      -todo (insert task name here)\n"
            + "      -deadline (insert task name here) /by (insert time here)\n"
            + "      -event (insert event name here) /at (insert time here)\n"
            + "      eg: 'deadline typing /by 1pm'\n"
            + line;
    private List<Task> taskList;

    public DukeSimulator() {
        taskList = new ArrayList<Task>();
    }

    public void greeting() {
//        String logo = " ____        _        \n"
//                + "|  _ \\ _   _| | _____ \n"
//                + "| | | | | | | |/ / _ \\\n"
//                + "| |_| | |_| |   <  __/\n"
//                + "|____/ \\__,_|_|\\_\\___|\n";
//        System.out.println("Hello from\n" + logo);
        String greeting = "     Hello! I'm Duke\n" + "     What can I do for you?\n";
        System.out.print(line + greeting + line);
    }

    public void bye() {
        String byeMessage = line + "     Bye. Hope to see you again soon!\n" + line;
        System.out.print(byeMessage);
    }

    public void run() {
        greeting();
        Scanner sc = new Scanner(System.in);
        String command;
        command = sc.nextLine();
        while(!command.equals("bye")) {
            processCmd(command);
            command = sc.nextLine();
        }
        bye();
    }

    private void processCmd(String command) {
        try {
            String[] parsedCommand = command.split(" ", 2);
            if(parsedCommand[0].equals("list")) {
                printList();
            } else if(parsedCommand[0].equals("done")) {
                doneTask(parsedCommand[1]);
            } else if(parsedCommand[0].equals("delete")) {
                deleteTask(parsedCommand[1]);
            } else {
                addTask(command);
            }
        } catch (DukeException e) {
            System.out.print(line + e.toString() + line);
        }
    }

    private void addTask(String command) throws DukeException {
        String[] parsedCommand = command.split(" ", 2);
        Task t;
        if (parsedCommand[0].equals("todo")) {
            t = toDoMaker(parsedCommand[1]);
        } else if (parsedCommand[0].equals("deadline")) {
            t = deadlineMaker(parsedCommand[1]);
        } else if (parsedCommand[0].equals("event")) {
            t = eventMaker(parsedCommand[1]);
        } else {
            throw new DukeWrongCommandException(parsedCommand[0]);
        }
        taskList.add(t);
        String taskCount =
                String.format("     Now you have %d task(s) in the list\n",
                        taskList.size());
        String addTask = line + "     Got it. I've added this task:\n"
                + "\t" + t.toString() + "\n" + taskCount + line;
        System.out.print(addTask);
    }

    private Task toDoMaker(String command) throws DukeMissingDescriptionException {
        if(command.equals("")) {
            throw new DukeMissingDescriptionException("todo");
        } else {
            return new ToDo(command);
        }
    }

    private Task deadlineMaker(String command) throws DukeWrongFormatException,
            DukeMissingDescriptionException {
        String[] parsedCmd = command.split(" /by ", 2);
        if(parsedCmd.length != 2) {
            throw new DukeWrongFormatException("deadline");
        } else if(parsedCmd[0].equals(" ") || parsedCmd[1].equals(" ")) {
            throw new DukeMissingDescriptionException("deadline");
        } else {
            try {
                LocalDateTime ldt = LocalDateTime.parse(parsedCmd[1],
                        DateTimeFormatter.ofPattern("yyyy-M-d Hmm"));
                return new Deadline(parsedCmd[0], ldt);
            } catch (DateTimeParseException e) {
                throw new DukeWrongFormatException("deadline");
            }
        }
    }

    private Task eventMaker(String command) throws DukeWrongFormatException,
            DukeMissingDescriptionException {
        String[] parsedCmd = command.split(" /at ", 2);
        if(parsedCmd.length != 2) {
            throw new DukeWrongFormatException("event1");
        } else if(parsedCmd[0].equals(" ") || parsedCmd[1].equals(" ")) {
            throw new DukeMissingDescriptionException("event");
        } else {
            try {
                String[] parsedDate = parsedCmd[1].split(" ");
                String date = parsedDate[0];
                String[] parsedTime = parsedDate[1].split("-");
                LocalDateTime ldtStart = LocalDateTime.parse(date + " " + parsedTime[0],
                        DateTimeFormatter.ofPattern("yyyy-M-d Hmm"));
                LocalDateTime ldtEnd = LocalDateTime.parse(date + " " + parsedTime[1],
                        DateTimeFormatter.ofPattern("yyyy-M-d Hmm"));
                return new Event(parsedCmd[0], ldtStart, ldtEnd);
            } catch (Exception e) {
                throw new DukeWrongFormatException("event");
            }
        }
    }

    private void printList() {
        int index = 1;
        System.out.print(line);
        for(Task t : taskList) {
            System.out.print(String.format("     %d. %s\n",
                    index++, t.toString()));
        }
        System.out.print(line);
    }

    public void doneTask(String s) {
        int taskNum = Integer.valueOf(s);
        Task t = taskList.get(taskNum - 1);
        t = t.finishTask();
        taskList.set(taskNum - 1, t);
        String statement = "     Nice! I've marked this task as done:\n"
                + String.format("\t%s\n", t.toString());
        System.out.print(line + statement + line);
    }

    public void deleteTask(String s) {
        int taskNum = Integer.valueOf(s);
        Task t = taskList.get(taskNum - 1);
        taskList.remove(taskNum - 1);
        String taskCount = String.format("     Now you have %d task(s) in the list\n",
                taskList.size());
        String deleteTask = line + "     Noted. I've removed this task:\n"
                + "\t" + t.toString() + "\n" + taskCount + line;
        System.out.print(deleteTask);
    }
}