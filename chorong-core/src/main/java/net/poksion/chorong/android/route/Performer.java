package net.poksion.chorong.android.route;

import android.os.Bundle;

public interface Performer<N> {
    void onNavigateTo(N to, Bundle bundle);
}
