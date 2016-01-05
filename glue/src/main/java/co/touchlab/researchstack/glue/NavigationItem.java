package co.touchlab.researchstack.glue;
public class NavigationItem
{

    private int   id;
    private int   title;
    private int   icon;
    private Class clazz;
    private int   groupId;
    private int   order;

    public NavigationItem()
    {
    }

    public int getId()
    {
        return id;
    }

    public NavigationItem setId(int id)
    {
        this.id = id;
        return this;
    }

    public int getGroupId()
    {
        return groupId;
    }

    public NavigationItem setGroupId(int groupId)
    {
        this.groupId = groupId;
        return this;
    }

    public int getTitle()
    {
        return title;
    }

    public NavigationItem setTitle(int title)
    {
        this.title = title;
        return this;
    }

    public int getIcon()
    {
        return icon;
    }

    public NavigationItem setIcon(int icon)
    {
        this.icon = icon;
        return this;
    }

    public Class getClazz()
    {
        return clazz;
    }

    public NavigationItem setClass(Class clazz)
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
}
