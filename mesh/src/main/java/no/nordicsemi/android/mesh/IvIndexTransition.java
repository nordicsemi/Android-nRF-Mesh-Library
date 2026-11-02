package no.nordicsemi.android.mesh;

import androidx.annotation.NonNull;

/**
 * The outcome of applying an authenticated Secure Network Beacon to the IV Index state
 * of the network.
 * <p>
 * The state of the network is the pair of IV Index and IV Update flag. When the beacon
 * changes it, a new {@link IvIndex} replaces the last one, carrying the date of the
 * transition and its own IV Recovery flag. When it does not, the last {@link IvIndex}
 * is kept as is, so that a beacon repeating the current state does not move the date
 * the 96-hour rule is counted from.
 * <p>
 * The last {@link IvIndex} is never modified, so it can be compared with the new one.
 */
final class IvIndexTransition {

    @NonNull
    final IvIndex ivIndex;
    final boolean resetSequenceNumber;

    private IvIndexTransition(@NonNull final IvIndex ivIndex, final boolean resetSequenceNumber) {
        this.ivIndex = ivIndex;
        this.resetSequenceNumber = resetSequenceNumber;
    }

    /**
     * Applies the IV Index received in a Secure Network Beacon to the last known one.
     * <p>
     * The beacon must already have been accepted by
     * {@link SecureNetworkBeacon#canOverwrite}.
     *
     * @param last     The last known IV Index of the network.
     * @param received The IV Index received in the Secure Network Beacon.
     * @return The IV Index to keep, and whether the local sequence numbers must be reset.
     */
    @NonNull
    static IvIndexTransition apply(@NonNull final IvIndex last, @NonNull final IvIndex received) {
        final boolean stateChanged = received.getIvIndex() != last.getIvIndex()
                || received.isIvUpdateActive() != last.isIvUpdateActive();
        if (!stateChanged) {
            return new IvIndexTransition(last, false);
        }

        final IvIndex next = new IvIndex(received.getIvIndex(), received.isIvUpdateActive(),
                received.getTransitionDate());
        // The IV Index was updated using IV Recovery procedure if it skipped a state
        // and ended in Normal Operation.
        next.setIvRecoveryFlag(next.getIvIndex() > last.getIvIndex() + 1 && !next.isIvUpdateActive());

        // If the IV Index used for transmitting messages effectively increased,
        // the Node shall reset the sequence number to 0x000000.
        return new IvIndexTransition(next, next.getTransmitIvIndex() > last.getTransmitIvIndex());
    }
}
