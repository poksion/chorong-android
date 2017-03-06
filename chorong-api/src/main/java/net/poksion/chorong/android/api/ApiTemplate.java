package net.poksion.chorong.android.api;

import com.google.gdata.client.Service;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

class ApiTemplate {

    interface Command<T extends ApiResult<?>> {
        T onTry() throws ServiceException, URISyntaxException, IOException;
        T getEmptyResult();
    }

    void setBearerToken(Service service, String loginToken) {
        if (loginToken != null && loginToken.length() > 0) {
            service.setHeader("Authorization", "Bearer " + loginToken);
        }
    }

    <T extends ApiResult<?>> T invoke(Command<T> command) {
        try {
            return command.onTry();
        } catch(ServiceException e) {
            e.printStackTrace();

            T result = command.getEmptyResult();
            if (e instanceof AuthenticationException) {
                result.error = ApiResult.Error.Auth;
            } else {
                result.error = ApiResult.Error.Service;
            }
            return  result;

        } catch(URISyntaxException e) {
            e.printStackTrace();

            T result = command.getEmptyResult();
            result.error = ApiResult.Error.Url;
            return  result;

        } catch(IOException e) {
            e.printStackTrace();

            T result = command.getEmptyResult();
            if (e instanceof MalformedURLException) {
                result.error = ApiResult.Error.Url;
            } else {
                result.error = ApiResult.Error.Network;
            }
            return  result;
        }
    }
}
