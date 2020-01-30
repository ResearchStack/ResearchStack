package org.researchstack.foundation.core.interfaces

interface UIStep : IStep {

    /**
     * Gets the string the title to display in the action bar (optional).
     *
     * @return the string for the title to display in the action bar
     */
    var title: String?

    /**
     * Additional text to display for the step in a localized string.
     * <p>
     * The additional text is displayed in a smaller font below <code>title</code>. If you need to
     * display a long question, it can work well to keep the title short and put the additional
     * content in the <code>text</code> property.
     *
     * @return
     */
    var text: String?

    /**
     * Gets the string the title to display in the action bar (optional).
     *
     * @return the string for the title to display in the action bar
     */
    var stepTitle: String?
}
