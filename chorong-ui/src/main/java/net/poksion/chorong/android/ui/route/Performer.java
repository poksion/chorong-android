package net.poksion.chorong.android.ui.route;

import android.os.Bundle;

public interface Performer<N> {
    void onNavigateTo(N to, Bundle bundle);
}
