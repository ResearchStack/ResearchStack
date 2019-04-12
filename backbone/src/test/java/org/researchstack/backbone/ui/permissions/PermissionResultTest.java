package org.researchstack.backbone.ui.permissions;

import android.Manifest;


import org.junit.Test;
import org.robolectric.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PermissionResultTest {

    private PermissionResult subject;

    @Test
    public void testContainsPermission() {
        String permission = Manifest.permission.CAMERA;

        subject = permissionsWith()
                .add(permission, true)
                .build();


        assertThat(subject.containsResult("not a real permission"), is(false));
        assertThat(subject.containsResult(permission), is(true));
    }

    @Test
    public void testIsGranted_isTrue() {
        String permission = Manifest.permission.CAMERA;

        subject = permissionsWith()
                .add(permission, true)
                .build();


        assertThat(subject.isGranted(permission), is(true));
    }

    @Test
    public void testIsGranted_isFalse() {
        String permission = Manifest.permission.CAMERA;

        subject = permissionsWith()
                .add(permission, false)
                .build();


        assertThat(subject.isGranted(permission), is(false));
    }

    private ResultBuilder permissionsWith() {
        return new ResultBuilder();
    }

    class ResultBuilder {
        List<Pair<String, Integer>> pairs = new ArrayList<>();

        public ResultBuilder add(String permission, boolean isGranted) {
            pairs.add(Pair.create(permission, isGranted? PERMISSION_GRANTED : PERMISSION_DENIED));
            return this;
        }

        public PermissionResult build() {
            String [] permissions = new String[pairs.size()];
            int [] grantResults = new int[pairs.size()];

            for(int i=0; i < permissions.length; i++) {
                permissions[i] = pairs.get(i).first;
                grantResults[i] = pairs.get(i).second;
            }

            return new PermissionResult(permissions, grantResults);
        }
    }
}
