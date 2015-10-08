package co.touchlab.touchkit.rk.dev;
import android.os.Bundle;

@Deprecated @SuppressWarnings("all")
public class CodeStructure
{

    /**
     * First comes Static variables and static methods at the top of a class.
     */
    public static final String STATIC = "static";

    public static String getValues()
    {
        return "value";
    }

    /**
     * Enums!
     */
    public enum ExampleEnum
    {
        EnumOne, EnumTwo
    }

    /**
     * Then field variables.
     */
    public String stringOne = "One";

    public int intTwo = 2;

    /**
     * Next comes any overridden android methods in order that they are called (i.e.
     * onCreate > onStart > onResume)
     */
    //@Override
    public void onCreate(Bundle savedInstanceState)
    {
    }

    //@Override
    public void onStart()
    {
    }

    //@Override
    public void onResume()
    {
    }

    //@Override
    public void onPause()
    {
    }

    /**
     * Then comes your private methods
     */
    private int calculateValueForView()
    {
        return 100;
    }

    /**
     * Lastly, any inner classes or interfaces
     */
    public static class ExampleClass
    {
    }

    public interface ExampleInterface
    {
        void doExample();
    }

}
