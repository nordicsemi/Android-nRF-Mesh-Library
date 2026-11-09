/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package no.nordicsemi.android.nrfmesh.dialog;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import no.nordicsemi.android.nrfmesh.databinding.DialogNewAppBinding;

/**
 * Asks the user to switch to the new, Kotlin based nRF Mesh app.
 */
public class DialogFragmentNewApp extends DialogFragment {
    public static final String TAG = "DialogFragmentNewApp";
    private static final String PREFS_NAME = "new_app_dialog";
    private static final String PREF_DONT_SHOW_NEW_APP_DIALOG = "PREF_DONT_SHOW_NEW_APP_DIALOG";
    private static final String NEW_APP_PACKAGE = "no.nordicsemi.android.nrfmesh";

    public static DialogFragmentNewApp newInstance() {
        return new DialogFragmentNewApp();
    }

    /**
     * Returns true unless the user chose "Don't show again".
     */
    public static boolean shouldShow(@NonNull final Context context) {
        return !getPreferences(context)
                .getBoolean(PREF_DONT_SHOW_NEW_APP_DIALOG, false);
    }

    private static SharedPreferences getPreferences(@NonNull final Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final DialogNewAppBinding binding = DialogNewAppBinding.inflate(getLayoutInflater());
        final AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(binding.getRoot())
                .create();
        binding.actionGet.setOnClickListener(v -> {
            openPlayStore(requireContext());
            dismiss();
        });
        binding.actionLater.setOnClickListener(v -> dismiss());
        binding.actionDontShowAgain.setOnClickListener(v -> {
            getPreferences(requireContext()).edit().putBoolean(PREF_DONT_SHOW_NEW_APP_DIALOG, true).apply();
            dismiss();
        });
        return dialog;
    }

    /**
     * Opens the Play Store listing of the new nRF Mesh app.
     */
    public static void openPlayStore(@NonNull final Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + NEW_APP_PACKAGE)));
        } catch (final ActivityNotFoundException e) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + NEW_APP_PACKAGE)));
            } catch (final ActivityNotFoundException ignored) {
                // No Play Store or browser available.
            }
        }
    }
}
