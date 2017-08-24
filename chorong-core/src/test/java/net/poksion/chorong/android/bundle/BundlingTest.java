package net.poksion.chorong.android.bundle;

import static org.assertj.core.api.Assertions.assertThat;

import android.os.Bundle;
import java.lang.reflect.Field;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class BundlingTest {
    public static class BundlingClass extends Bundling {

        @Member("explicit_id") String stringMember;
        @Member int primitiveIntMember;
        @Member Integer objectIntegerMember;

        @Override
        Object getValue(Field field, Object object) throws IllegalArgumentException, IllegalAccessException {
            return field.get(object);
        }

        @Override
        void setValue(Field filed, Object object, Object value) throws IllegalAccessException {
            filed.set(object, value);
        }
    }

    @Test
    public void test_bundle_behavior() {
        BundlingClass bundling = new BundlingClass();
        bundling.stringMember = "this is bundling string";
        bundling.primitiveIntMember = -1;
        bundling.objectIntegerMember = 1;

        Bundle bundle = bundling.toBundle();
        assertThat(bundle).isNotNull();
        assertThat(bundle.getString("explicit_id")).isEqualTo(bundling.stringMember);

        BundlingClass unBundling = new BundlingClass();
        unBundling.fromBundle(bundle);

        assertThat(unBundling.stringMember).isEqualTo(bundling.stringMember);
        assertThat(unBundling.primitiveIntMember).isEqualTo(bundling.primitiveIntMember);
        assertThat(unBundling.objectIntegerMember).isEqualTo(bundling.objectIntegerMember);
    }

}
