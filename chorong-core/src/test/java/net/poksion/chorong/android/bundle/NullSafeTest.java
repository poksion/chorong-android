package net.poksion.chorong.android.bundle;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class NullSafeTest {

    @Test
    public void null_safe_string_should_return_empty_when_set_to_null() {
        NullSafe.String nullSafeString = new NullSafe.String();

        nullSafeString.set(null);
        assertThat(nullSafeString.get()).isEqualTo("");
    }

    @Test
    public void null_safe_integer_should_return_0_when_set_to_null() {
        NullSafe.Integer nullSafeInteger = new NullSafe.Integer();

        nullSafeInteger.set(null);
        assertThat(nullSafeInteger.get()).isEqualTo(0);
    }

    @Test
    public void null_safe_long_should_return_0L_when_set_to_null() {
        NullSafe.Long nullSafeLong = new NullSafe.Long();

        nullSafeLong.set(null);
        assertThat(nullSafeLong.get()).isEqualTo(0L);
    }

}
