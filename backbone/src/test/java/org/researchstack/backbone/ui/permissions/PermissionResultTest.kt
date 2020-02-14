package org.researchstack.backbone.ui.permissions

import android.Manifest
import android.content.pm.PackageManager
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test
import java.util.ArrayList

class PermissionResultTest {
    private var subject: PermissionResult? = null

    @Test
    fun testContainsPermission() {
        val permission = Manifest.permission.CAMERA
        subject = permissionsWith()
            .add(permission, true)
            .build()
        MatcherAssert.assertThat(subject!!.containsResult("not a real permission"), CoreMatchers.`is`(false))
        MatcherAssert.assertThat(subject!!.containsResult(permission), CoreMatchers.`is`(true))
    }

    @Test
    fun testIsGranted_isTrue() {
        val permission = Manifest.permission.CAMERA
        subject = permissionsWith()
            .add(permission, true)
            .build()
        MatcherAssert.assertThat(subject!!.isGranted(permission), CoreMatchers.`is`(true))
    }

    @Test
    fun testIsGranted_isFalse() {
        val permission = Manifest.permission.CAMERA
        subject = permissionsWith()
            .add(permission, false)
            .build()
        MatcherAssert.assertThat(subject!!.isGranted(permission), CoreMatchers.`is`(false))
    }

    private fun permissionsWith(): ResultBuilder {
        return ResultBuilder()
    }

    internal inner class ResultBuilder {
        var pairs: MutableList<Pair<String, Int>> = ArrayList()

        fun add(permission: String, isGranted: Boolean): ResultBuilder {
            pairs.add(Pair(permission, if (isGranted) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED))
            return this
        }

        fun build(): PermissionResult {
            val permissions = arrayOfNulls<String>(pairs.size)
            val grantResults = IntArray(pairs.size)

            for (i in permissions.indices) {
                permissions[i] = pairs[i].first
                grantResults[i] = pairs[i].second
            }

            return PermissionResult(permissions, grantResults)
        }
    }
}