package co.touchlab.researchstack.glue;

public class DataResponse
{
    public boolean success;

    public String message;
    private int messages;

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public int getMessages()
    {
        return messages;
    }

    public void setMessages(int messages)
    {
        this.messages = messages;
    }
}
