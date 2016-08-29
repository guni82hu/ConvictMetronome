package hu.kts.cmetronome;

import com.google.android.gms.ads.AdRequest;

/**
 * To get the device id, filter logcat for the tag "ads" and there will be a similar row:
 * Use AdRequest.Builder.addTestDevice("XXXXXXXXXXXXXXXXXXXXXXXXX") to get test ads on this device.
 */
public final class AdMobTestDeviceFilteredBuilderFactory {

    private AdMobTestDeviceFilteredBuilderFactory() {};


    public static AdRequest.Builder get() {
        return new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("21A7F45D4BDEB3BCCD96A1057F1DB48A") //Oneplus One
                .addTestDevice("A60733E79D236E05846D3742AF9C6113") //Nexus 5
                .addTestDevice("3EEA5F0C49FB4B07428FD96F5998BFEB") //Alcatel POP C3
                ;
    }
}
