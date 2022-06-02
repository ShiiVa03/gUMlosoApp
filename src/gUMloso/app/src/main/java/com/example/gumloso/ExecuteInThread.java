package com.example.gumloso;

import com.android.volley.toolbox.RequestFuture;

import java.util.concurrent.ExecutionException;

public class ExecuteInThread implements Runnable{
    volatile String response;
    RequestFuture<String> future;

    public ExecuteInThread(RequestFuture<String> future) {
        this.future = future;
    }

    public void run() {
        try {
            response = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
