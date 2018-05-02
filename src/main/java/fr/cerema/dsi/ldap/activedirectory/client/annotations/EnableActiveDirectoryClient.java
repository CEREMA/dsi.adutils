package fr.cerema.dsi.ldap.activedirectory.client.annotations;

import fr.cerema.dsi.ldap.activedirectory.client.DelegatingActiveDirectoryClientConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(DelegatingActiveDirectoryClientConfiguration.class)
public @interface EnableActiveDirectoryClient {
}
