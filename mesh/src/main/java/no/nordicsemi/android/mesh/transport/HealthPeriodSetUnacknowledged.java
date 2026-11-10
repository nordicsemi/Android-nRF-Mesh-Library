package no.nordicsemi.android.mesh.transport;

import androidx.annotation.NonNull;

import no.nordicsemi.android.mesh.ApplicationKey;
import no.nordicsemi.android.mesh.logger.MeshLogger;
import no.nordicsemi.android.mesh.opcodes.ApplicationMessageOpCodes;
import no.nordicsemi.android.mesh.utils.SecureUtils;

/**
 * To be used as a wrapper class when creating a HealthPeriodSetUnacknowledged message.
 */
public class HealthPeriodSetUnacknowledged extends ApplicationMessage {

    private static final String TAG = HealthPeriodSetUnacknowledged.class.getSimpleName();
    private static final int OP_CODE = ApplicationMessageOpCodes.HEALTH_PERIOD_SET_UNACKNOWLEDGED;

    private final int mFastPeriodDivisor;

    /**
     * Constructs HealthPeriodSetUnacknowledged message
     *
     * @param appKey            {@link ApplicationKey} key for this message
     * @param fastPeriodDivisor Fast Period Divisor, 0x00 - 0x0F. The Health Server publishes with a period of
     *                          Health Publish Period / (2 ^ fastPeriodDivisor) while there are active faults.
     * @throws IllegalArgumentException if the fast period divisor is out of range
     */
    public HealthPeriodSetUnacknowledged(@NonNull final ApplicationKey appKey, final int fastPeriodDivisor) throws IllegalArgumentException {
        super(appKey);
        HealthPeriodSet.checkFastPeriodDivisor(fastPeriodDivisor);
        this.mFastPeriodDivisor = fastPeriodDivisor;
        assembleMessageParameters();
    }

    @Override
    public int getOpCode() {
        return OP_CODE;
    }

    @Override
    void assembleMessageParameters() {
        MeshLogger.verbose(TAG, "Fast Period Divisor: " + mFastPeriodDivisor);
        mAid = SecureUtils.calculateK4(mAppKey.getKey());
        mParameters = new byte[]{(byte) mFastPeriodDivisor};
    }

    /**
     * Returns the fast period divisor
     */
    public int getFastPeriodDivisor() {
        return mFastPeriodDivisor;
    }
}
