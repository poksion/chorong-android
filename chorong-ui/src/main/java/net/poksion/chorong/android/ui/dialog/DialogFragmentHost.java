package net.poksion.chorong.android.ui.dialog;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

public interface DialogFragmentHost<T extends Enum<T>> {
    FragmentManager getSupportFragmentManager();
    DialogFragment newDialogFragment(T dialogType, String title, String message);
}
