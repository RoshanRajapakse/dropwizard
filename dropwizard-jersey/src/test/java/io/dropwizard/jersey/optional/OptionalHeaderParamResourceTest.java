package io.dropwizard.jersey.optional;

import io.dropwizard.jersey.AbstractJerseyTest;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.jersey.MyMessage;
import io.dropwizard.jersey.MyMessageParamConverterProvider;
import io.dropwizard.jersey.params.UUIDParam;
import org.junit.jupiter.api.Test;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class OptionalHeaderParamResourceTest extends AbstractJerseyTest {

    @Override
    protected Application configure() {
        return DropwizardResourceConfig.forTesting()
                .register(OptionalHeaderParamResource.class)
                .register(MyMessageParamConverterProvider.class);
    }

    @Test
    void shouldReturnDefaultMessageWhenMessageIsNotPresent() {
        String defaultMessage = "Default Message";
        String response = target("/optional/message").request().get(String.class);
        assertThat(response).isEqualTo(defaultMessage);
    }

    @Test
    void shouldReturnDefaultMessageWhenMessageIsBlank() {
        String defaultMessage = "Default Message";
        String response = target("/optional/message").request().header("message", "").get(String.class);
        assertThat(response).isEqualTo(defaultMessage);
    }

    @Test
    void shouldReturnMessageWhenMessageIsPresent() {
        String customMessage = "Custom Message";
        String response = target("/optional/message").request().header("message", customMessage).get(String.class);
        assertThat(response).isEqualTo(customMessage);
    }

    @Test
    void shouldReturnDefaultMessageWhenMyMessageIsNotPresent() {
        String defaultMessage = "My Default Message";
        String response = target("/optional/my-message").request().get(String.class);
        assertThat(response).isEqualTo(defaultMessage);
    }

    @Test
    void shouldReturnMyMessageWhenMyMessageIsPresent() {
        String myMessage = "My Message";
        String response = target("/optional/my-message").request().header("mymessage", myMessage).get(String.class);
        assertThat(response).isEqualTo(myMessage);
    }

    @Test
    void shouldThrowBadRequestExceptionWhenInvalidUUIDIsPresent() {
        String invalidUUID = "invalid-uuid";
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(() ->
            target("/optional/uuid").request().header("uuid", invalidUUID).get(String.class));
    }

    @Test
    void shouldReturnDefaultUUIDWhenUUIDIsNotPresent() {
        String defaultUUID = "d5672fa8-326b-40f6-bf71-d9dacf44bcdc";
        String response = target("/optional/uuid").request().get(String.class);
        assertThat(response).isEqualTo(defaultUUID);
    }

    @Test
    void shouldReturnUUIDWhenValidUUIDIsPresent() {
        String uuid = "fd94b00d-bd50-46b3-b42f-905a9c9e7d78";
        String response = target("/optional/uuid").request().header("uuid", uuid).get(String.class);
        assertThat(response).isEqualTo(uuid);
    }

    @Path("/optional")
    public static class OptionalHeaderParamResource {
        @GET
        @Path("/message")
        public String getMessage(@HeaderParam("message") Optional<String> message) {
            return message.orElse("Default Message");
        }

        @GET
        @Path("/my-message")
        public String getMyMessage(@HeaderParam("mymessage") Optional<MyMessage> myMessage) {
            return myMessage.orElse(new MyMessage("My Default Message")).getMessage();
        }

        @GET
        @Path("/uuid")
        public String getUUID(@HeaderParam("uuid") Optional<UUIDParam> uuid) {
            return uuid.orElse(new UUIDParam("d5672fa8-326b-40f6-bf71-d9dacf44bcdc")).get().toString();
        }
    }
}
