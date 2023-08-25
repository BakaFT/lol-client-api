package com.hawolt.virtual.misc;

import com.hawolt.authentication.Authorization;

import java.util.function.Supplier;

/**
 * Created: 25/08/2023 15:02
 * Author: Twitter @hawolt
 **/

public interface Authorizable {
    Supplier<Authorization> getAuthorizationSupplier();
}
