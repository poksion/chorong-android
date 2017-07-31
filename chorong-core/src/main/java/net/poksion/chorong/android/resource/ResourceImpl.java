package net.poksion.chorong.android.resource;

import android.content.Context;

public class ResourceImpl {

    public static class String0 implements Resource.String0 {

        protected final Context applicationContext;

        public String0(Context applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Override
        public String read(int id) {
            return applicationContext.getString(id);
        }
    }

    public static class String1<T1> extends String0 implements Resource.String1<T1> {

        public String1(Context applicationContext) {
            super(applicationContext);
        }

        @Override
        public String read(int id, T1 formatIdx1) {
            return applicationContext.getString(id, formatIdx1);
        }
    }

    public static class String2<T1, T2> extends String0 implements Resource.String2<T1, T2> {

        public String2(Context applicationContext) {
            super(applicationContext);
        }

        @Override
        public String read(int id, T1 formatIdx1, T2 formatIdx2) {
            return applicationContext.getString(id, formatIdx1, formatIdx2);
        }
    }

    public static class String3<T1, T2, T3> extends String0 implements Resource.String3<T1, T2, T3> {

        public String3(Context applicationContext) {
            super(applicationContext);
        }

        @Override
        public String read(int id, T1 formatIdx1, T2 formatIdx2, T3 formatIdx3) {
            return applicationContext.getString(id, formatIdx1, formatIdx2, formatIdx3);
        }
    }

}
