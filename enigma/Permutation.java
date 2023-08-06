package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Divya Sivanandan
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = "";
        while (cycles.length() > 0) {
            cycles = cycles.trim();
            _cycles += cycles.charAt(0);
            cycles = cycles.substring(1);
        }

    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    public void addCycle(String cycle) {
        _cycles = cycle;
    }

    /** Returns the cycle used in current permutation. */
    public String getCycles() {
        return _cycles;
    }


    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int cycleIndex = _cycles.indexOf(_alphabet.toChar(wrap(p)));
        int aI = cycleIndex + 1;
        int sI = cycleIndex - 1;
        if (cycleIndex == -1) {
            return p;
        } else if ((_cycles.charAt(sI) == '(') && (_cycles.charAt(aI) == ')')) {
            return p;
        } else if (_cycles.charAt(aI) == ')') {
            int firstParen = sI;
            while (_cycles.charAt(firstParen) != '(') {
                firstParen -= 1;
            }
            return _alphabet.toInt(_cycles.charAt(firstParen + 1));
        } else {
            return _alphabet.toInt(_cycles.charAt(aI));
        }
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int cycleIndex = _cycles.indexOf(_alphabet.toChar(wrap(c)));
        int aI = cycleIndex + 1;
        int sI = cycleIndex - 1;
        if (cycleIndex == -1) {
            return c;
        } else if ((_cycles.charAt(sI) == '(') && (_cycles.charAt(aI) == ')')) {
            return c;
        } else if (_cycles.charAt(sI) == '(') {
            int lastParen = aI;
            while (_cycles.charAt(lastParen) != ')') {
                lastParen += 1;
            }
            return _alphabet.toInt(_cycles.charAt(lastParen - 1));
        } else {
            return _alphabet.toInt(_cycles.charAt(sI));
        }
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int index = _alphabet.toInt(p);
        int permuted = permute(index);
        return _alphabet.toChar(permuted);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int index = _alphabet.toInt(c);
        int inverted = invert(index);
        return _alphabet.toChar(inverted);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < size(); i++) {
            char curr = _alphabet.toChar(i);
            if (curr == permute(curr)) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Mappings of this permutation. */
    private String _cycles;
}
