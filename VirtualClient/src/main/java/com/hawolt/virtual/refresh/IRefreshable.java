package com.hawolt.virtual.refresh;

import java.util.List;

/**
 * Created: 25/08/2023 15:41
 * Author: Twitter @hawolt
 **/

public interface IRefreshable {
    List<ExceptionalRefreshable> getRefreshableList();

    void onRefreshException(Throwable throwable);

    void refresh();
}
