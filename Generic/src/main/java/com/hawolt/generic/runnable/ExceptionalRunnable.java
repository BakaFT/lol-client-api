package com.hawolt.generic.runnable;

/**
 * Created: 12/01/2023 23:27
 * Author: Twitter @hawolt
 **/

public abstract class ExceptionalRunnable implements Runnable {

    private final IExceptionCallback callback;

    public ExceptionalRunnable() {
        this(null);
    }

    public ExceptionalRunnable(IExceptionCallback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            execute();
            System.gc();
        } catch (Exception e) {
            if (callback != null) callback.onException(e);
        }
    }

    protected abstract void execute() throws Exception;
}
