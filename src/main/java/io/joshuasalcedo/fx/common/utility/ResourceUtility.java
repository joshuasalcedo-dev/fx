/* SPDX-License-Identifier: MIT */

package io.joshuasalcedo.fx.common.utility;

import io.joshuasalcedo.fx.Launcher;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Objects;
import java.util.prefs.Preferences;

/**
 * Utility class for handling application resources and configuration.
 * 
 * <p>This class provides methods for loading resources from the classpath,
 * resolving resource paths, accessing system properties and environment variables,
 * and managing user preferences.</p>
 * 
 * <p>All methods in this class are static and the class cannot be instantiated.</p>
 * 
 * @since 1.0
 */
public final class ResourceUtility {

    /**
     * The base directory for application resources (classpath root).
     */
    public static final String BASE_PACKAGE = "/io/joshuasalcedo/fx/";

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ResourceUtility() {
        throw new AssertionError("ResourceUtility is a utility class and should not be instantiated");
    }

    /**
     * Gets a resource as an input stream.
     * 
     * <p>This method resolves the resource path and loads the resource as an input stream.
     * If the resource is not found, a NullPointerException is thrown with a descriptive message.</p>
     * 
     * @param resource the resource path to load
     * @return an input stream for the resource
     * @throws NullPointerException if the resource is not found or if the resource parameter is null
     */
    public static InputStream getResourceAsStream(String resource) {
        String path = resolve(resource);
        return Objects.requireNonNull(
            Launcher.class.getResourceAsStream(path),
            "Resource not found: " + path
        );
    }

    /**
     * Gets a resource as a URI.
     * 
     * <p>This method resolves the resource path and loads the resource as a URI.
     * If the resource is not found, a NullPointerException is thrown with a descriptive message.</p>
     * 
     * @param resource the resource path to load
     * @return a URI for the resource
     * @throws NullPointerException if the resource is not found or if the resource parameter is null
     */
    public static URI getResource(String resource) {
        String path = resolve(resource);
        URL url = Objects.requireNonNull(Launcher.class.getResource(path), 
                                        "Resource not found: " + path);
        return URI.create(url.toExternalForm());
    }

    /**
     * Gets a resource as a URL string.
     * 
     * <p>This method resolves the resource path and loads the resource as a URL string.
     * If the resource is not found, a NullPointerException is thrown with a descriptive message.</p>
     * 
     * @param resource the resource path to load
     * @return a URL string for the resource
     * @throws NullPointerException if the resource is not found or if the resource parameter is null
     */
    public static String getResourceAsString(String resource) {
        String path = resolve(resource);
        URL url = Objects.requireNonNull(Launcher.class.getResource(path), 
                                        "Resource not found: " + path);
        return url.toExternalForm();
    }

    /**
     * Resolves a resource path.
     * 
     * <p>If the resource path starts with a forward slash (/), it is treated as an absolute classpath path.
     * Otherwise, it is treated as a relative path and is resolved against the base package directory.</p>
     * 
     * @param resource the resource path to resolve
     * @return the resolved resource path
     * @throws NullPointerException if the resource parameter is null
     */
    public static String resolve(String resource) {
        Objects.requireNonNull(resource, "Resource path cannot be null");
        return resource.startsWith("/") ? resource : BASE_PACKAGE + resource;
    }

    /**
     * Gets a system property or environment variable.
     * 
     * <p>This method first tries to get the value from system properties.
     * If the property is not found, it tries to get the value from environment variables.</p>
     * 
     * @param propertyKey the system property key
     * @param envKey the environment variable key
     * @return the value of the property or environment variable, or null if neither is found
     */
    public static String getPropertyOrEnv(String propertyKey, String envKey) {
        return System.getProperty(propertyKey, System.getenv(envKey));
    }

    /**
     * Loads an FXML file and returns the root node.
     * 
     * <p>This method resolves the FXML resource path and loads the FXML file.
     * If the resource is not found or loading fails, a RuntimeException is thrown.</p>
     * 
     * @param <T> the type of the root node
     * @param fxmlPath the path to the FXML file
     * @return the loaded root node
     * @throws RuntimeException if the FXML file cannot be loaded
     */
    public static <T> T loadFxml(String fxmlPath) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(Launcher.class.getResource(resolve(fxmlPath)));
            return fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML file: " + fxmlPath, e);
        }
    }

    /**
     * Gets the user preferences node for the application.
     * 
     * @return the user preferences node
     */
    public static Preferences getPreferences() {
        return Preferences.userRoot().node("atlantafx");
    }
}
