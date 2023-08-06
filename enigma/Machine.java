package enigma;

import java.util.HashMap;
import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Divya Sivanandan
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            HashMap<String, Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _rotorsUsed = new ArrayList<>();
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (String rName : rotors) {
            _rotorsUsed.add(_allRotors.get(rName));
        }
        for (int i = 0; i < (_rotorsUsed.size() - 1); i++) {
            Rotor curr = _rotorsUsed.get(i);
            curr.setRightRotor(_rotorsUsed.get(i + 1));
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() < _numRotors - 1) {
            throw error("initial settings not given for all rotors");
        }
        int index = 0;
        for (int i = 1; index < setting.length(); i++) {
            _rotorsUsed.get(i).set(setting.charAt(index));
            _rotorsUsed.get(i).setLeftPawlFalse();
            index++;
        }
        _rotorsUsed.get(_rotorsUsed.size() - _pawls).setLeftPawlTrue();
        _rotorsUsed.get(_numRotors - 1).setRightRotor(null);
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugBoard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        for (int i = _numRotors - 1; i >= (_numRotors - _pawls); i -= 1) {
            Rotor curr = _rotorsUsed.get(i);
            if (curr.getRightRotor() == null) {
                curr.turn(1);
            } else if (curr.getRightRotor().atNotch()) {
                curr.turn(1);
            } else if (curr.atNotch() && !curr.getLeftPawl()) {
                curr.turn(1);
            } else {
                curr.turn(0);
            }
        }
        for (int i = _numRotors - 1; i >= (_numRotors - _pawls); i -= 1) {
            _rotorsUsed.get(i).advance();
        }
        int currChar = _plugBoard.permute(c);
        for (int i = (_rotorsUsed.size() - 1); i >= 0; i -= 1) {
            currChar = _rotorsUsed.get(i).convertForward(currChar);
        }
        for (int i = 1; i < _rotorsUsed.size(); i++) {
            currChar = _rotorsUsed.get(i).convertBackward(currChar);
        }
        currChar = _plugBoard.invert(currChar);
        return currChar;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String converted = "";
        msg = msg.trim();
        while (msg.length() > 0) {
            int convertedIndex = convert(_alphabet.toInt(msg.charAt(0)));
            converted += _alphabet.toChar(convertedIndex);
            msg = msg.substring(1).trim();
        }
        return converted;
    }

    /** Returns the arraylist of rotors currently inserted
     * and in use in Machine. */
    ArrayList<Rotor> getRotorsUsed() {
        return _rotorsUsed;
    }

    /** Returns TRUE is rotor with NAME is in _allRotors. */
    boolean checkRotorAvailable(String name) {
        return _allRotors.containsKey(name);
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotor slots in Machine. */
    private int _numRotors;

    /** All rotors available for use in Machine. */
    private HashMap<String, Rotor> _allRotors;

    /** Number of available rotor slots for rotors that can advance. */
    private int _pawls;

    /** Rotors that are inserted in Machine. */
    private ArrayList<Rotor> _rotorsUsed;

    /** Set up of plugboard. */
    private Permutation _plugBoard;

}
