package com.hawolt.http.layer;

import com.hawolt.http.integrity.Diffuser;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created: 20/08/2023 00:04
 * Author: Twitter @hawolt
 **/

public interface IResponse {
    Pattern JWT_PATTERN = Pattern.compile("(ey(.*?))\\.([A-z0-9]+)\\.([A-z0-9-.]+)");
    String DEFAULT = "${REDACTED_USER_CRITICAL_VALUE}";

    static String translate(IResponse response) {
        StringBuilder builder = new StringBuilder()
                .append(System.lineSeparator())
                .append(response.method())
                .append(" ")
                .append(response.url())
                .append(" ")
                .append(response.code());
        Map<String, List<String>> requestHeaders = response.requestHeaders();
        translateHeaders(builder, requestHeaders);
        translateBody(builder, response.request());
        Map<String, List<String>> responseHeaders = response.headers();
        translateHeaders(builder, responseHeaders);
        boolean isNotAnImage = response.headers().containsKey("content-type") && response.headers().get("content-type").stream().anyMatch(o -> !o.startsWith("image"));
        if (isNotAnImage) translateBody(builder, response.request());
        return builder.toString();
    }

    static void translateBody(StringBuilder builder, byte[] b) {
        String body = new String(b);
        if (!Diffuser.PRIVACY_ENHANCEMENT) builder.append(body);
        else {
            String logSafeString = Diffuser.vaporize(body);
            Matcher matcher = JWT_PATTERN.matcher(logSafeString);
            while (matcher.find()) {
                logSafeString = logSafeString.replace(matcher.group(0), DEFAULT);
            }
            builder.append(System.lineSeparator()).append(logSafeString);
        }
    }

    static void translateHeaders(StringBuilder builder, Map<String, List<String>> headers) {
        for (String key : headers.keySet()) {
            List<String> collection = headers.get(key);
            for (String header : collection) {
                if (!Diffuser.PRIVACY_ENHANCEMENT) {
                    builder.append(System.lineSeparator()).append(key).append(": ").append(header);
                } else {
                    String logSafeString = header;
                    Matcher matcher = JWT_PATTERN.matcher(header);
                    while (matcher.find()) {
                        logSafeString = logSafeString.replace(matcher.group(0), DEFAULT);
                    }
                    builder.append(System.lineSeparator()).append(key).append(": ").append(logSafeString);
                }
            }
        }
    }

    Map<String, List<String>> requestHeaders();

    Map<String, List<String>> headers();

    String asString();

    byte[] response();

    byte[] request();

    String method();


    String url();

    int code();
}
