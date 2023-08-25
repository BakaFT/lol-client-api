package com.hawolt.virtual.refresh;

/**
 * Created: 25/08/2023 15:43
 * Author: Twitter @hawolt
 **/

public interface ExceptionalRefreshable {
    void refresh() throws Exception;
}
