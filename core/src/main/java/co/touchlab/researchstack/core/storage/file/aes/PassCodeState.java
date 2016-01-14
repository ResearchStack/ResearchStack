package co.touchlab.researchstack.core.storage.file.aes;
import rx.functions.Action2;
import rx.functions.Func1;

public class PassCodeState
{
    private String title;

    private Func1<String, Boolean> checkAction;

    private Action2<String, Exception> errorAction;

    public PassCodeState(String title, Func1<String, Boolean> checkAction, Action2<String, Exception> errorAction)
    {
        this.title = title;
        this.checkAction = checkAction;
        this.errorAction = errorAction;
    }

    public String getTitle()
    {
        return title;
    }

    public Func1<String, Boolean> getCheckAction() throws Exception
    {
        return checkAction;
    }

    public Action2<String, Exception> getErrorAction()
    {
        return errorAction;
    }
}
