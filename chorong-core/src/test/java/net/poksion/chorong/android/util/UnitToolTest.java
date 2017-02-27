package net.poksion.chorong.android.util;

import android.content.res.Resources;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.internal.Shadow;
import org.robolectric.shadows.ShadowResources;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class UnitToolTest {

    @Test
    public void dip_and_pixel_should_be_same_on_1_density() {
        Resources resources = Shadow.newInstanceOf(Resources.class);
        ShadowResources shadowResources = Shadows.shadowOf(resources);
        shadowResources.setDensity(1.0f);

        UnitTool unitTool = new UnitTool(resources);
        int pixel = unitTool.changeDipToPixel(10);
        assertThat(pixel).isEqualTo(10);
    }
}
