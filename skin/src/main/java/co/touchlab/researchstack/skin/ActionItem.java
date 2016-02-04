package co.touchlab.researchstack.skin;
import android.view.MenuItem;

//TODO Builder methods on non-builder class is weird.
public class ActionItem
{
    private int   id;
    private int   title;
    private int   icon;
    private Class clazz;
    private int   groupId;
    private int   order;
    private int action = MenuItem.SHOW_AS_ACTION_IF_ROOM;

    public ActionItem()
    {
    }

    public int getId()
    {
        return id;
    }

    public ActionItem setId(int id)
    {
        this.id = id;
        return this;
    }

    public int getGroupId()
    {
        return groupId;
    }

    public ActionItem setGroupId(int groupId)
    {
        this.groupId = groupId;
        return this;
    }

    public int getTitle()
    {
        return title;
    }

    public ActionItem setTitle(int title)
    {
        this.title = title;
        return this;
    }

    public int getIcon()
    {
        return icon;
    }

    public ActionItem setIcon(int icon)
    {
        this.icon = icon;
        return this;
    }

    public Class getClazz()
    {
        return clazz;
    }

    public ActionItem setClass(Class clazz)
    {
        this.clazz = clazz;
        return this;
    }

    public int getOrder()
    {
        return order;
    }

    public void setOrder(int order)
    {
        this.order = order;
    }

    public int getAction()
    {
        return action;
    }

    public ActionItem setAction(int action)
    {
        this.action = action;
        return this;
    }
}
