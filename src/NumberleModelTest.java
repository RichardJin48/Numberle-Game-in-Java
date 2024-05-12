package src;
import static org.junit.jupiter.api.Assertions.*;

class NumberleModelTest {
    private INumberleModel instance;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        instance = new NumberleModel();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        instance = null;
    }

    /**
     * @invariant invariant();
     * Ensures that after calling this method, the remainingAttempts decreases 1,
     * and the invariant is maintained.
     * @requires checkValid == true;
     * @ensures remainingAttempts == MAX_ATTEMPTS - 1
     * && invariant();
     */
    @org.junit.jupiter.api.Test
    void processValidInput() {
        instance.setCheckValid(true);
        instance.initialize();
        instance.processInput("1+1*1=2");
        assertEquals(instance.getRemainingAttempts(), INumberleModel.MAX_ATTEMPTS - 1);
        assertTrue(instance.invariant());
    }

    /**
     * @invariant invariant();
     * Ensures that after calling this method, the remainingAttempts does not change,
     * and the invariant is maintained.
     * @requires checkValid == true;
     * @ensures remainingAttempts == MAX_ATTEMPTS
     * && !isGameWon()
     * && invariant();
     */
    @org.junit.jupiter.api.Test
    void processInvalidInput() {
        instance.setCheckValid(true);
        instance.initialize();
        instance.processInput("1+1*1=3");
        assertEquals(instance.getRemainingAttempts(), INumberleModel.MAX_ATTEMPTS);
        assertFalse(instance.isGameWon());
        assertTrue(instance.invariant());
    }

    /**
     * @invariant invariant();
     * Ensures that after calling this method, the remainingAttempts decreases 1,
     * the gameWon turns true, and the invariant is maintained.
     * @requires random == false;
     * @ensures remainingAttempts == MAX_ATTEMPTS - 1
     * && isGameWon()
     * && invariant();
     */
    @org.junit.jupiter.api.Test
    void processTargetInput() {
        instance.setRandom(false);
        instance.initialize();
        instance.processInput("2+3*2=8");
        assertEquals(instance.getRemainingAttempts(), INumberleModel.MAX_ATTEMPTS - 1);
        assertTrue(instance.isGameWon());
        assertTrue(instance.invariant());
    }
}
