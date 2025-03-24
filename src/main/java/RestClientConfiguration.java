import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.client.RestClientSsl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {

    @Bean
    @Qualifier("attachment-client")
    public RestClient getRestClient(RestClientSsl ssl) {
        return RestClient.builder()
                .baseUrl("https://prod.mlt3.sll.se/api/archive/care/journal/admin/scan/filter/attachment/")
                .apply(ssl.fromBundle("mlt-truststore"))
                .build();
    }
}
