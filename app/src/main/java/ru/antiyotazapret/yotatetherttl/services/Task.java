package ru.antiyotazapret.yotatetherttl.services;

import android.os.AsyncTask;

public abstract class Task<Parameters,Result> extends AsyncTask<Parameters,Void,Result> {

    private OnResult<Result> callback;


    private Exception exception;

    @SafeVarargs
    @Override
    protected final Result doInBackground(Parameters... params) {
        return action(params[0]);
    }

    @Override
    protected void onPostExecute(Result result) {
        if (callback != null) {
            if (result == null && exception != null) {
                callback.onError(exception);
            } else {
                callback.onResult(result);
            }
        }
    }


    abstract Result action(Parameters p);

    public Result doInForeground(Parameters parameters) {
        return action(parameters);
    }

    // welcome to java shit-generics world
    @SuppressWarnings("unchecked")
    public void doInBackground(Parameters parameters) {
        execute(parameters);
    }

    public Task<Parameters,Result> attach(OnResult<Result> callback) {
        this.callback = callback;
        return this;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }


    public interface OnResult<Result> {
        void onResult(Result r);
        void onError(Exception e);
    }
}
