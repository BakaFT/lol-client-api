package com.hawolt.virtual.leagueclient.refresh;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created: 22/05/2023 11:52
 * Author: Twitter @hawolt
 **/

public class RefreshGroup implements Iterable<Refreshable> {
    private final List<Refreshable> list = new LinkedList<>();

    public RefreshGroup(Refreshable... refreshable) {
        list.addAll(Arrays.asList(refreshable));
    }

    public void add(Refreshable... refreshable) {
        list.addAll(Arrays.asList(refreshable));
    }

    @NotNull
    @Override
    public Iterator<Refreshable> iterator() {
        return list.iterator();
    }
}
