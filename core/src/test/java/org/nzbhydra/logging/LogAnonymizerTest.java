package org.nzbhydra.logging;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.nzbhydra.config.BaseConfig;
import org.nzbhydra.config.ConfigProvider;
import org.nzbhydra.config.UserAuthConfig;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class LogAnonymizerTest {

    @Mock
    private LogContentProvider logContentProviderMock;
    @Mock
    private ConfigProvider configProviderMock;

    @InjectMocks
    private LogAnonymizer testee = new LogAnonymizer();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        BaseConfig baseConfig = new BaseConfig();
        when(configProviderMock.getBaseConfig()).thenReturn(baseConfig);
        UserAuthConfig user = new UserAuthConfig();
        user.setUsername("someusername");
        baseConfig.getAuth().getUsers().add(user);
    }

    @Test
    public void shouldAnonymizeIPs() throws Exception {
        when(logContentProviderMock.getLog()).thenReturn("192.168.0.1 127.0.0.1 2001:db8:3:4:: 64:ff9b:: 2001:db8:a0b:12f0::1 2001:0db8:0a0b:12f0:0000:0000:0000:0001");

        String anonymized = testee.getAnonymizedLog();

        assertThat(anonymized, is("<IP> <IP> <IP> <IP> <IP>1 <IP>"));
    }

    @Test
    public void shouldAnonymizeUsernameFromUrl() throws Exception {
        when(logContentProviderMock.getLog()).thenReturn("http://arthur:miller@www.domain.com");

        String anonymized = testee.getAnonymizedLog();

        assertThat(anonymized, is("http://<USERNAME>:<PASSWORD>@www.domain.com"));
    }

    @Test
    public void shouldAnonymizeUsernameFromConfig() throws Exception {
        when(logContentProviderMock.getLog()).thenReturn("user=someusername USER:someusername username=someusername username:someusername");

        String anonymized = testee.getAnonymizedLog();

        assertThat(anonymized, is("user=<USERNAME> USER:<USERNAME> username=<USERNAME> username:<USERNAME>"));
    }


}