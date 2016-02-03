package co.touchlab.researchstack.backbone.dev;
public class DevUtils
{

    public static void throwUnsupportedOpException()
    {
        throwUnsupportedOpException("Method Not Implemented");
    }

    public static void throwUnsupportedOpException(String msg)
    {
        throw new UnsupportedOperationException(msg);
    }

    public static void throwIllegalArgumentException()
    {
        throw new IllegalArgumentException("Invalid argument, cannot be null");
    }

}
