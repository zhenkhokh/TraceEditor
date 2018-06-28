package ru.android.zheka.db;

import com.activeandroid.Configuration;
import com.activeandroid.content.ContentProvider;

import ru.android.zheka.db.Config;
import ru.android.zheka.db.Point;
import ru.android.zheka.db.Trace;

public class DatabaseContentProvider extends ContentProvider {

@Override
protected Configuration getConfiguration() {
    Configuration.Builder builder = new Configuration.Builder(getContext());
    builder.addModelClass(Config.class);
    builder.addModelClass(Trace.class);
    builder.addModelClass(Point.class);
    return builder.create();
 }
}

