package com.saebom.bulletinboard.global.web;

public final class RedirectUtils {

    private RedirectUtils() {}

    public static String safeReturnUrlOrDefault(String returnUrl, String defaultUrl) {

        if (returnUrl == null || returnUrl.isBlank()) return defaultUrl;

        if (!returnUrl.startsWith("/")) return defaultUrl;
        if (returnUrl.startsWith("//")) return defaultUrl;

        return returnUrl;
    }

    public static String buildTarget(String returnUrl, String defaultBaseUrl, Long resourceId) {
        String base = (returnUrl != null && !returnUrl.isBlank()) ? returnUrl : defaultBaseUrl;

        if (base.endsWith("/")) base = base.substring(0, base.length() - 1);

        return base + "/" + resourceId;
    }

    public static String redirectTo(String url) {
        return "redirect:" + url;
    }

}