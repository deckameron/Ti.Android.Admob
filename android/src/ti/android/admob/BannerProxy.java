package ti.android.admob;

import android.app.Activity;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiLifecycle;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

@Kroll.proxy(creatableInModule = AdmobModule.class)
public class BannerProxy extends TiViewProxy implements TiLifecycle.OnLifecycleEvent {

    private BannerView banner;

    public BannerProxy() {
        super();
    }

    @Override
    public TiUIView createView(Activity activity) {
        banner = new BannerView(this);
        return banner;
    }

    // Handle creation options
    @Override
    public void handleCreationDict(KrollDict options) {
        super.handleCreationDict(options);
        if (options.containsKey("adUnitId")) {
            AdmobModule.BANNER_AD_UNIT_ID = options.getString("adUnitId");
        }
    }

    // DESTROY ADS
    @Kroll.method
    public void destoyAdViewAndCancelRequest() {
        banner.destroy();
    }

    @Kroll.method
    public void pause() {
        banner.pause();
    }

    @Kroll.method
    public void resume() {
        banner.resume();
    }

    // EVENTS
    @Override
    public void onDestroy(Activity activity) {
        banner.destroy();
    }
}
