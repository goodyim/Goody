package im.goody.android.di.modules;

import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import im.goody.android.data.local.PreferencesManager;

@Module
public class LocalModule {
    @Provides
    @Singleton
    PreferencesManager providePreferencesManager(Context context) {
        return new PreferencesManager(context);
    }

    @Provides
    @Singleton
    FusedLocationProviderClient providerLocationClient(Context context) {
        return LocationServices.getFusedLocationProviderClient(context);
    }
}
