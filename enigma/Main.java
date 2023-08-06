package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Divya Sivanandan
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine current = readConfig();
        if (!_input.hasNextLine()) {
            throw error("No input");
        }
        setUp(current, _input.nextLine());
        String nextLine;
        while (_input.hasNextLine()) {
            nextLine = _input.nextLine();
            if (nextLine.equals("")) {
                _output.println();
                nextLine = _input.nextLine();
            }
            if (nextLine.charAt(0) == '*') {
                setUp(current, nextLine);
            } else {
                printMessageLine(current.convert(nextLine));
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            if (!_config.hasNextLine()) {
                throw error("no config file");
            }
            String alpha = _config.nextLine();
            if (alpha.contains("(") | alpha.contains("*")) {
                throw error("invalid characters for alphabet");
            }
            _alphabet = new Alphabet(alpha);
            Scanner rInfo = new Scanner(_config.nextLine());
            if (rInfo.hasNext("[(a-z)|(A-Z)]*")) {
                throw error("invalid pawl and numrotor");
            }
            int numRotors = Integer.parseInt(rInfo.next());
            int pawls = Integer.parseInt(rInfo.next());
            while (_config.hasNextLine()) {
                Rotor current = readRotor();
                _rotors.put(current.name(), current);
            }
            if (_rotors.size() == 0 && numRotors < pawls) {
                throw error("invalid Machine setUp");
            }
            return new Machine(_alphabet, numRotors, pawls, _rotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            if (!_config.hasNextLine()) {
                throw error("no rotor descriptions");
            }
            String rotorDesc = _config.nextLine();
            if (rotorDesc.contains("*")) {
                throw error("improper rotor description");
            }
            Scanner currRotor = new Scanner(rotorDesc);
            String rName = currRotor.next();
            String rConfig = currRotor.next();
            String cycle = "";
            while (currRotor.hasNext()) {
                cycle += currRotor.next();
            }
            Permutation rPerm = new Permutation(cycle, _alphabet);
            if (rConfig.charAt(0) == 'R') {
                _last = new Reflector(rName, rPerm);
            } else if (rConfig.charAt(0) == 'N') {
                _last = new FixedRotor(rName, rPerm);
            } else if (rConfig.charAt(0) == 'M') {
                _last = new MovingRotor(rName, rPerm, rConfig.substring(1));
            } else if (rConfig.charAt(0) == '(') {
                String cycles = _last.permutation().getCycles();
                cycles += rotorDesc;
                _last.permutation().addCycle(cycles);
            } else {
                throw error("undefined rotor type");
            }
            return _last;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        if (settings.charAt(0) != '*') {
            throw error("wrong format for settings");
        }
        String[] split = settings.trim().split("\\s+");
        int permIndex = 0;
        String swaps = "";
        for (int i = 0; i < split.length; i++) {
            if (split[i].charAt(0) == '(') {
                permIndex = i;
                break;
            }
        }
        if (permIndex == 0) {
            String[] rNames = new String[split.length - 2];
            System.arraycopy(split, 1, rNames, 0, split.length - 2);
            String initialPosn = split[split.length - 1];
            if (M.getRotorsUsed().size() != 0) {
                M.getRotorsUsed().clear();
            }
            for (String name: rNames) {
                if (!M.checkRotorAvailable(name)) {
                    throw error("Rotor not available");
                }
            }
            M.insertRotors(rNames);
            M.setRotors(initialPosn);
        } else {
            String[] rNames = new String[permIndex - 2];
            System.arraycopy(split, 1, rNames, 0, permIndex - 2);
            String initialPosn = split[permIndex - 1];
            for (int i = permIndex; i < split.length; i++) {
                swaps += split[i];
            }
            if (M.getRotorsUsed().size() != 0) {
                M.getRotorsUsed().clear();
            }
            M.insertRotors(rNames);
            M.setRotors(initialPosn);
        }
        M.setPlugboard(new Permutation(swaps, _alphabet));
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String toPrint = "", sentence = "";
        msg = msg.trim();
        for (int i = 0; i < msg.length(); i++) {
            if (!_alphabet.contains(msg.charAt(i))) {
                throw error("invalid input");
            }
        }
        while (msg.length() > 5) {
            for (int count = 0; count < 5; count++) {
                toPrint += msg.charAt(0);
                msg = msg.substring(1).trim();
            }
            if (toPrint.length() == 5) {
                sentence += toPrint + " ";
                toPrint = "";
            }
        }
        for (int i = 0; msg.length() > 0; i++) {
            msg.trim();
            toPrint += msg.charAt(0);
            msg = msg.substring(1);
        }
        sentence += toPrint;
        _output.println(sentence);
    }


    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** All rotors available for use in Machine. */
    private HashMap<String, Rotor> _rotors = new HashMap<String, Rotor>();

    /** Last rotor read in readRotor(). */
    private Rotor _last;
}
