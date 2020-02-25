package org.researchstack.backbone.step;

import org.junit.Test;
import org.researchstack.backbone.R;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

public class StepTest {

    private static final String IDENTIFIER = "identifier";
    private static final String TITLE = "title";

    @Test
    public void testClone() throws CloneNotSupportedException {
        final Step subject = makeStep();
        final Step clonedSubject = (Step) subject.clone();

        assertNotSame(subject, clonedSubject);
        assertEquals(subject, clonedSubject);

        // LayoutClass is cloned by reference; this is acceptable for the use-case.
        assertSame(subject.getStepLayoutClass(), clonedSubject.getStepLayoutClass());
        assertEquals(subject.getStepLayoutClass(), clonedSubject.getStepLayoutClass());

        // Ensure Strings are the same (primitives get cloned by value, no need to test them)
        assertEquals(subject.getText(), clonedSubject.getText());
        assertEquals(subject.getIdentifier(), clonedSubject.getIdentifier());
        assertEquals(subject.getQuestion(), clonedSubject.getQuestion());
        assertEquals(subject.getStepTitle(), clonedSubject.getStepTitle());
    }

    @Test
    public void test_constructors_setValues() {

        Step subject = makeStep();
        assertEquals(IDENTIFIER, subject.getIdentifier());
        assertEquals(TITLE, subject.getTitle());

        final String newIdentifier = IDENTIFIER + "X";
        final String newTitle = TITLE + "X";

        subject = new Step(newIdentifier);
        subject.setTitle(newTitle);

        assertEquals(newIdentifier, subject.getIdentifier());
        assertEquals(newTitle, subject.getTitle());
    }

    @Test
    public void test_equalsAndHashCode_onlyUseIdentifier() {
        // The design of the Step class only uses Identifier as the comparison/hash field, this
        // test is here to ensure that if that changes, this test fails; if the updated equals
        // implementation is valid, then update this test to reflect that.
        
        final Step subject1 = new Step(IDENTIFIER, "should-be-ignored-in-equals-and-hashcode");
        final Step subject2 = new Step(IDENTIFIER);
        final int hashSubject1 = subject1.hashCode();
        final int hashSubject2 = subject2.hashCode();

        assertEquals(subject1, subject2);
        assertEquals(hashSubject1, hashSubject2);
    }

    @Test
    public void test_setTheme_setValues() {
        final Step subject = makeStep();

        assertEquals(R.color.rsb_colorPrimary, subject.getPrimaryColor());
        assertEquals(R.color.rsb_colorPrimaryDark, subject.getColorPrimaryDark());
        assertEquals(R.color.rsb_colorAccent, subject.getColorSecondary());
        assertEquals(R.color.rsb_colorAccentDark, subject.getPrincipalTextColor());
        assertEquals(R.color.rsb_black, subject.getSecondaryTextColor());
        assertEquals(R.color.rsb_black_20, subject.getActionFailedColor());

        subject.setStepTheme(
                R.color.md_material_blue_600,
                R.color.md_material_blue_600,
                R.color.md_material_blue_600,
                R.color.md_material_blue_600,
                R.color.md_material_blue_600,
                R.color.md_material_blue_600
        );

        assertEquals(R.color.md_material_blue_600, subject.getPrimaryColor());
        assertEquals(R.color.md_material_blue_600, subject.getColorPrimaryDark());
        assertEquals(R.color.md_material_blue_600, subject.getColorSecondary());
        assertEquals(R.color.md_material_blue_600, subject.getPrincipalTextColor());
        assertEquals(R.color.md_material_blue_600, subject.getSecondaryTextColor());
        assertEquals(R.color.md_material_blue_600, subject.getActionFailedColor());
    }

    private Step makeStep() {
        final Step step = new Step(IDENTIFIER, TITLE);
        step.setStepTheme(
                R.color.rsb_colorPrimary,
                R.color.rsb_colorPrimaryDark,
                R.color.rsb_colorAccent,
                R.color.rsb_colorAccentDark,
                R.color.rsb_black,
                R.color.rsb_black_20);

        step.setOptional(true);
        step.setQuestion("question");
        step.setText("step_text");
        step.setHidden(true);

        return step;
    }
}