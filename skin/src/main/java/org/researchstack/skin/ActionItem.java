package org.researchstack.skin;

import android.view.MenuItem;

public class ActionItem {
    private int id;

    private int title;

    private int icon;

    private Class clazz;

    private int groupId;

    private int order;

    private int action;

    public ActionItem(ActionItemBuilder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.icon = builder.icon;
        this.clazz = builder.clazz;
        this.groupId = builder.groupId;
        this.order = builder.order;
        this.action = builder.action;
    }

    public int getId() {
        return id;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }

    public Class getClazz() {
        return clazz;
    }

    public int getOrder() {
        return order;
    }

    public int getAction() {
        return action;
    }


    public static class ActionItemBuilder {
        private int id;
        private int title;
        private int icon;
        private Class clazz;
        private int groupId;
        private int order;
        private int action = MenuItem.SHOW_AS_ACTION_IF_ROOM;

        public ActionItem build() {
            return new ActionItem(this);
        }

        public ActionItemBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public ActionItemBuilder setTitle(int title) {
            this.title = title;
            return this;
        }

        public ActionItemBuilder setIcon(int icon) {
            this.icon = icon;
            return this;
        }

        public ActionItemBuilder setGroupId(int groupId) {
            this.groupId = groupId;
            return this;
        }

        public ActionItemBuilder setOrder(int order) {
            this.order = order;
            return this;
        }

        public ActionItemBuilder setAction(int action) {
            this.action = action;
            return this;
        }

        public ActionItemBuilder setClass(Class clazz) {
            this.clazz = clazz;
            return this;
        }
    }
}
