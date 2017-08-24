package net.poksion.chorong.android.bundle;

public final class NullSafe {
    private static final java.lang.String DEFAULT_STRING = "";
    private static final java.lang.Integer DEFAULT_INTEGER = 0;
    private static final java.lang.Long DEFAULT_LONG = 0L;

    private NullSafe() {}

    static Object getNullSafeValue(Object object) {
        if (object instanceof Base) {
            return ((Base) object).getNullSafeValue();
        } else {
            return object;
        }
    }

    static abstract class Base {
        Object value;
        abstract Object getDefaultValue();

        Object getNullSafeValue() {
            if (value == null) {
                value = getDefaultValue();
            }

            return value;
        }
    }

    public static final class String extends Base {
        @Override
        Object getDefaultValue() {
            return DEFAULT_STRING;
        }

        public String set(java.lang.String value) {
            this.value = value;
            return this;
        }

        public java.lang.String get() {
            return (java.lang.String) getNullSafeValue();
        }
    }

    public static final class Integer extends Base {
        @Override
        Object getDefaultValue() {
            return DEFAULT_INTEGER;
        }

        public Integer set(java.lang.Integer value) {
            this.value = value;
            return this;
        }

        public java.lang.Integer get() {
            return (java.lang.Integer) getNullSafeValue();
        }
    }

    public static final class Long extends Base {
        @Override
        Object getDefaultValue() {
            return DEFAULT_LONG;
        }

        public Long set(java.lang.Long value) {
            this.value = value;
            return this;
        }

        public java.lang.Long get() {
            return (java.lang.Long) getNullSafeValue();
        }
    }

}
