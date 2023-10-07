package com.hawolt.generic.data;

/**
 * Created: 20/01/2023 13:21
 * Author: Twitter @hawolt
 **/

public enum Platform {
    RU("RU"),
    EUW1("EUW"),
    JP1("JP"),
    EUN1("EUNE"),
    KR("KR"),
    TW2("TW2"),
    BR1("BR"),
    NA1("NA"),
    PH2("PH2"),
    VN2("VN2"),
    LA2("LA2"),
    OC1("OC1"),
    LA1("LA1"),
    TR1("TR"),
    SG2("SG2"),
    TH2("TH2"),
    PBE1("PBE1");
    final String friendlyName;

    Platform(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public static final Platform[] PLATFORMS = Platform.values();

    public static Platform findByFriendlyName(String name) {
        for (Platform platform : PLATFORMS) {
            if (platform.getFriendlyName().equalsIgnoreCase(name)) {
                return platform;
            }
        }
        return null;
    }
}
