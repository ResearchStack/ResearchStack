package co.touchlab.touchkit.rk.common.step;

import co.touchlab.touchkit.rk.dev.DevUtils;

public class InstructionStep extends Step
{

    private String detailText;

    private int imageResourceId;

    public InstructionStep(String identifier, String title, String detailText)
    {
        super(identifier, title);
        this.detailText = detailText;
    }

    @Override
    public Class getStepFragment()
    {
        DevUtils.throwUnsupportedOpException();
        return null;
    }

    public int getImageResourceId()
    {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId)
    {
        this.imageResourceId = imageResourceId;
    }

    public String getDetailText()
    {
        return detailText;
    }

    public void setDetailText(String detailText)
    {
        this.detailText = detailText;
    }
}
