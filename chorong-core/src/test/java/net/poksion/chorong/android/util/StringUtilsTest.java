package net.poksion.chorong.android.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class StringUtilsTest {
    @Test
    public void null_str_should_not_be_non_empty() {
        assertThat(StringUtils.isNonEmpty(null)).isFalse();
    }

    @Test
    public void empty_str_should_not_be_non_empty() {
        assertThat(StringUtils.isNonEmpty("")).isFalse();
    }

    @Test
    public void white_space_should_be_non_empty() {
        assertThat(StringUtils.isNonEmpty(" ")).isTrue();
    }
}
