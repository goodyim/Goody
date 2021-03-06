package im.goody.android;

import android.app.Application;
import android.content.Context;

import com.squareup.picasso.Picasso;

import dagger.Provides;
import im.goody.android.di.components.DataComponent;
import im.goody.android.di.components.RootComponent;
import im.goody.android.di.modules.DataModule;
import im.goody.android.di.modules.RootModule;

public class App extends Application {
    private static Context appContext;
    private static DataComponent dataComponent;
    private static RootComponent rootComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        initDaggerComponents();

        Picasso.with(this).setLoggingEnabled(true);
    }

    public static Context getAppContext() {
        return appContext;
    }

    //region ================= DI =================

    public static DataComponent getDataComponent() {
        return dataComponent;
    }

    public static RootComponent getRootComponent() {
        return rootComponent;
    }

    private void initDaggerComponents() {
        AppComponent appComponent = DaggerApp_AppComponent.builder()
                .appModule(new AppModule(appContext))
                .build();
        dataComponent = appComponent.plus(new DataModule());
        rootComponent = dataComponent.plus(new RootModule());
    }

    @dagger.Module
    class AppModule {
        private Context context;

        AppModule(Context context) {
            this.context = context;
        }

        @Provides
        Context provideContext () {
            return context;
        }
    }

    @dagger.Component(modules = AppModule.class)
    interface AppComponent {
        Context getContext();
        DataComponent plus(DataModule module);
    }

    //endregion
}
