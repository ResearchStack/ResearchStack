package co.touchlab.touchkit.rk.dev;
public class DevUtils
{

    public static void throwUnsupportedOpException()
    {
        throw new UnsupportedOperationException("Method Not Implemented");
    }

    public static void throwIllegalArgumentException()
    {
        throw new IllegalArgumentException("Invalid argument, cannot be null");
    }

}
