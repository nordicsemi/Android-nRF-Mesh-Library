package no.nordicsemi.android.mesh.transport;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import no.nordicsemi.android.mesh.logger.MeshLogger;
import no.nordicsemi.android.mesh.opcodes.ApplicationMessageOpCodes;
import no.nordicsemi.android.mesh.utils.MeshAddress;

/**
 * To be used as a wrapper class for when creating the HealthPeriodStatus message.
 */
public class HealthPeriodStatus extends ApplicationStatusMessage {

    private static final String TAG = HealthPeriodStatus.class.getSimpleName();
    private static final int HEALTH_PERIOD_STATUS_LENGTH = 1;
    private static final int OP_CODE = ApplicationMessageOpCodes.HEALTH_PERIOD_STATUS;

    private Integer mFastPeriodDivisor;

    /**
     * Constructs HealthPeriodStatus message
     *
     * @param message access message
     */
    public HealthPeriodStatus(@NonNull AccessMessage message) {
        super(message);
        this.mMessage = message;
        this.mParameters = message.getParameters();
        parseStatusParameters();
    }

    @Override
    void parseStatusParameters() {
        MeshLogger.verbose(TAG, "Received health period status from: " + MeshAddress.formatAddress(mMessage.getSrc(), true));
        if (mParameters == null || mParameters.length != HEALTH_PERIOD_STATUS_LENGTH) {
            MeshLogger.error(TAG, "Invalid Health Period Status length");
            return;
        }
        final int fastPeriodDivisor = mParameters[0] & 0xFF;
        if (fastPeriodDivisor > HealthPeriodSet.FAST_PERIOD_DIVISOR_MAX) {
            MeshLogger.error(TAG, "Prohibited Fast Period Divisor: " + fastPeriodDivisor);
            return;
        }
        mFastPeriodDivisor = fastPeriodDivisor;
        MeshLogger.verbose(TAG, "Fast Period Divisor: " + mFastPeriodDivisor);
    }

    @Override
    public int getOpCode() {
        return OP_CODE;
    }

    /**
     * Returns the fast period divisor of the Health Server, 0x00 - 0x0F
     *
     * @return fast period divisor or null if the status is malformed or contains a prohibited value
     */
    @Nullable
    public Integer getFastPeriodDivisor() {
        return mFastPeriodDivisor;
    }
}
