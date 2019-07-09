package org.researchstack.skin.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SectionModel {

    @SerializedName("items")
    private List<Section> sections;

    public List<Section> getSections() {
        return sections;
    }

    public static class Section {
        @SerializedName("section_title")
        String title;

        @SerializedName("row_items")
        private List<SectionRow> items;

        public List<SectionRow> getItems() {
            return items;
        }

        public String getTitle() {
            return title;
        }
    }

    public static class SectionRow {
        @SerializedName("title")
        String title;

        @SerializedName("details")
        String details;

        @SerializedName("icon_image")
        String iconImage;

        @SerializedName("tint_color")
        String tintColor;


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public String getIconImage() {
            return iconImage;
        }

        public void setIconImage(String iconImage) {
            this.iconImage = iconImage;
        }

        public String getTintColor() {
            return tintColor;
        }

        public void setTintColor(String tintColor) {
            this.tintColor = tintColor;
        }
    }

}
