package com.example.adrian.homecalc.database;

import java.util.List;

public interface DBCallback<T> {
    void onCallback(List<T> array);
}