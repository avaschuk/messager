package com.andrei.messager;

public interface IDataSubscriber {
    void onDataLoaded(String data);
    void onLoadError();
}
