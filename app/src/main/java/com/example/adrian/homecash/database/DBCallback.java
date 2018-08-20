package com.example.adrian.homecash.database;

import java.util.List;

public interface DBCallback<T> {
    void onCallback(List<T> array);
}