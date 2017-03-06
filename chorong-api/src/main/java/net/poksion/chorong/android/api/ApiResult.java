package net.poksion.chorong.android.api;

public class ApiResult<T> {
    public enum Error {
        Url,
        Service,

        Network,
        Auth,

        None
    }

    public T data;
    public Error error = Error.None;
}
