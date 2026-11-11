package no.nordicsemi.android.mesh.transport;

import androidx.annotation.NonNull;

import no.nordicsemi.android.mesh.ApplicationKey;
import no.nordicsemi.android.mesh.opcodes.ApplicationMessageOpCodes;
import no.nordicsemi.android.mesh.utils.SecureUtils;

/**
 * To be used as a wrapper class when creating a HealthPeriodGet message.
 */
public class HealthPeriodGet extends ApplicationMessage {

    private static final int OP_CODE = ApplicationMessageOpCodes.HEALTH_PERIOD_GET;

    /**
     * Constructs HealthPeriodGet message
     *
     * @param appKey {@link ApplicationKey} key for this message
     */
    public HealthPeriodGet(@NonNull final ApplicationKey appKey) {
        super(appKey);
        assembleMessageParameters();
    }

    @Override
    public int getOpCode() {
        return OP_CODE;
    }

    @Override
    void assembleMessageParameters() {
        mAid = SecureUtils.calculateK4(mAppKey.getKey());
    }
}
