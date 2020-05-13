package org.toomuch2learn.quarkus.minio.deployment;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ExtensionSslNativeSupportBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

/**
 * References for implementing Quarkus Extension
 *
 * https://quarkus.io/guides/writing-extensions
 * https://quarkus.io/guides/writing-native-applications-tips
 * https://github.com/oracle/graal/blob/master/substratevm/DYNAMIC_PROXY.md
 * https://docs.oracle.com/javase/8/docs/technotes/guides/reflection/proxy.html
 * https://www.adam-bien.com/roller/abien/entry/simplest_possible_quarkus_extension
 */
class QuarkusMinioProcessor {

    private static final String FEATURE = "minio";

    /**
     * The features listed reflect the types of extensions that are installed.
     * An extension declares its display name using a Build Step Processors method that produces a FeatureBuildItem
     */
    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    /**
     * A convenient way to tell Quarkus that the extension requires SSL and it should be enabled during native image build.
     * When using this feature, we need to add your extension to the list of extensions that offer SSL support automatically on the native and ssl guide
     * - https://github.com/quarkusio/quarkus/blob/master/docs/src/main/asciidoc/native-and-ssl.adoc
     */
    @BuildStep
    ExtensionSslNativeSupportBuildItem activateSslNativeSupport() {
        return new ExtensionSslNativeSupportBuildItem(FEATURE);
    }

    /**
     * Registers a class for reflection in Substrate. Constructors are always registered, while methods and fields are optional.
     */
    @BuildStep
    ReflectiveClassBuildItem reflective() {
        return new ReflectiveClassBuildItem(true, true,
            io.minio.messages.LocationConstraint.class.getCanonicalName(),
            io.minio.messages.InitiateMultipartUploadResult.class.getCanonicalName(),
            io.minio.messages.ErrorResponse.class.getCanonicalName(),

            // getting Canonical name does not include $ rather includes . which is failing in native build run
            "io.minio.ErrorCode$ErrorCodeConverter",
            io.minio.messages.CompleteMultipartUpload.class.getCanonicalName(),
            io.minio.messages.Part.class.getCanonicalName(),

            // XML Parser configurations
            "org.simpleframework.xml.core.TextLabel",
            "org.simpleframework.xml.core.ElementLabel",
            "org.simpleframework.xml.core.ElementListLabel");
    }

    /**
     * A class that will be initialized at runtime rather than build time.
     * This will cause the build to fail if the class is initialized as part of the native executable build process, so care must be taken.
     * https://quarkus.io/guides/writing-native-applications-tips#delay-class-initialization
     * https://github.com/oracle/graal/blob/master/substratevm/CLASS-INITIALIZATION.md
     * https://medium.com/graalvm/understanding-class-initialization-in-graalvm-native-image-generation-d765b7e4d6ed
     * https://medium.com/graalvm/updates-on-class-initialization-in-graalvm-native-image-generation-c61faca461f7
     *
     @BuildStep
     RuntimeInitializedClassBuildItem runtime() {
        return new RuntimeInitializedClassBuildItem(CryptoConfiguration.class.getCanonicalName());
     }*/

    /**
     * Quarkus allows extensions authors to register a NativeImageProxyDefinitionBuildItem
     * to manage proxy classes
     * https://docs.oracle.com/javase/8/docs/technotes/guides/reflection/proxy.html
     * https://www.baeldung.com/java-dynamic-proxies
     * https://medium.com/@mathiasdpunkt/ease-creation-of-graalvm-native-images-using-assisted-configuration-68a86dea07c7
     *
     @BuildStep
     NativeImageProxyDefinitionBuildItem proxies() {
         return new NativeImageProxyDefinitionBuildItem(
             "org.apache.http.conn.HttpClientConnectionManager",
             "org.apache.http.pool.ConnPoolControl",
             "com.amazonaws.http.conn.Wrapped");
     }*/

    /**
     * Includes static resources into the native executable.
     * https://quarkus.io/guides/writing-native-applications-tips#including-resources-2
     * https://github.com/oracle/graal/blob/master/substratevm/RESOURCES.md
     @BuildStep
     NativeImageResourceBuildItem resources() {
         //new NativeImageResourceBuildItem("com/amazonaws/partitions/endpoints.json"));

        //mime.types
         return new NativeImageResourceBuildItem("com/amazonaws/partitions/endpoints.json", "mime.types");
     }*/
}
