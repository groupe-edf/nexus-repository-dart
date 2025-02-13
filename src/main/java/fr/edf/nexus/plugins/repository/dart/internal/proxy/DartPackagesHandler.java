package fr.edf.nexus.plugins.repository.dart.internal.proxy;

import fr.edf.nexus.plugins.repository.dart.AssetKind;
import fr.edf.nexus.plugins.repository.dart.internal.util.DartJsonProcessor;
import org.sonatype.nexus.repository.http.HttpResponses;
import org.sonatype.nexus.repository.http.HttpStatus;
import org.sonatype.nexus.repository.view.Context;
import org.sonatype.nexus.repository.view.Handler;
import org.sonatype.nexus.repository.view.Response;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public class DartPackagesHandler implements Handler {

    public static final String DO_NOT_REWRITE = "DartProviderHandler.doNotRewrite";

    private final DartJsonProcessor dartJsonProcessor;

    @Inject
    public DartPackagesHandler(DartJsonProcessor dartJsonProcessor) {
        this.dartJsonProcessor = dartJsonProcessor;
    }

    /**
     * Intercept requests for dart APIs calls and rewrite urls in json response
     *
     * @param context: {@link Context} of the request
     * @return modified response
     */
    @Nonnull
    @Override
    public Response handle(@Nonnull final Context context) throws Exception {
        Response response = context.proceed();
        AssetKind assetKind = context.getAttributes().require(AssetKind.class);

        if (!Boolean.parseBoolean(context.getRequest().getAttributes().get(DO_NOT_REWRITE, String.class))
                && response.getStatus().getCode() == HttpStatus.OK && response.getPayload() != null) {
            switch (assetKind) {
                case PACKAGES_METADATA:
                    response = HttpResponses
                            .ok(dartJsonProcessor.rewritePackagesJson(context.getRepository(), response.getPayload()));
                    break;
                case PACKAGE_METADATA:
                    response = HttpResponses
                            .ok(dartJsonProcessor.rewritePackageJson(context.getRepository(), response.getPayload()));
                    break;
                case PACKAGE_VERSION_METADATA:
                    response = HttpResponses
                            .ok(dartJsonProcessor.rewriteVersionJson(context.getRepository(), response.getPayload()));
                    break;
                default:
                    throw new IllegalStateException(
                            "Unexpected asset kind in DartPackagesHandler : " + assetKind + ". Cannot rewrite JSON");
            }
        }
        return response;
    }
}
