package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Divya Sivanandan
 */
class Rotor {

    /**
     * A rotor named NAME whose permutation is given by PERM.
     */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _setting = 0;
        _rightRotor = null;
        _leftNoPawl = false;
    }

    /**
     * Return my name.
     */
    String name() {
        return _name;
    }

    /**
     * Return my alphabet.
     */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /**
     * Return my permutation.
     */
    Permutation permutation() {
        return _permutation;
    }

    /**
     * Return the size of my alphabet.
     */
    int size() {
        return _permutation.size();
    }

    /**
     * Return true iff I have a ratchet and can move.
     */
    boolean rotates() {
        return false;
    }

    /**
     * Return true iff I reflect.
     */
    boolean reflecting() {
        return false;
    }

    /**
     * Return my current setting.
     */
    int setting() {
        return _setting;
    }

    /**
     * Set setting() to POSN.
     */
    void set(int posn) {
        _setting = _permutation.wrap(posn);
    }

    /**
     * Set setting() to character CPOSN.
     */
    void set(char cposn) {
        Alphabet alphabet = _permutation.alphabet();
        _setting = _permutation.wrap(alphabet.toInt(cposn));
    }

    /**
     * Return the conversion of P (an integer in the range 0..size()-1)
     * according to my permutation.
     */
    int convertForward(int p) {
        int permuted = _permutation.permute(p + _setting);
        return _permutation.wrap((permuted - _setting));
    }

    /**
     * Return the conversion of E (an integer in the range 0..size()-1)
     * according to the inverse of my permutation.
     */
    int convertBackward(int e) {
        int inverted = _permutation.invert(e + _setting);
        return _permutation.wrap(inverted - _setting);
    }

    /**
     * Returns true iff I am positioned to allow the rotor to my left
     * to advance.
     */
    boolean atNotch() {
        return false;
    }

    /** Set the rotor R to the right of current rotor. */
    public void setRightRotor(Rotor r) {
        _rightRotor = r;
    }

    /** Get the rotor to the right of current rotor,
     * which returns RightRotor. */
    public Rotor getRightRotor() {
        return _rightRotor;
    }

    /** Determine if rotor should rotate in during the processing
     * of current character depending on the value of Y passed in. */
    void turn(int y) {
    }

    /** Sets if current rotor is the leftmost MovingRotor
     * in current sequence. */
    void setLeftPawlTrue() {
        _leftNoPawl = true;
    }

    /** Sets non-leftmost moving rotors to False. */
    void setLeftPawlFalse() {
        _leftNoPawl = false;
    }

    /** Returns TRUE if the rotor to its left has no pawl. */
    boolean getLeftPawl() {
        return _leftNoPawl;
    }

    /**
     * Advance me one position, if possible. By default, does nothing.
     */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /**
     * My name.
     */
    private final String _name;

    /**
     * The permutation implemented by this rotor in its 0 position.
     */
    private Permutation _permutation;

    /** Current rotor position. */
    private int _setting;

    /** Rotor to its right. */
    private Rotor _rightRotor;

    /** Determines if left rotor has a pawl. */
    private boolean _leftNoPawl;

}
