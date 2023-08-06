package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Divya Sivanandan
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initially in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _permutation = this.permutation();
        _notches = notches;
    }

    /** MovingRotor can rotate if returns TRUE. */
    boolean rotates() {
        return true;
    }

    /** Determines if rotor advances based on the value of Y. */
    void turn(int y) {
        if (y == 1) {
            _rotates = true;
        } else {
            _rotates = false;
        }
    }


    /** If _setting is in _notches, returns true.*/
    boolean atNotch() {
        for (int i = 0; i < _notches.length(); i++) {
            int notch = _permutation.alphabet().toInt(_notches.charAt(i));
            if (notch == setting()) {
                return true;
            }
        }
        return false;
    }

    @Override
    void advance() {
        if (_rotates) {
            set(setting() + 1);
        } else if (getRightRotor() == null) {
            set(setting() + 1);
        }
    }

    /** notches of the MovingRotor if any.*/
    private String _notches;

    /**Permutation of the MovingRotor if any.*/
    private Permutation _permutation;

    /** Determines if it advances.*/
    private boolean _rotates;


}

