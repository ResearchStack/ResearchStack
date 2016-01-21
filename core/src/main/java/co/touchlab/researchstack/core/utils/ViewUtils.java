package co.touchlab.researchstack.core.utils;
import android.text.InputFilter;

public class ViewUtils
{

    public static InputFilter[] addFilter(InputFilter[] filters, InputFilter filter)
    {
        if(filters == null || filters.length == 0)
        {
            return new InputFilter[] {filter};
        }
        else
        {
            // Overwrite value if the filter to be inserted already exists in the filters array
            for(int i = 0, size = filters.length; i < size; i++)
            {
                if(filters[i].getClass().isInstance(filter))
                {
                    filters[i] = filter;
                    return filters;
                }
            }

            // If our loop fails to find filter class type, create a new array and insert that
            // filter at the end of the array.
            int newSize = filters.length + 1;
            InputFilter newFilters[] = new InputFilter[newSize];
            System.arraycopy(filters, 0, newFilters, 0, filters.length);
            newFilters[newSize - 1] = filter;

            return newFilters;
        }
    }

}
