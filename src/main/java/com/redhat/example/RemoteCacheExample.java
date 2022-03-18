package com.redhat.example;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.impl.ConfigurationProperties;

import javax.security.auth.callback.*;
import javax.security.sasl.RealmCallback;

public class RemoteCacheExample {

    public static void main(String[] args){
        new RemoteCacheExample();
    }

    public RemoteCacheExample() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer()
                .host("127.0.0.1")
                .port(ConfigurationProperties.DEFAULT_HOTROD_PORT)
                .security()
                .authentication()
                .realm("default")
                .saslMechanism("DIGEST-SHA-256")
                .callbackHandler(new LoginHandler("admin", "changeme".toCharArray(), "default"));
        RemoteCacheManager cacheManager = new RemoteCacheManager(builder.build());
        RemoteCache<String, String> cache = cacheManager.getCache("mycache");
        System.out.println("[Before put] Statistics currentNumberOfEntries: " + cache.serverStatistics().getStatsMap().get("currentNumberOfEntries"));
        cache.put("firstKey", "firstValue");
        System.out.println("[After put] Statistics currentNumberOfEntries: " + cache.serverStatistics().getStatsMap().get("currentNumberOfEntries"));
        String value = cache.get("firstKey");
        System.out.println("Value of key 'firstKey' retrieved from remote cache: " + value);
        cache.remove("firstKey");
        System.out.println("[After remove] Statistics currentNumberOfEntries: " + cache.serverStatistics().getStatsMap().get("currentNumberOfEntries"));
    }

    class LoginHandler implements CallbackHandler {
        final private String login;
        final private char[] password;
        final private String realm;

        public LoginHandler(String login, char[] password, String realm) {
            this.login = login;
            this.password = password;
            this.realm = realm;
        }

        @Override
        public void handle(Callback[] callbacks) throws UnsupportedCallbackException {
            for (Callback callback : callbacks) {
                if (callback instanceof NameCallback) {
                    ((NameCallback) callback).setName(login);
                } else if (callback instanceof PasswordCallback) {
                    ((PasswordCallback) callback).setPassword(password);
                } else if (callback instanceof RealmCallback) {
                    ((RealmCallback) callback).setText(realm);
                } else {
                    throw new UnsupportedCallbackException(callback);
                }
            }
        }
    }
}
