package no.nordicsemi.android.mesh;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class IvIndexTransitionTest {

    private static Calendar hoursAgo(final int hours) {
        final Calendar date = Calendar.getInstance();
        date.add(Calendar.HOUR, -hours);
        return date;
    }

    @Test
    public void testEnteringIvUpdateInProgressKeepsSequenceNumber() {
        final IvIndex last = new IvIndex(1, false, hoursAgo(200));
        final Calendar now = Calendar.getInstance();
        final IvIndex received = new IvIndex(2, true, now);

        final IvIndexTransition transition = IvIndexTransition.apply(last, received);

        // The transmit IV Index stays 1, so the sequence number must not be reset.
        assertFalse(transition.resetSequenceNumber);
        assertEquals(2, transition.ivIndex.getIvIndex());
        assertTrue(transition.ivIndex.isIvUpdateActive());
        assertSame(now, transition.ivIndex.getTransitionDate());
        assertFalse(transition.ivIndex.getIvRecoveryFlag());
    }

    @Test
    public void testLeavingIvUpdateInProgressResetsSequenceNumber() {
        final Calendar lastDate = hoursAgo(100);
        final IvIndex last = new IvIndex(2, true, lastDate);
        final Calendar now = Calendar.getInstance();
        final IvIndex received = new IvIndex(2, false, now);

        final IvIndexTransition transition = IvIndexTransition.apply(last, received);

        // The transmit IV Index goes from 1 to 2.
        assertTrue(transition.resetSequenceNumber);
        assertEquals(2, transition.ivIndex.getIvIndex());
        assertFalse(transition.ivIndex.isIvUpdateActive());
        // The 96-hour rule is counted from this transition.
        assertSame(now, transition.ivIndex.getTransitionDate());
        assertFalse(transition.ivIndex.getIvRecoveryFlag());
    }

    @Test
    public void testLastIvIndexIsNotModified() {
        final Calendar lastDate = hoursAgo(100);
        final IvIndex last = new IvIndex(2, true, lastDate);
        last.setIvRecoveryFlag(true);

        final IvIndexTransition transition = IvIndexTransition.apply(last, new IvIndex(2, false, Calendar.getInstance()));

        assertNotSame(last, transition.ivIndex);
        assertEquals(2, last.getIvIndex());
        assertTrue(last.isIvUpdateActive());
        assertSame(lastDate, last.getTransitionDate());
        assertTrue(last.getIvRecoveryFlag());
    }

    @Test
    public void testRepeatedStateKeepsLastIvIndex() {
        final Calendar lastDate = hoursAgo(10);
        final IvIndex last = new IvIndex(2, false, lastDate);
        last.setIvRecoveryFlag(true);

        final IvIndexTransition transition = IvIndexTransition.apply(last, new IvIndex(2, false, Calendar.getInstance()));

        // A beacon repeating the current state must not move the transition date,
        // nor clear the IV Recovery flag.
        assertSame(last, transition.ivIndex);
        assertSame(lastDate, transition.ivIndex.getTransitionDate());
        assertTrue(transition.ivIndex.getIvRecoveryFlag());
        assertFalse(transition.resetSequenceNumber);
    }

    @Test
    public void testIvRecoveryResetsSequenceNumberAndSetsTheFlag() {
        final IvIndex last = new IvIndex(1, false, hoursAgo(400));

        final IvIndexTransition transition = IvIndexTransition.apply(last, new IvIndex(3, false, Calendar.getInstance()));

        assertTrue(transition.resetSequenceNumber);
        assertEquals(3, transition.ivIndex.getIvIndex());
        assertTrue(transition.ivIndex.getIvRecoveryFlag());
    }

    @Test
    public void testIvRecoveryToIvUpdateInProgressDoesNotSetTheFlag() {
        final IvIndex last = new IvIndex(1, false, hoursAgo(400));

        final IvIndexTransition transition = IvIndexTransition.apply(last, new IvIndex(3, true, Calendar.getInstance()));

        // The transmit IV Index goes from 1 to 2.
        assertTrue(transition.resetSequenceNumber);
        assertFalse(transition.ivIndex.getIvRecoveryFlag());
    }

    @Test
    public void testNormalTransitionClearsTheIvRecoveryFlag() {
        final IvIndex last = new IvIndex(3, false, hoursAgo(100));
        last.setIvRecoveryFlag(true);

        final IvIndexTransition transition = IvIndexTransition.apply(last, new IvIndex(4, true, Calendar.getInstance()));

        assertFalse(transition.ivIndex.getIvRecoveryFlag());
    }
}
