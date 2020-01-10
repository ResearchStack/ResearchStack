package org.researchstack.backbone.factory;

import android.support.annotation.NonNull;
import org.researchstack.backbone.utils.ObservableUtils;
import rx.Observable;

/**
 * This class encapsulates creating Observable.Transformer instances. This is needed to enable unit
 * tests, since the default transformer calls through to Schedulers.io() and Android.mainThread().
 */
public class ObservableTransformerFactory {
    /** Singleton instance. */
    public static final ObservableTransformerFactory INSTANCE = new ObservableTransformerFactory();

    /**
     * Private constructor, to enforce the singleton property. This prevents creating additional
     * instances, but the factory can still be mocked.
     */
    private ObservableTransformerFactory() {
    }

    /** Applies the default transformers. Used with Observable.compose(). */
    @NonNull
    public <T> Observable.Transformer<T, T> defaultTransformer() {
        return ObservableUtils.applyDefault();
    }
}
