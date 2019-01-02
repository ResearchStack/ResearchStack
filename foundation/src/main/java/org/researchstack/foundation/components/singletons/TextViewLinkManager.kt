package org.researchstack.foundation.components.singletons

import android.content.Context
import org.researchstack.foundation.components.common.ui.views.TextViewLinkHandler
import org.researchstack.foundation.components.web.ui.activities.ViewWebDocumentActivity
import java.lang.RuntimeException

public abstract class TextViewLinkManager {

    companion object {

        private var _sharedManager: TextViewLinkManager? = LegacyTextViewLinkManager()

        fun setSharedManager(manager: TextViewLinkManager) {
            this._sharedManager = manager
        }

        fun getSharedManager(): TextViewLinkManager {
            val manager = this._sharedManager
            if (manager == null) {
                throw RuntimeException("Shared Manager instance is null. Make sure you configure this by calling TextViewLinkManager.init()")
            }
            return manager
        }

    }

    abstract fun getHandler(context: Context, title: String): TextViewLinkHandler

}

public class LegacyTextViewLinkManager: TextViewLinkManager() {

    public class LegacyTextViewLinkHandler(val context: Context, val title: String): TextViewLinkHandler() {
        override fun onLinkClick(url: String?) {
            val path = ResourcePathManager.getInstance().generateAbsolutePath(ResourcePathManager.Resource.TYPE_HTML, url)
            val intent = ViewWebDocumentActivity.newIntentForPath(
                    context,
                    title,
                    path)
            context.startActivity(intent)
        }
    }

    override fun getHandler(context: Context, title: String): TextViewLinkHandler {
        return LegacyTextViewLinkHandler(context, title)
    }
}