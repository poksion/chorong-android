package net.poksion.chorong.android.resource;

public interface Resource {

    interface String0 {
        String read(int id);
    }

    interface String1<T1> {
        String read(int id, T1 formatIdx1);
    }

    interface String2<T1, T2> {
        String read(int id, T1 formatIdx1, T2 formatIdx2);
    }

    interface String3<T1, T2, T3> {
        String read(int id, T1 formatIdx1, T2 formatIdx2, T3 formatIdx3);
    }

}
