package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Divya Sivanandan
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    public Permutation getNewPermutation(String cycles, Alphabet alphabet) {
        return new Permutation(cycles, alphabet);
    }

    public Alphabet getNewAlphabet(String chars) {
        return new Alphabet(chars);
    }

    public Alphabet getNewAlphabet() {
        return new Alphabet();
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }


    @Test
    public void testPermuteChar() {
        Permutation p = getNewPermutation(" (BACD)", getNewAlphabet("ABCD"));
        int permuted1 = p.permute('C');
        assertEquals('D', permuted1);
        int permuted2 = p.permute('A');
        assertEquals('C', permuted2);
        int permuted3 = p.permute('D');
        assertEquals('B', permuted3);

        Permutation q = getNewPermutation("(HIG)(NF) (L)",
                getNewAlphabet("HILFNGR"));
        assertEquals('G', q.permute('I'));
        assertEquals('L', q.permute('L'));
        assertEquals('F', q.permute('N'));
        assertEquals('N', q.permute('F'));
        assertEquals('R', q.permute('R'));

        Permutation r = getNewPermutation("(AELTPHQXRU) (IV)",
                getNewAlphabet());
        assertEquals('A', r.permute('U'));
        assertEquals('S', r.permute('S'));
        assertEquals('I', r.permute('V'));

    }

    @Test
    public void testPermuteInt() {
        Permutation p = getNewPermutation(" (BACD)", getNewAlphabet("ABCD"));
        int permuted1 = p.permute(2);
        assertEquals(3, permuted1);
        int permuted2 = p.permute(0);
        assertEquals(2, permuted2);
        int permuted3 = p.permute(3);
        assertEquals(1, permuted3);

        Permutation q = getNewPermutation("(HIG)(NF) (L)",
                getNewAlphabet("HILFNGR"));
        assertEquals(5, q.permute(1));
        assertEquals(2, q.permute(2));
        assertEquals(3, q.permute(4));
        assertEquals(4, q.permute(3));
        assertEquals(6, q.permute(6));
    }

    @Test
    public void testInvertChar() {
        Permutation p = getNewPermutation(" (BACD)", getNewAlphabet("ABCD"));
        int inverted1 = p.invert('C');
        assertEquals('A', inverted1);
        int inverted2 = p.invert('A');
        assertEquals('B', inverted2);
        int inverted3 = p.invert('B');
        assertEquals('D', inverted3);

        Permutation q = getNewPermutation("(HIG)(NF) (L)",
                getNewAlphabet("HILFNGR"));
        assertEquals('G', q.invert('H'));
        assertEquals('L', q.invert('L'));
        assertEquals('F', q.invert('N'));
        assertEquals('N', q.invert('F'));
        assertEquals('R', q.invert('R'));
    }

    @Test
    public void testInvertInt() {
        Permutation p = getNewPermutation(" (BACD)", getNewAlphabet("ABCD"));
        int inverted1 = p.invert(2);
        assertEquals(0, inverted1);
        int inverted2 = p.invert(0);
        assertEquals(1, inverted2);
        int inverted3 = p.invert(1);
        assertEquals(3, inverted3);
        int inverted4 = p.invert(3);
        assertEquals(2, inverted4);

        Permutation q = getNewPermutation("(HIG)(NF) (L)",
                getNewAlphabet("HILFNGR"));
        assertEquals(5, q.invert(0));
        assertEquals(2, q.invert(2));
        assertEquals(3, q.invert(4));
        assertEquals(4, q.invert(3));
        assertEquals(6, q.invert(6));
    }

    @Test
    public void testSize() {
        Permutation p = getNewPermutation(" (BACD)",
                getNewAlphabet("ABCD"));
        assertEquals(4, p.size());

        Permutation q = getNewPermutation("(HIG)(NF) (L)",
                getNewAlphabet("HILFNGR"));
        assertEquals(7, q.size());
    }

    @Test
    public void testDerangement() {
        Permutation p = getNewPermutation(" (BACD)", getNewAlphabet("ABCD"));
        assertTrue(p.derangement());

        Permutation q = getNewPermutation("(HIG)(NF) (L)",
                getNewAlphabet("HILFNGR"));
        assertFalse(q.derangement());
    }

}



