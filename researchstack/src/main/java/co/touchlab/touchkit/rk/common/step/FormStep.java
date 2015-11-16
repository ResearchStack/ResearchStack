package co.touchlab.touchkit.rk.common.step;
import java.util.List;

import co.touchlab.touchkit.rk.ui.scene.FormScene;

public class FormStep extends Step
{
    private List<FormScene.FormItem> formItems;

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

    public void setFormItems(List<FormScene.FormItem> formItems)
    {
        this.formItems = formItems;
    }

    public List<FormScene.FormItem> getFormItems()
    {
        return formItems;
    }
}
