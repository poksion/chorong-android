package net.poksion.chorong.android.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.gdata.client.Service;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import org.junit.Test;

public class ApiTemplateTest {

    private enum Type {
        Auth,
        Service,
        UriSyntax,
        MalformedUri,
        Io
    }

    private static class ExceptionCommand implements ApiTemplate.Command< ApiResult<String> > {

        private final Type type;

        ExceptionCommand(Type type) {
            this.type = type;
        }

        @Override
        public ApiResult<String> onTry() throws ServiceException, URISyntaxException, IOException {
            switch(type) {
                case Auth:
                    throw new AuthenticationException("");
                case Service:
                    throw new ServiceException("");
                case UriSyntax:
                    throw new URISyntaxException("", "");
                case MalformedUri:
                    throw new MalformedURLException("");
                case Io:
                    throw new IOException("");
            }
            return null;
        }

        @Override
        public ApiResult<String> getEmptyResult() {
            return new ApiResult<>();
        }
    }

    @Test
    public void exception_should_be_converted_result_error() {
        ApiTemplate apiTemplate = new ApiTemplate();

        ExceptionCommand command = new ExceptionCommand(Type.Auth);
        ApiResult<String> result = apiTemplate.invoke(command);
        assertThat(result.error).isEqualTo(ApiResult.Error.Auth);

        command = new ExceptionCommand(Type.Service);
        result = apiTemplate.invoke(command);
        assertThat(result.error).isEqualTo(ApiResult.Error.Service);

        command = new ExceptionCommand(Type.UriSyntax);
        result = apiTemplate.invoke(command);
        assertThat(result.error).isEqualTo(ApiResult.Error.Url);

        command = new ExceptionCommand(Type.MalformedUri);
        result = apiTemplate.invoke(command);
        assertThat(result.error).isEqualTo(ApiResult.Error.Url);

        command = new ExceptionCommand(Type.Io);
        result = apiTemplate.invoke(command);
        assertThat(result.error).isEqualTo(ApiResult.Error.Network);
    }

    @Test
    public void create_service_is_proper_for_string_param_string_class() {
        ApiTemplate apiTemplate = new ApiTemplate();

        Service service = apiTemplate.createService(Service.class, null);
        assertThat(service).isNull();
    }

}
