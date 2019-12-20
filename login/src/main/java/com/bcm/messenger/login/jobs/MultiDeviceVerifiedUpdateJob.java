package com.bcm.messenger.login.jobs;


import android.content.Context;
import android.util.Log;

import com.bcm.messenger.common.core.Address;
import com.bcm.messenger.common.database.repositories.IdentityRepo;
import com.bcm.messenger.common.jobs.ContextJob;
import com.bcm.messenger.common.preferences.TextSecurePreferences;

import org.whispersystems.jobqueue.JobParameters;
import org.whispersystems.jobqueue.requirements.NetworkRequirement;
import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.signalservice.api.crypto.UntrustedIdentityException;
import org.whispersystems.signalservice.api.messages.multidevice.VerifiedMessage;
import org.whispersystems.signalservice.api.push.exceptions.PushNetworkException;

import java.io.IOException;


public class MultiDeviceVerifiedUpdateJob extends ContextJob {

    private static final long serialVersionUID = 1L;

    private static final String TAG = MultiDeviceVerifiedUpdateJob.class.getSimpleName();

    private final String destination;
    private final byte[] identityKey;
    private final IdentityRepo.VerifiedStatus verifiedStatus;
    private final long timestamp;

    public MultiDeviceVerifiedUpdateJob(Context context, Address destination, IdentityKey identityKey, IdentityRepo.VerifiedStatus verifiedStatus) {
        super(context, JobParameters.newBuilder()
                .withRequirement(new NetworkRequirement(context))
                .withPersistence()
                .withGroupId("__MULTI_DEVICE_VERIFIED_UPDATE__")
                .create());

        this.destination = destination.serialize();
        this.identityKey = identityKey.serialize();
        this.verifiedStatus = verifiedStatus;
        this.timestamp = System.currentTimeMillis();

    }

    @Override
    public void onRun() throws IOException, UntrustedIdentityException {
        try {
            if (!TextSecurePreferences.isMultiDevice(context)) {
                Log.w(TAG, "Not multi device...");
                return;
            }

            if (destination == null) {
                Log.w(TAG, "No destination...");
                return;
            }

            Address canonicalDestination = Address.fromSerialized(destination);
            VerifiedMessage.VerifiedState verifiedState = getVerifiedState(verifiedStatus);
            VerifiedMessage verifiedMessage = new VerifiedMessage(canonicalDestination.serialize(), new IdentityKey(identityKey, 0), verifiedState, timestamp);

        } catch (InvalidKeyException e) {
            throw new IOException(e);
        }
    }

    private VerifiedMessage.VerifiedState getVerifiedState(IdentityRepo.VerifiedStatus status) {
        VerifiedMessage.VerifiedState verifiedState;

        switch (status) {
            case DEFAULT:
                verifiedState = VerifiedMessage.VerifiedState.DEFAULT;
                break;
            case VERIFIED:
                verifiedState = VerifiedMessage.VerifiedState.VERIFIED;
                break;
            case UNVERIFIED:
                verifiedState = VerifiedMessage.VerifiedState.UNVERIFIED;
                break;
            default:
                throw new AssertionError("Unknown status: " + verifiedStatus);
        }

        return verifiedState;
    }

    @Override
    public boolean onShouldRetry(Exception exception) {
        return exception instanceof PushNetworkException;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onCanceled() {

    }
}