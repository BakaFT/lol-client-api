package com.hawolt.rman.io.downloader;

import com.hawolt.manifest.ManifestType;
import com.hawolt.rman.body.RMANFileBodyBundle;
import com.hawolt.rman.util.Hex;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created: 05/01/2023 14:09
 * Author: Twitter @hawolt
 **/

public class BundleDownloader {

    public static List<Bundle> download(ManifestType type, Set<RMANFileBodyBundle> bundles) {
        return bundles.parallelStream().map(bundle -> downloadBlocking(type, bundle)).collect(Collectors.toList());
    }

    private static Bundle downloadBlocking(ManifestType type, RMANFileBodyBundle rmanFileBodyBundle) {
        Bundle bundle = new Bundle(type.getBundleUrl(), String.join(".", Hex.from(rmanFileBodyBundle.getBundleId(), 16), "bundle"));
        bundle.download();
        return bundle;
    }
}
