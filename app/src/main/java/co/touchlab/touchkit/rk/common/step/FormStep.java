package co.touchlab.touchkit.rk.common.step;
import java.util.List;

import co.touchlab.touchkit.rk.ui.scene.GenericFormScene;

public class FormStep extends Step
{
    private List<GenericFormScene.FormItem> formItems;

    public FormStep(String identifier)
    {
        super(identifier);
    }

    public FormStep(String identifier, String title)
    {
        super(identifier, title);
    }

    public FormStep(String identifier, String title, String text)
    {
        super(identifier, title);
        setText(text);
    }

    public void setFormItems(List<GenericFormScene.FormItem> formItems)
    {
        this.formItems = formItems;
    }

    public List<GenericFormScene.FormItem> getFormItems()
    {
        return formItems;
    }
}
