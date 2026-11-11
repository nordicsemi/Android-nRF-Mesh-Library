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
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import no.nordicsemi.android.nrfmesh.R;

public class DialogFragmentIvUpdate extends DialogFragmentMessage {

    private static final String COMPLETE = "COMPLETE";

    public interface DialogFragmentIvUpdateListener {
        /**
         * Invoked when the IV Update is confirmed.
         *
         * @param complete True to complete the IV Update procedure, false to initiate it.
         */
        void onIvUpdateConfirmed(final boolean complete);
    }

    public static DialogFragmentIvUpdate newInstance(final String title, final String message, final boolean complete) {
        Bundle args = new Bundle();
        DialogFragmentIvUpdate fragment = new DialogFragmentIvUpdate();
        args.putString(TITLE, title);
        args.putString(MESSAGE, message);
        args.putBoolean(COMPLETE, complete);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final boolean complete = requireArguments().getBoolean(COMPLETE);
        alertDialogBuilder = new AlertDialog.Builder(requireActivity());
        alertDialogBuilder.setIcon(R.drawable.ic_index);
        alertDialogBuilder.setNegativeButton(getString(R.string.no), null);
        alertDialogBuilder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
            if (getParentFragment() != null)
                ((DialogFragmentIvUpdateListener) getParentFragment()).onIvUpdateConfirmed(complete);
        });

        return super.onCreateDialog(savedInstanceState);
    }
}
