package org.researchstack.backbone.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by TheMDP on 1/9/17.
 */

public class ProfileInfoOptionsTest {

    @Test
    public void testToProfileInfoOption() {
        assertEquals(ProfileInfoOption.NAME, ProfileInfoOption.toProfileInfoOption("name"));
        assertEquals(ProfileInfoOption.EMAIL, ProfileInfoOption.toProfileInfoOption("email"));
        assertEquals(null, ProfileInfoOption.toProfileInfoOption("fake_name"));
    }
}
