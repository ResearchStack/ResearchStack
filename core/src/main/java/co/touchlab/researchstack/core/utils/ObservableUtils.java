package co.touchlab.researchstack.core.utils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ObservableUtils
{
    public static <T> Observable.Transformer<T, T> applyDefault()
    {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
